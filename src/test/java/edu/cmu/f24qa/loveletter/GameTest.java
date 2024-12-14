package edu.cmu.f24qa.loveletter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.MockedConstruction;

import edu.cmu.f24qa.loveletter.actions.JesterAction;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
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
        doReturn(new ArrayList<>()).when(spyPlayers).getGameWinner(); // No game winner initially

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
        doReturn(new ArrayList<>()).when(spyPlayers).getGameWinner();  // Return null for game winner initially
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
     * according to Love Letter game rules for 2-4 players:
     * - 5 Guard cards
     * - 2 each of Priest, Baron, Handmaiden, and Prince
     * - 1 each of King, Countess, and Princess
     * Total deck size should be 16 cards.
     */
    @Test
    void testInitializeDeckContainsCorrect16Cards() {
        // Create a new deck
        Deck deck = new Deck();
        deck.build16Cards();
        
        // Get the deck
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
                case GUARD : guardCount++; break;
                case PRIEST : priestCount++; break;
                case BARON : baronCount++; break;
                case HANDMAIDEN : handmaidenCount++; break;
                case PRINCE : princeCount++; break;
                case KING : kingCount++; break;
                case COUNTESS : countessCount++; break;
                case PRINCESS : princessCount++; break;
                default: break;
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
     * Tests that the deck is initialized with the correct number of each card type
     * according to Love Letter game rules for 5-8 players:
     * - 8 Guard cards
     * - 2 each of Priest, Baron, Handmaiden, Prince, Cardinal, Baroness, Sycophant, and Count
     * - 1 each of King, Countess, Princess, Jester, Assassin, Constable, Queen, and Bishop
     * Total deck size should be 32 cards.
     */
    @Test
    void testInitializeDeckContainsCorrect32Cards() {
        // Create a new deck
        Deck deck = new Deck();
        deck.build32Cards();
        
        // Get the deck
        Stack<Card> cards = deck.getDeck();
        
        // Count occurrences of each card type
        int guardCount = 0;
        int priestCount = 0;
        int baronCount = 0;
        int handmaidenCount = 0;
        int princeCount = 0;
        int cardinalCount = 0;
        int baronessCount = 0;
        int sycophantCount = 0;
        int countCount = 0;
        int kingCount = 0;
        int countessCount = 0;
        int princessCount = 0;
        int jesterCount = 0;
        int assassinCount = 0;
        int constableCount = 0;
        int queenCount = 0;
        int bishopCount = 0;
        
        for (Card card : cards) {
            switch (card) {
                case GUARD : guardCount++; break;
                case PRIEST : priestCount++; break;
                case BARON : baronCount++; break;
                case HANDMAIDEN : handmaidenCount++; break;
                case PRINCE : princeCount++; break;
                case CARDINAL : cardinalCount++; break;
                case BARONESS : baronessCount++; break;
                case SYCOPHANT : sycophantCount++; break;
                case COUNT : countCount++; break;
                case KING : kingCount++; break;
                case COUNTESS : countessCount++; break;
                case PRINCESS : princessCount++; break;
                case JESTER : jesterCount++; break;
                case ASSASSIN : assassinCount++; break;
                case CONSTABLE : constableCount++; break;
                case QUEEN : queenCount++; break;
                case BISHOP : bishopCount++; break;
                default: break;
            }
        }
        
        // Verify correct number of each card
        assertEquals(8, guardCount, "Should have 8 Guard cards");
        assertEquals(2, priestCount, "Should have 2 Priest cards");
        assertEquals(2, baronCount, "Should have 2 Baron cards");
        assertEquals(2, handmaidenCount, "Should have 2 Handmaiden cards");
        assertEquals(2, princeCount, "Should have 2 Prince cards");
        assertEquals(2, cardinalCount, "Should have 2 Cardinal cards");
        assertEquals(2, baronessCount, "Should have 2 Baroness cards");
        assertEquals(2, sycophantCount, "Should have 2 Sycophant cards");
        assertEquals(2, countCount, "Should have 2 Count cards");
        assertEquals(1, kingCount, "Should have 1 King card");
        assertEquals(1, countessCount, "Should have 1 Countess card");
        assertEquals(1, princessCount, "Should have 1 Princess card");
        assertEquals(1, jesterCount, "Should have 1 Jester card");
        assertEquals(1, assassinCount, "Should have 1 Assassin card");
        assertEquals(1, constableCount, "Should have 1 Constable card");
        assertEquals(1, queenCount, "Should have 1 Queen card");
        assertEquals(1, bishopCount, "Should have 1 Bishop card");
        
        // Verify total deck size
        assertEquals(32, cards.size(), "Deck should contain 32 cards total");
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

        // Create a player list with two players
        PlayerList players = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);
        
        // Create game with any deck (it will be replaced)
        Game game = new Game(players, spyDeck, System.in);

        // Use reflection to set our spy deck
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, spyDeck);

        // Call initializeDeck
        game.setupNewGame();
        
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
     * 
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Test
    void testPreviousRoundWinnerGoesFirst() throws NoSuchFieldException, IllegalAccessException {
        // Setup: Create a player list with two players
        PlayerList players = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);
        // add cards to players' hands
        player1.getHand().add(Card.BARON);
        player2.getHand().add(Card.GUARD);

        PlayerList spyPlayers = spy(players);

        // Mock the Deck
        Deck mockDeck = mock(Deck.class);

        // Mock the scenario where deck becomes empty after two turns
        when(mockDeck.hasMoreCards()).thenReturn(true, true, false);

        // Create a game instance
        Game game = new Game(null, null, new ByteArrayInputStream(new byte[0])); // Use a dummy deck for now

        // Use reflection to inject the mocked deck and players
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true); // Allow access to the private field
        deckField.set(game, mockDeck); // Inject the mocked deck into the game
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, spyPlayers);
        // simulate the second round of a game
        Field roundField = Game.class.getDeclaredField("round");
        roundField.setAccessible(true);
        roundField.setInt(game, 1);
        // Simulate Player 2 as the round winner of last round
        Field lastRoundWinnerField = Game.class.getDeclaredField("lastRoundWinners");
        lastRoundWinnerField.setAccessible(true);
        lastRoundWinnerField.set(game, new ArrayList<>(Arrays.asList(player2)));

        // Stub out irrelevant methods
        Game spyGame = spy(game);
        doNothing().when(spyGame).executeTurn(any(Player.class));
        doNothing().when(spyGame).setupNewGame(); // Skip new game setup
        doNothing().when(spyGame).determineRoundWinner(); // Skip round winner determination

        // Verify the execution order of turns
        InOrder inOrder = inOrder(spyGame);

        // Start the round
        spyGame.startRound();

        // Verify Player 2 (last round's winner) goes first, followed by Player 1
        verify(mockDeck, times(3)).hasMoreCards();
        verify(spyPlayers, times(3)).checkForRoundWinner();
        verify(spyGame, times(2)).executeTurn(any(Player.class));
        inOrder.verify(spyGame).executeTurn(player2); // Player 2 (round winner) goes first
        inOrder.verify(spyGame).executeTurn(player1); // Player 1 goes next
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
        cards.push(Card.PRIEST); // Player 2's initial card
        cards.push(Card.GUARD); // Player 1's initial card

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
     * Verifies that a card requiring a target is discarded without effect
     * if no eligible players can be chosen due to effects such as Handmaid.
     */
    @Test
    void testPlayTurnCardWhenNoEligiblePlayers() throws Exception {
        // Setup: Create a player list with two players
        PlayerList players = new PlayerList();
        Player player1 = spy(new Player("Player 1", new Hand(), new DiscardPile(), false, 0));

        // Create a spy for Player 2's Hand
        Hand player2Hand = spy(new Hand());
        Player player2 = spy(new Player("Player 2", player2Hand, new DiscardPile(), true, 0)); // Protected by Handmaid
        players.addPlayer(player1);
        players.addPlayer(player2);

        // Give Player 1 a card requiring a target (e.g., Guard)
        player1.getHand().add(Card.GUARD);

        // Set Player 2's hand with a specific card (e.g., Priest)
        player2.getHand().add(Card.PRIEST);

        // Simulate input to select the Guard card (index 0 in hand)
        String simulatedInput = "0\nPRIEST\n"; // Simulate user input to play the first card
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());

        // Create a custom deck (not used in this test, but required for Game instance)
        Deck customDeck = new Deck();

        // Create a game instance
        Game game = spy(new Game(players, customDeck, inputStream));

        // Capture console output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Directly test `playTurnCard` with Player 1
        game.playTurnCard(player1);

        // Verify console output
        String output = outputStream.toString();
        assertTrue(output.contains("Not enough available players can be selected to satisfy the requirement of targeting at least 1 player(s)."), 
            "Should show message about all opponents being protected");

        // Verify that the card was discarded
        assertTrue(player1.getHand().getHand().isEmpty(), "Player 1's hand should be empty after playing the card.");
        assertEquals(Card.GUARD, player1.getDiscarded().getCards().get(0), "Player 1's discard pile should contain the Guard card.");

        // Verify that Player 2's hand remains intact
        assertEquals(Card.PRIEST, player2.getHand().peek(0), "Player 2 should still have the Priest card.");

        // Verify that Player 2 was not eliminated
        assertFalse(player2.isEliminated(), "Player 2 should not be eliminated even if their card was guessed correctly.");

        // Verify that peek() was never called on Player 2's hand
        verify(player2Hand, never()).peek(anyInt());
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

    /**
     * Verifies that in case of a tie, players sum the values of their discard piles
     * to determine the winner; if a tie persists, all tied players are considered winners.
     */
    @Test
    void testDetermineRoundWinnerWithDifferentDiscardedPileValues() 
            throws NoSuchFieldException, IllegalAccessException {
        // Setup: Create a player list with two players
        PlayerList players = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);
    
        // Add identical cards to players' hands to simulate a tie on hand comparison
        player1.getHand().add(Card.GUARD);
        player2.getHand().add(Card.GUARD);

        // Tiebreaker resolved with different discard pile values
        player1.addCardToDiscarded(Card.KING); // Higher discard pile value
        player2.addCardToDiscarded(Card.PRINCE);
        
        Game game = new Game(null, null, new ByteArrayInputStream(new byte[0]));

        // Inject the mocked players
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        // Execute the method under test
        game.determineRoundWinner();

        // Verify the correct winner is returned by getLastRoundWinners
        List<Player> lastRoundWinners = game.getLastRoundWinners();
        assertEquals(1, lastRoundWinners.size()); // Verify only one winner
        assertEquals(player1, lastRoundWinners.get(0)); // Verify Player 1 is the winner
    } 

    /**
     * Verifies that in case of a tie, players sum the values of their discard piles
     * to determine the winner; if a tie persists, all tied players are considered winners.
     */
    @Test
    void testDetermineRoundWinnerWithIdenticalDiscardedPileValues() 
            throws NoSuchFieldException, IllegalAccessException {
        // Setup: Create a player list with two players
        PlayerList players = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);
    
        // Add identical cards to players' hands to simulate a tie on hand comparison
        player1.getHand().add(Card.GUARD);
        player2.getHand().add(Card.GUARD);

        // Tiebreaker unresolved with identical discard pile values
        player1.addCardToDiscarded(Card.PRINCE); // Same discard pile value
        player2.addCardToDiscarded(Card.PRINCE);

        Game game = new Game(null, null, new ByteArrayInputStream(new byte[0]));
        
        // Inject the mocked players
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, players);

        // Execute the method under test
        game.determineRoundWinner();

        // Verify all players are considered winners
        List<Player> lastRoundWinners = game.getLastRoundWinners();
        assertEquals(2, lastRoundWinners.size()); // Verify two winners
        assertTrue(lastRoundWinners.contains(player1)); // Verify Player 1 is a winner
        assertTrue(lastRoundWinners.contains(player2)); // Verify Player 2 is a winner
    } 

    /**
     * Tests that a token is added to the player if the guess is correct.
     *
     * Tests that the Sycophant's effect is properly reset after the next player's turn.
     * 
     * Test sequence:
     * 1. Player 1 (Alice) plays Sycophant and selects a target
     * 2. Player 2 (Bob) plays their turn with a Guard card
     * 3. Verify the Sycophant's forced target is reset to null
     */
    @Test
    void testSycophantForcedPlayerResetAfterNextPlayerCard() throws NoSuchFieldException, IllegalAccessException {
        // Setup players
        PlayerList players = new PlayerList();
        Player player1 = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);
        
        // Simulate input for both players' turns
        // Player 1: "0" to select Sycophant, "Bob" to select Bob as target
        // Player 2: "0" to select Handmaiden
        String simulatedInput = "0\nBob\n0\n";
        Readable inputReader = new StringReader(simulatedInput);
        GameContext context = new GameContext(players, new Deck(), inputReader);
        GameContext spyContext = spy(context);
        
        // Create game and inject context using reflection
        Game game = new Game(players, new Deck(), System.in);
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(game, spyContext);
        
        // Add Sycophant to player1's hand and Handmaiden to player2's hand
        player1.addCard(Card.SYCOPHANT);
        player2.addCard(Card.HANDMAIDEN);
        
        // Set player1 as current player and play Sycophant
        when(spyContext.getCurrentUser()).thenReturn(player1);
        game.playTurnCard(player1);
        
        // Verify Sycophant effect was set
        verify(spyContext).setSycophantForcedPlayer(any(Player.class));
        
        // Now player2 plays their Guard card
        when(spyContext.getCurrentUser()).thenReturn(player2);
        game.playTurnCard(player2);
        
        // Verify Sycophant effect was reset
        verify(spyContext, times(1)).resetSycophantForcedPlayer();
    }
  
    @Test
    /**
     * Tests that a token is added to the player if the Jester's guess is correct.
     *
     * This test simulates a scenario where a player uses the Jester card to guess
     * another player's card. If the guess is correct, the player should receive an
     * additional token. The test verifies that the correct player is identified as
     * the guessed player and that the Jester player receives the token.
     */
    void testDetermineRoundWinnerTokenAddedIfJesterGuessCorrect() {
        Player player = new Player("Player1", new Hand(), new DiscardPile(), false, 0);
        Player opponent = new Player("Opponent", new Hand(), new DiscardPile(), false, 0);

        player.addCard(Card.GUARD);
        opponent.addCard(Card.PRINCESS);

        PlayerList playerList = new PlayerList();
        playerList.addPlayer(player);
        playerList.addPlayer(opponent);

        Game game = new Game(playerList, new Deck(), new ByteArrayInputStream(new byte[0]));
     
        // Simulate JesterAction execution
        GameContext context = spy(new GameContext(playerList, new Deck(), new InputStreamReader(new ByteArrayInputStream(new byte[0]))));

        // Use reflection to set the private context and playerList fields
        try {
            Field contextField = Game.class.getDeclaredField("context");
            contextField.setAccessible(true);
            contextField.set(game, context);

            Field playerListField = Game.class.getDeclaredField("players");
            playerListField.setAccessible(true);
            playerListField.set(game, playerList);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Mock context behavior
        doReturn(player).when(context).getCurrentUser();
        doReturn(Arrays.asList(opponent)).when(context).selectOpponents(1,1,true);
   
        JesterAction action = new JesterAction();
        action.execute(context);

        // Simulate the correct guess scenario
        game.determineRoundWinner();

        // Verify that the guessed player and jester player are set correctly
        assertEquals(opponent, context.getGuessedPlayer());
        assertEquals(player, context.getJesterPlayer());

        // Verify that an extra token is added to the player if the guess is correct
        assertEquals(1, player.getTokens());
    }

    @Test
    void testStartRoundExitsWhenPlayerWinsGame() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        // Setup
        PlayerList playerList = new PlayerList();
        Deck deck = mock(Deck.class);
        // Input for Bishop action: select Bob, guess 1
        String simulatedInput = "1\nBob\n";
        InputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        
        // Add players with initial cards and tokens
        Player player1 = new Player("Alice", new Hand(), new DiscardPile(), false, 3); // 3 tokens, needs 1 more to win
        Player player2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        Player player3 = new Player("Susan", new Hand(), new DiscardPile(), false, 0);
        Player player4 = new Player("Tom", new Hand(), new DiscardPile(), false, 0);
        Player player5 = new Player("John", new Hand(), new DiscardPile(), false, 0);
        player1.addCard(Card.BISHOP);  // Alice has Bishop to make a guess
        player2.addCard(Card.GUARD);   // Bob has Guard that Alice will guess
        player3.addCard(Card.GUARD);
        player4.addCard(Card.GUARD);
        player5.addCard(Card.GUARD);
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);
        playerList.addPlayer(player3);
        playerList.addPlayer(player4);
        playerList.addPlayer(player5);
        
        // Mock deck behavior
        when(deck.hasMoreCards()).thenReturn(true); // Ensure loop continues
        
        // Mock player list behavior
        PlayerList spyPlayerList = spy(playerList);
        when(spyPlayerList.getCurrentPlayer()).thenReturn(player1);
        
        // Create game with spied playerList
        Game game = new Game(null, deck, inputStream);
        // Replace Game's playerList
        Field playersField = Game.class.getDeclaredField("players");
        playersField.setAccessible(true);
        playersField.set(game, spyPlayerList);

        Field gameDeckField = Game.class.getDeclaredField("deck");
        gameDeckField.setAccessible(true);
        gameDeckField.set(game, deck);
        
        // Replace GameContext's playerList
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        GameContext context = (GameContext) contextField.get(game);
        
        Field contextPlayersField = GameContext.class.getDeclaredField("players");
        contextPlayersField.setAccessible(true);
        contextPlayersField.set(context, spyPlayerList);

        Game spyGame = spy(game);
        // Skip setupNewGame to preserve hands
        doNothing().when(spyGame).setupNewGame();
        doReturn(0).when(spyGame).getCardIdx(player1);
        
        // Execute
        spyGame.startRound();

        // Verify
        assertEquals(0, spyGame.getRound()); // Round counter should not increment
        assertEquals(4, player1.getTokens(), "Player 1 should have won token from Bishop guess");
        List<Player> winners = spyPlayerList.getGameWinner();
        assertEquals(1, winners.size());
        assertEquals(player1, winners.get(0));
    }

    @Test
    public void testTieBreakingRoundCalled() {
        // Setup
        PlayerList playerList = new PlayerList();
        Player player1 = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);
        PlayerList spyPlayerList = spy(playerList);
        Deck mockDeck = mock(Deck.class);
        InputStream mockInput = new ByteArrayInputStream("".getBytes());
        
        // Create game with spied instance to verify method calls
        Game gameSpy = spy(new Game(null, mockDeck, mockInput));
        
        // Use reflection to set the players field
        try {
            Field playersField = Game.class.getDeclaredField("players");
            playersField.setAccessible(true);
            playersField.set(gameSpy, spyPlayerList);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set players field using reflection: " + e.getMessage());
        }
        
        // Setup mock behaviors
        List<Player> tiedWinners = Arrays.asList(player1, player2);
        List<Player> finalWinner = Arrays.asList(player1);
        
        when(spyPlayerList.getGameWinner()).thenReturn(tiedWinners);  // Second call after while loop
        
        doReturn(finalWinner).when(gameSpy).startRoundForTiedWinners(tiedWinners);
        
        // Execute
        gameSpy.start();
        
        // Verify
        verify(gameSpy, times(1)).startRoundForTiedWinners(tiedWinners);
        verify(gameSpy, times(1)).announceGameWinner(finalWinner);
    }

    @Test
    public void testAnnounceGameWinnerSingleWinner() {
        // Arrange
        Game game = new Game(new PlayerList(), new Deck(), new ByteArrayInputStream(new byte[0]));
        List<Player> winners = new ArrayList<>();
        winners.add(new Player("Alice", new Hand(), new DiscardPile(), false, 0));
        
        // Act & Assert
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        game.announceGameWinner(winners);
        
        assertEquals("Alice has won the game and the heart of the princess!\n", outContent.toString());
    }

    @Test
    public void testAnnounceGameWinnerNoWinners() {
        // Arrange
        Game game = new Game(new PlayerList(), new Deck(), new ByteArrayInputStream(new byte[0]));
        List<Player> winners = new ArrayList<>();
        
        // Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> game.announceGameWinner(winners)
        );
        assertEquals("There is no winner in the game", exception.getMessage());
    }

    @Test
    public void testAnnounceGameWinnerMultipleWinners() {
        // Arrange
        Game game = new Game(new PlayerList(), new Deck(), new ByteArrayInputStream(new byte[0]));
        List<Player> winners = new ArrayList<>();
        winners.add(new Player("Alice", new Hand(), new DiscardPile(), false, 0));
        winners.add(new Player("Bob", new Hand(), new DiscardPile(), false, 0));
        
        // Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> game.announceGameWinner(winners)
        );
        assertEquals("There are multiple winners in the game", exception.getMessage());
    }

    @Test
    void testStartRoundForTiedWinnersNoSecondTie() {
        Player winner1 = new Player("winner1", new Hand(), new DiscardPile(), false, 7);
        Player winner2 = new Player("winner2", new Hand(), new DiscardPile(), false, 7);
        List<Player> winners = List.of(winner1, winner2);
        Game firstGame = new Game(mock(PlayerList.class), mock(Deck.class), new ByteArrayInputStream(new byte[0]));

        try(MockedConstruction<Game> mockedConstruction = mockConstruction(Game.class, (mock, context) -> {
            doNothing().when(mock).startRound();
            doReturn(List.of(winner2)).when(mock).getLastRoundWinners();
        })) {
            List<Player> finalWinners = firstGame.startRoundForTiedWinners(winners);

            assertEquals(1, finalWinners.size());
            assertEquals(winner2, finalWinners.get(0));
        }
    }

    @Test
    void testStartRoundForTiedWinnersStartThirdGameForSecondTieGame() {
        Player winner1 = new Player("winner1", new Hand(), new DiscardPile(), false, 7);
        Player winner2 = new Player("winner2", new Hand(), new DiscardPile(), false, 7);
        List<Player> winners = List.of(winner1, winner2);
        Game firstGame = new Game(mock(PlayerList.class), mock(Deck.class), new ByteArrayInputStream(new byte[0]));
        try(MockedConstruction<Game> mockedSecondGameConstruction = mockConstruction(Game.class, (mockSecondGame, contextSecondGame) -> {
            doNothing().when(mockSecondGame).startRound();
            doReturn(winners).when(mockSecondGame).getLastRoundWinners();

            try(MockedConstruction<Game> mockedThirdGameConstruction = mockConstruction(Game.class, (mockThirdGame, contextThirdGame) -> {
                doNothing().when(mockThirdGame).startRound();
                doReturn(List.of(winner1)).when(mockThirdGame).getLastRoundWinners();
            })) {
                List<Player> finalWinners = firstGame.startRoundForTiedWinners(winners);
                
                assertEquals(1, finalWinners.size());
                assertEquals(winner1, finalWinners.get(0));
            }
        })) {
        }
    }
}
