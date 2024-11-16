package edu.cmu.f24qa.loveletter.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.checkerframework.checker.nullness.qual.NonNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.mock;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class BlackboxGuardTest {
    private @NonNull PlayerList players;
    private @NonNull Game game;
    private @NonNull Player player;
    private @NonNull Player opponent;
    private @Mock Deck deck;
    private ByteArrayOutputStream outContent;

    /**
     * Initializes a new test instance with two players and necessary mocks.
     * Sets up the test environment with a player (Alice) and an opponent (Bob),
     * and captures System.out for verification.
     * Uses spy objects for players to enable method verification.
     */
    public BlackboxGuardTest() {
        this.players = new PlayerList();
        this.player = spy(new Player("Alice", new Hand(), new DiscardPile(), false, 0));
        this.opponent = spy(new Player("Bob", new Hand(), new DiscardPile(), false, 0));
        this.players.addPlayer(this.player);
        this.players.addPlayer(this.opponent);
        this.deck = mock(Deck.class);

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
    private void setUpGameWithSimulatedInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
            input.getBytes(StandardCharsets.UTF_8)
        );
        this.game = spy(new Game(players, deck, inputStream));
        this.player = players.getCurrentPlayer();
    }

    /**
     * Tests the Guard card play when the player correctly guesses the opponent's card.
     * Tests from an external perspective without knowledge of internal implementation.
     * Expected behavior: 
     * - The opponent should be eliminated
     * - The opponent should not be protected
     * - A success message should be displayed
     */
    @Test
    public void testPlayGuardGuessCorrect() {
        // Set input
        setUpGameWithSimulatedInput("0\nPrince\nBob\n");

        // Set up the player's hand with Guard
        this.player.addCard(Card.GUARD);

        // Set up the opponent's hand with a royalty card
        this.opponent.addCard(Card.PRINCE);

        // Execute the turn
        this.game.playTurnCard(this.player);

        // Verify that the opponent was eliminated
        assertFalse(this.opponent.getIsProtected());
        assertTrue(this.opponent.isEliminated());
        assertTrue(this.outContent.toString().contains("You have guessed correctly!"));
    }

    /**
     * Tests the Guard card play when the player incorrectly guesses the opponent's card.
     * Tests from an external perspective without knowledge of internal implementation.
     * Expected behavior: 
     * - The opponent should not be eliminated
     * - The opponent should not be protected
     * - A failure message should be displayed
     */
    @Test
    public void testPlayGuardGuessIncorrect() {
        // Set input
        setUpGameWithSimulatedInput("0\nBaron\nBob\n");

        // Set up the player's hand with Guard
        this.player.addCard(Card.GUARD);

        // Set up the opponent's hand with a royalty card
        this.opponent.addCard(Card.PRINCE);

        // Execute the turn
        this.game.playTurnCard(this.player);

        // Verify that the opponent was not eliminated
        assertFalse(this.opponent.getIsProtected());
        assertFalse(this.opponent.isEliminated());
        assertTrue(this.outContent.toString().contains("You have guessed incorrectly."));
    }

    /**
     * Tests the Guard card play when targeting a protected opponent.
     * Tests from an external perspective without knowledge of internal implementation.
     * Expected behavior: 
     * - The opponent should remain protected
     * - The opponent should not be eliminated
     * - No guess result messages should be displayed
     * Note: This test is currently disabled.
     */
    @Test
    public void testPlayGuardOpponentIsProtected() {
        // Set input
        setUpGameWithSimulatedInput("0\nBaron\nBob\n");

        // Set up the player's hand with Guard
        this.player.addCard(Card.GUARD);

        // Set up the opponent's hand with a protected card
        this.opponent.addCard(Card.PRINCESS);
        this.opponent.switchProtection();

        // Execute the turn
        this.game.playTurnCard(this.player);

        // Verify that the opponent was not eliminated
        assertTrue(this.opponent.getIsProtected());
        assertFalse(this.opponent.isEliminated());
        assertFalse(this.outContent.toString().contains("You have guessed correctly."));
        assertFalse(this.outContent.toString().contains("You have guessed incorrectly."));
    }
}