package edu.cmu.f24qa.loveletter;

import java.util.Collections;
import java.util.Stack;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Deck {
    private Stack<Card> deck;
    private Card removedTopCard;

    public Deck() {
        this.deck = new Stack<>();
        this.removedTopCard = null;
    }

    /*
     * Copy Constructor
     */
    public Deck(Deck deck) {
        this.deck = new Stack<>();
        if (deck != null && deck.deck != null) {
            this.deck.addAll(deck.deck);
        }
    }

    /*
     * Builds a deck of 16 cards for 2-4 players
     */
    public void build16Cards() {
        // 5 guards
        for (int i = 0; i < 5; i++) {
            deck.push(Card.GUARD);
        }

        // 2 each of Priest, Baron, Handmaiden, and Prince
        for (int i = 0; i < 2; i++) {
            deck.push(Card.PRIEST);
            deck.push(Card.BARON);
            deck.push(Card.HANDMAIDEN);
            deck.push(Card.PRINCE);
        }

        // 1 King, Countess, and Princess
        deck.push(Card.KING);
        deck.push(Card.COUNTESS);
        deck.push(Card.PRINCESS);
    }

    /*
     * Builds a deck of 32 cards for 5-8 players
     */
    public void build32Cards() {
        build16Cards();
        
        // Add 3 guards
        for (int i = 0; i < 3; i++) {
            deck.push(Card.GUARD);
        }

        // Add 2 cardinals, 2 baronesses, 2 sycophants, and 2 counts
        for (int i = 0; i < 2; i++) {
            deck.push(Card.CARDINAL);
            deck.push(Card.BARONESS);
            deck.push(Card.SYCOPHANT);
            deck.push(Card.COUNT);
        }

        // Add 1 jester, 1 assassin, 1 queen, and 1 bishop
        deck.push(Card.JESTER);
        deck.push(Card.ASSASSIN);
        deck.push(Card.CONSTABLE);
        deck.push(Card.QUEEN);
        deck.push(Card.BISHOP);
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

    public void setDeck(Stack<Card> newDeck) {
        this.deck = new Stack<>();
        if (newDeck != null) {
            this.deck.addAll(newDeck);
        }
    }

    /**
     * Returns the card that was removed from the top of the deck.
     *
     * @return The removed top card.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public Card getRemovedTopCard() {
        return removedTopCard;
    }

    /**
     * Removes the top card from the deck and sets it aside.
     * This card can be retrieved later using getRemovedTopCard().
     */
    public void removeCardFromDeck() {
        removedTopCard = draw();
        System.out.println("One card has been removed from the deck and set aside.");
    }

    public void clearRemovedTopCard() {
        removedTopCard = null;
    }
}
