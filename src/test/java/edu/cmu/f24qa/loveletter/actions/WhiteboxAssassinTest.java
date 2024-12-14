package edu.cmu.f24qa.loveletter.actions;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import edu.cmu.f24qa.loveletter.*;

public class WhiteboxAssassinTest {

    @Spy
    private GameContext context;

    private Player user;
    private Player opponent;
    private Deck deck;

    @BeforeEach
    void setUp() {
        // Initialize players
        user = new Player("user", new Hand(), new DiscardPile(), false, 0);
        opponent = spy(new Player("opponent", new Hand(), new DiscardPile(), false, 0));

        // Create a PlayerList and add players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(user);
        playerList.addPlayer(opponent);

        // Mock context and deck
        deck = mock(Deck.class);
        InputStream dummyInputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        context = spy(new GameContext(null, deck, new InputStreamReader(dummyInputStream)));
        doReturn(user).when(context).getCurrentUser();
        doReturn(deck).when(context).getDeck();
    }

    /**
     * Tests the handleAssassinGuessed method when the current user is null.
     * Verifies that no action is taken and the appropriate message is printed.
     */
    @Test
    public void testHandleAssassinGuessedWithNullPlayer() {
        // Setup the context without a current user
        doReturn(null).when(context).getCurrentUser();

        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        AssassinAction action = new AssassinAction();
        action.handleAssassinGuessed(context, null, opponent);

        // Verify that no deck interaction occurs
        verify(context, never()).getDeck();

        // Check that the message was not printed
        String output = outContent.toString();
        assertFalse(output.contains("user played the Assassin card. No action taken."));

        // Reset System.out
        System.setOut(System.out);
    }

    /**
     * Tests the handleAssassinGuessed method when the user is null.
     * Verifies that no action is taken and the appropriate message is printed.
     */
    @Test
    public void testHandleAssassinGuessedWithNullUser() {
        // Setup the context with a null user
        doReturn(null).when(context).getCurrentUser();

        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        AssassinAction action = new AssassinAction();
        action.handleAssassinGuessed(context, null, opponent);

        // Verify that no deck interaction occurs
        verify(context, never()).getDeck();

        // Check that the correct message was printed
        String output = outContent.toString();
        assertTrue(output.contains("No current user found"));

        // Reset System.out
        System.setOut(System.out);
    }

    /**
     * Tests the handleAssassinGuessed method with a valid player and a deck that has more cards.
     * Verifies that the user is eliminated and the opponent draws a new card.
     */
    @Test
    public void testHandleAssassinGuessedWithValidPlayerAndDeckHasMoreCards() {
        // Setup
        user.addCard(Card.GUARD);
        opponent.addCard(Card.ASSASSIN);
        when(deck.hasMoreCards()).thenReturn(true);
        when(context.drawCard()).thenReturn(Card.PRINCE);

        AssassinAction action = new AssassinAction();
        action.handleAssassinGuessed(context, user, opponent);

        // Verify
        assertTrue(user.isEliminated());
        assertEquals(Card.ASSASSIN, opponent.getDiscarded().getCards().get(0));
        assertEquals(Card.PRINCE, opponent.getHand().peek(0));
    }

    /**
     * Tests the handleAssassinGuessed method with a valid player and a deck that has no more cards.
     * Verifies that the user is eliminated and the opponent draws the removed top card.
     */
    @Test
    public void testHandleAssassinGuessedWithValidPlayerAndDeckHasNoMoreCards() {
        // Setup
        user.addCard(Card.GUARD);
        opponent.addCard(Card.ASSASSIN);
        when(deck.hasMoreCards()).thenReturn(false);
        when(context.getRemovedTopCard()).thenReturn(Card.PRINCE);

        AssassinAction action = new AssassinAction();
        action.handleAssassinGuessed(context, user, opponent);

        // Verify
        assertTrue(user.isEliminated());
        assertEquals(Card.ASSASSIN, opponent.getDiscarded().getCards().get(0));
        assertEquals(Card.PRINCE, opponent.getHand().peek(0));
    }

    /**
     * Tests the execute method to verify the correct message is printed when a user plays the Assassin card.
     */
    @Test
    public void testExecuteWithValidUser() {
        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        AssassinAction action = new AssassinAction();
        action.execute(context);

        // Check that the correct message was printed
        String output = outContent.toString();
        assertTrue(output.contains("played the Assassin card. No action taken."));

        // Reset System.out
        System.setOut(System.out);
    }

    /**
     * Tests the execute method when the current user is null.
     * Verifies that no action is taken and the appropriate message is printed.
     */
    @Test
    public void testExecuteWithNullUser() {
        // Setup the context with a null user
        doReturn(null).when(context).getCurrentUser();

        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        AssassinAction action = new AssassinAction();
        action.execute(context);

        // Check that the correct message was printed
        String output = outContent.toString();
        assertTrue(output.contains("No current user found"));

        // Reset System.out
        System.setOut(System.out);
    }
} 