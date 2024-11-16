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

public class BlackboxHandmaidenTest {

    /**
     * Tests that when a player plays the Handmaiden card, the player will be protected 
     * from any actions until their next turn.
     * 
     * Scenario:
     * - Player plays Handmaiden card
     * 
     * Expected Results:
     * - Player should be protected
     * - Appropriate protection message should be displayed
     */
    @Disabled("This test is temporary disabled")
    @Test
    public void testHandmaidenProtectsSelf() {
        // Capture console output
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        // Set up player
        Player testPlayer = new Player("Christy", new Hand(), new DiscardPile(), false, 0);
        testPlayer.addCard(Card.HANDMAIDEN);

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

        // Play Handmaiden
        game.playTurnCard(testPlayer);
        
        // Assert
        assertTrue(outputStreamCaptor.toString().contains(testPlayer.getName()+" is now protected until their next turn."));  
        assertTrue(testPlayer.getIsProtected()); 
    }
}