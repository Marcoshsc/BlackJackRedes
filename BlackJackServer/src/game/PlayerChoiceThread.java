package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Game;
import domain.Player;

import java.io.IOException;
import java.util.Arrays;

public class PlayerChoiceThread implements Runnable {

    private Player player;
    private PlayAgainState playAgainState;

    public PlayerChoiceThread(Player player, PlayAgainState playAgainState) {
        this.player = player;
        this.playAgainState = playAgainState;
    }

    @Override
    public void run() {
        try {
            CommunicationAnswer answer = CommunicationHandler.of(player.getConnectionHandler()).getMessage(
                    Arrays.asList(CommunicationTypes.LEAVE, CommunicationTypes.PLAY_AGAIN),
                    Arrays.asList(null, null)
            );
            playAgainState.wants(player, answer.getType() != CommunicationTypes.LEAVE);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
