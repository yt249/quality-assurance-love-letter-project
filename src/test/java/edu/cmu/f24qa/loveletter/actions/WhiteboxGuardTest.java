package edu.cmu.f24qa.loveletter.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.checkerframework.checker.nullness.qual.NonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import org.mockito.Mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class WhiteboxGuardTest {
    private @NonNull PlayerList players;
    private @NonNull Game game;
    private @NonNull Player player;
    private @NonNull Player opponent;
    private @Mock Deck deck;
    private @Mock ActionFactory mockActionFactory;
    private ByteArrayOutputStream outContent;

    /**
     * Initializes a new test instance with two players and necessary mocks.
     * Sets up the test environment with a player (Alice) and an opponent (Bob),
     * and captures System.out for verification.
     * Uses spy objects for players to enable method verification.
     */
    public WhiteboxGuardTest() {
        this.players = new PlayerList();
        this.player = spy(new Player("Alice", new Hand(), new DiscardPile(), false, 0));
        this.opponent = spy(new Player("Bob", new Hand(), new DiscardPile(), false, 0));
        this.players.addPlayer(this.player);
        this.players.addPlayer(this.opponent);
        this.deck = mock(Deck.class);
        this.mockActionFactory = mock(ActionFactory.class);

        // Capture System.out
        this.outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.outContent));
    }

    /**
     * Sets up simulated user input for testing.
     * Creates a new spy Game instance with the provided input string and updates the current player.
     * Used to simulate user input during card selection and target player selection.
     *
     * @param input The string to be used as simulated user input (e.g., "0\nPrince\nBob\n")
     */
    private void setSimulatedInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
            input.getBytes(StandardCharsets.UTF_8)
        );
        this.game = spy(new Game(players, deck, inputStream, mockActionFactory));
        this.player = players.getCurrentPlayer();
    }

    /**
     * Sets up a mock Guard action in the action factory.
     * Creates a spy of GuardAction and configures the mock factory to return it.
     * This allows for verification of Guard action behavior during tests.
     */
    private void setGuardAction() {
        GuardAction guardAction = spy(new GuardAction());
        when(this.mockActionFactory.getAction(Card.GUARD.getName())).thenReturn(guardAction);
    }

    /**
     * Tests the Guard card play when there is no current user in the game context.
     * This is a white-box test that uses reflection to modify the game context.
     * Expected behavior: The turn should be skipped with an appropriate message.
     * 
     * @throws NoSuchFieldException if the context field is not found
     * @throws IllegalAccessException if the context field cannot be accessed
     */
    @Test
    public void testPlayGuardNullPlayer() throws NoSuchFieldException, IllegalAccessException {
        setSimulatedInput("0\n");
        setGuardAction();

        GameContext mockContext = mock(GameContext.class);
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(this.game, mockContext);
        when(mockContext.getCurrentUser()).thenReturn(null);

        // Set up the player's hand with Guard
        this.player.addCard(Card.GUARD);

        // Execute the turn
        doReturn(0).when(this.game).getCardIdx(this.player);
        this.game.playTurnCard(this.player);

        // Verify that the player's turn was skipped
        assertTrue(this.outContent.toString().contains("No current user found"));
    }

    /**
     * Tests the Guard card play when the player correctly guesses the opponent's card.
     * Expected behavior: The opponent should be eliminated and a success message should be displayed.
     * Tests the core functionality of the Guard card when used successfully.
     */
    @Test
    public void testPlayGuardGuessCorrect() {
        // Set input
        setSimulatedInput("0\nPrince\nBob\n");
        setGuardAction();

        // Set up the player's hand with Guard
        this.player.addCard(Card.GUARD);

        // Set up the opponent's hand with a royalty card
        this.opponent.addCard(Card.PRINCE);

        // Execute the turn
        this.game.playTurnCard(this.player);

        // Verify that the opponent was eliminated
        assertTrue(this.opponent.isEliminated());
        assertTrue(this.outContent.toString().contains("You have guessed correctly!"));
    }

    /**
     * Tests the Guard card play when the player incorrectly guesses the opponent's card.
     * Expected behavior: The opponent should not be eliminated and a failure message should be displayed.
     * Tests the core functionality of the Guard card when the guess is wrong.
     */
    @Test
    public void testPlayGuardGuessIncorrect() {
        // Set input
        setSimulatedInput("0\nBaron\nBob\n");
        setGuardAction();

        // Set up the player's hand with Guard
        this.player.addCard(Card.GUARD);

        // Set up the opponent's hand with a royalty card
        this.opponent.addCard(Card.PRINCE);

        // Execute the turn
        this.game.playTurnCard(this.player);

        // Verify that the opponent was not eliminated
        assertFalse(this.opponent.isEliminated());
        assertTrue(this.outContent.toString().contains("You have guessed incorrectly."));
    }
}
