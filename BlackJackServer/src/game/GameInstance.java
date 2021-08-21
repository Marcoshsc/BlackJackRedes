package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.DrawDecision;
import domain.Player;
import domain.RaiseDecision;
import domain.enums.PlayerStatus;
import server.ConnectionHandler;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class GameInstance implements Runnable {

    private final Game game;
    private final PlayerQueue playerQueue;

    public GameInstance(Game game) {
        this.game = game;
        this.playerQueue = new PlayerQueue(game.getPlayers());
    }

    @Override
    public void run() {
        runGame();
    }

    private void runGame() {
        try {
            do {
                for (int i = 0; i < game.getPlayers().size(); i++) {
                    Player currentPlayer = playerQueue.dequeue();
                    if(currentPlayer.getStatus() != PlayerStatus.PLAYING)
                        continue;
                    ConnectionHandler connectionHandler = currentPlayer.getConnectionHandler();
                    RaiseDecision raiseDecision = (RaiseDecision) CommunicationHandler.of(connectionHandler).getMessage(
                            Collections.singletonList(CommunicationTypes.RAISE_DECISION),
                            Collections.singletonList(RaiseDecision.networkTransferable())).getValue();
                    currentPlayer.raiseBet(raiseDecision.getRaiseValue());
                }

                for (int i = 0; i < game.getPlayers().size(); i++) {
                    Player currentPlayer = playerQueue.dequeue();
                    if(currentPlayer.getStatus() != PlayerStatus.PLAYING)
                        continue;
                    ConnectionHandler connectionHandler = currentPlayer.getConnectionHandler();
                    DrawDecision drawDecision = (DrawDecision) CommunicationHandler.of(connectionHandler).getMessage(
                            Collections.singletonList(CommunicationTypes.DRAW_DECISION),
                            Collections.singletonList(DrawDecision.networkTransferable())
                    ).getValue();
                    if(drawDecision.isGiveup()) {
                        currentPlayer.giveUp();
                    }
                    else {
                        game.drawCard(currentPlayer);
                    }
                }
            } while (true);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    private boolean handleMove(Player currentPlayer, ConnectionHandler connectionHandler,
                            ConnectionHandler notPlayingConnectionHandler, DrawDecision move) throws IOException {
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
