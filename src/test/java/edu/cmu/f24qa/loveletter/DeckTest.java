package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Stack;

import org.junit.jupiter.api.Test;

public class DeckTest {
    
    /*
     * Verify that hasMoreCards() returns true when the deck contains cards.
     */
    @Test
    void testHasMoreCardsTrue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Stack<Card> testDeck = new Stack<>();
        testDeck.push(Card.GUARD); // Add a single card
        Deck deck = new Deck();
        Field deckField = Deck.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(deck, testDeck); // Replace the private deck field with the predefined stack

        boolean hasMoreCards = deck.hasMoreCards();

        assertTrue(hasMoreCards, "hasMoreCards() should return true when the deck has cards.");
    }

    /*
     * Verify that hasMoreCards() returns false when the deck is empty.
     */
    @Test
    void testHasMoreCardsFalse() {
        Deck deck = new Deck();

        boolean hasMoreCards = deck.hasMoreCards();

        assertFalse(hasMoreCards, "hasMoreCards() should return false when the deck is empty.");
    }
}
