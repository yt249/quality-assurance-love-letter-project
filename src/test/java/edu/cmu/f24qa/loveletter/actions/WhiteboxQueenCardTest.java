package edu.cmu.f24qa.loveletter.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.PlayerList;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.Hand;

public class WhiteboxQueenCardTest {
    private Game game;
    private Player user;
    private Player opponent;
    private PlayerList playerList;
    private Game spyGame;
    private Player spyUser;
    private Player spyOpponent;
    private ByteArrayOutputStream outContent;

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
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                selectOpponentInput.getBytes(StandardCharsets.UTF_8));
        game = new Game(playerList, mock(Deck.class), inputStream);
        spyGame = spy(game);

        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    /*
     * Test null player playing Queen card.
     */
    @Test
    void testNullPlayer() {
        GameContext context = mock(GameContext.class);
        doReturn(null).when(context).getCurrentUser();
        QueenAction queenAction = new QueenAction();

        queenAction.execute(context);

        assertTrue(outContent.toString().contains("No current user found"));
        verify(context, never()).selectOpponents(anyInt(), anyInt(), anyBoolean());
    }

    /*
     * Test if player's hand < opponent's hand, player should be eliminated.
     */
    @Test
    void testPlayerHandLessThanOpponentHand() {
        // Set up user's hand with Queen (index 0) and Guard (index 1)
        spyUser.addCard(Card.QUEEN);
        spyUser.addCard(Card.GUARD); // value 1

        // Set up opponent's hand with Countess
        spyOpponent.addCard(Card.COUNTESS); // value 7

        // user plays Queen
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // opponent should be eliminated
        verify(spyOpponent, times(1)).eliminate();
        // user should not be eliminated
        verify(spyUser, never()).eliminate();
        assertTrue(outContent.toString().contains("You have the smaller card! Opponent is eliminated."));
    }

    /*
     * Test if player's hand > opponent's hand, opponent should be eliminated.
     */
    @Test
    void testPlayerHandGreaterThanOpponentHand() {
        // Set up user's hand with Queen (index 0) and Countess (index 1)
        spyUser.addCard(Card.QUEEN);
        spyUser.addCard(Card.COUNTESS); // value 7

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD); // value 1

        // user plays Queen
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // opponent should not be eliminated
        verify(spyOpponent, never()).eliminate();
        // user should be eliminated
        verify(spyUser, times(1)).eliminate();
        assertTrue(outContent.toString().contains("You have the larger card! You are eliminated."));
    }

    /*
     * Test if player's hand == opponent's hand, no one should be eliminated.
     */
    @Test
    void testPlayerHandEqualOpponentHand() {
        // Set up user's hand with Queen (index 0) and Guard (index 1)
        spyUser.addCard(Card.QUEEN);
        spyUser.addCard(Card.GUARD); // value 1

        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD); // value 1

        // user plays Queen
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // Neither player should be eliminated
        verify(spyUser, never()).eliminate();
        verify(spyOpponent, never()).eliminate();
        assertTrue(outContent.toString().contains("Tie! No one is eliminated."));
    }
} 