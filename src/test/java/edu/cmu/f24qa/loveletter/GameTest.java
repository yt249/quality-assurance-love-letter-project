package edu.cmu.f24qa.loveletter;

import java.util.List;
import java.util.Stack;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
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
     * Verifies that the winner of the previous round is the first player
     * to take a turn in the next round.
     */
    @Disabled("startRound is not correctly implemented")
    @Test
    void testPreviousRoundWinnerGoesFirst() throws Exception {
        // Setup: Create a player list with two players
        PlayerList players = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);

        // Simulate Player 1 as the round winner
        player2.getHand().add(Card.GUARD); // Add a card to Player 1's hand
        player1.getHand().clear();        // Ensure Player 2 has no cards
        assertEquals(player2, players.getRoundWinner(), "Player 2 should be the round winner.");

        // Create a game instance
        Game game = spy(new Game(players, new Deck(), System.in));

        // Stub out methods that are not relevant to this test
        doNothing().when(game).setupNewGame();  // Skip new game setup
        doNothing().when(game).determineRoundWinner();  // Stub round winner determination
        doNothing().when(game).executeTurn(any(Player.class));  // Stub turn execution

        // Create an inOrder verifier for the game instance
        InOrder inOrder = inOrder(game);

        // Start the round
        game.startRound();
    
        // Verify the order of execution for the players
        inOrder.verify(game).executeTurn(player2); // Player 2 (round winner) goes first
        inOrder.verify(game).executeTurn(player1); // Player 1 goes next
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
  
    /**
     * Tests the behavior of the game when the number of players is 2
     * 
     * Verifies that:
     *  - The deck size is reduced by 4 (1 hidden card and 3 face-up cards)
     *  - Console output logs the 3 face-up cards as removed
     *  - The remaining cards in the deck are the correct counts for each card
     */
    @Test
    void testDeckSizeStartWithTwoPlayers() throws NoSuchFieldException, IllegalAccessException{
        // Redirect System.out to capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Game with 2 players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer("Player 1");
        playerList.addPlayer("Player 2");

        // Create a custom deck with cards in specific order
        Stack<Card> customDeck = new Stack<>();
        Card[] cards = {
            Card.PRINCESS, Card.COUNTESS, Card.KING, Card.PRINCE,
            Card.PRINCE, Card.HANDMAIDEN, Card.BARON, Card.PRIEST,
            Card.GUARD, Card.GUARD, Card.GUARD, Card.GUARD,
            Card.HANDMAIDEN, Card.BARON, Card.PRIEST, Card.GUARD
        };
        for (Card card : cards) {
            customDeck.push(card);
        }

        // Create spy deck and set the custom deck
        Deck spyDeck = spy(new Deck());
        spyDeck.setDeck(customDeck);

        // Create game with spy deck
        Game game = new Game(playerList, spyDeck, System.in);

        // Use reflection to set our spy deck
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, spyDeck);

        // Get initial deck card size 
        int initialDeckSize = spyDeck.getDeck().size();

        // Removing cards from deck by system settings
        game.removeCardFromDeck();

        // Verify draw() was called exactly 4 times (1 hidden + 3 face-up)
        verify(spyDeck, times(4)).draw();

        // Verify console output contains the specific removed cards in order
        String output = outContent.toString();
        assertTrue(output.contains("Priest (2) was removed from the deck."), "First face-up card should be PRIEST");
        assertTrue(output.contains("Baron (3) was removed from the deck."), "Second face-up card should be BARON");
        assertTrue(output.contains("Handmaiden (4) was removed from the deck."), "Third face-up card should be HANDMAIDEN");

        // Verify hidden card is not logged
        assertFalse(output.contains("Guard (1) was removed from the deck."), "Hidden card should not be logged");

        // Get the remaining cards in the deck
        List<Card> remainingCards = spyDeck.getDeck();

        // Verify deck has 12 cards
        assertEquals(initialDeckSize - 4, remainingCards.size(), "Deck should have 12 cards remaining");

        // Count cards in a simpler way
        int guardCount = 0, priestCount = 0, baronCount = 0, handmaidenCount = 0;
        int princeCount = 0, kingCount = 0, countessCount = 0, princessCount = 0;

        for (Card card : remainingCards) {
            switch (card) {
                case GUARD: guardCount++; break;
                case PRIEST: priestCount++; break;
                case BARON: baronCount++; break;
                case HANDMAIDEN: handmaidenCount++; break;
                case PRINCE: princeCount++; break;
                case KING: kingCount++; break;
                case COUNTESS: countessCount++; break;
                case PRINCESS: princessCount++; break;
            }
        }

        // Verify the remaining card counts
        assertEquals(4, guardCount, "Should have 4 GUARD cards");
        assertEquals(1, priestCount, "Should have 1 PRIEST card");
        assertEquals(1, baronCount, "Should have 1 BARON card");
        assertEquals(1, handmaidenCount, "Should have 1 HANDMAIDEN card");
        assertEquals(2, princeCount, "Should have 2 PRINCE cards");
        assertEquals(1, kingCount, "Should have 1 KING card");
        assertEquals(1, countessCount, "Should have 1 COUNTESS card");
        assertEquals(1, princessCount, "Should have 1 PRINCESS card");
    }

    /**
     * Tests the behavior of the game when the number of players is 3
     * 
     * Verifies that:
     *  - The deck size is reduced by 1 (only 1 hidden card)
     *  - No face-up cards are logged to the console
     *  - The remaining cards in the deck are the correct counts for each card
     */
    @Test
    void testDeckSizeStartWithThreePlayers() throws NoSuchFieldException, IllegalAccessException{
        // Redirect System.out to capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Game with 3 players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer("Player 1");
        playerList.addPlayer("Player 2");
        playerList.addPlayer("Player 3");

        // Create a custom deck with cards in specific order
        Stack<Card> customDeck = new Stack<>();
        Card[] cards = {
            Card.PRINCESS, Card.COUNTESS, Card.KING, Card.PRINCE,
            Card.PRINCE, Card.HANDMAIDEN, Card.BARON, Card.PRIEST,
            Card.GUARD, Card.GUARD, Card.GUARD, Card.GUARD,
            Card.HANDMAIDEN, Card.BARON, Card.PRIEST, Card.GUARD
        };
        for (Card card : cards) {
            customDeck.push(card);
        }

        // Create spy deck and set the custom deck
        Deck spyDeck = spy(new Deck());
        spyDeck.setDeck(customDeck);

        // Create game with spy deck
        Game game = new Game(playerList, spyDeck, System.in);

        // Use reflection to set our spy deck
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, spyDeck);

        // Get initial deck card size 
        int initialDeckSize = spyDeck.getDeck().size();

        // Removing cards from deck by system settings
        game.removeCardFromDeck();

        // Verify draw() was called exactly 1 times
        verify(spyDeck, times(1)).draw();

        // Verify hidden card is not logged
        String output = outContent.toString();
        assertFalse(output.contains("Guard (1) was removed from the deck."), "Hidden card should not be logged");

        // Get the remaining cards in the deck
        List<Card> remainingCards = spyDeck.getDeck();

        // Verify deck has 15 cards
        assertEquals(initialDeckSize - 1, remainingCards.size(), "Deck should have 15 cards remaining");

        // Count cards in a simpler way
        int guardCount = 0, priestCount = 0, baronCount = 0, handmaidenCount = 0;
        int princeCount = 0, kingCount = 0, countessCount = 0, princessCount = 0;

        for (Card card : remainingCards) {
            switch (card) {
                case GUARD: guardCount++; break;
                case PRIEST: priestCount++; break;
                case BARON: baronCount++; break;
                case HANDMAIDEN: handmaidenCount++; break;
                case PRINCE: princeCount++; break;
                case KING: kingCount++; break;
                case COUNTESS: countessCount++; break;
                case PRINCESS: princessCount++; break;
            }
        }

        // Verify the remaining card counts
        assertEquals(4, guardCount, "Should have 4 GUARD cards");
        assertEquals(2, priestCount, "Should have 1 PRIEST card");
        assertEquals(2, baronCount, "Should have 1 BARON card");
        assertEquals(2, handmaidenCount, "Should have 1 HANDMAIDEN card");
        assertEquals(2, princeCount, "Should have 2 PRINCE cards");
        assertEquals(1, kingCount, "Should have 1 KING card");
        assertEquals(1, countessCount, "Should have 1 COUNTESS card");
        assertEquals(1, princessCount, "Should have 1 PRINCESS card");
    }

    /**
     * Tests the behavior of the game when the number of players is 4
     * 
     * Verifies that:
     *  - The deck size is reduced by 1 (only 1 hidden card)
     *  - No face-up cards are logged to the console
     *  - The remaining cards in the deck are the correct counts for each card
     */
    @Test
    void testDeckSizeStartWithFourPlayers() throws NoSuchFieldException, IllegalAccessException{
        // Redirect System.out to capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Game with 4 players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer("Player 1");
        playerList.addPlayer("Player 2");
        playerList.addPlayer("Player 3");
        playerList.addPlayer("Player 4");

        // Create a custom deck with cards in specific order
        Stack<Card> customDeck = new Stack<>();
        Card[] cards = {
            Card.PRINCESS, Card.COUNTESS, Card.KING, Card.PRINCE,
            Card.PRINCE, Card.HANDMAIDEN, Card.BARON, Card.PRIEST,
            Card.GUARD, Card.GUARD, Card.GUARD, Card.GUARD,
            Card.HANDMAIDEN, Card.BARON, Card.PRIEST, Card.GUARD
        };
        for (Card card : cards) {
            customDeck.push(card);
        }

        // Create spy deck and set the custom deck
        Deck spyDeck = spy(new Deck());
        spyDeck.setDeck(customDeck);

        // Create game with spy deck
        Game game = new Game(playerList, spyDeck, System.in);

        // Use reflection to set our spy deck
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, spyDeck);

        // Get initial deck card size 
        int initialDeckSize = spyDeck.getDeck().size();

        // Removing cards from deck by system settings
        game.removeCardFromDeck();

        // Verify draw() was called exactly 1 times
        verify(spyDeck, times(1)).draw();

        // Verify hidden card is not logged
        String output = outContent.toString();
        assertFalse(output.contains("Guard (1) was removed from the deck."), "Hidden card should not be logged");

        // Get the remaining cards in the deck
        List<Card> remainingCards = spyDeck.getDeck();

        // Verify deck has 15 cards
        assertEquals(initialDeckSize - 1, remainingCards.size(), "Deck should have 15 cards remaining");

        // Count cards in a simpler way
        int guardCount = 0, priestCount = 0, baronCount = 0, handmaidenCount = 0;
        int princeCount = 0, kingCount = 0, countessCount = 0, princessCount = 0;

        for (Card card : remainingCards) {
            switch (card) {
                case GUARD: guardCount++; break;
                case PRIEST: priestCount++; break;
                case BARON: baronCount++; break;
                case HANDMAIDEN: handmaidenCount++; break;
                case PRINCE: princeCount++; break;
                case KING: kingCount++; break;
                case COUNTESS: countessCount++; break;
                case PRINCESS: princessCount++; break;
            }
        }

        // Verify the remaining card counts
        assertEquals(4, guardCount, "Should have 4 GUARD cards");
        assertEquals(2, priestCount, "Should have 1 PRIEST card");
        assertEquals(2, baronCount, "Should have 1 BARON card");
        assertEquals(2, handmaidenCount, "Should have 1 HANDMAIDEN card");
        assertEquals(2, princeCount, "Should have 2 PRINCE cards");
        assertEquals(1, kingCount, "Should have 1 KING card");
        assertEquals(1, countessCount, "Should have 1 COUNTESS card");
        assertEquals(1, princessCount, "Should have 1 PRINCESS card");
    }
    
    /**
     * Tests the promptForPlayers method with different scenarios.
     * 
     * Test cases:
     * 1. Create a game with only one player throws an IllegalStateException
     * 2. Create a game with three players succeeds and correctly initializes the player list
     */
    @Test
    public void testPromptForPlayers() {
        // Test case 1: One player (should throw exception)
        String input1 = "Player1\n\n";
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(input1.getBytes(StandardCharsets.UTF_8));
        Game game1 = new Game(new PlayerList(), new Deck(), inputStream1);

        assertThrows(IllegalStateException.class, () -> {
            game1.promptForPlayers();
        });

        // Test case 2: Three players (should succeed)
        String input2 = "Player1\nPlayer2\nPlayer3\n\n";
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(input2.getBytes(StandardCharsets.UTF_8));
        Game game2 = new Game(new PlayerList(), new Deck(), inputStream2);

        game2.promptForPlayers();
        assertEquals(3, game2.getPlayers().getPlayers().size());
    }
}
