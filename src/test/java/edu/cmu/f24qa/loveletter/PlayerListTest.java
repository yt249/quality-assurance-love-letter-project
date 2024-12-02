package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        Player result = players.getGameWinner();

        assertEquals(players.getPlayers().size(), 2);
        assertEquals(result.getName(), "winner");
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

        Player result = players.getGameWinner();

        assertEquals(players.getPlayers().size(), 3);
        assertEquals(result.getName(), "winner");
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

        Player result = players.getGameWinner();

        assertEquals(players.getPlayers().size(), 4);
        assertEquals(result.getName(), "winner");
    }

    /*
     * Verify that getGameWinner should throw exception when there are not 2-4 players.
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
}