package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


public class GameTest {

    /*
     * Integration Test: Verify that a round shall end if the deck is empty.
     */
    @Disabled("Game.round not increased after round ends.")
    @Test
    void testRoundEndsIfDeckIsEmpty() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        // Mock Deck and stub hasMoreCards to simulate the deck-empty scenario
        Deck mockDeck = mock(Deck.class);
        doReturn(true, true, false).when(mockDeck).hasMoreCards();  // Simulate the deck becoming empty after 3 checks

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
        assertTrue(spyGame.getRound() == initialRound + 2, "The round number should increase after the round ends.");
    }
}