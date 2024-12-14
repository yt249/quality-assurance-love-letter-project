package edu.cmu.f24qa.loveletter.actions;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import edu.cmu.f24qa.loveletter.*;

/**
 * Unit tests for the King card's behavior in the Love Letter game.
 * This class tests various scenarios involving the King card, such as 
 * hand-swapping between players, handling edge cases, and validating 
 * the behavior when no current user or opponent is selected.
 */
public class WhiteboxKingTest {

    @Spy
    private GameContext context;

    private Player player;
    private Player opponent;

    /**
     * Sets up the test environment by initializing GameContext, Player, and Hand objects.
     * Mock behaviors are added for GameContext and the opponent player.
     */
    @BeforeEach
    void setup() {
        InputStream dummyInputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        context = spy(new GameContext(null, null, new InputStreamReader(dummyInputStream)));

        // Initialize hands
        Hand playerHand = new Hand();
        playerHand.add(Card.KING); // Add King card to player's hand

        Hand opponentHand = new Hand();
        opponentHand.add(Card.GUARD); // Add Guard card to opponent's hand

        // Initialize players
        player = new Player("Player1", playerHand, new DiscardPile(), false, 0);
        opponent = spy(new Player("Opponent", opponentHand, new DiscardPile(), false, 0));
    }

    /**
     * Tests the playTurnCard method to ensure that selecting and playing the King card
     * swaps hands with the selected opponent.
     */
    @Test
    void testPlayTurnCardPlayKingCard() throws Exception {
        // Modify the player's hand for this test
        Hand hand = new Hand();
        hand.add(Card.PRINCE);
        hand.add(Card.KING);
        player = new Player("Player1", hand, new DiscardPile(), false, 0);

        // Create a Game instance
        InputStream inputStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        Game game = new Game(null, null, inputStream);

        // Use reflection to inject the test's context spy
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(game, context); // Replace with the test's spy context

        // Mock context behavior
        doReturn("1").when(context).readLine(); // User selects the King card (index 1)
        doReturn(List.of(opponent)).when(context).selectOpponents(1, 1, false);

        // Call playTurnCard
        game.playTurnCard(player);

        // Verify that the current user was set correctly in the context
        verify(context).setCurrentUser(player);

        // Verify hand-swapping behavior
        assertEquals(Card.GUARD, player.getHand().peek(0)); // Player now has the Guard
        assertEquals(Card.PRINCE, opponent.getHand().peek(0)); // Opponent now has the King
    }

    /**
     * Tests the KingAction behavior when there is no current user in the context.
     * Ensures that no action is taken and an appropriate error message is printed.
     */
    @Test
    void testKingActionNoCurrentUser() {
        // Capture system output
        ByteArrayOutputStream systemout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemout));

        doReturn(null).when(context).getCurrentUser();

        KingAction action = new KingAction();
        action.execute(context);

        // Verify no opponent selection was attempted
        verify(context, never()).selectOpponents(1, 1, false);

        // Verify error message output
        assertEquals("No current user found", systemout.toString().trim());
    }

    /**
     * Tests the KingAction behavior when no opponent is selected.
     * Ensures that no hand-swapping occurs and the current player's hand remains unchanged.
     */
    @Test
    void testKingActionNoOpponentSelected() {
        // Mock context behavior
        doReturn(player).when(context).getCurrentUser();
        doReturn(new ArrayList<>()).when(context).selectOpponents(1, 1, false); // No opponent selected

        KingAction action = new KingAction();
        action.execute(context);

        // Ensure no action is taken and the player's hand remains unchanged
        assertEquals(Card.KING, player.getHand().peek(0)); // Player still has the King
    }
}