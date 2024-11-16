package edu.cmu.f24qa.loveletter.actions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.Game;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class WhiteboxPriestCardTest {

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
     * Test null player playing Priest card.
     */
    @Test
    void testNullPlayer() {
        GameContext context = mock(GameContext.class);
        doReturn(null).when(context).getCurrentUser();
        PriestAction priestAction = new PriestAction();

        priestAction.execute(context);

        verify(context, never()).selectOpponent();
    }

    /*
     * Test opponent reveals its hand to player.
     */
    @Test
    void testPriestPeekOpponentHand() {
        spyUser.addCard(Card.PRIEST);
        
        opponent.addCard(Card.GUARD);

        doReturn(0).when(spyGame).getCardIdx(spyUser);
        spyGame.playTurnCard(spyUser);

        // opponent should reveal card
        assertTrue(outputStream.toString().contains("opponent shows you a Guard (1)\n"));
    }
}
