package domain;

import domain.enums.Faces;
import domain.enums.PlayerStatus;
import domain.enums.Shape;
import domain.enums.Suit;
import main.ConnectionHandler;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String username;
    private double balance = 1000d;
    private double bet = 0d;
    private PlayerStatus status = PlayerStatus.PLAYING;
    private List<Card> cards = new ArrayList<>();

    public Player(String username) {
        this.username = username;
    }

    public Player(String username, double balance, List<Card> cards, double bet,
                  PlayerStatus status) {
        this.username = username;
        this.balance = balance;
        this.cards = cards;
        this.bet = bet;
        this.status = status;
    }

    public void raiseBet(double value) {
        if(bet + value > balance) {
            throw new IllegalArgumentException("Invalid bet raise.");
        }
        bet += value;
        balance -= value;
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

    public void reset() {
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
                return String.format("%s/%f/%s/%f/%s", value.username, value.balance, stringBuilder.toString(), value.bet, value.status);
            }

            @Override
            public Player fromTransferString(String transferString) {
                String[] values = transferString.split("/");
                List<Card> newCards = new ArrayList<>();
                System.out.println("Values 2: " + values[2]);
                if(!values[2].equals("")) {
                    String[] splittedCards = values[2].split("-");
                    for (String splittedCard : splittedCards) {
                        newCards.add(new Card(splittedCard));
                    }
                }
                return new Player(
                        values[0],
                        Double.parseDouble(values[1]),
                        newCards,
                        Double.parseDouble(values[3]),
                        PlayerStatus.valueOf(values[4])
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

    public String getUsername() {
        return username;
    }
}
