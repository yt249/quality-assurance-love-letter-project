package edu.cmu.f24qa.loveletter.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class BlackboxCountessTest {
    private @NonNull PlayerList players;
    private @NonNull Game game;
    private @NonNull Player player;
    private @NonNull Player opponent;
    private @NonNull Deck deck;
    private @Mock ActionFactory mockActionFactory;
    private ActionFactory actionFactory;
    private ByteArrayOutputStream outContent;

    /**
     * Initializes a new test instance with two players and necessary mocks.
     * Sets up the test environment with a player (Alice) and an opponent (Bob),
     * and captures System.out for verification.
     */
    public BlackboxCountessTest() {
        this.players = new PlayerList();
        this.player = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        this.opponent = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        this.players.addPlayer(this.player);
        this.players.addPlayer(this.opponent);
        this.deck = new Deck();
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
     * Tests playing Guard card when player has both Countess and Guard.
     * Expected behavior: Player should be able to choose and successfully play the Guard card.
     */
    @Test
    public void testCountessWithGuardPlayGuard() {
        ByteArrayInputStream inputStream = setSimulatedInput("1\n");
        setMockAction(Card.GUARD, new GuardAction());
        setUpGame(inputStream, this.mockActionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.GUARD);
        
        game.playTurnCard(this.player);
        assertEquals(Card.GUARD, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Countess card when player has both Countess and Guard.
     * Expected behavior: Player should be able to choose and play Countess with appropriate message.
     */
    @Test
    public void testCountessWithGuardPlayCountess() {
        ByteArrayInputStream inputStream = setSimulatedInput("0\n");
        setUpGame(inputStream, this.actionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.GUARD);
        
        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Countess card. No action taken."));
    }

    /**
     * Tests playing Priest card when player has both Countess and Priest.
     * Expected behavior: Player should be able to choose and successfully play the Priest card.
     */
    @Test
    public void testCountessWithPriestPlayPriest() {
        ByteArrayInputStream inputStream = setSimulatedInput("1\n");
        setMockAction(Card.PRIEST, new PriestAction());
        setUpGame(inputStream, this.mockActionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRIEST);
        
        game.playTurnCard(this.player);
        assertEquals(Card.PRIEST, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Countess card when player has both Countess and Priest.
     * Expected behavior: Player should be able to choose and play Countess with appropriate message.
     */
    @Test
    public void testCountessWithPriestPlayCountess() {
        ByteArrayInputStream inputStream = setSimulatedInput("0\n");
        setUpGame(inputStream, this.actionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRIEST);

        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Countess card. No action taken."));
    }

    /**
     * Tests playing Baron card when player has both Countess and Baron.
     * Expected behavior: Player should be able to choose and successfully play the Baron card.
     */
    @Test
    public void testCountessWithBaronPlayBaron() {
        ByteArrayInputStream inputStream = setSimulatedInput("1\n");
        setMockAction(Card.BARON, new BaronAction());
        setUpGame(inputStream, this.mockActionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.BARON);
        
        game.playTurnCard(this.player);
        assertEquals(Card.BARON, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Countess card when player has both Countess and Baron.
     * Expected behavior: Player should be able to choose and play Countess with appropriate message.
     */
    @Test
    public void testCountessWithBaronPlayCountess() {
        ByteArrayInputStream inputStream = setSimulatedInput("0\n");
        setUpGame(inputStream, this.actionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.BARON);

        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Countess card. No action taken."));
    }

    /**
     * Tests playing Handmaid card when player has both Countess and Handmaid.
     * Expected behavior: Player should be able to choose and successfully play the Handmaid card.
     */
    @Test
    public void testCountessWithHandmaidenPlayHandmaiden() {
        ByteArrayInputStream inputStream = setSimulatedInput("1\n");
        setMockAction(Card.HANDMAIDEN, new HandmaidenAction());
        setUpGame(inputStream, this.mockActionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.HANDMAIDEN);
        
        game.playTurnCard(this.player);
        assertEquals(Card.HANDMAIDEN, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Countess card when player has both Countess and Handmaid.
     * Expected behavior: Player should be able to choose and play Countess with appropriate message.
     */
    @Test
    public void testCountessWithHandmaidenPlayCountess() {
        ByteArrayInputStream inputStream = setSimulatedInput("0\n");
        setUpGame(inputStream, this.actionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.HANDMAIDEN);

        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Countess card. No action taken."));
    }

    /**
     * Tests playing Prince card when player has both Countess and Prince.
     * Expected behavior: Player should be able to choose and successfully play the Prince card.
     */
    @Test
    public void testCountessWithPrincePlayPrince() {
        setUpGame(null, this.actionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRINCE);
        
        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing King card when player has both Countess and King.
     * Expected behavior: Player should be able to choose and successfully play the King card.
     */
    @Test
    public void testCountessWithKingPlayKing() {
        setUpGame(null, this.actionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.KING);
        
        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Princess card when player has both Countess and Princess.
     * Expected behavior: Player should be able to choose and successfully play the Princess card.
     */
    @Test
    public void testCountessWithPrincessPlayPrincess() {
        setMockAction(Card.PRINCESS, new PrincessAction());
        ByteArrayInputStream inputStream = setSimulatedInput("1\n");
        setUpGame(inputStream, this.mockActionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRINCESS);
        
        game.playTurnCard(this.player);
        assertEquals(Card.PRINCESS, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Countess card when player has both Countess and Princess.
     * Expected behavior: Player should be able to choose and play Countess with appropriate message.
     */
    @Test
    public void testCountessWithPrincessPlayCountess() {
        ByteArrayInputStream inputStream = setSimulatedInput("0\n");
        setUpGame(inputStream, this.actionFactory);

        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRINCESS);

        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Countess card. No action taken."));
    }
}
