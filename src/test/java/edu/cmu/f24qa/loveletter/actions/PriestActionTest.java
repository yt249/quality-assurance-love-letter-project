package edu.cmu.f24qa.loveletter.actions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PriestActionTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testExecute() {
        PriestAction priestAction = new PriestAction();
        GameContext mockGameContext = mock(GameContext.class);
        Player currentUser = mock(Player.class);
        Player opponent = mock(Player.class);
        Hand opponentHand = new Hand();
        opponentHand.add(Card.BARON);
        when(opponent.getName()).thenReturn("opponent");
        when(opponent.getHand()).thenReturn(opponentHand);
        when(mockGameContext.getCurrentUser()).thenReturn(currentUser);
        when(mockGameContext.selectOpponents(1, 1, false)).thenReturn(List.of(opponent));

        priestAction.execute(mockGameContext);

        assertEquals("opponent shows you a Baron (3)\n", outputStream.toString());
    }
}
