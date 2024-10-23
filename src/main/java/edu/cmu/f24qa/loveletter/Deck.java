package edu.cmu.f24qa.loveletter;

import java.util.Collections;
import java.util.Stack;

public class Deck {
    private Stack<Card> deck;

    public Deck() {
        this.deck = new Stack<>();
    }

    public void build() {
        for (int i = 0; i < 5; i++) {
            deck.push(Card.GUARD);
        }

        for (int i = 0; i < 2; i++) {
            deck.push(Card.PRIEST);
            deck.push(Card.BARON);
            deck.push(Card.HANDMAIDEN);
            deck.push(Card.PRINCE);
        }

        deck.push(Card.KING);
        deck.push(Card.COUNTESS);
        deck.push(Card.PRINCESS);
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card draw() {
        return deck.pop();
    }

    public boolean hasMoreCards() {
        return deck.size() > 0;
    }
}
