package edu.cmu.f24qa.loveletter;

import java.util.Collections;
import java.util.Stack;

public class Deck {
    private Stack<Card> deck;

    public Deck() {
        this.deck = new Stack<>();
    }

    /*
     * Copy Constructor
     */
    public Deck(Deck deck) {
        this.deck = new Stack<>();
        this.deck.addAll(deck.deck);
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

    /**
     * Getter for the deck of cards.
     *
     * @return the deck as a Stack of Card objects.
     */
    public Stack<Card> getDeck() {
        Stack<Card> deckCopy = new Stack<>();
        deckCopy.addAll(this.deck); // Create a copy of the stack to avoid exposing internal representation
        return deckCopy;
    }
}
