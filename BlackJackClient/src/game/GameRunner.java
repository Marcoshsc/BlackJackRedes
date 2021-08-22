package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.*;
import domain.enums.PlayerStatus;
import main.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class GameRunner implements Runnable {

    private Game game;
    private final ConnectionHandler connectionHandler;
    private final String username;
    private final List<Player> players;
    private final ScoreCounter scoreCounter;
    private final Scanner scanner = new Scanner(System.in);

    public GameRunner(Game game, ConnectionHandler connectionHandler, List<Player> players, String username) {
        this.game = game;
        this.connectionHandler = connectionHandler;
        this.players = players;
        this.scoreCounter = new ScoreCounter();
        this.username = username;
    }

    public GameRunner(Game game, ConnectionHandler connectionHandler, List<Player> players, ScoreCounter scoreCounter, String username) {
        this.game = game;
        this.username = username;
        this.connectionHandler = connectionHandler;
        this.players = players;
        this.scoreCounter = scoreCounter;
    }

    @Override
    public void run() {
        runGame();
    }

    private void runGame() {
        try {
            while(true) {
                betPhase();
                System.out.println("Fase de apostas concluida! Verificando vencedor...");
                CommunicationAnswer answer = CommunicationHandler.of(connectionHandler).getMessage(
                        Arrays.asList(CommunicationTypes.GAME_INFO, CommunicationTypes.GAME_END),
                        Arrays.asList(Game.networkTransferable(), GameEndInfo.gameEndInfoNetworkTransferable()));
                if (answer.getType() == CommunicationTypes.GAME_END) {
                    GameEndInfo endInfo = (GameEndInfo) answer.getValue();
                    System.out.printf("Game ended! %s are the winners with %d points.\n",
                            String.join(",", endInfo.getWinners()), endInfo.getPoints());
                    break;
                } else {
                    game = (Game) answer.getValue();
                    drawPhase();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void drawPhase() throws IOException {
        for(int i = 0; i < game.getPlayers().size(); i++) {
            System.out.printf("Verification number %d\n", i);
            printGameState();
            Player player = getThisPlayer();
            assert player != null;
            if (game.getTurn().equals(username) && player.getStatus() == PlayerStatus.PLAYING) {
                System.out.println("Fase de puxar cartas! Quer puxar outra carta (p) ou manter (m)?");
                String answer = scanner.next();
                CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.DRAW_DECISION, DrawDecision.networkTransferable(),
                        new DrawDecision(answer.equals("m")));
            }
            game = (Game) CommunicationHandler.of(connectionHandler).getMessage(Collections.singletonList(CommunicationTypes.GAME_INFO),
                    Collections.singletonList(Game.networkTransferable())).getValue();
        }
    }

    private void betPhase() throws IOException {
        while(true) {
            Player player = getThisPlayer();
            assert player != null;
            printGameState();
            if(game.getTurn().equals(username) && player.getStatus() == PlayerStatus.PLAYING &&
                    (player.getBet() < game.getCurrentBet() || game.getCurrentBet() == 0)) {
                System.out.printf("Fase de apostas! O maior valor apostado foi %f, sua aposta foi %f e você precisa igualar se quiser continuar jogando.\n",
                        game.getCurrentBet(), player.getBet());
                System.out.println("Vai betar quanto?");
                double bet = scanner.nextDouble();
                CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.RAISE_DECISION,
                        RaiseDecision.networkTransferable(), new RaiseDecision(bet, false));
            }
            else {
                System.out.println("Aguardando " + game.getTurn() + " Betar...");
            }
            if(areBetsEqual()) {
                break;
            }
            game = (Game) CommunicationHandler.of(connectionHandler).getMessage(Collections.singletonList(CommunicationTypes.GAME_INFO),
                    Collections.singletonList(Game.networkTransferable())).getValue();
        }
        game = (Game) CommunicationHandler.of(connectionHandler).getMessage(Collections.singletonList(CommunicationTypes.GAME_INFO),
                Collections.singletonList(Game.networkTransferable())).getValue();
    }

    private boolean areBetsEqual() {
        if(game.getCurrentBet() == 0d) {
            return false;
        }
        boolean equal = true;
        for (int i = 1; i < game.getPlayers().size(); i++) {
            if(game.getPlayers().get(i).getBet() != game.getPlayers().get(0).getBet()) {
                equal = false;
            }
        }
        return equal;
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
//        Thread playAgainThread = new Thread(new PlayAgainManager(connectionHandler, player, scoreCounter, isP1));
//        playAgainThread.start();
    }


}
