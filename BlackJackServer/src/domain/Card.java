package domain;

import domain.enums.Faces;
import domain.enums.Suit;

public class Card {

    private final Faces faces;
    private final Suit suit;

    public Card(Faces faces, Suit suit) {
        this.faces = faces;
        this.suit = suit;
    }

    public Card(String string) {
        String[] splitted = string.split("\\*");
        this.faces = Faces.valueOf(splitted[0]);
        this.suit = Suit.valueOf(splitted[1]);
    }

    public Faces getFaces() {
        return faces;
    }

    public Suit getSuit() {
        return suit;
    }
}
