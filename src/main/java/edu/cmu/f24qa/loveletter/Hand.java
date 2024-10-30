package edu.cmu.f24qa.loveletter;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand;

    public Hand() {
        this.hand  = new ArrayList<>();
    }

    /**
     * Peeks the card held by the player.
     *
     * @param idx
     *          the index of the Card to peek
     *
     * @return the card held by the player
     */
    public Card peek(int idx) {
        return this.hand.get(idx);
    }

    public void add(Card card) {
        this.hand.add(card);
    }

    public Hand copy() {
        Hand copy = new Hand();
        copy.hand.addAll(this.hand);  // Copy all cards to the new Hand instance
        return copy;
    }

    /**
     * Removes the card at the given index from the hand.
     *
     * @param idx
     *          the index of the card
     *
     * @return the card at the given index
     */
    public Card remove(int idx) {
        return this.hand.remove(idx);
    }

    /**
     * Finds the position of a royal card in the hand.
     *
     * @return the position of a royal card, -1 if no royal card is in hand
     */
    public int royaltyPos() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).value() == 5 || hand.get(i).value() == 6) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasCards() {
        return !this.hand.isEmpty();
    }

    public void clear() {
        this.hand.clear();
    }

    public void print() {
        for (Card c : this.hand) {
            System.out.println(c);
        }
    }
}
