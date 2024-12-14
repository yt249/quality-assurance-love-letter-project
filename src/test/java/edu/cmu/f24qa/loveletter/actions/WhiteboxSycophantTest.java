package edu.cmu.f24qa.loveletter.actions;

import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class WhiteboxSycophantTest {
    
    private GameContext context;
    private SycophantAction action;
    private ByteArrayOutputStream outContent;
    
    @BeforeEach
    void setUp() {
        context = mock(GameContext.class);
        action = new SycophantAction();
        // setup output capture
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }
    
    @Test
    void testExecuteWithValidSelectionSetsSycophantPlayer() {
        // Arrange
        Player currentPlayer = new Player("current", new Hand(), new DiscardPile(), false, 0);
        Player targetPlayer = new Player("target", new Hand(), new DiscardPile(), false, 0);
        when(context.getCurrentUser()).thenReturn(currentPlayer);
        when(context.selectOpponents(1, 1, true))
            .thenReturn(Arrays.asList(targetPlayer));
        
        // Act
        action.execute(context);
        
        // Assert
        verify(context).setSycophantForcedPlayer(targetPlayer);
        String expectedOutput = "current played the Sycophant card and forced target to be targeted when the next card is played.";
        assertEquals(expectedOutput, outContent.toString().trim());
    }
    
    @Test
    void testExecuteWithNullCurrentUserDoesNotSetSycophant() {
        // Arrange
        when(context.getCurrentUser()).thenReturn(null);
        
        // Act
        action.execute(context);
        
        // Assert
        verify(context, never()).setSycophantForcedPlayer(any());
        verify(context, never()).selectOpponents(anyInt(), anyInt(), anyBoolean());
    }
    
    @Test
    void testExecuteWithEmptySelectionDoesNotSetSycophant() {
        // Arrange
        Player currentPlayer = new Player("current", new Hand(), new DiscardPile(), false, 0);
        when(context.getCurrentUser()).thenReturn(currentPlayer);
        when(context.selectOpponents(1, 1, true))
            .thenReturn(Collections.emptyList());
        
        // Act
        action.execute(context);
        
        // Assert
        verify(context, never()).setSycophantForcedPlayer(any());
        String expectedOutput = "current played the Sycophant card but no opponent can be selected.";
        assertEquals(expectedOutput, outContent.toString().trim());
    }
} 