package edu.cmu.f24qa.loveletter.actions;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import edu.cmu.f24qa.loveletter.*;

/**
 * Unit tests for the Jester card's behavior in the Love Letter game.
 * This class tests various scenarios involving the Jester card, such as 
 * guessing the winner and handling edge cases like missing users in the context.
 */
public class WhiteboxJesterTest {

    @Spy
    private GameContext context;

    private Player player;
    private Player opponent;
    private Game game;

    private void setupGameContext(Card playerCard, Card opponentCard) {
        // Initialize players
        player = new Player("Player1", new Hand(), new DiscardPile(), false, 0);
        opponent = spy(new Player("Opponent", new Hand(), new DiscardPile(), false, 0));

        // Create a PlayerList and add players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(player);
        playerList.addPlayer(opponent);

        InputStream dummyInputStream = new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
        context = spy(new GameContext(null, null, new InputStreamReader(dummyInputStream)));
        player.addCard(playerCard);
        opponent.addCard(opponentCard);

        // Mock context behavior
        doReturn(player).when(context).getCurrentUser();
        doReturn(Arrays.asList(opponent)).when(context).selectOpponents(1,1,true);

        // Mock the Game class
        game = spy(new Game(playerList, new Deck(), dummyInputStream));

        // Use reflection to set the private context and playerList fields
        try {
            Field contextField = Game.class.getDeclaredField("context");
            contextField.setAccessible(true);
            contextField.set(game, context);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests the JesterAction behavior when a player guesses an opponent.
     */
    @Test
    void testJesterActionGuessOpponent() {
        setupGameContext(Card.GUARD, Card.PRINCESS);

        JesterAction action = new JesterAction();
        action.execute(context);

        // Verify that the guessed player and jester player are set correctly
        assertEquals(opponent, context.getGuessedPlayer());
        assertEquals(player, context.getJesterPlayer());
    }

    /**
     * Tests the JesterAction behavior when there is no current user in the context.
     * Ensures that no action is taken and an appropriate message is printed.
     */
    @Test
    void testJesterActionNoCurrentUser() {
        // Setup the game context without a current user
        setupGameContext(Card.GUARD, Card.PRINCESS);
        doReturn(null).when(context).getCurrentUser();

        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        JesterAction action = new JesterAction();
        action.execute(context);

        // Verify that no guessed player or jester player is set
        assertNull(context.getGuessedPlayer());
        assertNull(context.getJesterPlayer());

        // Verify that the appropriate message is printed
        String expectedMessage = "No current user found";
        assertTrue(outContent.toString().contains(expectedMessage));

        // Reset System.out
        System.setOut(System.out);
    }

    /**
     * Tests the JesterAction behavior when no opponents are selected.
     * Ensures that the Jester card is discarded without effect and an appropriate message is printed.
     */
    @Test
    void testJesterActionNoOpponents() {
        setupGameContext(Card.GUARD, Card.PRINCESS);

        // Mock context to return an empty list of opponents
        doReturn(Arrays.asList()).when(context).selectOpponents(1, 1, true);

        // Redirect System.out to capture the output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        JesterAction action = new JesterAction();
        action.execute(context);

        // Verify that no guessed player or jester player is set
        assertNull(context.getGuessedPlayer());
        assertNull(context.getJesterPlayer());

        // Verify that the appropriate message is printed
        String expectedMessage = "Jester is discarded without effect.";
        assertTrue(outContent.toString().contains(expectedMessage));

        // Reset System.out
        System.setOut(System.out);
    }
} 