package domain.enums;

public enum Suit {

    SPADES ("♠"),
    HEARTS ("♥"),
    DIAMONDS ("♦"),
    CLUBS ("♣");

    private final String suit;

    Suit(String s) {
        this.suit = s;
    }

    @Override
    public String toString() {
        return this.suit;
    }

}
