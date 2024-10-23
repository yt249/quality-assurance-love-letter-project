package edu.cmu.f24qa.loveletter;

import java.util.ArrayList;

public class DiscardPile {
    private ArrayList<Card> cards;

    public DiscardPile() {
        this.cards = new ArrayList<>();
    }

    public void add(Card c) {
        this.cards.add(c);
    }

    public int value() {
        int RET = 0;
        for (Card c : this.cards) {
            RET = c.value();
        }
        return RET;
    }

    public void clear() {
        this.cards.clear();
    }

    public void print() {
        for (Card c : this.cards) {
            System.out.println(c);
        }
    }
}
