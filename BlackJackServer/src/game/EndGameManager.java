package game;

import domain.Player;

public class EndGameManager implements Runnable {

    private final Player p1;
    private final Player p2;

    public EndGameManager(Player p1, Player p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    @Override
    public void run() {
       runAction();
    }

    private void runAction() {
        PlayAgainState playAgainState = new PlayAgainState(p1, p2);
        PlayerChoiceThread threadP1 = new PlayerChoiceThread(p1, p2, true, playAgainState);
        PlayerChoiceThread threadP2 = new PlayerChoiceThread(p1, p2, false, playAgainState);
        Thread t1 = new Thread(threadP1);
        Thread t2 = new Thread(threadP2);
        t1.start();
        t2.start();
    }

}
