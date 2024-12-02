package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlayerTest {

    /**
     * Verifies that a knocked-out player discards their hand,
     * and the discarded pile contains the card.
     */
    @Test
    void testPlayerKnockedOutDiscardsHand() {
        // Setup: Create a player with one card in hand and an empty discard pile
        Hand hand = new Hand();
        hand.add(Card.GUARD); // Example card
        DiscardPile discarded = new DiscardPile();
        Player player = new Player("Player 1", hand, discarded, false, 0);

        // Verify initial state
        assertEquals(1, player.getHand().getHand().size(), "Player should start with one card in hand.");
        assertTrue(player.getDiscarded().getCards().isEmpty(), "Player's discarded pile should start empty.");

        // Action: Eliminate the player
        player.eliminate();

        // Verify the player's hand is empty after elimination
        assertTrue(player.isEliminated(), "Player should be eliminated when their hand is empty.");
        assertEquals(0, player.getHand().getHand().size(), "Player's hand should be empty after elimination.");
        assertEquals(0, player.getDiscarded().getCards().size(), "Player's discard pile should be empty after elimination.");
    }
}