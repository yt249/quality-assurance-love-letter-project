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
        players = new PlayerList();
        players.addPlayer("Watson");  // Add Watson as a real player
    
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