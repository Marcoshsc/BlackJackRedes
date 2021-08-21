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

public class PlayAgainManager implements Runnable {

    private final ConnectionHandler connectionHandler;
    private final Player player;
    private final ScoreCounter prevScore;
    private final boolean isP1;

    public PlayAgainManager(ConnectionHandler connectionHandler, Player player, ScoreCounter prevScore, boolean isP1) {
        this.connectionHandler = connectionHandler;
        this.player = player;
        this.prevScore = prevScore;
        this.isP1 = isP1;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        int p1Score = isP1 ? prevScore.getP1Score() : prevScore.getP2Score();
        int p2Score = isP1 ? prevScore.getP2Score() : prevScore.getP1Score();
        int games = prevScore.getGames();
        System.out.println("Você tem " + p1Score + " pontos.");
        System.out.println("Seu adversário tem " + p2Score + " pontos.");
        System.out.println("Vocês já jogaram " + games + " jogos.");
        System.out.println();
        System.out.println("Deseja jogar novamente (1 - sim, 0 - não)?");
        int answer = scanner.nextInt();
        try {
            if(answer == 0) {
                CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.LEAVE);
                return;
            }
            System.out.println("Esperando resposta do seu oponente...");
            CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.PLAY_AGAIN);
            CommunicationAnswer answerServer = CommunicationHandler.of(connectionHandler).getMessage(
                    Arrays.asList(CommunicationTypes.LEAVE, CommunicationTypes.GAME_FOUND),
                    Arrays.asList(null, Game.networkTransferable())
            );
            if(answerServer.getType() == CommunicationTypes.LEAVE) {
                System.out.println("Seu oponente não quer jogar mais!");
                return;
            }
            else {
                Game game = (Game) answerServer.getValue();
                GameRunner gameRunner = new GameRunner(game, connectionHandler, player, prevScore);
                new Thread(gameRunner).start();
            }
        } catch(IOException exc) {
            exc.printStackTrace();
            System.out.println("Seu oponente não quer jogar mais!");
        }
    }
}
