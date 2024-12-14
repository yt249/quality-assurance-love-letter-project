package edu.cmu.f24qa.loveletter.actions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class WhiteboxBaronessTest {
    private Game game;
    private Player user ;
    private Player opponent1;
    private Player opponent2;
    private PlayerList playerList;
    private Game spyGame;
    private Player spyUser;
    private Player spyOpponent1;
    private Player spyOpponent2;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Create players
        user = new Player("user", new Hand(), new DiscardPile(), false, 0);
        opponent1 = new Player("opponent1", new Hand(), new DiscardPile(), false, 0);
        opponent2 = new Player("opponent2", new Hand(), new DiscardPile(), false, 0);
        
        // Create spies for the players
        spyUser = spy(user);
        spyOpponent1 = spy(opponent1);
        spyOpponent2 = spy(opponent2);

        // Add players to player list
        playerList = new PlayerList();
        playerList.addPlayer(spyUser);
        playerList.addPlayer(spyOpponent1);
        playerList.addPlayer(spyOpponent2);

        // Create input stream for testing (select opponent1 and "yes" to select opponent2)
        String selectOpponentInput = "opponent1\nyes\nopponent2\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(selectOpponentInput.getBytes(StandardCharsets.UTF_8));

        // Capture System.out
        System.setOut(new PrintStream(outputStream));

        // Create game instance
        game = new Game(playerList, mock(Deck.class), inputStream);
        spyGame = spy(game);
    }

    /*
     * Test null player playing Baroness card.
     */
    @Test
    void testNullPlayer() {
        // Set up null player
        GameContext context = mock(GameContext.class);
        doReturn(null).when(context).getCurrentUser();
        BaronessAction baronessAction = new BaronessAction();

        // Play Baroness card
        baronessAction.execute(context);

        // No opponent should be selected
        verify(context, never()).selectOpponents(1, 2, false);

        // Print message showing no current user found
        assertTrue(outputStream.toString().contains("No current user found"));
    }

    /**
     * Test opponent reveals its hand to player.
     */
    @Test
    void testPlayBaronessCard() {
        // Set up user's hand with Baroness
        spyUser.addCard(Card.BARONESS);

        // Set up opponent's hand with Guard
        spyOpponent1.addCard(Card.GUARD);
        spyOpponent2.addCard(Card.COUNTESS);
        
        // user plays Baroness
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should reveal card
        assertTrue(outputStream.toString().contains("opponent1 shows you a Guard (1)\n"));
        assertTrue(outputStream.toString().contains("opponent2 shows you a Countess (7)\n"));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}