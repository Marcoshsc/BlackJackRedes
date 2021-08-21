package game;

import communication.CommunicationAnswer;
import communication.CommunicationHandler;
import communication.CommunicationTypes;
import domain.Player;

import java.io.IOException;
import java.util.Arrays;

public class PlayerChoiceThread implements Runnable {

    private Player p1;
    private Player p2;
    private boolean forP1;
    private PlayAgainState playAgainState;

    public PlayerChoiceThread(Player p1, Player p2, boolean forP1, PlayAgainState playAgainState) {
        this.p1 = p1;
        this.p2 = p2;
        this.forP1 = forP1;
        this.playAgainState = playAgainState;
    }

    @Override
    public void run() {
        Player player = forP1 ? p1 : p2;
        Player opponent = forP1 ? p2 : p1;
        try {
            if (player.isComputer()) {
                if (forP1)
                    playAgainState.setP1Wants();
                else
                    playAgainState.setP2Wants();
                return;
            }
            try {
                CommunicationAnswer answer = CommunicationHandler.of(player.getConnectionHandler()).getMessage(
                        Arrays.asList(CommunicationTypes.LEAVE, CommunicationTypes.PLAY_AGAIN),
                        Arrays.asList(null, null)
                );
                if (answer.getType() == CommunicationTypes.LEAVE) {
                    if (player.getConnectionHandler() != null) {
                        CommunicationHandler.of(player.getConnectionHandler()).sendMessage(CommunicationTypes.LEAVE);
                    }
                    if (opponent.getConnectionHandler() != null) {
                        CommunicationHandler.of(opponent.getConnectionHandler()).sendMessage(CommunicationTypes.LEAVE);
                    }
                } else {
                    if (forP1)
                        playAgainState.setP1Wants();
                    else
                        playAgainState.setP2Wants();
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }
}
