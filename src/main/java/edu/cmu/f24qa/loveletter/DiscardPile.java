package edu.cmu.f24qa.loveletter;

import java.util.ArrayList;

public class DiscardPile {
    private ArrayList<Card> cards;

    public DiscardPile() {
        this.cards = new ArrayList<>();
    }

    /**
     * Copy constructor
     */
    public DiscardPile(DiscardPile discardPile) {
        this.cards = new ArrayList<>(discardPile.cards);
    }

    public void add(Card c) {
        this.cards.add(c);
    }

    public int value() {
        int ret = 0;
        for (Card c : this.cards) {
            ret += c.getValue();
        }
        return ret;
    }

    public void clear() {
        this.cards.clear();
    }

    public void print() {
        for (Card c : this.cards) {
            System.out.println(c);
        }
    }

    public DiscardPile copy() {
        DiscardPile copy = new DiscardPile();
        copy.cards.addAll(this.cards); // Copy the cards to the new instance
        return copy;
    }

    /**
     * Getter for the cards in the discard pile.
     *
     * @return a copy of the cards in the discard pile.
     */
    public ArrayList<Card> getCards() {
        return new ArrayList<>(this.cards); // Return a copy to avoid exposing internal representation
    }

}
