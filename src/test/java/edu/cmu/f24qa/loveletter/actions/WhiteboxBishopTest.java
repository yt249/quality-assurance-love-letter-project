package edu.cmu.f24qa.loveletter.actions;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.cmu.f24qa.loveletter.*;

public class WhiteboxBishopTest {

    private GameContext context;
    private Deck deck;
    private BishopAction bishopAction;
    private PlayerList playerList;
    private Player currentUser;
    private Player opponent;

    @BeforeEach
    void setUp() {
        deck = mock(Deck.class);
        bishopAction = new BishopAction();
        playerList = new PlayerList();
        currentUser = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        opponent = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        playerList.addPlayer(currentUser);
        playerList.addPlayer(opponent);
    }

    private void setupContext(String simulatedInput) {
        // Create input stream with simulated user input
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Readable readable = new InputStreamReader(inputStream);
        
        // Create new GameContext with our test setup
        context = new GameContext(playerList, deck, readable);
        context.setCurrentUser(currentUser);
    }

    @Test
    public void testBishopWithNoOpponents() {
        // setup players' hands
        currentUser.addCard(Card.BISHOP);
        opponent.addCard(Card.GUARD);
        // no opponents can be selected
        opponent.switchProtection();
        // guess number 1
        setupContext("1\n");

        // Capture System.out.println calls
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        bishopAction.execute(context);
        
        // Verify that the appropriate message was printed
        assertTrue(outContent.toString().contains("No opponent found. Bishop is discarded without effect."));
    }

    @Test
    void testCorrectGuessWithOpponentDiscardsHand() {
        // setup players' hands
        currentUser.addCard(Card.BISHOP);
        opponent.addCard(Card.GUARD);

        // setup mock deck response
        Card newCard = Card.PRINCESS;
        when(deck.draw()).thenReturn(newCard);

        // setup context with simulated input: opponent name, value guess (1 for Guard), discard choice
        setupContext("1\nBob\ny\n");

        // record initial state
        int initialTokens = currentUser.getTokens();

        // execute bishop action
        bishopAction.execute(context);
        
        // verify
        assertEquals(initialTokens + 1, currentUser.getTokens(), "Current user should gain a token");
        assertTrue(opponent.getHand().getHand().contains(newCard), "Opponent should have drawn new card");
        assertFalse(opponent.getHand().getHand().contains(Card.GUARD), "Opponent should not have original card");
        assertTrue(opponent.getDiscarded().getCards().contains(Card.GUARD), "Opponent should have discarded original card");
    }

    @Test
    void testIncorrectGuess() {
        // setup players' hands
        currentUser.addCard(Card.BISHOP);
        opponent.addCard(Card.COUNTESS);  // Countess has different value than Guard

        // setup context with simulated input: opponent name, wrong value guess
        setupContext("1\nBob\n");  // Guessing 1 (Guard) when opponent has Countess

        int initialTokens = currentUser.getTokens();

        // execute bishop action
        bishopAction.execute(context);
        
        // verify
        assertEquals(initialTokens, currentUser.getTokens(), "Current user should not gain a token");
        assertTrue(opponent.getHand().getHand().contains(Card.COUNTESS), "Opponent should still have original card");
        assertEquals(1, opponent.getHand().getHand().size(), "Opponent should have exactly one card");
    }

    @Test
    void testNullCurrentUser() {
        // setup players' hands
        currentUser.addCard(Card.BISHOP);
        opponent.addCard(Card.GUARD);

        // setup context without setting current user
        setupContext("1\nBob\n");
        context.setCurrentUser(null);  // Explicitly set current user to null

        // execute bishop action
        bishopAction.execute(context);
        
        // verify
        assertEquals(0, opponent.getTokens(), "Opponent should not gain any tokens");
        assertTrue(opponent.getHand().getHand().contains(Card.GUARD), "Opponent's hand should remain unchanged");
        assertEquals(1, opponent.getHand().getHand().size(), "Opponent should still have exactly one card");
    }

    @Test
    public void testGuessRightEarnsEnoughTokensToWin() {
        GameContext mockContext = mock(GameContext.class);
        PlayerList playerList = new PlayerList();
        // Player 1 has 6 tokens, one less than winning amount
        Player currentPlayer = new Player("Player1", new Hand(), new DiscardPile(), false, 6);
        Player opponent = new Player("Player2", new Hand(), new DiscardPile(), false, 0);
        playerList.addPlayer(currentPlayer);
        playerList.addPlayer(opponent);
        
        // Capture System.out.println calls
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        // Set up opponent with Prince card
        opponent.addCard(Card.PRINCE);
        
        // Mock the necessary method calls
        when(mockContext.getCurrentUser()).thenReturn(currentPlayer);
        when(mockContext.readLine()).thenReturn("5");  // Guess the correct card value
        when(mockContext.selectOpponents(1, 1, false)).thenReturn(List.of(opponent));
        when(mockContext.getPlayers()).thenReturn(playerList);
        
        // Execute
        bishopAction.execute(mockContext);
        
        // Verify
        assertEquals(7, currentPlayer.getTokens());  // Player should get one more token
        assertFalse(outContent.toString().contains("would you like to discard your hand"));
    }

    @Test
    void testBishopCorrectGuessOpponentDiscardsPrincess() {
        // setup players' hands
        currentUser.addCard(Card.BISHOP);
        opponent.addCard(Card.PRINCESS);

        // setup context with simulated input: 
        // value guess (8 for Princess), opponent name, discard choice
        setupContext("8\nBob\ny\n");

        // execute bishop action
        bishopAction.execute(context);
        
        // verify
        assertEquals(1, currentUser.getTokens(), "Current user should gain a token");
        assertTrue(opponent.isEliminated(), "Opponent should be eliminated due to Princess discard");
        verify(deck, never()).draw();  // No card should be drawn since Princess eliminates
    }
}
