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
        try {
            while(true) {
                System.out.println("Suas cartas: ");
                assert player != null;
                for (Card card : player.getCards()) {
                    System.out.printf("%s of %s\n", card.getFaces(), card.getSuit());
                }
                if(game.getTurn().equals(username)) {
                    System.out.println(game.getTurn());
                    System.out.println("Vai betar quanto?");
                    double bet = scanner.nextDouble();
                    CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.RAISE_DECISION,
                            RaiseDecision.networkTransferable(), new RaiseDecision(bet, false));
                }
                System.out.println("Aguardando " + game.getTurn() + " Betar...");
                game = (Game) CommunicationHandler.of(connectionHandler).getMessage(Collections.singletonList(CommunicationTypes.GAME_INFO),
                        Collections.singletonList(Game.networkTransferable())).getValue();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
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
