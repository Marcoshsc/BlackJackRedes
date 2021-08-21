package domain;

import java.util.ArrayList;
import java.util.List;

public class Game {


    private final Deck deck = new Deck();
    private final List<Player> players;
    private final String turn;

    public Game(List<Player> players, String turn) {
        this.players = players;
        this.turn = turn;
    }

    public void drawCard(Player player) {
        Card card = deck.getCard();
        player.addCard(card);
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
                return String.format("%s@%s@%s@%s@%s", playerNetworkTransferable.toTransferString(value.players.get(0)),
                        playerNetworkTransferable.toTransferString(value.players.get(1)),
                        playerNetworkTransferable.toTransferString(value.players.get(2)),
                        playerNetworkTransferable.toTransferString(value.players.get(3)),
                        value.turn);
            }

            @Override
            public Game fromTransferString(String transferString) {
                String[] splitted = transferString.split("@");
                List<Player> players = new ArrayList<>();
                for (int i = 0; i < splitted.length - 1; i++) {
                    String string = splitted[i];
                    players.add(Player.networkTransferable().fromTransferString(string));
                }
                return new Game(players, splitted[splitted.length - 1]);
            }
        };
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Player> getPlayers() {
        return players;
    }
}