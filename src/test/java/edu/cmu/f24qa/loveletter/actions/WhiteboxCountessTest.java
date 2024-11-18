package edu.cmu.f24qa.loveletter.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

public class WhiteboxCountessTest {
    private @NonNull PlayerList players;
    private @NonNull Game game;
    private @NonNull Player player;
    private @NonNull Player opponent;
    private @Mock Deck deck;
    private @Mock ActionFactory mockActionFactory;
    private ActionFactory actionFactory;
    private ByteArrayOutputStream outContent;

    /**
     * Initializes a new test instance with two players and necessary mocks.
     * Sets up the test environment with a player (Alice) and an opponent (Bob),
     * and captures System.out for verification.
     */
    public WhiteboxCountessTest() {
        this.players = new PlayerList();
        this.player = spy(new Player("Alice", new Hand(), new DiscardPile(), false, 0));
        this.opponent = spy(new Player("Bob", new Hand(), new DiscardPile(), false, 0));
        this.players.addPlayer(this.player);
        this.players.addPlayer(this.opponent);
        this.deck = mock(Deck.class);
        this.mockActionFactory = mock(ActionFactory.class);
        this.actionFactory = new ActionFactory();

        // Capture System.out
        this.outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.outContent));
    }

    /**
     * Sets up simulated user input for testing.
     * Creates a ByteArrayInputStream with the provided input string encoded in UTF-8.
     * Used to simulate user input during card selection and target player selection.
     *
     * @param input The string to be used as simulated user input (e.g., "0\n" for first option)
     * @return ByteArrayInputStream containing the encoded input string
     */
    private ByteArrayInputStream setSimulatedInput(String input) {
        return new ByteArrayInputStream(
            input.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Sets up a mock action for a specific card in the action factory.
     * Creates a mock of the specified action and configures the factory to return it.
     *
     * @param card The card to create the mock action for
     * @param action The action instance to be mocked
     */
    private void setMockAction(Card card, CardAction action) {
        CardAction mockAction = mock(CardAction.class);
        when(this.mockActionFactory.getAction(card.getName())).thenReturn(mockAction);
    }

    /**
     * Sets up a game instance for testing with specified input stream and action factory.
     * Creates a spy object of Game class to enable method verification.
     * If no input stream is provided, uses System.in as default.
     * Updates the current player reference after game creation.
     *
     * @param inputStream The input stream to be used for game input, can be null
     * @param actionFactory The action factory to be used for creating card actions
     */
    private void setUpGame(@Nullable ByteArrayInputStream inputStream, ActionFactory actionFactory) {
        if (inputStream == null) {
            this.game = spy(new Game(players, deck, System.in, actionFactory));
        } else {
            this.game = spy(new Game(players, deck, inputStream, actionFactory));
        }
        this.player = players.getCurrentPlayer();
    }

    /**
     * Tests Countess card play when there is no current user in the game context.
     * This is a white-box test that uses reflection to modify the game context.
     * Expected behavior: The turn should be skipped with an appropriate message.
     * 
     * @throws NoSuchFieldException if the context field is not found
     * @throws IllegalAccessException if the context field cannot be accessed
     */
    @Test
    public void testPlayCountessNullPlayer() throws NoSuchFieldException, IllegalAccessException {
        ByteArrayInputStream inputStream = setSimulatedInput("0\n");
        setUpGame(inputStream, this.actionFactory);

        GameContext mockContext = mock(GameContext.class);
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(this.game, mockContext);
        when(mockContext.getCurrentUser()).thenReturn(null);

        // Set up the player's hand with Countess
        this.player.addCard(Card.COUNTESS);

        // Execute the turn
        doReturn(0).when(this.game).getCardIdx(this.player);
        game.playTurnCard(this.player);

        // Verify that the player's turn was skipped
        assertTrue(this.outContent.toString().contains("No current user found"));
    }

    /**
     * Tests the forced play rule when player has both Countess and Prince.
     * This is a white-box test examining the internal rule enforcement.
     * Expected behavior: Player must play the Prince card according to game rules.
     */
    @Test
    public void testPlayTurnCardWithCountessAndPrince() {
        setUpGame(null, this.actionFactory);

        // Set up the player's hand with Countess and a royalty card
        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRINCE); // Royalty card

        // Execute the turn
        game.playTurnCard(this.player);

        // Verify that the Countess card was played
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests the forced play rule when player has both Countess and King.
     * This is a white-box test examining the internal rule enforcement.
     * Expected behavior: Player must play the King card according to game rules.
     */
    @Test
    public void testPlayTurnCardWithCountessAndKing() {
        setUpGame(null, this.actionFactory);

        // Set up the player's hand with Countess and King
        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.KING);

        // Execute the turn
        game.playTurnCard(this.player);

        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Countess when holding a non-royalty card.
     * This is a white-box test verifying the internal game logic.
     * Expected behavior: Player should be able to choose and play Countess with appropriate message.
     */
    @Test
    public void testPlayCountessWithNonRoyalty() {
        ByteArrayInputStream inputStream = setSimulatedInput("0\n");
        setUpGame(inputStream, this.actionFactory);

        // Set up the player's hand with Countess and non-royalty card
        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.GUARD);

        game.playTurnCard(this.player);

        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Countess card. No action taken."));
    }

    /**
     * Tests playing a non-royalty card when holding Countess.
     * This is a white-box test verifying the internal game logic.
     * Expected behavior: Player should be able to choose and play the non-royalty card.
     */
    @Test
    public void testPlayNonRoyaltyWithCountess() {
        ByteArrayInputStream inputStream = setSimulatedInput("1\n");
        setMockAction(Card.GUARD, new GuardAction());
        setUpGame(inputStream, this.mockActionFactory);

        // Set up the player's hand with Countess and non-royalty card
        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.GUARD);

        game.playTurnCard(this.player);

        assertEquals(Card.GUARD, this.player.getDiscarded().getCards().get(0));
    }
}
