package edu.cmu.f24qa.loveletter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameContextTest {
    private GameContext gameContext;
    private PlayerList players;

    @BeforeEach
    void setUp() {
        // Set up game with 2 players each having one card in their hands
        players = new PlayerList();
        Player player1 = new Player("Watson", new Hand(), new DiscardPile(), false, 0);
        Player player2 = new Player("Sarah", new Hand(), new DiscardPile(), false, 0);
        player1.addCard(Card.BARON);
        player2.addCard(Card.COUNTESS);
        players.addPlayer(player1);
        players.addPlayer(player2);
    
        String simulatedInput = "NonExistent\nWatson\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        gameContext = new GameContext(players, mock(Deck.class), new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    @Test
    void testSelectOpponentRePromptsForNonExistentOpponent() {
        Optional<Player> selectedOpponent = gameContext.selectOpponent();

        assertTrue(selectedOpponent.isPresent(), "Expected valid opponent to be found after re-prompt");
        assertEquals("Watson", selectedOpponent.get().getName(), "Selected opponent should match the second input");
    }
}