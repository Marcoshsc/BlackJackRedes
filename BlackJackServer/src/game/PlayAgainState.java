package game;

import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.Player;

import java.io.IOException;

public class PlayAgainState {

    private boolean p1Wants = false;
    private boolean p2Wants = false;
    private final Player p1;
    private final Player p2;

    public PlayAgainState(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public synchronized void setP1Wants() throws IOException {
        p1Wants = true;
        if(p2Wants) {
            createNewGame();
        }
    }

    public synchronized void setP2Wants() throws IOException {
        p2Wants = true;
        if(p1Wants) {
            createNewGame();
        }
    }

    private void createNewGame() throws IOException {
//        Game game = new Game(p1, p2);
        Game game = null;
        if(p1.getConnectionHandler() != null) {
            CommunicationHandler.of(p1.getConnectionHandler()).sendMessage(CommunicationTypes.GAME_FOUND,
                    Game.networkTransferable(), game);
        }
        if(p2.getConnectionHandler() != null) {
            CommunicationHandler.of(p2.getConnectionHandler()).sendMessage(CommunicationTypes.GAME_FOUND,
                    Game.networkTransferable(), game);
        }
        GameInstance gameInstance = new GameInstance(game);
        new Thread(gameInstance).start();
    }
}
