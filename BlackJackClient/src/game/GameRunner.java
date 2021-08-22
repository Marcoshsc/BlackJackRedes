package game;

import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Card;
import domain.Game;
import domain.Player;
import domain.RaiseDecision;
import main.ConnectionHandler;

import java.io.IOException;
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
        Player player = getThisPlayer();
        assert player != null;
        try {
            betPhase(player);
            System.out.println("Fase de apostas concluida! Puxando novas cartas...");
            game = (Game) CommunicationHandler.of(connectionHandler).getMessage(Collections.singletonList(CommunicationTypes.GAME_INFO),
                    Collections.singletonList(Game.networkTransferable())).getValue();
            return;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void betPhase(Player player) throws IOException {
        while(true) {
            printGameState();
            if(game.getTurn().equals(username) && (player.getBet() < game.getCurrentBet() || game.getCurrentBet() == 0)) {
                System.out.printf("Fase de apostas! O maior valor apostado foi %f, e você precisa igualar se quiser continuar jogando.\n",
                        game.getCurrentBet());
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
        System.out.println();
        System.out.println("Seus oponentes: ");
        for (Player opponent : game.getPlayers()) {
            System.out.printf("%s com %f de saldo e %f apostados, status %s\n", opponent.getUsername(),
                    opponent.getBalance(), opponent.getBet(), opponent.getStatus());
        }
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
