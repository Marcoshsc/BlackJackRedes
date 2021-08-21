package game;

public class ScoreCounter {

    private int p1Score = 0;
    private int p2Score = 0;
    private int games = 0;

    public synchronized void p1Scored() {
        p1Score++;
    }

    public synchronized void p2Scored() {
        p2Score++;
    }

    public synchronized void incrementGames() {
        games++;
    }

    public synchronized int getP1Score() {
        return p1Score;
    }

    public synchronized int getP2Score() {
        return p2Score;
    }

    public synchronized int getGames() {
        return games;
    }
}
