package domain;

import domain.enums.PlayerStatus;

import java.util.ArrayList;
import java.util.List;

public class Game {


    private final Deck deck = new Deck();
    private final List<Player> players;
    private final String turn;
    private final String stage;
    private final double currentBet;

    public Game(List<Player> players, String turn, String stage, double currentBet) {
        this.players = players;
        this.turn = turn;
        this.stage = stage;
        this.currentBet = currentBet;
    }

    public void drawCard(Player player) {
        Card card = deck.getCard();
        player.addCard(card);
    }

    public double getCurrentBet() {
        return currentBet;
    }

    public String getStage() {
        return stage;
    }

    public String getTurn() {
        return turn;
    }

    public List<Player> getWinners() {
        List<Player> winners = new ArrayList<>();
        boolean blackJack = false;
        for (Player player : players) {
            if (player.blackJack()) {
                winners.add(player);
                blackJack = true;
                continue;
            }
            int points = player.getValue();
            if (points == 21 && !blackJack) {
                winners.add(player);
            }
        }
        return winners;
    }

    public static NetworkTransferable<Game> networkTransferable() {
        NetworkTransferable<Player> playerNetworkTransferable = Player.networkTransferable();
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Game value) {
                return String.format("%s@%s@%s@%s@%s@%s", playerNetworkTransferable.toTransferString(value.players.get(0)),
                        playerNetworkTransferable.toTransferString(value.players.get(1)),
                        playerNetworkTransferable.toTransferString(value.players.get(2)),
                        playerNetworkTransferable.toTransferString(value.players.get(3)),
                        value.turn, value.stage);
            }

            @Override
            public Game fromTransferString(String transferString) {
                String[] splitted = transferString.split("@");
                List<Player> players = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    String string = splitted[i];
                    players.add(Player.networkTransferable().fromTransferString(string));
                }
                return new Game(players, splitted[4], splitted[5], Double.parseDouble(splitted[6]));
            }
        };
    }

    public Deck getDeck() {
        return deck;
    }

    public int getValidPlayers() {
        int number = 0;
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).getStatus() == PlayerStatus.PLAYING)
                number++;
        }
        return number;
    }

    public boolean everyoneBetted() {
        boolean betted = true;
        for (Player player : players) {
            System.out.printf("Player: %s - Betted: %s\n", player.getUsername(), player.isBetted());
            if(!player.isBetted()) {
                betted = false;
            }
        }
        return betted;
    }

    public List<Player> getPlayers() {
        return players;
    }
}