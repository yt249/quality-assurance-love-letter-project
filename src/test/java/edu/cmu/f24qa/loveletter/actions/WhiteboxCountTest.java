package edu.cmu.f24qa.loveletter.actions;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.assertEquals;

class WhiteboxCountTest {


    /**
     * Tests the execute method when there is no current user (null).
     * Verifies that the appropriate error message is printed to System.out
     */
    @Test
    void testExecuteWithNullUser() {
        // Arrange
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        
        GameContext mockContext = mock(GameContext.class);
        when(mockContext.getCurrentUser()).thenReturn(null);
        CountAction countAction = new CountAction();

        // Act
        countAction.execute(mockContext);

        // Assert
        verify(mockContext).getCurrentUser();
        assertEquals("No current user found", outputStreamCaptor.toString().trim());
    }

    /**
     * Tests the execute method when there is a valid current user.
     * Verifies that the correct message is printed to System.out
     */
    @Test
    void testExecuteWithValidUser() {
        // Arrange
        ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
        
        GameContext mockContext = mock(GameContext.class);
        Player mockPlayer = mock(Player.class);
        when(mockContext.getCurrentUser()).thenReturn(mockPlayer);
        when(mockPlayer.getName()).thenReturn("TestPlayer");
        CountAction countAction = new CountAction();

        // Act
        countAction.execute(mockContext);

        // Assert
        assertEquals("TestPlayer played the Count card. No action taken.", 
            outputStreamCaptor.toString().trim());
    }
} 