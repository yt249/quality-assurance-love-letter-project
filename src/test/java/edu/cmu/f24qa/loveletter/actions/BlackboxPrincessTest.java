package edu.cmu.f24qa.loveletter.actions;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class BlackboxPrincessTest {
    
    /**
     * Tests that when a player plays the Princess card, the player will be eliminated.
     * 
     * Scenario:
     * - Player plays Princess card
     * 
     * Expected Results:
     * - Player should be eliminated from the game
     * - Appropriate elimination message should be displayed
     */
    @Disabled("This test is temporary disabled")
    @Test
    public void testPrincessEliminatesSelf() {
        // Capture console output
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        // Set up player
        Player testPlayer = new Player("Christy", new Hand(), new DiscardPile(), false, 0);
        testPlayer.addCard(Card.PRINCESS);

        // Set up players
        PlayerList players = new PlayerList(); 
        players.addPlayer(testPlayer);

        // Set up deck
        Deck deck = new Deck();

        // Set up input stream
        String simulatedInput = "0\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        // Set up game
        Game game = new Game(players, deck, inputStream);

        // Play Princess
        game.playTurnCard(testPlayer);
        
        // Assert
        assertTrue(outputStreamCaptor.toString().contains(testPlayer.getName()+" played the Princess and is eliminated"));  
        assertTrue(testPlayer.isEliminated()); 
    }
}