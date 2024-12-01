package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.util.Stack;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

public class RoundTest {
    // Test scenario configuration
    private static class TestScenario {
        final String[] players;
        final String[] inputs;
        final Card[] deckCards;
        
        TestScenario(
            String[] players,
            String[] inputs,
            Card[] deckCards
        ) {
            this.players = players;
            this.inputs = inputs;
            this.deckCards = deckCards;
        }
    }

    // Define test scenarios as constants
    private static final TestScenario ROUND_1_SCENARIO = new TestScenario(
        // Players
        new String[]{"Alice", "Bob", "Charlie", "David"},
        
        // Player inputs
        new String[]{
            // initial state:
            // Alice: GUARD, Bob: HANDMAIDEN, Charlie: PRIEST, David: COUNTESS

            // ==== mini round 1 ====
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

            // ==== mini round 2 ====
            // Bob: (KING), BARON, Charlie: PRINCESS, David: COUNTESS
            "0",        // Bob plays King
            "Charlie",  // Target Charlie (Bob and Charlie swap cards)

            // Bob: PRINCESS, Charlie: (BARON), GUARD, David: COUNTESS
            "0",        // Charlie plays Baron
            "David",    // Target David (Charlie loses and is eliminated)

            // Bob: PRINCESS, David: (COUNTESS), PRINCE
            "1",        // David plays COUNTESS

            // ==== mini round 3 ====
            // Bob: PRINCESS, (PRIEST), David: PRINCE
            "0",        // Bob plays Priest
            "David",    // Target David

            // Bob: PRINCESS, David: (PRINCE), HANDMAIDEN
            "0",        // David plays Prince
            "Bob",      // Target Bob (Bob discards PRINCESS and is eliminated)

            // David Wins
        },

        // Deck cards (in order of drawing)
        new Card[]{
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
        TestScenario scenario = ROUND_1_SCENARIO;
        
        // Setup players
        PlayerList players = createPlayers(scenario.players);
        
        // Setup deck
        Deck mockDeck = createMockDeck(scenario.deckCards);
        
        // Setup input stream
        ByteArrayInputStream inputStream = createInputStream(scenario.inputs);

        // Create and run game
        Game game = new Game(players, null, inputStream);
        // Set deck field with mock using Reflection
        Field deckField = Game.class.getDeclaredField("deck");
        deckField.setAccessible(true);
        deckField.set(game, mockDeck);
        
        game.startRound();

        String winnerName = "David";
        Player winner = players.getPlayer(winnerName);
        
        assertEquals(1, winner.getTokens(), "Winner should receive one token");
        for (Player player : players.getPlayers()) {
            if (player.getName().equals(winnerName)) {
                assertFalse(player.isEliminated(), "Winner should not be eliminated");
            } else {
                assertTrue(player.isEliminated(), "Loser should be eliminated");
            }
        }
    }

    private PlayerList createPlayers(String[] playerNames) {
        PlayerList players = new PlayerList();
        for (String name : playerNames) {
            players.addPlayer(name);
        }
        return players;
    }

    private ByteArrayInputStream createInputStream(String[] inputs) {
        String simulatedInput = String.join("\n", inputs);
        return new ByteArrayInputStream(simulatedInput.getBytes());
    }
}
