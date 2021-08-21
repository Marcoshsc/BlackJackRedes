package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.Move;
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
        this.isP1 = player.getShape() == game.getPlayer1().getShape();
        this.scoreCounter = new ScoreCounter();
    }

    public GameRunner(Game game, ConnectionHandler connectionHandler, Player player, ScoreCounter scoreCounter) {
        this.game = game;
        this.connectionHandler = connectionHandler;
        this.player = player;
        this.isP1 = player.getShape() == game.getPlayer1().getShape();
        this.scoreCounter = scoreCounter;
    }

    @Override
    public void run() {
        runGame();
    }

    private void runGame() {
        String opponentName = (isP1 ? game.getPlayer2().getUsername() : game.getPlayer1().getUsername());
        System.out.println("Oponente: " + opponentName);
        try {
            int madeMoves = 0;
            while (true) {
                System.out.println(game.getBoardString());
                boolean p1Turn = madeMoves % 2 == 0;
                if ((p1Turn && isP1) || (!p1Turn && !isP1)) {
                    Move move = readMove();
                    CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.PLAYER_MOVE, Move.networkTransferable(),
                            move);
                    CommunicationAnswer answer = CommunicationHandler.of(connectionHandler).getMessage(
                            Arrays.asList(CommunicationTypes.PLAYER_MOVE, CommunicationTypes.ILLEGAL_MOVE, CommunicationTypes.GAME_END),
                            Arrays.asList(Game.networkTransferable(), null, Game.networkTransferable())
                    );
                    if(answer.getType() == CommunicationTypes.GAME_END) {
                        game = (Game) answer.getValue();
                        scoreCounter.incrementGames();
                        System.out.println(game.getBoardString());
                        if(!game.someoneWon())
                            System.out.println("Empate!");
                        else {
                            System.out.println("Você Venceu!");
                            if(isP1)
                                scoreCounter.p1Scored();
                            else
                                scoreCounter.p2Scored();
                        }
                        goToPlayAgainHandler();
                        break;
                    }
                    if(answer.getType() == CommunicationTypes.PLAYER_MOVE)
                        game = (Game) answer.getValue();
                    if(answer.getType() == CommunicationTypes.ILLEGAL_MOVE) {
                        System.out.println("Jogada ilegal!");
                        continue;
                    }
                    madeMoves++;
                }
                if ((!p1Turn && isP1) || (p1Turn && !isP1)) {
                    System.out.println("Esperando seu oponente jogar...");
                    CommunicationAnswer answer = CommunicationHandler.of(connectionHandler).getMessage(
                            Arrays.asList(CommunicationTypes.PLAYER_MOVE, CommunicationTypes.GAME_END),
                            Arrays.asList(Game.networkTransferable(), Game.networkTransferable())
                    );
                    if(answer.getType() == CommunicationTypes.GAME_END) {
                        game = (Game) answer.getValue();
                        System.out.println(game.getBoardString());
                        scoreCounter.incrementGames();
                        if(!game.someoneWon())
                            System.out.println("Empate!");
                        else {
                            System.out.println("Você Perdeu! Vitória de " + opponentName);
                            if(isP1)
                                scoreCounter.p2Scored();
                            else
                                scoreCounter.p1Scored();
                        }
                        goToPlayAgainHandler();
                        break;
                    }
                    if(answer.getType() == CommunicationTypes.PLAYER_MOVE)
                        game = (Game) answer.getValue();
                    System.out.println("Jogada do seu oponente:");
                    madeMoves++;
                }
            }
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }

    private void goToPlayAgainHandler() {
        Thread playAgainThread = new Thread(new PlayAgainManager(connectionHandler, player, scoreCounter, isP1));
        playAgainThread.start();
    }

    private Move readMove() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite a linha do seu movimento!");
        int row = scanner.nextInt();
        System.out.println("Digite a coluna do seu movimento!");
        int column = scanner.nextInt();
        return new Move(row, column);
    }

}
