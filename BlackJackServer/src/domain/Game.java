package domain;

import game.PlayerQueue;
import server.ConnectionHandler;

import java.util.*;

public class Game {

    private final Deck deck = new Deck();
    private final List<Player> players;
    private String turn = null;

    public Game(List<Player> players) {
        this.players = players;
    }

    public void drawCard(Player player) {
        Card card = deck.getCard();
        player.addCard(card);
    }

    public List<Player> getWinners() {
        List<Player> winners = new ArrayList<>();
        boolean blackJack = false;
        for (Player player : players) {
            if(player.blackJack()) {
                winners.add(player);
                blackJack = true;
                continue;
            }
            int points = player.getValue();
            if(points == 21 && !blackJack) {
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
                        value.getTurn());
            }

            @Override
            public Game fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                throw new IllegalArgumentException("Server cannot read games.");
            }
        };
    }



    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
