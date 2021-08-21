package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.Move;
import domain.NetworkTransferable;
import domain.Player;
import server.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class GameInstance implements Runnable {

    private final Game game;
    private final Player p1;
    private final Player p2;
    private boolean p1Turn = true;

    public GameInstance(Game game) {
        this.game = game;
        this.p1 = game.getPlayer1();
        this.p2 = game.getPlayer2();
    }

    @Override
    public void run() {
        runGame();
    }

    private void runGame() {
        try {
            do {
                Player currentPlayer = p1Turn ? p1 : p2;
                Player notPlayingPlayer = p1Turn ? p2 : p1;
                ConnectionHandler connectionHandler = currentPlayer.getConnectionHandler();
                ConnectionHandler notPlayingConnectionHandler = notPlayingPlayer.getConnectionHandler();
                if(connectionHandler == null) {
                    Move move = game.getRandomValidMove();
                    boolean finished = handleMove(currentPlayer, null, notPlayingConnectionHandler, move);
                    if(finished)
                        break;
                    p1Turn = !p1Turn;
                    continue;
                }
                CommunicationAnswer answer = CommunicationHandler.of(connectionHandler).getMessage(
                        Arrays.asList(CommunicationTypes.PLAYER_MOVE),
                        Arrays.asList(Move.networkTransferable(), null)
                );
                Move move = (Move) answer.getValue();
                try {
                    boolean finished = handleMove(currentPlayer, connectionHandler, notPlayingConnectionHandler, move);
                    if(finished)
                        break;
                } catch (IllegalArgumentException exc) {
                    CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.ILLEGAL_MOVE);
                    continue;
                }
                p1Turn = !p1Turn;
            } while (true);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private boolean handleMove(Player currentPlayer, ConnectionHandler connectionHandler,
                            ConnectionHandler notPlayingConnectionHandler, Move move) throws IOException {
        game.makeMove(move, currentPlayer.getShape());
        if(game.isFinished()) {
            if(connectionHandler != null) {
                CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.GAME_END,
                        Game.networkTransferable(), game);
            }
            if(notPlayingConnectionHandler != null) {
                CommunicationHandler.of(notPlayingConnectionHandler).sendMessage(CommunicationTypes.GAME_END,
                        Game.networkTransferable(), game);
            }
            EndGameManager endGameManager = new EndGameManager(p1, p2);
            Thread thread = new Thread(endGameManager);
            thread.start();
            return true;
        }
        if(connectionHandler != null) {
            CommunicationHandler.of(connectionHandler).sendMessage(CommunicationTypes.PLAYER_MOVE,
                    Game.networkTransferable(), game);
        }
        if(notPlayingConnectionHandler != null) {
            CommunicationHandler.of(notPlayingConnectionHandler).sendMessage(CommunicationTypes.PLAYER_MOVE,
                    Game.networkTransferable(), game);
        }
        return false;
    }
}
