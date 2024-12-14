package edu.cmu.f24qa.loveletter.actions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Card;

public class PrincessActionTest {

    private GameContext context;
    private CardAction princessAction;
    private Player player;

    @BeforeEach
    public void setUp() {
        context = mock(GameContext.class);
        princessAction = new PrincessAction();
        player = new Player("Test Player", new Hand(), new DiscardPile(), false, 0);
        player.addCard(Card.PRINCESS);
        when(context.getCurrentUser()).thenReturn(player);
    }

    @Test
    public void testPrincessActionDoesNotAllowOpponentSelection() {
        princessAction.execute(context);
        verify(context, never()).selectOpponents(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    public void testPrincessActionEliminatesPlayer() {
        Player user = context.getCurrentUser();
        assertNotNull(player, "Player should not be null");
        princessAction.execute(context);
        assertTrue(user.isEliminated());
    }
}
