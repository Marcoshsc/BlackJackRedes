package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.Player;
import main.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class GameRunner implements Runnable {

    private Game game;
    private final ConnectionHandler connectionHandler;
    private final Player player;
    private final boolean isP1;
    private final ScoreCounter scoreCounter;

    public GameRunner(Game game, ConnectionHandler connectionHandler, Player player) {
        this.game = game;
        this.connectionHandler = connectionHandler;
        this.player = player;
//        this.isP1 = player.getShape() == game.getPlayer1().getShape();
        this.isP1 = true;
        this.scoreCounter = new ScoreCounter();
    }

    public GameRunner(Game game, ConnectionHandler connectionHandler, Player player, ScoreCounter scoreCounter) {
        this.game = game;
        this.connectionHandler = connectionHandler;
        this.player = player;
//        this.isP1 = player.getShape() == game.getPlayer1().getShape();
        this.isP1 = true;
        this.scoreCounter = scoreCounter;
    }

    @Override
    public void run() {
        runGame();
    }

    private void runGame() {
    }

    private void goToPlayAgainHandler() {
        Thread playAgainThread = new Thread(new PlayAgainManager(connectionHandler, player, scoreCounter, isP1));
        playAgainThread.start();
    }


}
