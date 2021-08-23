package game;

import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayAgainState {

    private List<Boolean> wantsArray = new ArrayList<>();
    private Game game;

    public PlayAgainState(Game game) {
        this.game = game;
        for (Player ignored : game.getPlayers()) {
            wantsArray.add(null);
        }
    }

    public synchronized void wants(Player player, boolean value) throws IOException {
        int position = game.getPlayers().indexOf(player);
        wantsArray.set(position, value);
        checkWanting();
    }

    public synchronized void checkWanting() throws IOException {
        boolean allWants = true;
        boolean allAnswered = true;
        for (Boolean value : wantsArray) {
            if(value == null) {
                allAnswered = false;
            }
            if(value != null && !value) {
                allWants = false;
            }
        }
        if(!allAnswered) {
            return;
        }
        if(allWants) {
            System.out.println("creating a new game");
            createNewGame();
        }
        else {
            System.out.println("Finishing the game");
            for (Player player : game.getPlayers()) {
                CommunicationHandler.of(player.getConnectionHandler()).sendMessage(CommunicationTypes.LEAVE);
            }
        }
    }

    private void createNewGame() throws IOException {
        game.reset();
        for (Player player : game.getPlayers()) {
            CommunicationHandler.of(player.getConnectionHandler()).sendMessage(CommunicationTypes.GAME_FOUND,
                    Game.networkTransferable(), player.getUsername(), game);
        }
        GameInstance gameInstance = new GameInstance(game);
        new Thread(gameInstance).start();
    }
}
