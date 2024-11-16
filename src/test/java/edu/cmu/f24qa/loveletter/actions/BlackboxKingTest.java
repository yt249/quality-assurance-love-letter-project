package edu.cmu.f24qa.loveletter.actions;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.*;

/**
 * Blackbox tests for the King card's behavior in the Love Letter game.
 * These tests verify the King card's hand-swapping functionality without
 * relying on the internal implementation details of the game components.
 */
public class BlackboxKingTest {

    private GameContext context;
    private Player player;
    private Player opponent;
    private KingAction action;

    /**
     * Sets up the test environment, including initializing players, 
     * player list, and game context with simulated input.
     */
    @BeforeEach
    void setup() {
        // Simulated input for selecting an opponent
        String simulatedInput = "Opponent\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));

        // Initialize players
        Hand playerHand = new Hand();
        playerHand.add(Card.KING);
        player = new Player("Player1", playerHand, new DiscardPile(), false, 0);

        Hand opponentHand = new Hand();
        opponentHand.add(Card.GUARD);
        opponent = new Player("Opponent", opponentHand, new DiscardPile(), false, 0);

        // Initialize PlayerList with both players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(player);
        playerList.addPlayer(opponent);

        // Initialize game context
        context = new GameContext(playerList, new Deck(), new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        action = new KingAction();
    }

    /**
     * Tests the KingAction to ensure that hands are swapped between the current player
     * and the selected opponent. This test is currently disabled because the getHand 
     * method returns a copy of the hand, making it difficult to validate the swap.
     */
    @Disabled("getHand method is returning copy")
    @Test
    void testKingActionSwapHands() {
        // Set the current user
        context.setCurrentUser(player);

        // Execute KingAction
        action.execute(context);

        // Verify that hands are swapped
        assertEquals(Card.GUARD, player.getHand().peek(0)); // Player now has the Guard
        assertEquals(Card.KING, opponent.getHand().peek(0)); // Opponent now has the King
    }

    /**
     * Tests the KingAction to ensure that the game re-prompts for a valid opponent
     * if an invalid opponent name is provided. This test is currently disabled because 
     * the getHand method returns a copy of the hand, making it difficult to validate the swap.
     */
    @Disabled("getHand method is returning copy")
    @Test
    void testKingActionRePromptsForValidOpponent() {
        // Simulated input: invalid first input, valid second input
        String simulatedInput = "NonExistent\nOpponent\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));

        // Reinitialize the context with the new input
        context = new GameContext(context.getPlayers(), new Deck(), new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        context.setCurrentUser(player);

        // Execute KingAction
        action.execute(context);

        // Verify that hands are swapped after valid input
        assertEquals(Card.GUARD, player.getHand().peek(0)); // Player now has the Guard
        assertEquals(Card.KING, opponent.getHand().peek(0)); // Opponent now has the King
    }

    /**
     * Tests the behavior of the King card when the opponent is protected.
     * Verifies that no card-swapping occurs, and both the player and the opponent
     * retain their original cards.
     */
    @Disabled
    @Test
    public void testKingActionOpponentIsProtected() {
        // Enable protection for the opponent
        opponent.switchProtection();

        // Set the current user
        context.setCurrentUser(player);

        // Execute KingAction
        action.execute(context);

        // Assert that the opponent is protected
        assertTrue(opponent.getIsProtected(), "Opponent should remain protected.");

        // Assert that the cards are not swapped
        assertEquals(Card.KING, player.getHand().peek(0), "Player should still have the King card.");
        assertEquals(Card.GUARD, opponent.getHand().peek(0), "Opponent should still have their original card.");
    }
}