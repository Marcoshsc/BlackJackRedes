package game;

import domain.Player;

import java.util.List;

public class PlayerQueue {

    private final List<Player> players;
    private final int size;
    private int current = 0;

    public PlayerQueue(List<Player> players) {
        this.players = players;
        this.size = players.size();
    }

    public synchronized Player dequeue() {
        Player player = players.get(current);
        current = (current + 1) % size;
        return player;
    }

}
