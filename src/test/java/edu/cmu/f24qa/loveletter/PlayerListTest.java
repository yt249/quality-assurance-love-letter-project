package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class PlayerListTest {
   
    /*
     * Verify that in a two-player game,
     * a player shall win the game after earning 7 Tokens of Affection.
     */
    @Test
    void testGetGameWinnerForTwoPlayerGame() {
        PlayerList players = new PlayerList();
        Player winner = new Player("winner", new Hand(), new DiscardPile(), false, 7);
        Player loser = new Player("loser", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(winner);
        players.addPlayer(loser);

        List<Player> result = players.getGameWinner();

        assertEquals(players.getPlayers().size(), 2);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getName(), "winner");
    }

    /*
     * Verify that in a three-player game,
     * a player shall win the game after earning 5 Tokens of Affection.
     */
    @Test
    void testGetGameWinnerForThreePlayerGame() {
        PlayerList players = new PlayerList();
        Player winner = new Player("winner", new Hand(), new DiscardPile(), false, 5);
        Player loser1 = new Player("loser1", new Hand(), new DiscardPile(), false, 0);
        Player loser2 = new Player("loser2", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(winner);
        players.addPlayer(loser1);
        players.addPlayer(loser2);

        List<Player> result = players.getGameWinner();

        assertEquals(players.getPlayers().size(), 3);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getName(), "winner");
    }

    /*
     * Verify that in a four-player game,
     * a player shall win the game after earning 4 Tokens of Affection.
     */
    @Test
    void testGetGameWinnerForFourPlayerGame() {
        PlayerList players = new PlayerList();
        Player winner = new Player("winner", new Hand(), new DiscardPile(), false, 4);
        Player loser1 = new Player("loser1", new Hand(), new DiscardPile(), false, 0);
        Player loser2 = new Player("loser2", new Hand(), new DiscardPile(), false, 0);
        Player loser3 = new Player("loser3", new Hand(), new DiscardPile(), false, 0);
        players.addPlayer(winner);
        players.addPlayer(loser1);
        players.addPlayer(loser2);
        players.addPlayer(loser3);

        List<Player> result = players.getGameWinner();

        assertEquals(players.getPlayers().size(), 4);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getName(), "winner");
    }

    /*
     * Verify that getGameWinner should throw exception when there are not 2-8 players.
     */
    @Test
    void testGetGameWinnerForInvalidNumberOfPlayers() {
        PlayerList players = new PlayerList();
        Player player = new Player("player", new Hand(), new DiscardPile(), false, 4);
        players.addPlayer(player);

        Exception exception = assertThrows(IllegalStateException.class, players::getGameWinner);

        // Assert: Verify the exception message
        assertEquals("Invalid number of players", exception.getMessage()); 
    }

    /*
     * Verify that checkForRoundWinner returns true when only one player has cards. 
     */
    @Test
    void testCheckForRoundWinnerWhenOnlyOnePlayerHasCards() {
        // Add two players to the player list
        PlayerList playerList = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        player1.addCard(Card.PRIEST); // only Player 1 has a card
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);

        boolean hasRoundWinner = playerList.checkForRoundWinner();

        assertTrue(hasRoundWinner, "There should be a round winner");
    }

    /*
     * Verify that checkForRoundWinner returns false when multiple players have cards.
     */
    @Test
    void testCheckForRoundWinnerWhenMultiplePlayersHaveCards() {
        // Add two players to the player list
        PlayerList playerList = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        // Both players have a card
        player1.addCard(Card.PRIEST);
        player2.addCard(Card.BARON);
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);

        boolean hasRoundWinner = playerList.checkForRoundWinner();

        assertFalse(hasRoundWinner, "There should not be a round winner when multiple players have cards.");
    }

    /*
     * Verify that checkForRoundWinner throws exception when no players have cards.
     */
    @Test
    void testCheckForRoundWinnerWhenNoPlayersHaveCards() {
        PlayerList playerList = new PlayerList();
        Player player1 = new Player("Player 1", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Player 2", new Hand(), new DiscardPile(), false, 0);
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);

        Exception exception = assertThrows(
            IllegalStateException.class, 
            () -> playerList.checkForRoundWinner(), 
            "Expected checkForRoundWinner to throw an IllegalStateException when no players have cards."
        );

        assertEquals("No players have cards.", exception.getMessage());
    }

    @Test
    void testCompareHandBishopLosesToPrincess() {
        // Setup
        PlayerList playerList = new PlayerList();

        // Create players with Bishop and Princess
        Player player1 = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);

        // Add Bishop (value 9) to player1's hand
        player1.addCard(Card.BISHOP);

        // Add Princess (value 8) to player2's hand
        player2.addCard(Card.PRINCESS);

        // Add players to the list
        playerList.addPlayer(player1);
        playerList.addPlayer(player2);

        // Execute
        List<Player> winners = playerList.compareHand();

        // Verify
        assertEquals(1, winners.size(), "Should be exactly one winner");
        assertEquals(player2, winners.get(0), "Player with Princess should win over player with Bishop");
    }
    
    @Test
    public void testCompareHandEmptyList() {
        PlayerList playerList = new PlayerList();
        Exception exception = assertThrows(IllegalStateException.class, () -> playerList.compareHand());
        assertEquals("Player list is empty", exception.getMessage());
    }
    
    @Test
    public void testCompareHandSingleWinner() {
        // Create players with different hand values
        Player p1 = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player p2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        
        // Give players different cards
        p1.addCard(Card.PRINCESS); // Value 8
        p2.addCard(Card.GUARD);    // Value 1
        
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(p1);
        playerList.addPlayer(p2);
        
        List<Player> winners = playerList.compareHand();
        assertEquals(1, winners.size());
        assertEquals("Alice", winners.get(0).getName());
    }
    
    @Test
    public void testCompareHandWithCountBonus() {
        Player p1 = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player p2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        
        // Give players cards
        p1.addCard(Card.BARON);    // Value 3
        p2.addCard(Card.PRIEST);   // Value 2
        
        // Add Count cards to p2's discard pile
        p2.addCardToDiscarded(Card.COUNT);  // +1 bonus
        p2.addCardToDiscarded(Card.COUNT);  // +1 bonus
        
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(p1);
        playerList.addPlayer(p2);
        
        List<Player> winners = playerList.compareHand();
        assertEquals(1, winners.size());
        assertEquals("Bob", winners.get(0).getName()); // Should win with 2 + 2 = 4
    }
    
    @Test
    public void testCompareHandTie() {
        Player p1 = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player p2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        
        // Give both players the same card
        p1.addCard(Card.KING);     // Value 6
        p2.addCard(Card.KING);     // Value 6
        
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(p1);
        playerList.addPlayer(p2);
        
        List<Player> winners = playerList.compareHand();
        assertEquals(2, winners.size());
        assertTrue(winners.contains(p1));
        assertTrue(winners.contains(p2));
    }

    @Test
    public void testCompareHandIgnoresEliminatedPlayers() {
        Player p1 = new Player("Alice", new Hand(), new DiscardPile(), false, 0);
        Player p2 = new Player("Bob", new Hand(), new DiscardPile(), false, 0);
        
        // Only give one player a card
        p1.addCard(Card.HANDMAIDEN); // Value 4
        
        PlayerList playerList = new PlayerList();
        playerList.addPlayer(p1);
        playerList.addPlayer(p2);
        
        List<Player> winners = playerList.compareHand();
        assertEquals(1, winners.size());
        assertEquals("Alice", winners.get(0).getName());
    }
}