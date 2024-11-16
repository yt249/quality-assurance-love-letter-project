package edu.cmu.f24qa.loveletter.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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

        verify(context, never()).selectOpponent();
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
    @Disabled("getCurrentUser substitute player with a new instance, player is not eliminated")
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
     * Test if player's hand == opponent's hand and player's discard pile total > opponent's discard pile total,
     * opponent should be eliminated.
     */
    @Test
    @Disabled("BaronAction compare DiscardPile wrong elimination logic.")
    void testPlayerDiscardPileGreaterThanOpponentDiscardPile() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Set up user's hand with Guard (index 0) and Baron (index 1)
        spyUser.addCard(Card.GUARD);
        spyUser.addCard(Card.BARON);
        
        // Set up user's discard pile with Prince and Handmaiden
        spyUser.addCardToDiscarded(Card.PRINCE); // value 5
        spyUser.addCardToDiscarded(Card.HANDMAIDEN); // value 4

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD); 

        // Set up opponent's discard pile with Guard and Countess
        spyOpponent.addCardToDiscarded(Card.COUNTESS); // value 7
        spyOpponent.addCardToDiscarded(Card.GUARD); // value 1

        GameContext mockContext = mock(GameContext.class);
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(game, mockContext);
        // Stub getCurrentUser() to return mockUser
        when(mockContext.getCurrentUser()).thenReturn(spyUser);

        // user plays Baron
        doReturn(1).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // user should not be eliminated
        verify(spyUser, never()).eliminate();
        // opponent eliminated
        verify(spyOpponent, times(1)).eliminate();
    }

    /*
     * Test if player's hand == opponent's hand and player's discard pile total < opponent's discard pile total,
     * player should be eliminated.
     */
    @Test
    @Disabled("BaronAction compare DiscardPile wrong elimination logic.")
    void testPlayerDiscardPileLessThanOpponentDiscardPile() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Set up user's hand with Guard (index 0) and Baron (index 1)
        spyUser.addCard(Card.GUARD);
        spyUser.addCard(Card.BARON);

        // Set up user's discard pile with Guard and Guard
        spyUser.addCardToDiscarded(Card.GUARD); // value 1
        spyUser.addCardToDiscarded(Card.GUARD); // value 1

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD);

        // Setup opponent's discard pile with Prince and Handmaiden
        spyOpponent.addCardToDiscarded(Card.PRINCE); // value 5
        spyOpponent.addCardToDiscarded(Card.HANDMAIDEN); // value 4

        GameContext mockContext = mock(GameContext.class);
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(game, mockContext);
        // Stub getCurrentUser() to return mockUser
        when(mockContext.getCurrentUser()).thenReturn(spyUser);
        
        // user plays Baron
        doReturn(1).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // user eliminated
        verify(spyUser, times(1)).eliminate();
        // opponent should not be eliminated
        verify(spyOpponent, never()).eliminate();
    }
}
