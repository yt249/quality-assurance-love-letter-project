package edu.cmu.f24qa.loveletter.actions;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.DiscardPile;

public class WhiteboxPrincessTest {
    
    /**
     * Tests playing Princess card correctly eliminates the player
     *       + displays the appropriate elimination message
     */
    @Test
    public void testPrincessEliminatesPlayerSuccessfully() {
        // Capture console output
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        
        // Set up players with spies
        Player testPlayer = new Player("TestPlayer", new Hand(), new DiscardPile(), false, 0);
        Player spyPlayer = spy(testPlayer);
        
        // Set up PlayerList
        PlayerList players = new PlayerList();
        players.addPlayer(spyPlayer);
        
        // Create game with a simulated input stream that will return "0" when asked for card choice
        String simulatedInput = "0\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Game game = new Game(players, mock(Deck.class), inputStream);
        Game spyGame = spy(game);
        
        // Add Princess to player's hand
        spyPlayer.addCard(Card.PRINCESS);
        spyPlayer.addCard(Card.GUARD); // a card for eliminate() to remove
        
        // Execute the action
        spyGame.playTurnCard(spyPlayer);

        // Verify the results
        assertTrue(outputStreamCaptor.toString().contains("TestPlayer played the Princess and is eliminated"));    
        verify(spyPlayer, times(1)).eliminate();
    }

    /**
     * Tests playing Princess card where there is no current user
     *       + displays an error message
     */
    @Test
    public void testPrincessWithNullUserHandling() {
        // Setting up the standard output   
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        
        // Setting up the mock objects
        GameContext mockContext = mock(GameContext.class);
        when(mockContext.getCurrentUser()).thenReturn(null);
        PrincessAction action = new PrincessAction();

        // Executing the action
        action.execute(mockContext);

        // Asserting the results
        assertEquals("No current user found", outputStreamCaptor.toString().trim());
    }
} 