package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class PlayerListTest {
   
    /*
     * Verify that in a two-player game,
     * a player shall win the game after earning 7 Tokens of Affection.
     */
    @Disabled("Incorrect getGameWinner implementation.")
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
}
