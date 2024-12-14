package edu.cmu.f24qa.loveletter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.io.StringReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class GameContextTest {
    private GameContext context;
    private PlayerList players;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        players = new PlayerList();
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    private void setupContext(PlayerList players, String simulatedInput) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        context = new GameContext(players, mock(Deck.class), new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    /*
     * Test the opponent selection logic when there are not enough available players to select
     * 
     * Setup:
     * - 2 players: Watson (current), Sarah 
     * - Sarah is protected
     * - Watson plays a card that does not allow her to select herself
     */
    @Test
    void testSelectOpponentsNotEnoughAvailablePlayers() {
        Player player1 = new Player("Watson", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Sarah", new Hand(), new DiscardPile(), true, 0);
        player1.addCard(Card.BARON);
        player2.addCard(Card.COUNTESS);
        players.addPlayer(player1);
        players.addPlayer(player2);
        setupContext(players, "");
        context.setCurrentUser(player1);

        // Execute
        List<Player> selectedOpponents = context.selectOpponents(1, 1, false);

        assertEquals(0, selectedOpponents.size());
        assertTrue(outContent.toString().contains("Not enough available players can be selected to satisfy " +
                                                "the requirement of targeting at least 1 player(s)."));
    }

    /**
     * Tests the opponent selection logic when a Sycophant effect is active during a Cardinal play.
     * 
     * This test verifies that:
     * 1. When a Sycophant has previously targeted a player, that player is automatically selected first
     * 2. The user can still manually select a second player for the Cardinal effect
     * 3. The total number of selected players matches the Cardinal's requirement (2 players)
     * 
     * Setup:
     * - 4 players: Alice (current), Bob (Sycophant target), Charlie, and David (protected)
     * - Bob is pre-selected due to Sycophant effect
     * - User first inputs a non-existent player, a protected player, then selects Charlie
     */
    @Test
    void testSelectOpponentsWithSycophantForCardinal() {
        // Setup
        PlayerList players = new PlayerList();
        Player currentPlayer = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player sycophantTarget = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        Player player3 = new Player("Charlie", new Hand(), new DiscardPile(), false, 0);
        Player player4 = new Player("David", new Hand(), new DiscardPile(), true, 0);  // protected
        // add cards to players' hands
        currentPlayer.addCard(Card.CARDINAL);
        sycophantTarget.addCard(Card.PRINCE);
        player3.addCard(Card.BARON);
        player4.addCard(Card.COUNTESS);
        players.addPlayer(currentPlayer);
        players.addPlayer(sycophantTarget);
        players.addPlayer(player3);
        players.addPlayer(player4);
        
        // Simulate input for selecting the second player (after sycophant-forced player)
        // first enters a non-existent player, then enters a not available (protected or eliminated) player, then Charlie
        String simulatedInput = "non-existent\nDavid\nCharlie\n";
        StringReader reader = new StringReader(simulatedInput);
        
        GameContext context = new GameContext(players, new Deck(), reader);
        context.setCurrentUser(currentPlayer);
        context.setSycophantForcedPlayer(sycophantTarget);  // Set Bob as the Sycophant-forced target
        
        // Execute
        // Cardinal requires 2 players, allows max 2, and can include self
        List<Player> selectedOpponents = context.selectOpponents(2, 2, true);
        
        // Verify
        assertEquals(2, selectedOpponents.size());
        assertEquals("Bob", selectedOpponents.get(0).getName());    // First player should be Sycophant-forced
        assertEquals("Charlie", selectedOpponents.get(1).getName()); // Second player manually selected
        assertTrue(outContent.toString().contains("No such player found. Please try again."));  // when user inputs a non-existent player
        assertTrue(outContent.toString().contains("Please target a player within available opponents"));  // when user inputs a not available player
    }
    
    /**
     * Tests the Baroness card's ability to optionally select a second target.
     * When offered to select another player, the user accepts.
     * 
     * Setup:
     * - 3 players: Alice (current), Bob, and Charlie
     * - User selects Bob first
     * - User accepts the offer to select a second target
     * 
     * Expected:
     * - Returns list with two targets (Bob, Charlie)
     * - Verifies prompt for additional player was shown
     */
    @Test
    void testSelectOpponentsAcceptToSelectOptionalTarget() {
        // Setup game with 3 players
        PlayerList players = new PlayerList();
        Player currentPlayer = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player target1 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        Player target2 = new Player("Charlie", new Hand(), new DiscardPile(), false, 0);
        
        // Add cards to players' hands to prevent them from being considered eliminated
        currentPlayer.addCard(Card.BARONESS);
        target1.addCard(Card.GUARD);
        target2.addCard(Card.PRIEST);
        
        players.addPlayer(currentPlayer);
        players.addPlayer(target1);
        players.addPlayer(target2);
        
        // First select Bob, then say "no" to selecting another player
        String simulatedInput = "Bob\nyes\nCharlie\n";
        StringReader reader = new StringReader(simulatedInput);
        
        GameContext context = new GameContext(players, mock(Deck.class), reader);
        context.setCurrentUser(currentPlayer);
        
        // Baroness can target 1-2 players
        List<Player> selected = context.selectOpponents(1, 2, false);
        
        assertEquals(2, selected.size());
        assertEquals("Bob", selected.get(0).getName());
        assertEquals("Charlie", selected.get(1).getName());
        assertTrue(outContent.toString().contains("Would you like to select another player? (yes/no):"));
    }


    /**
     * Tests the Baroness card's ability to optionally select a second target.
     * When offered to select another player, the user declines.
     * 
     * Setup:
     * - 3 players: Alice (current), Bob, and Charlie
     * - User selects Bob first
     * - User declines to select a second target
     * 
     * Expected:
     * - Returns list with single target (Bob)
     * - Verifies prompt for additional player was shown
     */
    @Test
    void testSelectOpponentsRefuseToSelectOptionalTarget() {
        // Setup game with 3 players
        PlayerList players = new PlayerList();
        Player currentPlayer = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player target1 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        Player target2 = new Player("Charlie", new Hand(), new DiscardPile(), false, 0);
        
        // Add cards to players' hands to prevent them from being considered eliminated
        currentPlayer.addCard(Card.BARONESS);
        target1.addCard(Card.GUARD);
        target2.addCard(Card.PRIEST);
        
        players.addPlayer(currentPlayer);
        players.addPlayer(target1);
        players.addPlayer(target2);
        
        // First select Bob, then say "no" to selecting another player
        String simulatedInput = "Bob\nno\n";
        StringReader reader = new StringReader(simulatedInput);
        
        GameContext context = new GameContext(players, mock(Deck.class), reader);
        context.setCurrentUser(currentPlayer);
        
        // Baroness can target 1-2 players
        List<Player> selected = context.selectOpponents(1, 2, false);
        
        assertEquals(1, selected.size());
        assertEquals("Bob", selected.get(0).getName());
        assertTrue(outContent.toString().contains("Would you like to select another player? (yes/no):"));
    }

    /**
     * Tests that when a Sycophant effect forces self-targeting but the card doesn't allow it,
     * the selection fails and returns an empty list.
     * 
     * Setup:
     * - 3 players: Alice (current), Bob, and Charlie
     * - Sycophant effect is forcing Alice to target herself
     * - Card being played doesn't allow self-targeting (includeSelf = false)
     * 
     * Expected:
     * - Returns empty list
     * - Prints error message about not being able to target self
     */
    @Test
    void testSelectOpponentsSycophantForcedTargetingSelf() {
        // Setup
        PlayerList players = new PlayerList();
        Player currentPlayer = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        Player player3 = new Player("Charlie", new Hand(), new DiscardPile(), false, 0);
        
        // Add cards to prevent being considered eliminated
        currentPlayer.addCard(Card.GUARD);
        player2.addCard(Card.PRIEST);
        player3.addCard(Card.BARON);
        
        players.addPlayer(currentPlayer);
        players.addPlayer(player2);
        players.addPlayer(player3);
        
        GameContext context = new GameContext(players, mock(Deck.class), new StringReader(""));
        context.setCurrentUser(currentPlayer);
        context.setSycophantForcedPlayer(currentPlayer);  // Force targeting self
        
        // Try to select opponents when self-targeting is not allowed
        List<Player> selected = context.selectOpponents(1, 1, false);
        
        assertTrue(selected.isEmpty());
        assertEquals("The Sycophant effect enforces targeting yourself, but current card cannot target yourself.\n",
                    outContent.toString());
    }   

    /**
     * Tests drawing a new card for a player when the deck has cards.
     * 
     * Setup:
     * - Mock a deck to indicate it has more cards and return a specific card when drawn.
     * 
     * Expected:
     * - The player's hand should contain the drawn card.
     */
    @Test
    void testDrawNewCardForPlayerByDeckStatusWithCardsInDeck() {
        // Setup
        Deck mockDeck = mock(Deck.class);
        Card expectedCard = Card.GUARD;
        when(mockDeck.hasMoreCards()).thenReturn(true);
        when(mockDeck.draw()).thenReturn(expectedCard);
        
        GameContext context = new GameContext(players, mockDeck, new StringReader(""));
        Player player = new Player("TestPlayer", new Hand(), new DiscardPile(), false, 0);
        
        // Execute
        context.drawNewCardForPlayerByDeckStatus(player);
        
        // Verify
        assertEquals(1, player.getHand().getHand().size(), "Player should have one card in hand.");
        assertEquals(expectedCard, player.getHand().getHand().get(0), "The card in hand should match the expected card.");
    }

    /**
     * Tests drawing a new card for a player when the deck has no cards.
     * 
     * Setup:
     * - Mock a deck to indicate it has no more cards and return a specific card as the removed top card.
     * 
     * Expected:
     * - The player's hand should contain the removed top card.
     */
    @Test
    void testDrawNewCardForPlayerByDeckStatusWithNoCardsInDeck() {
        // Setup
        Deck mockDeck = mock(Deck.class);
        Card expectedCard = Card.COUNTESS;
        when(mockDeck.hasMoreCards()).thenReturn(false);
        when(mockDeck.getRemovedTopCard()).thenReturn(expectedCard);
        
        GameContext context = new GameContext(players, mockDeck, new StringReader(""));
        Player player = new Player("TestPlayer", new Hand(), new DiscardPile(), false, 0);
        
        // Execute
        context.drawNewCardForPlayerByDeckStatus(player);
        
        // Verify
        assertEquals(1, player.getHand().getHand().size(), "Player should have one card in hand.");
        assertEquals(expectedCard, player.getHand().getHand().get(0), "The card in hand should match the expected card.");
    }

    /*
     * initializeDeck() should throw exception when there are not 2-8 players.
     */
    @Test
    void testInitializeDeckCalledWithInvalidNumberOfPlayers() {
        PlayerList players = new PlayerList();
        Player player = new Player("player", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player);
        GameContext context = new GameContext(players, new Deck(), new StringReader(""));
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            context::initializeDeck,  // Method to invoke
            "Expected initializeDeck to throw, but it didn't"
        );

        assertEquals("Invalid number of players. Only 2-8 players are allowed.", exception.getMessage());
    }

    /*
     * initializeDeck() should initialize a deck with 16 cards when there are 2-4 players.
     */
    @Test
    void testInitializeDeckForTwoPlayers() {
        PlayerList players = new PlayerList();
        Player player1 = new Player("player1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("player2", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);
        GameContext context = new GameContext(players, new Deck(), new StringReader(""));

        context.initializeDeck();

        assertEquals(16, context.getDeck().getDeck().size());
    }

    /*
     * initializeDeck() should initialize a deck with 32 cards when there are 5-8 players.
     */
    @Test
    void testInitializeDeckForFivePlayers() {
        PlayerList players = new PlayerList();
        Player player1 = new Player("player1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("player2", new Hand(), new DiscardPile(), false, 0);
        Player player3 = new Player("player3", new Hand(), new DiscardPile(), false, 0);
        Player player4 = new Player("player4", new Hand(), new DiscardPile(), false, 0);
        Player player5 = new Player("player5", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(player1);
        players.addPlayer(player2);
        players.addPlayer(player3);
        players.addPlayer(player4);
        players.addPlayer(player5);
        GameContext context = new GameContext(players, new Deck(), new StringReader(""));

        context.initializeDeck();

        assertEquals(32, context.getDeck().getDeck().size());
    }

    /* Test the behavior of GameContext.removeTopCard() for a 2-player game
     * Verifies that:
     * - The deck size is reduced by 4 (1 hidden card and 3 face-up cards)
     * - Console output logs the 3 face-up cards as removed 
     * - GameContext.removedTopCard is set
     */
    @Test
    void testRemoveTopCardForTwoPlayerGame() {
        // Redirect System.out to capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Game with 3 players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer("Player 1");
        playerList.addPlayer("Player 2");

        // Create a custom deck with cards in specific order
        Deck deck = new Deck();
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
        deck.setDeck(customDeck);
        GameContext context = new GameContext(playerList, deck, new StringReader(""));

        context.removeCardFromDeck();
        String output = outContent.toString();
        // Verify hidden card is not logged
        assertFalse(output.contains("Guard (1) was removed from the deck."), "Hidden card should not be logged");
        // Verify 3 cards are removed facing up
        assertTrue(output.contains("Priest (2) was removed from the deck."), "First face-up card should be PRIEST");
        assertTrue(output.contains("Baron (3) was removed from the deck."), "Second face-up card should be BARON");
        assertTrue(output.contains("Handmaiden (4) was removed from the deck."), "Third face-up card should be HANDMAIDEN");
        // Guard is the removed top card
        assertEquals(Card.GUARD, context.getRemovedTopCard());
        // there are 15 cards in the deck
        assertEquals(12, context.getDeck().getDeck().size());
    }

    /* Test the behavior of GameContext.removeTopCard() for a 3-to-8-player game
     * Verifies that:
     * - The deck size is reduced by 1 (only 1 hidden card)
     * - No face-up cards are logged to the console 
     * - GameContext.removedTopCard is set
     */
    @Test
    void testRemoveTopCardForThreePlayerGame() {
        // Redirect System.out to capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Game with 3 players
        PlayerList playerList = new PlayerList();
        playerList.addPlayer("Player 1");
        playerList.addPlayer("Player 2");
        playerList.addPlayer("Player 3");

        // Create a custom deck with cards in specific order
        Deck deck = new Deck();
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
        deck.setDeck(customDeck);
        GameContext context = new GameContext(playerList, deck, new StringReader(""));

        context.removeCardFromDeck();

        // Verify hidden card is not logged
        String output = outContent.toString();
        assertFalse(output.contains("Guard (1) was removed from the deck."), "Hidden card should not be logged"); 
        // Guard is the removed top card
        assertEquals(Card.GUARD, context.getRemovedTopCard());
        // there are 15 cards in the deck
        assertEquals(15, context.getDeck().getDeck().size());
    }
}
