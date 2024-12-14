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
    private PlayerList playerList;
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

        // Initialize deck
        deck = new Deck();
        deck.build16Cards();
        deck.shuffle();

        // Initialize players
        player = new Player("Player1", new Hand(), new DiscardPile(), false, 0);
        player.addCard(Card.PRINCE);

        opponent = new Player("Opponent", new Hand(), new DiscardPile(), false, 0);

        // Initialize PlayerList with both players
        playerList = new PlayerList();
        playerList.addPlayer(player);
        playerList.addPlayer(opponent);

        action = new PrinceAction();
    }

    /*
     * Initialize context with simulated input
     *
     */
    private void setupContext(String simulatedInput) {
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        context = new GameContext(playerList, deck, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * Tests the behavior of the Prince card when the target opponent holds the Princess card.
     * Verifies that the opponent is eliminated and their card is discarded.
     */
    @Test
    void testPrinceActionTargetPlayerEliminated() {
        // Simulated input for selecting an opponent
        String simulatedInput = "Opponent\n";
        setupContext(simulatedInput);

        // opponent holds princess
        opponent.addCard(Card.PRINCESS);
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
        // Simulated input for selecting an opponent
        String simulatedInput = "Opponent\n";
        setupContext(simulatedInput);
        // opponnet holds non-princess card 
        opponent.addCard(Card.GUARD);

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
     * Verifies that player should discard its hand and draw a card, and the opponent retains
     * their original card and remains in the game.
     */
    @Test
    public void testPrinceActionOpponentIsProtected() {
        // player is forced to select itself because all other players are protected 
        String simulatedInput = "Player1";
        setupContext(simulatedInput);
        // Enable protection for the opponent
        opponent.addCard(Card.PRINCESS);
        opponent.switchProtection();

        // Set the current user
        context.setCurrentUser(player);

        // Execute PrinceAction
        action.execute(context);

        // Assert that the opponent is protected
        assertTrue(opponent.getIsProtected(), "Opponent should remain protected.");

        // Assert that opponent does not discard hand or draw a new card
        assertEquals(Card.PRINCESS, opponent.getHand().peek(0), "Opponent should still have their original card.");
        assertFalse(opponent.isEliminated(), "Opponent should not be eliminated.");

        // assert that player discards its card and draws a new card
        assertEquals(Card.PRINCE, player.getDiscarded().getCards().get(0));
        assertEquals(1, player.getHand().getHand().size());
    }
}