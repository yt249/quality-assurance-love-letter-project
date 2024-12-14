package edu.cmu.f24qa.loveletter.actions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.checkerframework.checker.nullness.qual.NonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class WhiteboxConstableTest {
    private @NonNull PlayerList players;
    private @NonNull Game game;
    private @NonNull Player player;
    private @NonNull Player opponent;
    private ByteArrayOutputStream outContent;

    /**
     * Initializes a new test instance with two players and necessary mocks.
     * Sets up the test environment with a player (Alice) and an opponent (Bob),
     * and captures System.out for verification.
     */
    public WhiteboxConstableTest() {
        this.players = new PlayerList();
        this.player = spy(new Player("Alice", new Hand(), new DiscardPile(), false, 0));
        this.opponent = spy(new Player("Bob", new Hand(), new DiscardPile(), false, 0));
        this.players.addPlayer(this.player);
        this.players.addPlayer(this.opponent);

        // Capture System.out
        this.outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.outContent));
    }
    
    /**
     * Tests the constable card when there is no current user in the game context.
     * This is a white-box test that uses reflection to modify the game context.
     * Expected behavior: The turn should be skipped with an appropriate message.
     * 
     * @throws NoSuchFieldException if the context field is not found
     * @throws IllegalAccessException if the context field cannot be accessed
     */
    @Test
    public void testPlayCostableNullPlayer() throws NoSuchFieldException, IllegalAccessException {
        this.game = spy(new Game(this.players, null, System.in));

        GameContext mockContext = mock(GameContext.class);
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(this.game, mockContext);
        when(mockContext.getCurrentUser()).thenReturn(null);

        // Set up the player's hand with Constable
        this.player.addCard(Card.CONSTABLE);

        // Execute the turn
        doReturn(0).when(this.game).getCardIdx(this.player);
        game.playTurnCard(this.player);

        // Verify that the player's turn was skipped
        assertTrue(this.outContent.toString().contains("No current user found"));
    }


    /**
     * Tests the constable card when there is a current user in the game context.
     * This is a white-box test that uses reflection to modify the game context.
     * Expected behavior: The turn should be skipped with an appropriate message.
     * 
     * @throws NoSuchFieldException if the context field is not found
     * @throws IllegalAccessException if the context field cannot be accessed
     */
    @Test
    public void testPlayConstable() {
        this.game = spy(new Game(this.players, null, System.in));

        this.player.addCard(Card.CONSTABLE);

        doReturn(0).when(this.game).getCardIdx(this.player);
        game.playTurnCard(this.player);

        assertEquals(Card.CONSTABLE, this.player.getDiscarded().getCards().get(0));
        assertTrue(this.outContent.toString().contains(this.player.getName() + " played the Constable card. No action taken."));
    }

    /**
     * Tests the player elimination with a constable card in the discard pile.
     * Expected behavior: The player should get a token.
     */
    @Test
    public void testPlayerEliminatedWithConstableInDiscardPile() {
        this.player.addCardToDiscarded(Card.CONSTABLE);
        this.player.eliminate();
        assertEquals(1, this.player.getTokens());
        assertTrue(this.outContent.toString().contains("Player " + this.player.getName() + " has a constable card in their discard pile and gets a token."));
    }

    /**
     * Tests the player elimination without a constable card in the discard pile.
     * Expected behavior: The player should not get a token.
     */
    @Test
    public void testPlayerEliminatedWithoutConstableInDiscardPile() {
        this.player.eliminate();
        assertEquals(0, this.player.getTokens());
    }
}
