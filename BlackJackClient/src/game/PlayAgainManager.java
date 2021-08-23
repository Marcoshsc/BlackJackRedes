package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.Player;
import main.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PlayAgainManager implements Runnable {

    private final ConnectionHandler connectionHandler;
    private final Player player;
    private final List<String> winners;
    private final ScoreCounter scoreCounter;

    public PlayAgainManager(ConnectionHandler connectionHandler, Player player, List<String> winners, ScoreCounter scoreCounter) {
        this.connectionHandler = connectionHandler;
        this.player = player;
        this.winners = winners;
        this.scoreCounter = scoreCounter;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        for (String winner : winners) {
            scoreCounter.incrementPlayer(winner);
        }
        System.out.println("Estatisticas: ");
        scoreCounter.print();
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
                System.out.println("Algum(uns) de seus oponentes desistiram de jogar mais!");
                return;
            }
            else {
                Game game = (Game) answerServer.getValue();
                GameRunner gameRunner = new GameRunner(game, connectionHandler, scoreCounter, player.getUsername());
                new Thread(gameRunner).start();
            }
        } catch(IOException exc) {
            exc.printStackTrace();
            System.out.println("Seu oponente não quer jogar mais!");
        }
    }
}
