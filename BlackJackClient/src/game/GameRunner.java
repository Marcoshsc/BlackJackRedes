package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.*;
import main.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class GameRunner implements Runnable {

    private Game game;
    private final ConnectionHandler connectionHandler;
    private final String username;
    private final ScoreCounter scoreCounter;
    private final Scanner scanner = new Scanner(System.in);

    public GameRunner(Game game, ConnectionHandler connectionHandler, String username) {
        this.game = game;
        this.connectionHandler = connectionHandler;
        this.scoreCounter = new ScoreCounter();
        this.username = username;
    }

    public GameRunner(Game game, ConnectionHandler connectionHandler, ScoreCounter scoreCounter, String username) {
        this.game = game;
        this.username = username;
        this.connectionHandler = connectionHandler;
        this.scoreCounter = scoreCounter;
    }

    @Override
    public void run() {
        runGame();
    }

    private void runGame() {
        try {
            while(true) {
                printGameState();
                Player player = getThisPlayer();
                assert player != null;
                if(game.getStage().equals("bet") && game.getTurn().equals(player.getUsername())) {
                    System.out.printf("Fase de apostas! O maior valor apostado foi %f, sua aposta foi %f e você precisa igualar se quiser continuar jogando.\n",
                            game.getCurrentBet(), player.getBet());
                    System.out.println("Vai apostar quanto? Pra correr digite -1. Lembrando que o valor é somado ao que você já apostou.");
                    double bet;
                    do {
                        bet = scanner.nextDouble();
                        if(bet != -1 && bet + player.getBet() < game.getCurrentBet()) {
                            System.out.println("Pelo menos precisa completar a aposta atual, se não aguentar corre!");
                        }
                    } while(bet != -1 && bet + player.getBet() < game.getCurrentBet());
                    CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.RAISE_DECISION,
                            RaiseDecision.networkTransferable(), new RaiseDecision(bet, bet == -1));
                }
                if(game.getStage().equals("draw") && game.getTurn().equals(player.getUsername())) {
                    System.out.println("Fase de puxar cartas! Quer puxar outra carta (p) ou manter (m)?");
                    String answer = scanner.next();
                    CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.DRAW_DECISION, DrawDecision.networkTransferable(),
                            new DrawDecision(answer.equals("m")));
                }
                CommunicationAnswer answer = CommunicationHandler.of(connectionHandler).getMessage(
                        Arrays.asList(CommunicationTypes.GAME_INFO, CommunicationTypes.GAME_END),
                        Arrays.asList(Game.networkTransferable(), GameEndInfo.gameEndInfoNetworkTransferable()));
                if (answer.getType() == CommunicationTypes.GAME_END) {
                    GameEndInfo endInfo = (GameEndInfo) answer.getValue();
                    String winnersInfo = endInfo.getWinners().size() != 0 ? String.join(",", endInfo.getWinners()) : "Casa";
                    System.out.printf("Fim de jogo! %s são os vencedores com %d pontos. A casa ficou com %d pontos.\n", winnersInfo,
                            endInfo.getPoints(), endInfo.getHouse());
                    break;
                }
                else {
                    game = (Game) answer.getValue();
                }
            }
            goToPlayAgainHandler();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void printGameState() {
        Player player = getThisPlayer();
        assert player != null;
        System.out.printf("Você tem %f de saldo e %f apostados.\n", player.getBalance(), player.getBet());
        System.out.println("Suas cartas: ");
        System.out.println(player.getCards().size() + " Cards.");
        for (Card card : player.getCards()) {
            System.out.printf("%s of %s\n", card.getFaces(), card.getSuit());
        }
        System.out.printf("Sua pontuação: %d\n", player.getValue());
        System.out.println();
        System.out.println("Seus oponentes: ");
        for (Player opponent : game.getPlayers()) {
            System.out.printf("%s com %f de saldo e %f apostados, status %s, primeira carta %s de %s\n", opponent.getUsername(),
                    opponent.getBalance(), opponent.getBet(), opponent.getStatus(), opponent.getCards().get(0).getFaces(),
                    opponent.getCards().get(0).getSuit());
        }
        System.out.printf("Carta da casa: %s de %s \n", game.getCroupietCard().getFaces(), game.getCroupietCard().getSuit());
        System.out.println("Vez de " + game.getTurn());
    }

    private Player getThisPlayer() {
        for (Player player : game.getPlayers()) {
            if(player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    private void goToPlayAgainHandler() {
        Thread playAgainThread = new Thread(new PlayAgainManager(connectionHandler, getThisPlayer()));
        playAgainThread.start();
    }


}
