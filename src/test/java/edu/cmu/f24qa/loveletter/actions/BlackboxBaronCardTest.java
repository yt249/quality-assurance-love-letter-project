package edu.cmu.f24qa.loveletter.actions;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class BlackboxBaronCardTest {
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
     * Test if all other players are protected, nothing should happen when the player
     * plays the Baron card.
     */
    @Test
    void testAllOtherPlayersAreProtected() {
        // Set up user's hand with Baron (index 0) and Countess (index 1)
        spyUser.addCard(Card.BARON);
        spyUser.addCard(Card.COUNTESS);
        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD);
        // opponent is protected
        spyOpponent.switchProtection();

        // user plays Baron
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should not eliminate user
        verify(spyUser, never()).eliminate();
        // should not eliminate opponent
        verify(spyOpponent, never()).eliminate();
    }

    /*
     * Test if player's hand > opponent's hand, opponent should be eliminated.
     */
    @Test
    void testPlayerHandGreaterThanOpponentHand() {
        // Set up user's hand with Baron (index 0) and Countess (index 1)
        spyUser.addCard(Card.BARON);
        spyUser.addCard(Card.COUNTESS);

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.PRINCE);

        // user plays Baron
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should not eliminate user
        verify(spyUser, never()).eliminate();
        // should eliminate opponent
        verify(spyOpponent, times(1)).eliminate();
    }

    /*
     * Test if player's hand < opponent's hand, player should be eliminated.
     */
    @Test
    void testPlayerHandLessThanOpponentHand() {
        // Set up user's hand with Guard (index 0) and Baron (index 1)
        spyUser.addCard(Card.GUARD); // value 1
        spyUser.addCard(Card.BARON);

        // Set up opponent's hand with Priest
        spyOpponent.addCard(Card.PRIEST); // value 2

        // user plays Baron
        doReturn(1).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);
        
        // should eliminate user
        verify(spyUser, times(1)).eliminate();
        // should not eliminate opponent
        verify(spyOpponent, never()).eliminate();
    }

    /*
     * Test if player's hand == opponent's hand and player's discard pile total > opponent's discard pile total,
     * opponent should be eliminated.
     */
    @Test
    void testPlayerDiscardPileGreaterThanOpponentDiscardPile() {
        // Set up user's hand with Guard (index 0) and Baron (index 1)
        spyUser.addCard(Card.GUARD);
        spyUser.addCard(Card.BARON);
        
        // Set up user's discard pile with Prince and Handmaiden
        spyUser.addCardToDiscarded(Card.PRINCE); // value 5
        spyUser.addCardToDiscarded(Card.HANDMAIDEN); // value 4

        // Set up opponent's hand with Guard (index 0)
        spyOpponent.addCard(Card.GUARD);

        // Set up opponent's discard pile with Guard and Countess
        spyOpponent.addCardToDiscarded(Card.COUNTESS); // value 7
        spyOpponent.addCardToDiscarded(Card.GUARD); // value 1

        // user plays Baron
        doReturn(1).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should not eliminate user
        verify(spyUser, never()).eliminate();
        // should eliminate opponent
        verify(spyOpponent, times(1)).eliminate();
    }

    /*
     * Test if player's hand == opponent's hand and player's discard pile total < opponent's discard pile total,
     * player should be eliminated.
     */
    @Test
    void testPlayerDiscardPileLessThanOpponentDiscardPile() {
        // Set up user's hand with Guard (index 0) and Baron (index 1)
        spyUser.addCard(Card.GUARD);
        spyUser.addCard(Card.BARON);

        // Set up user's discard pile with Guard
        spyUser.addCardToDiscarded(Card.GUARD);

        // Set up opponent's hand with Guard (index 0)
        spyOpponent.addCard(Card.GUARD);

        // Set up opponent's discard pile with Prince
        spyOpponent.addCardToDiscarded(Card.PRINCE);

        // user plays Baron
        doReturn(1).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should eliminate user
        verify(spyUser, times(1)).eliminate();
        // should not eliminate opponent
        verify(spyOpponent, never()).eliminate();
    }

    /*
     * Test if player's hand == opponent's hand and player's discard pile total == opponent's discard pile total,
     * nothing should happen (nobody is eliminated).
     */
    @Test
    void testPlayerDiscardPileEqualsToOpponentDiscardPile() {
        // Set up user's hand with Guard (index 0) and Baron (index 1)
        spyUser.addCard(Card.GUARD);
        spyUser.addCard(Card.BARON);

        // Set up user's discard pile with Guard
        spyUser.addCardToDiscarded(Card.GUARD); // value 1

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD);

        // Set up opponent's discard pile with Handmaiden
        spyOpponent.addCardToDiscarded(Card.HANDMAIDEN);  // value 4
        
        // user plays Baron
        doReturn(1).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should eliminate user
        verify(spyUser, never()).eliminate();
        // should not eliminate opponent
        verify(spyOpponent, never()).eliminate();
    }
}