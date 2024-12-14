package edu.cmu.f24qa.loveletter.actions;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.PlayerList;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.Hand;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

public class GuardActionTest {

    @Test
    void testExecute() {

        // Capture the system output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Create mock objects
        Player currentUser = mock(Player.class);
        Player opponent = mock(Player.class);

        when(opponent.getName()).thenReturn("opponent");
        Hand opponentHand = new Hand();
        opponentHand.add(Card.BARON);
        
        when(opponent.getHand()).thenReturn(opponentHand);
        GuardAction guardAction = new GuardAction();
        Readable mockScanner = new StringReader("Baron\nopponent\nopponent");
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(opponent);
        GameContext gameContext = new GameContext(playerList, new Deck(), mockScanner);
        GameContext gameContextSpy = Mockito.spy(gameContext);
        when(gameContextSpy.getCurrentUser()).thenReturn(currentUser);
        
        // Execute
        guardAction.execute(gameContextSpy);

        // Check
        verify(gameContextSpy, times(1)).selectOpponents(1, 1, false);
        assertTrue(outContent.toString().contains("Who would you like to target: "));
    }
    
}