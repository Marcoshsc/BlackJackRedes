package domain;

import server.ConnectionHandler;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final ConnectionHandler connectionHandler;
    private final String username;
    private double balance = 1000d;
    private double bet = 0d;
    private List<Card> cards = new ArrayList<>();

    public Player(ConnectionHandler connectionHandler, String username) {
        this.connectionHandler = connectionHandler;
        this.username = username;
    }

    public Player(ConnectionHandler connectionHandler, String username, double balance, List<Card> cards, double bet) {
        this.connectionHandler = connectionHandler;
        this.username = username;
        this.balance = balance;
        this.cards = cards;
        this.bet = bet;
    }

    public static NetworkTransferable<Player> networkTransferable() {
        return new NetworkTransferable<>() {
            @Override
            public String toTransferString(Player value) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < value.cards.size(); i++) {
                    stringBuilder.append(String.format("%s*%s", value.cards.get(i).getFaces(), value.cards.get(i).getSuit()));
                    if(i != value.getCards().size() - 1) {
                        stringBuilder.append("-");
                    }
                }
                return String.format("%s/%f/%s/%f", value.username, value.balance, stringBuilder.toString(), value.bet);
            }

            @Override
            public Player fromTransferString(String transferString, ConnectionHandler connectionHandler) {
                String[] values = transferString.split("/");
                List<Card> newCards = new ArrayList<>();
                String[] splittedCards = values[2].split("-");
                for (String splittedCard : splittedCards) {
                    newCards.add(new Card(splittedCard));
                }
                return new Player(
                        connectionHandler,
                        values[0],
                        Double.parseDouble(values[1]),
                        newCards,
                        Double.parseDouble(values[3])
                );
            }
        };
    }

    public double getBet() {
        return bet;
    }

    public double getBalance() {
        return balance;
    }

    public List<Card> getCards() {
        return cards;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public String getUsername() {
        return username;
    }

}
