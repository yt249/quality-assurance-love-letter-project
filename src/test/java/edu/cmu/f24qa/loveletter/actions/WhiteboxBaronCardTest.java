package edu.cmu.f24qa.loveletter.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.PlayerList;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.Hand;

public class WhiteboxBaronCardTest {
    private Game game;
    private Player user ;
    private Player opponent;
    private PlayerList playerList;
    private Game spyGame;
    private Player spyUser;
    private Player spyOpponent;

    @BeforeEach
    void setUp() {
        user = new Player("user", new Hand(), new DiscardPile(), false, 0);
        opponent = new Player("opponent", new Hand(), new DiscardPile(), false, 0);
        spyUser = spy(user);
        spyOpponent = spy(opponent);
        playerList = new PlayerList();
        playerList.addPlayer(spyUser);
        playerList.addPlayer(spyOpponent);
        String selectOpponentInput = "opponent\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(selectOpponentInput.getBytes(StandardCharsets.UTF_8));
        game = new Game(playerList, mock(Deck.class), inputStream);
        spyGame = spy(game);
    }

    /*
     * Test null player playing Baron card.
     */
    @Test
    void testNullPlayer() {
        GameContext context = mock(GameContext.class);
        doReturn(null).when(context).getCurrentUser();
        BaronAction baronAction = new BaronAction();

        baronAction.execute(context);

        verify(context, never()).selectOpponents(1, 1, false);
    }
    
    /*
     * Test if player's hand > opponent's hand, opponent should be eliminated.
     */
    @Test
    void testPlayerHandGreaterThanOpponentHand() {
        // Set up user's hand with Baron (index 0) and Countess (index 1)
        spyUser.addCard(Card.BARON);
        spyUser.addCard(Card.COUNTESS); // value 7

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD); // value 1

        // user plays Baron
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // user should not be eliminated
        verify(spyUser, never()).eliminate();
        // opponent eliminated
        verify(spyOpponent, times(1)).eliminate();
    }

    /*
     * Test if player's hand < opponent's hand, player should be eliminated.
     */
    @Test
    void testPlayerHandLessThanOpponentHand() {
        // Set up user's hand with Baron (index 0) and Guard (index 1)
        spyUser.addCard(Card.BARON);
        spyUser.addCard(Card.GUARD);  // value 1

        // Set up opponent's hand with Countess
        spyOpponent.addCard(Card.COUNTESS);  // value 7
        
        // user plays Baron
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // user eliminated
        verify(spyUser, times(1)).eliminate();
        // opponent should not be eliminated
        verify(spyOpponent, never()).eliminate();
    }

    /*
     * Test if player's hand == opponent's hand, no one is eliminated.
     */
    @Test
    void testPlayerHandEqualsToOpponentHand() {
        // Set up user's hand with Baron (index 0) and Guard (index 1)
        spyUser.addCard(Card.BARON);
        spyUser.addCard(Card.GUARD);

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD); 
        
        // user plays Baron
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // user eliminated
        assertFalse(spyUser.isEliminated());
        // opponent should not be eliminated
        assertFalse(spyOpponent.isEliminated());
    }
}

