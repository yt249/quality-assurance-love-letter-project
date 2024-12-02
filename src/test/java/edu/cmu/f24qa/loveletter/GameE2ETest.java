package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Disabled;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.Scanner;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doAnswer;

public class GameE2ETest {
    private static class GameScenario {
        final String[] players;
        final RoundScenario[] rounds;

        GameScenario(String[] players, RoundScenario[] rounds) {
            this.players = players;
            this.rounds = rounds;
        }
    }

    // Test scenario configuration
    private static class RoundScenario {
        final String[] inputs;
        final Card[] deckCards;
        
        RoundScenario(
            String[] inputs,
            Card[] deckCards
        ) {
            this.inputs = inputs;
            this.deckCards = deckCards;
        }
    }

    // Define test scenarios as constants
    private static final RoundScenario ROUND_1_SCENARIO = new RoundScenario(
        // Player inputs
        new String[]{
            // ======== Round 1 ========
            // initial state:
            // Alice: GUARD, Bob: HANDMAIDEN, Charlie: PRIEST, David: COUNTESS

            // ---- mini round 1 ----
            // Alice: (GUARD), GUARD, Bob: HANDMAIDEN, Charlie: PRIEST, David: COUNTESS
            "0",        // Alice plays Guard
            "Priest",   // Guess Priest
            "Bob",      // Target Bob (wrong)

            // Alice: GUARD, Bob: (HANDMAIDEN), KING, Charlie: PRIEST, David: COUNTESS
            "0",        // Bob plays Handmaiden

            // Alice: GUARD, Bob: KING, Charlie: (PRIEST), PRINCESS, David: COUNTESS
            "0",        // Charlie plays Priest
            "David",    // Target David

            // Alice: GUARD, Bob: KING, Charlie: PRINCESS, David: COUNTESS, (GUARD)
            "1",        // David plays Guard
            "Guard",    // Guess Guard
            "Alice",    // Target Alice (correct, Alice is eliminated)

            // ---- mini round 2 ----
            // Bob: (KING), BARON, Charlie: PRINCESS, David: COUNTESS
            "0",        // Bob plays King
            "Charlie",  // Target Charlie (Bob and Charlie swap cards)

            // Bob: PRINCESS, Charlie: (BARON), GUARD, David: COUNTESS
            "0",        // Charlie plays Baron
            "David",    // Target David (Charlie loses and is eliminated)

            // Bob: PRINCESS, David: (COUNTESS), PRINCE
            "1",        // David plays COUNTESS

            // ---- mini round 3 ----
            // Bob: PRINCESS, (PRIEST), David: PRINCE
            "0",        // Bob plays Priest
            "David",    // Target David

            // Bob: PRINCESS, David: (PRINCE), HANDMAIDEN
            "0",        // David plays Prince
            "Bob",      // Target Bob (Bob discards PRINCESS and is eliminated)

            // David Wins Round 1
        },

        // Deck cards (in order of drawing)
        new Card[]{
            // ======== Round 1 ========
            // dummy card
            Card.GUARD,

            // Initial cards (dealt to players)
            Card.GUARD,             // Alice's initial card
            Card.HANDMAIDEN,        // Bob's initial card
            Card.PRIEST,            // Charlie's initial card
            Card.COUNTESS,          // David's initial card

            // mini round 1
            Card.GUARD,             // Alice draws
            Card.KING,              // Bob draws
            Card.PRINCESS,          // Charlie draws
            Card.GUARD,             // David draws

            // mini round 2
            Card.BARON,             // Bob draws
            Card.GUARD,             // Charlie draws
            Card.PRINCE,            // David draws

            // mini round 3
            Card.PRIEST,            // Bob draws
            Card.HANDMAIDEN,        // David draws
        }
    );

    private static final RoundScenario ROUND_2_SCENARIO = new RoundScenario(
        new String[]{
            // ======== Round 2 ========
            // initial state:
            // Alice: GUARD, Bob: GUARD, Charlie: GUARD, David: GUARD

            // ---- mini round 1 ----
            // Alice: (GUARD), PRIEST, Bob: GUARD, Charlie: GUARD, David: GUARD
            "0",        // Alice plays Guard
            "Priest",   // Guess Priest
            "Bob",      // Target Bob (wrong)

            // Alice: PRIEST, Bob: (GUARD), PRIEST, Charlie: GUARD, David: GUARD
            "0",        // Bob plays Guard
            "Guard",    // Guess Guard
            "Alice",    // Target Alice (wrong)

            // Alice: PRIEST, Bob: PRIEST, Charlie: (GUARD), BARON, David: GUARD
            "0",        // Charlie plays Guard
            "Guard",    // Guess Guard
            "Alice",    // Target Alice (wrong)

            // Alice: PRIEST, Bob: PRIEST, Charlie: BARON, David: (GUARD), BARON
            "0",        // David plays Guard
            "Guard",    // Guess Guard
            "Alice",    // Target Alice (wrong)

            // ---- mini round 2 ----
            // Alice: (PRIEST), HANDMAIDEN, Bob: PRIEST, Charlie: BARON, David: BARON
            "0",        // Alice plays Priest
            "Bob",      // Target Bob

            // Alice: HANDMAIDEN, Bob: (PRIEST), HANDMAIDEN, Charlie: BARON, David: BARON
            "0",        // Bob plays Priest
            "Charlie",  // Target Charlie

            // Alice: HANDMAIDEN, Bob: HANDMAIDEN, Charlie: BARON, (PRINCE), David: BARON
            "1",        // Charlie plays Prince
            "David",    // Target David (David discards BARON and gets PRINCE)

            // Alice: HANDMAIDEN, Bob: HANDMAIDEN, Charlie: BARON, David: (PRINCE), PRINCESS
            "0",        // David plays PRINCE
            "Charlie",  // Target Charlie (Charlie discards BARON and gets KING)

            // ---- mini round 3 ----
            // Alice: HANDMAIDEN, (COUNTESS), Bob: HANDMAIDEN, Charlie: KING, David: PRINCESS
            "1",        // Alice plays Countess

            // Deck is empty, round ends, David wins because Princess has the biggest value
        },
        new Card[]{
            // dummy card
            Card.GUARD,

            // initial cards
            Card.GUARD,             // Alice's initial card
            Card.GUARD,             // Bob's initial card
            Card.GUARD,             // Charlie's initial card
            Card.GUARD,             // David's initial card

            // mini round 1
            Card.PRIEST,            // Alice draws
            Card.PRIEST,            // Bob draws
            Card.BARON,             // Charlie draws
            Card.BARON,             // David draws

            // mini round 2
            Card.HANDMAIDEN,        // Alice draws
            Card.HANDMAIDEN,        // Bob draws
            Card.PRINCE,            // Charlie draws
            Card.PRINCE,            // David draws by Charlie's PRINCE
            Card.PRINCESS,          // David draws
            Card.KING,              // Charlie draws by David's PRINCE

            // mini round 3
            Card.COUNTESS,          // Alice draws
        }
    );

    private static final RoundScenario ROUND_3_SCENARIO = new RoundScenario(
        new String[]{
            // ======== Round 3 ========
            // initial state:
            // Alice: BARON, Bob: PRIEST, Charlie: PRIEST, David: COUNTESS

            // ---- mini round 1 ----
            // Alice: (BARON), GUARD, Bob: PRIEST, Charlie: PRIEST, David: COUNTESS
            "0",        // Alice plays Baron
            "Bob",      // Target Bob (Alice loses and is eliminated)

            // Bob: (PRIEST), PRINCE, Charlie: PRIEST, David: COUNTESS
            "0",        // Bob plays Priest
            "Charlie",  // Target Charlie

            // Bob: PRINCE, Charlie: (PRIEST), PRINCE, David: COUNTESS
            "0",        // Charlie plays Priest
            "David",    // Target David

            // Bob: PRINCE, Charlie: PRINCE, David: (COUNTESS), KING
            // David plays Countess automatically

            // ---- mini round 2 ----
            // Bob: (PRINCE), PRINCESS, Charlie: PRINCE, David: KING
            "1",        // Bob plays Princess (Bob is eliminated)

            // Charlie: (PRINCE), HANDMAIDEN, David: KING
            "0",        // Charlie plays Prince
            "David",    // Target David (David discards KING and gets HANDMAIDEN)

            // Charlie: HANDMAIDEN, David: (HANDMAIDEN), GUARD
            "0",        // David plays Handmaiden

            // ---- mini round 3 ----
            // Charlie: (HANDMAIDEN), GUARD, David: GUARD
            "0",        // Charlie plays Handmaiden

            // Charlie: GUARD, David: (GUARD), GUARD
            "1",        // David plays Guard
            "King",     // Guess King (Nothing happens because Charlie is protected)

            // ---- mini round 4 ----
            // Charlie: (GUARD), GUARD, David: GUARD
            "0",        // Charlie plays Guard
            "Priest",   // Guess Priest
            "David",    // Target David (wrong)

            // Deck is empty, round ends, David wins because he has the highest value in discard pile
        },
        new Card[]{
            // dummy card
            Card.BARON,

            // initial cards
            Card.BARON,             // Alice's initial card
            Card.PRIEST,            // Bob's initial card
            Card.PRIEST,            // Charlie's initial card
            Card.COUNTESS,          // David's initial card

            // mini round 1
            Card.GUARD,             // Alice draws
            Card.PRINCE,            // Bob draws
            Card.PRINCE,            // Charlie draws
            Card.KING,              // David draws

            // mini round 2
            Card.PRINCESS,         // Bob draws
            Card.HANDMAIDEN,       // Charlie draws
            Card.HANDMAIDEN,       // David draws by Charlie's PRINCE
            Card.GUARD,            // David draws

            // mini round 3
            Card.GUARD,            // Charlie draws
            Card.GUARD,            // David draws

            // mini round 4
            Card.GUARD,            // Charlie draws
        }
    );

    private static final RoundScenario ROUND_4_SCENARIO = new RoundScenario(
        new String[]{
            // ======== Round 4 ========
            // initial state:
            // Alice: GUARD, Bob: GUARD, Charlie: PRINCESS, David: COUNTESS

            // ---- mini round 1 ----
            // Alice: (GUARD), GUARD, Bob: GUARD, Charlie: PRINCESS, David: COUNTESS
            "0",        // Alice plays Guard
            "Priest",   // Guess Priest
            "Bob",      // Target Bob (wrong)

            // Alice: GUARD, Bob: GUARD, (PRINCE), Charlie: PRINCESS, David: COUNTESS
            "1",        // Bob plays Prince
            "Charlie",  // Target Charlie (Charlie discards PRINCESS and is eliminated)

            // Alice: GUARD, Bob: GUARD, David: COUNTESS, (BARON)
            "1",        // David plays Baron
            "Alice",    // Target Alice (Alice loses and is eliminated)

            // ---- mini round 2 ----
            // Bob: GUARD, (PRIEST), David: COUNTESS
            "1",        // Bob plays Priest
            "David",    // Target David

            // Bob: GUARD, David: COUNTESS, (BARON)
            "0",        // David plays Baron
            "Bob",      // Target Bob (Bob loses and is eliminated)

            // All players except David are eliminated, David wins
        },
        new Card[]{
            // dummy card
            Card.GUARD,

            // initial cards
            Card.GUARD,             // Alice's initial card
            Card.GUARD,             // Bob's initial card
            Card.PRINCESS,          // Charlie's initial card
            Card.COUNTESS,          // David's initial card

            // mini round 1
            Card.GUARD,             // Alice draws
            Card.PRINCE,            // Bob draws
            Card.BARON,             // David draws

            // mini round 2
            Card.PRIEST,            // Bob draws
            Card.BARON,             // David draws
        }
    );

    private static final GameScenario GAME_SCENARIO = new GameScenario(
        new String[]{"Alice", "Bob", "Charlie", "David"},
        new RoundScenario[]{ROUND_1_SCENARIO, ROUND_1_SCENARIO, ROUND_4_SCENARIO, ROUND_3_SCENARIO}
    );

    private class MockDeck extends Deck {
        private final Card[] deckCards;

        public MockDeck(Card[] deckCards) {
            this.deckCards = deckCards;
        }

        @Override
        public void build() {
            Stack<Card> deck = new Stack<>();
            for (int i = this.deckCards.length - 1; i >= 0; i--) {
                deck.push(this.deckCards[i]);
            }
            this.setDeck(deck);
        }

        @Override
        public void shuffle() {
            // Do nothing
        }
    }

    private Deck createMockDeck(Card[] cards) {
        return new MockDeck(cards);
    }

    @Test
    public void testSingleRoundScenario() throws NoSuchFieldException, IllegalAccessException {
        // Setup game with scenario
        GameScenario scenario = GAME_SCENARIO;
        
        // Setup players
        PlayerList players = createPlayers(scenario.players);
        Game game = new Game(players, null, System.in);

        // Get fields for reflection
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        Field contextField = Game.class.getDeclaredField("context");
        contextField.setAccessible(true);
        Field contextDeckField = GameContext.class.getDeclaredField("deck");
        contextDeckField.setAccessible(true);
        Field contextInputScannerField = GameContext.class.getDeclaredField("inputScanner");
        contextInputScannerField.setAccessible(true);

        // Override setupNewGame to inject new deck and input stream for each round
        Game spyGame = spy(game);
        doAnswer(invocation -> {
            // Get current round number
            int currentRound = spyGame.getRound();
            RoundScenario roundScenario = scenario.rounds[currentRound];
            
            // Create new mock deck for this round
            Deck mockDeck = createMockDeck(roundScenario.deckCards);
            deckField.set(spyGame, mockDeck);

            // Create new mock context for this round
            GameContext gameContext = new GameContext(spyGame.getPlayers(), mockDeck, createInputStream(roundScenario.inputs));
            contextDeckField.set(gameContext, mockDeck);
            contextInputScannerField.set(gameContext, new Scanner(createInputStream(roundScenario.inputs)));

            contextField.set(spyGame, gameContext);
            
            // Call original setupNewGame
            Object result = invocation.callRealMethod();
            return result;
        }).when(spyGame).setupNewGame();

        // Start game
        spyGame.start();

        String winnerName = "David";
        Player winner = players.getGameWinner();

        assertEquals(winnerName, winner.getName());
    }

    private PlayerList createPlayers(String[] playerNames) {
        PlayerList players = new PlayerList();
        for (String name : playerNames) {
            players.addPlayer(name);
        }
        return players;
    }

    private InputStreamReader createInputStream(String[] inputs) {
        String simulatedInput = String.join("\n", inputs);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }
}
