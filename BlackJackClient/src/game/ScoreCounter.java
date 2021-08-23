package game;

import domain.Game;
import domain.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreCounter {

    private int games = 0;
    private Map<String, Integer> wins = new HashMap<>();

    public ScoreCounter(Game game) {
        for (Player player : game.getPlayers()) {
            wins.put(player.getUsername(), 0);
        }
    }

    public synchronized void incrementPlayer(String username) {
        wins.put(username, wins.get(username) + 1);
    }

    public Map<String, Integer> getWins() {
        return wins;
    }

    public synchronized void incrementGames() {
        games++;
    }

    public synchronized int getGames() {
        return games;
    }

    public void print() {
        System.out.printf("%d Jogos jogados\n", games);
        for (String key : wins.keySet()) {
            System.out.printf("%s ganhou %d vezes\n", key, wins.get(key));
        }
    }
}
