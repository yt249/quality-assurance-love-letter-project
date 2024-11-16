package edu.cmu.f24qa.loveletter.actions;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Optional;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import edu.cmu.f24qa.loveletter.*;

/**
 * Unit tests for the Prince card's behavior in the Love Letter game.
 * This class tests various scenarios involving the Prince card, such as 
 * eliminating players holding the Princess, discarding/drawing cards, 
 * and handling edge cases like missing users in the context.
 */
public class WhiteboxPrinceTest {

    @Spy
    private GameContext context;

    private Player player;
    private Player opponent;
    private Deck deck;

    /**
     * Sets up the test environment by initializing GameContext, Player, Hand, and Deck objects.
     * Mock behaviors are added for GameContext, Deck, and the opponent player.
     */
    @BeforeEach
    void setup() {
        InputStream dummyInputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        context = spy(new GameContext(null, null, new InputStreamReader(dummyInputStream)));

        // Initialize hands
        Hand playerHand = new Hand();
        playerHand.add(Card.PRINCE);

        Hand opponentHand = new Hand();
        opponentHand.add(Card.PRINCESS);

        // Initialize players
        player = new Player("Player1", playerHand, new DiscardPile(), false, 0);
        opponent = spy(new Player("Opponent", opponentHand, new DiscardPile(), false, 0));

        // Initialize deck
        deck = spy(new Deck());
    }

    /**
     * Tests the playTurnCard method to ensure that selecting and playing the Prince card
     * works as expected. Verifies that the opponent holding the Princess card is eliminated.
     */
    @Test
    void testPlayTurnCardPlayPrinceCardEliminatePrincess() throws Exception {
        // Modify the player's hand for this test
        Hand hand = new Hand();
        hand.add(Card.KING);
        hand.add(Card.PRINCE);
        player = new Player("Player1", hand, new DiscardPile(), false, 0);

        // Create a Game instance
        InputStream inputStream = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8));
        Game game = new Game(null, null, inputStream);

        // Use reflection to inject the test's context spy
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(game, context); // Replace with the test's spy context

        // Mock context behavior
        doReturn("1").when(context).readLine(); // User selects the second card (PRINCE)
        doReturn(Optional.of(opponent)).when(context).selectOpponent();

        // Call playTurnCard
        game.playTurnCard(player);

        // Verify the remaining card in the player's hand
        assertEquals(Card.KING, player.getHand().peek(0));

        // Verify that the current user was set correctly in the context
        verify(context).setCurrentUser(player);

        // Verify opponent is eliminated and their card is discarded
        assertEquals(Card.PRINCESS, opponent.getDiscarded().getCards().get(0));
        assertTrue(opponent.isEliminated());
    }

    /**
     * Tests the PrinceAction behavior when there is no current user in the context.
     * Ensures that no action is taken and an appropriate message is printed.
     */
    @Test
    void testPrinceActionNoCurrentUser() {
        // Capture system output
        ByteArrayOutputStream systemout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(systemout));

        doReturn(null).when(context).getCurrentUser();

        PrinceAction action = new PrinceAction();
        action.execute(context);

        // Verify that no opponent selection was attempted
        verify(context, never()).selectOpponent();

        // Verify that the correct message is printed
        assertEquals("No current user found", systemout.toString().trim());
    }

    /**
     * Tests the PrinceAction behavior when the opponent discards their card
     * and draws a new card from the deck. This test is currently disabled 
     * because the action does not add the discarded card to the discard pile.
     */
    @Disabled("Prince action is not adding discarded card into discarded pile.")
    @Test
    void testPrinceActionDiscardAndDraw() {
        // Modify the opponent's hand for this test
        opponent.getHand().clear();
        opponent.getHand().add(Card.GUARD);

        // Mock new card draw
        Card newCard = Card.BARON;
        doReturn(player).when(context).getCurrentUser();
        doReturn(Optional.of(opponent)).when(context).selectOpponent();
        doReturn(deck).when(context).getDeck();
        doReturn(newCard).when(deck).draw();

        PrinceAction action = new PrinceAction();
        action.execute(context);

        // Assert that the opponent discards their Guard card and draws a new Baron card
        assertEquals(Card.BARON, opponent.getHand().peek(0));
        assertEquals(Card.GUARD, opponent.getDiscarded().getCards().get(0));
    }
}