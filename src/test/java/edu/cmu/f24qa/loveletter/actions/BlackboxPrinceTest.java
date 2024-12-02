package edu.cmu.f24qa.loveletter.actions;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.*;

/**
 * Blackbox tests for the Prince card's behavior in the Love Letter game.
 * These tests validate the Prince card's ability to discard a player's card,
 * draw a new one, and handle elimination scenarios, without relying on internal
 * implementation details of the game components.
 */
public class BlackboxPrinceTest {

    private GameContext context;
    private Player player;
    private Player opponent;
    private Deck deck;
    private PrinceAction action;

    /**
     * Sets up the test environment, including initializing the deck, players,
     * player list, and game context with simulated input.
     */
    @BeforeEach
    void setup() {
        // Simulated input for selecting an opponent
        String simulatedInput = "Opponent\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));

        // Initialize deck
        deck = new Deck();
        deck.build();
        deck.shuffle();

        // Initialize players
        Hand playerHand = new Hand();
        playerHand.add(Card.PRINCE);
        player = new Player("Player1", playerHand, new DiscardPile(), false, 0);

        Hand opponentHand = new Hand();
        opponentHand.add(Card.PRINCESS);
        opponent = new Player("Opponent", opponentHand, new DiscardPile(), false, 0);

        // Initialize PlayerList with both players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(player);
        playerList.addPlayer(opponent);

        // Initialize context with simulated input
        context = new GameContext(playerList, deck, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        action = new PrinceAction();
    }

    /**
     * Tests the behavior of the Prince card when the target opponent holds the Princess card.
     * Verifies that the opponent is eliminated and their card is discarded.
     */
    @Test
    void testPrinceActionTargetPlayerEliminated() {
        // Set the current user
        context.setCurrentUser(player);

        // Execute PrinceAction
        action.execute(context);

        // Verify opponent is eliminated
        assertTrue(opponent.isEliminated());
    }

    /**
     * Tests the behavior of the Prince card when the target opponent discards a non-Princess card.
     * Verifies that the opponent discards their card and draws a new one from the deck.
     */
    @Test
    void testPrinceActionDiscardAndDraw() {
        // Modify the opponent's hand
        opponent.getHand().clear();
        opponent.getHand().add(Card.GUARD);

        // Set the current user
        context.setCurrentUser(player);

        // Execute PrinceAction
        action.execute(context);

        // Verify discard and draw behavior
        assertEquals(Card.GUARD, opponent.getDiscarded().getCards().get(0)); // Opponent discarded their card
        assertEquals(1, opponent.getHand().getHand().size()); // Opponent now has one card in hand
    }

    /**
     * Tests the behavior of the Prince card when the opponent is protected.
     * Verifies that no card is discarded or drawn, and the opponent retains
     * their original card and remains in the game.
     */
    @Test
    public void testPrinceActionOpponentIsProtected() {
        // Enable protection for the opponent
        opponent.switchProtection();

        // Set the current user
        context.setCurrentUser(player);

        // Execute PrinceAction
        action.execute(context);

        // Assert that the opponent is protected
        assertTrue(opponent.getIsProtected(), "Opponent should remain protected.");

        // Assert that no card is discarded or drawn
        assertEquals(Card.PRINCESS, opponent.getHand().peek(0), "Opponent should still have their original card.");
        assertFalse(opponent.isEliminated(), "Opponent should not be eliminated.");
    }
}