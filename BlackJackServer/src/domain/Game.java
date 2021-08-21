package domain;

import game.PlayerQueue;
import server.ConnectionHandler;

import java.util.*;

public class Game {

    private final Deck deck = new Deck();
    private final List<Player> players;

    public Game(List<Player> players) {
        this.players = players;
    }

    public void drawCard(Player player) {
        Card card = deck.getCard();
        player.addCard(card);
    }

    public static NetworkTransferable<Game> networkTransferable() {
        NetworkTransferable<Player> playerNetworkTransferable = Player.networkTransferable();
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Game value) {
                return String.format("%s@%s@%s@%s", playerNetworkTransferable.toTransferString(value.players.get(0)),
                        playerNetworkTransferable.toTransferString(value.players.get(1)),
                        playerNetworkTransferable.toTransferString(value.players.get(2)),
                        playerNetworkTransferable.toTransferString(value.players.get(3)));
            }

            @Override
            public Game fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                throw new IllegalArgumentException("Server cannot read games.");
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
