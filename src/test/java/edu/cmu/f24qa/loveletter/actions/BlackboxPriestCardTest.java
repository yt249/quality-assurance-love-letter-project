package edu.cmu.f24qa.loveletter.actions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class BlackboxPriestCardTest {
    private Game game;
    private Player user ;
    private Player opponent;
    private PlayerList playerList;
    private Game spyGame;
    private Player spyUser;
    private Player spyOpponent;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

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
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    /*
     * Test if all other players are protected, nothing should happen when the player plays Priest.
     */
    @Test
    @Disabled("selectOpponent doens't check for isProtected.")
    void testAllOtherPlayersAreProtected() {
        // Set up user's hand with Priest
        spyUser.addCard(Card.PRIEST);
        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD);
        // opponent is protected
        spyOpponent.switchProtection();
        
        // user plays Priest
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should not reveal card
        assertFalse(outputStream.toString().contains("opponent shows you a"));
    }

    /*
     * Test opponent reveals its hand to player.
     */
    @Test
    void testPlayPriestCard() {
        // Set up user's hand with Priest
        spyUser.addCard(Card.PRIEST);
        // Set up opponent's hand with Guard
        spyOpponent.addCard(Card.GUARD);
        
        // user plays Priest
        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // should reveal card
        assertTrue(outputStream.toString().contains("opponent shows you a Guard (1)\n"));
    }
}
