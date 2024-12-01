package edu.cmu.f24qa.loveletter;

import java.util.Stack;
import java.lang.reflect.Field;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameTest {

    /* 
     * Verify a round shall end when only one player has cards.
     */
    @Test
    void testRoundEndsIfOnlyOnePlayerHasCards() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Deck mockDeck = mock(Deck.class);
        when(mockDeck.hasMoreCards()).thenReturn(true); // Deck is not empty

        Player player1 = mock(Player.class);
        PlayerList players = new PlayerList();
        players.addPlayer(player1);
        PlayerList spyPlayers = spy(players);
        doReturn(player1).when(spyPlayers).getCurrentPlayer();

        doReturn(false, false, true).when(spyPlayers).checkForRoundWinner(); // Simulate only one player has cards after 2 turns
        doReturn(null, player1).when(spyPlayers).getGameWinner(); // No game winner initially

        Game game = new Game(null, null, new ByteArrayInputStream(new byte[0]));
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, mockDeck);

        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, spyPlayers);

        Game spyGame = spy(game);

        doNothing().when(spyGame).setupNewGame(); // Skip new game setup
        doNothing().when(spyGame).executeTurn(any(Player.class)); // Skip actual turn execution
        doNothing().when(spyGame).determineRoundWinner(); // Skip determine round winner

        int initialRound = spyGame.getRound();
        spyGame.startRound();;

        verify(spyPlayers, times(3)).checkForRoundWinner(); // Verify checkForRoundWinner was called
        verify(spyGame, times(1)).determineRoundWinner(); // Verify round ends when one player has cards
        verify(spyGame, times(2)).executeTurn(any(Player.class));
        assertTrue(spyGame.getRound() == initialRound + 1, "The round number should increase after the round ends.");  
    }

    /*
     * Integration Test: Verify that a round shall end if the deck is empty.
     */
    @Test
    void testRoundEndsIfDeckIsEmpty() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Mock Deck and stub hasMoreCards to simulate the deck-empty scenario
        Deck mockDeck = mock(Deck.class);
        doReturn(true, true, false).when(mockDeck).hasMoreCards();  // Simulate the deck becoming empty after 3 turns

        Player mockPlayer = mock(Player.class);
        PlayerList players = new PlayerList();
        players.addPlayer(mockPlayer); 
        PlayerList spyPlayers = spy(players);
        doReturn(false).when(spyPlayers).checkForRoundWinner();  // Simulate no winner during the round
        doReturn(null, mockPlayer).when(spyPlayers).getGameWinner();  // Return null for game winner initially
        doReturn(mockPlayer).when(spyPlayers).getCurrentPlayer();  // Always return the mocked player as the current player

        // Create a Game instance
        Game game = new Game(null, null, new ByteArrayInputStream(new byte[0]));
        // Set players and deck field with mocks using Reflection
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, mockDeck);
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, spyPlayers);

        // Spy on the Game instance and stub irrelevant methods
        Game spyGame = spy(game);
        doNothing().when(spyGame).setupNewGame();  // Skip new game setup
        doNothing().when(spyGame).executeTurn(any(Player.class));  // Skip actual turn execution
        doNothing().when(spyGame).determineRoundWinner();  // Stub round winner determination
       
        // Execute the round loop
        int initialRound = spyGame.getRound();
        spyGame.startRound();

        verify(mockDeck, times(3)).hasMoreCards();
        verify(spyGame, times(1)).determineRoundWinner();
        assertTrue(spyGame.getRound() == initialRound + 1, "The round number should increase after the round ends.");
    }
  
    /**
     * Tests that the deck is initialized with the correct number of each card type
     * according to Love Letter game rules:
     * - 5 Guard cards
     * - 2 each of Priest, Baron, Handmaiden, and Prince
     * - 1 each of King, Countess, and Princess
     * Total deck size should be 16 cards.
     */
    @Test
    void testInitializeDeckContainsCorrectCards() {
        
        Deck deck = new Deck();
        deck.build();
        
        Stack<Card> cards = deck.getDeck();
        
        // Count occurrences of each card type
        int guardCount = 0;
        int priestCount = 0;
        int baronCount = 0;
        int handmaidenCount = 0;
        int princeCount = 0;
        int kingCount = 0;
        int countessCount = 0;
        int princessCount = 0;
        
        for (Card card : cards) {
            switch (card) {
                case GUARD -> guardCount++;
                case PRIEST -> priestCount++;
                case BARON -> baronCount++;
                case HANDMAIDEN -> handmaidenCount++;
                case PRINCE -> princeCount++;
                case KING -> kingCount++;
                case COUNTESS -> countessCount++;
                case PRINCESS -> princessCount++;
            }
        }
        
        // Verify correct number of each card
        assertEquals(5, guardCount, "Should have 5 Guard cards");
        assertEquals(2, priestCount, "Should have 2 Priest cards");
        assertEquals(2, baronCount, "Should have 2 Baron cards");
        assertEquals(2, handmaidenCount, "Should have 2 Handmaiden cards");
        assertEquals(2, princeCount, "Should have 2 Prince cards");
        assertEquals(1, kingCount, "Should have 1 King card");
        assertEquals(1, countessCount, "Should have 1 Countess card");
        assertEquals(1, princessCount, "Should have 1 Princess card");
        
        // Verify total deck size
        assertEquals(16, cards.size(), "Deck should contain 16 cards total");
    }

    /**
     * Verifies that the shuffle method is called exactly once when initializing
     * the deck. Uses reflection to inject a spy deck to track the method call,
     * ensuring the deck is properly shuffled during game setup.
     * 
     * @throws NoSuchFieldException if the deck field doesn't exist in Game class
     * @throws IllegalAccessException if the deck field cannot be accessed
     */
    @Test
    void testInitializeDeckCallsShuffle() throws NoSuchFieldException, IllegalAccessException {
        // Create a spy deck to verify the shuffle method is called
        Deck spyDeck = spy(new Deck());
        
        // Create game with any deck (it will be replaced)
        Game game = new Game(new PlayerList(), new Deck(), System.in);
        
        // Use reflection to set our spy deck
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, spyDeck);
        
        // Call initializeDeck
        game.initializeDeck();
        
        // Verify that shuffle was called
        verify(spyDeck, times(1)).shuffle();
    }

    /*
     * Verify that player with the highest number in hand wins the round
     * when the round ends with more than one player having cards.
     */
    @Test
    void testDetermineRoundWinnerBasedOnHandCards() {
        PlayerList players = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);

        player1.addCard(Card.PRINCE); // Value = 5
        player2.addCard(Card.PRINCESS); // Value = 8

        players.addPlayer(player1);
        players.addPlayer(player2);

        Game game = new Game(players, null, new ByteArrayInputStream(new byte[0]));
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        game.determineRoundWinner();

        assertEquals(0, player1.getTokens(), "Player 1 should not win the round.");
        assertEquals(1, player2.getTokens(), "Player 2 should win the round with the Princess card.");
        assertTrue(outContent.toString().contains("Player 2 has won this round!"));
    }

    /**
     * Tests the complete action of playing a card and applying its effect.
     */
    @Test
    public void testExecuteTurnWithHandmaiden() throws NoSuchFieldException, IllegalAccessException {
        // Create 2 players
        Player player1 = new Player("Player1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player2", new Hand(), new DiscardPile(), false, 0);
        
        // Create and populate PlayerList
        PlayerList players = new PlayerList();
        players.addPlayer(player1);
        players.addPlayer(player2);
        
        // Create custom deck using Stack
        Stack<Card> cards = new Stack<>();
        cards.push(Card.HANDMAIDEN); // Will be drawn by Player 1
        cards.push(Card.PRIEST);     // Player 2's initial card
        cards.push(Card.GUARD);      // Player 1's initial card

        // Create spy deck and set the custom deck
        Deck spyDeck = spy(new Deck());
        spyDeck.setDeck(cards);

        // Custom input to simulate selecting Handmaiden (index 1)
        String simulatedInput = "1\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        Game game = new Game(players, spyDeck, inputStream);

        // Use reflection to set our spy deck
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, spyDeck);
        
        // Reset players and deal cards
        players.dealCards(spyDeck);

        // Execute player1's turn
        game.executeTurn(player1);
        
        // Verify player1 is protected
        assertTrue(player1.getIsProtected());

        // Verify Handmaiden is in player1's discard pile
        assertTrue(player1.getDiscarded().getCards().contains(Card.HANDMAIDEN), "Handmaiden should be in player1's discard pile");

        // Verify Guard is still in Player 1's hand
        assertTrue(player1.getHand().getHand().contains(Card.GUARD), "Guard should still be in player1's hand");
    }
}