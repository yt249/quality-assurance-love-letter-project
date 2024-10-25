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
        int ret = 0;
        for (Card c : this.cards) {
            ret = c.value();
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
}
