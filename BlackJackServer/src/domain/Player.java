package domain;

import domain.enums.Faces;
import domain.enums.PlayerStatus;
import domain.enums.Suit;
import server.ConnectionHandler;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final ConnectionHandler connectionHandler;
    private final String username;
    private double balance = 1000d;
    private double bet = 0d;
    private boolean betted;
    private PlayerStatus status = PlayerStatus.PLAYING;
    private String lastDecision;
    private List<Card> cards = new ArrayList<>();

    public Player(ConnectionHandler connectionHandler, String username) {
        this.connectionHandler = connectionHandler;
        this.username = username;
    }

    public Player(ConnectionHandler connectionHandler, String username, double balance, List<Card> cards, double bet,
                  PlayerStatus status, String lastDecision, boolean betted) {
        this.connectionHandler = connectionHandler;
        this.username = username;
        this.balance = balance;
        this.cards = cards;
        this.bet = bet;
        this.status = status;
        this.lastDecision = lastDecision;
        this.betted = betted;
    }

    public void raiseBet(double value) {
        if(bet + value > balance) {
            throw new IllegalArgumentException("Invalid bet raise.");
        }
        bet += value;
        balance -= value;
    }

    public String getLastDecision() {
        return lastDecision;
    }

    public void setLastDecision(String lastDecision) {
        this.lastDecision = lastDecision;
    }

    public boolean blackJack() {
        return cards.size() == 2 && (
                (cards.get(0).getFaces() == Faces.ACE && cards.get(0).getSuit() == Suit.SPADES && cards.get(1).getFaces() == Faces.JACK && cards.get(1).getSuit() == Suit.SPADES) ||
                (cards.get(1).getFaces() == Faces.ACE && cards.get(1).getSuit() == Suit.SPADES && cards.get(0).getFaces() == Faces.JACK && cards.get(0).getSuit() == Suit.SPADES));
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void giveUp() {
        status = PlayerStatus.GIVEUP;
    }

    public void increaseBalance(double value) {
        balance += value;
    }

    public void checkBlown() {
        int value = getValue();
        if(value > 21) {
            status = PlayerStatus.BLOWN;
        }
    }

    public void reset() {
        lastDecision = null;
        betted = false;
        bet = 0;
        status = PlayerStatus.PLAYING;
    }

    public void blown() {
        status = PlayerStatus.BLOWN;
    }

    public int getValue() {
        int value1 = 0;
        int value2 = 0;
        for (Card card: cards) {
            value1 += card.getFaces().getValue1();
            value2 += card.getFaces().getValue2();
        }
        if(value1 > 21 || value2 > 21) {
            return Math.min(value2, value1);
        }
        return Math.max(value1, value2);
    }

    public boolean isBetted() {
        return betted;
    }

    public void setBetted(boolean betted) {
        this.betted = betted;
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
                return String.format("%s/%f/%s/%f/%s/%s/%s", value.username, value.balance, stringBuilder.toString(), value.bet,
                        value.status, value.lastDecision, value.betted);
            }

            @Override
            public String toTransferString(Player value, String context) {
                StringBuilder stringBuilder = new StringBuilder();
                int sizeToUse = !context.equals(value.getUsername()) ? 1 : value.cards.size();
                for (int i = 0; i < sizeToUse; i++) {
                    stringBuilder.append(String.format("%s*%s", value.cards.get(i).getFaces(), value.cards.get(i).getSuit()));
                    if(i != sizeToUse - 1) {
                        stringBuilder.append("-");
                    }
                }
                return String.format("%s/%f/%s/%f/%s/%s/%s", value.username, value.balance, stringBuilder.toString(), value.bet,
                        value.status, value.lastDecision, value.betted);
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
                        Double.parseDouble(values[3]),
                        PlayerStatus.valueOf(values[4]),
                        values[5],
                        Boolean.parseBoolean(values[6])
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

    public PlayerStatus getStatus() {
        return status;
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
