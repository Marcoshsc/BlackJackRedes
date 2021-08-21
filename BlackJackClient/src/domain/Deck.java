package domain;

import domain.enums.Faces;
import domain.enums.Suit;

import java.util.*;

public class Deck {

    private final List<Card> cardList = Arrays.asList(
            new Card(Faces.ACE, Suit.CLUBS),
            new Card(Faces.TWO, Suit.CLUBS),
            new Card(Faces.THREE, Suit.CLUBS),
            new Card(Faces.FOUR, Suit.CLUBS),
            new Card(Faces.FIVE, Suit.CLUBS),
            new Card(Faces.SIX, Suit.CLUBS),
            new Card(Faces.SEVEN, Suit.CLUBS),
            new Card(Faces.EIGHT, Suit.CLUBS),
            new Card(Faces.NINE, Suit.CLUBS),
            new Card(Faces.TEN, Suit.CLUBS),
            new Card(Faces.JACK, Suit.CLUBS),
            new Card(Faces.QUEEN, Suit.CLUBS),
            new Card(Faces.KING, Suit.CLUBS),

            new Card(Faces.ACE, Suit.HEARTS),
            new Card(Faces.TWO, Suit.HEARTS),
            new Card(Faces.THREE, Suit.HEARTS),
            new Card(Faces.FOUR, Suit.HEARTS),
            new Card(Faces.FIVE, Suit.HEARTS),
            new Card(Faces.SIX, Suit.HEARTS),
            new Card(Faces.SEVEN, Suit.HEARTS),
            new Card(Faces.EIGHT, Suit.HEARTS),
            new Card(Faces.NINE, Suit.HEARTS),
            new Card(Faces.TEN, Suit.HEARTS),
            new Card(Faces.JACK, Suit.HEARTS),
            new Card(Faces.QUEEN, Suit.HEARTS),
            new Card(Faces.KING, Suit.HEARTS),

            new Card(Faces.ACE, Suit.SPADES),
            new Card(Faces.TWO, Suit.SPADES),
            new Card(Faces.THREE, Suit.SPADES),
            new Card(Faces.FOUR, Suit.SPADES),
            new Card(Faces.FIVE, Suit.SPADES),
            new Card(Faces.SIX, Suit.SPADES),
            new Card(Faces.SEVEN, Suit.SPADES),
            new Card(Faces.EIGHT, Suit.SPADES),
            new Card(Faces.NINE, Suit.SPADES),
            new Card(Faces.TEN, Suit.SPADES),
            new Card(Faces.JACK, Suit.SPADES),
            new Card(Faces.QUEEN, Suit.SPADES),
            new Card(Faces.KING, Suit.SPADES),

            new Card(Faces.ACE, Suit.DIAMONDS),
            new Card(Faces.TWO, Suit.DIAMONDS),
            new Card(Faces.THREE, Suit.DIAMONDS),
            new Card(Faces.FOUR, Suit.DIAMONDS),
            new Card(Faces.FIVE, Suit.DIAMONDS),
            new Card(Faces.SIX, Suit.DIAMONDS),
            new Card(Faces.SEVEN, Suit.DIAMONDS),
            new Card(Faces.EIGHT, Suit.DIAMONDS),
            new Card(Faces.NINE, Suit.DIAMONDS),
            new Card(Faces.TEN, Suit.DIAMONDS),
            new Card(Faces.JACK, Suit.DIAMONDS),
            new Card(Faces.QUEEN, Suit.DIAMONDS),
            new Card(Faces.KING, Suit.DIAMONDS)
    );

    private final Stack<Card> cards = new Stack<>();
    private final int numberCards = 52;

    public Deck() {
        Set<Integer> alreadyPicked = new HashSet<>();
        Random random = new Random();
        while(alreadyPicked.size() < numberCards) {
            int index = random.nextInt(numberCards);
            if(alreadyPicked.contains(index))
                continue;
            Card card = cardList.get(index);
            cards.push(card);
            alreadyPicked.add(index);
        }
    }

    public Card getCard() {
        return cards.pop();
    }

}
