package edu.cmu.f24qa.loveletter.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.checkerframework.checker.nullness.qual.NonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        // Capture System.out
        this.outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.outContent));
    }

    /**
     * Sets up simulated user input for testing.
     * Creates a new game instance with the provided input string.
     *
     * @param input The string to be used as simulated user input
     */
    private void setSimulatedInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
            input.getBytes(StandardCharsets.UTF_8)
        );
        this.game = new Game(players, deck, inputStream, mockActionFactory);
        this.player = players.getCurrentPlayer();
    }

    /**
     * Sets up a mock Countess action in the action factory.
     * Creates a spy of CountessAction and configures the mock factory to return it.
     */
    private void setCountessAction() {
        CountessAction countessAction = new CountessAction();
        when(this.mockActionFactory.getAction(Card.COUNTESS.getName())).thenReturn(countessAction);
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
     * Tests playing Guard card when player has both Countess and Guard.
     * Expected behavior: Player should be able to choose and successfully play the Guard card.
     */
    @Test
    public void testCountessWithGuardPlayGuard() {
        setMockAction(Card.GUARD, new GuardAction());

        setSimulatedInput("1\n"); // Choose to play Guard
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
        setCountessAction();

        setSimulatedInput("0\n"); // Choose to play Countess
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
        // mock priest action
        setMockAction(Card.PRIEST, new PriestAction());

        setSimulatedInput("1\n"); // Choose to play Priest
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
        setCountessAction();

        setSimulatedInput("0\n"); // Choose to play Priest
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
        setMockAction(Card.BARON, new BaronAction());

        setSimulatedInput("1\n"); // Choose to play Baron
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
        setCountessAction();

        setSimulatedInput("0\n"); // Choose to play Baron
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
        // mock handmaiden action
        setMockAction(Card.HANDMAIDEN, new HandmaidenAction());

        setSimulatedInput("1\n"); // Choose to play Handmaid
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
        setCountessAction();

        setSimulatedInput("0\n"); // Choose to play Handmaid
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
        // mock prince action
        setMockAction(Card.PRINCE, new PrinceAction());

        setSimulatedInput("1\n"); // Choose to play Prince
        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRINCE);
        
        game.playTurnCard(this.player);
        assertEquals(Card.PRINCE, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing King card when player has both Countess and King.
     * Expected behavior: Player should be able to choose and successfully play the King card.
     */
    @Test
    public void testCountessWithKingPlayKing() {
        // mock king action
        setMockAction(Card.KING, new KingAction());

        setSimulatedInput("1\n"); // Choose to play King
        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.KING);
        
        game.playTurnCard(this.player);
        assertEquals(Card.KING, this.player.getDiscarded().getCards().get(0));
    }

    /**
     * Tests playing Princess card when player has both Countess and Princess.
     * Expected behavior: Player should be able to choose and successfully play the Princess card.
     */
    @Test
    public void testCountessWithPrincessPlayPrincess() {
        setMockAction(Card.PRINCESS, new PrincessAction());

        setSimulatedInput("1\n"); // Choose to play Princess
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
        setCountessAction();

        setSimulatedInput("0\n"); // Choose to play Princess
        this.player.addCard(Card.COUNTESS);
        this.player.addCard(Card.PRINCESS);

        game.playTurnCard(this.player);
        assertEquals(Card.COUNTESS, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Countess card. No action taken."));
    }
}
