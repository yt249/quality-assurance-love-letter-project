package edu.cmu.f24qa.loveletter.actions;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.Deck;
import edu.cmu.f24qa.loveletter.DiscardPile;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;
import edu.cmu.f24qa.loveletter.PlayerList;

public class WhiteboxCardinalTest {
    private CardinalAction cardinalAction;
    private Deck deck;
    private GameContext context;
    private Player user ;
    private Player opponent1;
    private Player opponent2;
    private PlayerList playerList;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        deck = mock(Deck.class);
        cardinalAction = new CardinalAction();
        playerList = new PlayerList();
        user = new Player("user", new Hand(), new DiscardPile(), false, 0);
        opponent1 = new Player("opponent1", new Hand(), new DiscardPile(), false, 0);
        opponent2 = new Player("opponent2", new Hand(), new DiscardPile(), false, 0);
        playerList.addPlayer(user);
        playerList.addPlayer(opponent1);
        playerList.addPlayer(opponent2);
        // Capture System.out
        System.setOut(new PrintStream(outputStream));
    }

    private void setupContext(String simulatedInput) {
        // Create input stream with simulated user input
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Readable readable = new InputStreamReader(inputStream);
        
        // Create new GameContext with our test setup
        context = new GameContext(playerList, deck, readable);
        context.setCurrentUser(user);
    }

    /*
     * Test null player playing Cardinal card.
     */
    @Test
    void testNullPlayer() {
        // Set up null player
        GameContext context = mock(GameContext.class);
        doReturn(null).when(context).getCurrentUser();
        CardinalAction cardinalAction = new CardinalAction();

        // Play Cardinal card
        cardinalAction.execute(context);

        // No opponent should be selected
        verify(context, never()).selectOpponents(2, 2, true);

        // Print message showing no current user found
        assertTrue(outputStream.toString().contains("No current user found"));
    }

    /**
     * Test 2 players' hand cards are swapped, and
     * user chooses to peek at the second opponent's hand after the swap.
     */
    @Test
    void testPeekAtOpponent2() {
        // Set up user's hand with Cardinal
        user.addCard(Card.CARDINAL);
        // setup context with simulated input: 2 opponents' names, opponent to peek at, 
        setupContext("opponent1\nopponent2\nopponent2");

        // Set up opponents' hands
        opponent1.addCard(Card.GUARD); 
        opponent2.addCard(Card.COUNTESS);

        // user plays Cardinal
        cardinalAction.execute(context);

        // opponent1 and opponent2's hands should be swapped
        assertEquals(Card.COUNTESS, opponent1.getHand().peek(0));
        assertEquals(Card.GUARD, opponent2.getHand().peek(0));
        // user chooses to peek at opponent2's hand
        assertTrue(outputStream.toString().contains("opponent2 shows you a Guard (1)"));
    }

    /*
     * Test 2 players' hand cards are swapped, and user first enter an invalid name,
     * then chooses to peek at the first opponent's hand.
     */
    @Test
    void testRepromptAndPeekAtOpponent1() {
        // Set up user's hand with Cardinal
        user.addCard(Card.CARDINAL);
        // setup context with simulated input: 2 opponents' names, opponent to peek at
        // (non-existent user, then opponent1) 
        setupContext("opponent1\nopponent2\nnon-existent\nopponent1");

        // Set up opponents' hands
        opponent1.addCard(Card.GUARD); 
        opponent2.addCard(Card.COUNTESS);

        // user plays Cardinal
        cardinalAction.execute(context);

        // opponent1 and opponent2's hands should be swapped
        assertEquals(Card.COUNTESS, opponent1.getHand().peek(0));
        assertEquals(Card.GUARD, opponent2.getHand().peek(0));
        // reprompt to ask user to choose from the opponents swapping hands
        assertTrue(outputStream.toString().contains("Please select one of the two players whose cards were swapped."));
        // user chooses to peek at opponent2's hand
        assertTrue(outputStream.toString().contains("opponent1 shows you a Countess (7)"));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }
}
