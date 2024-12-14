package edu.cmu.f24qa.loveletter;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.List;
import java.util.Scanner;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doAnswer;

public class GameE2ETest5Player {
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
            // Alice: HANDMAIDEN, Bob: SYCOPHANT, Charlie: COUNT, David: PRIEST, Eve: GUARD

            // ---- mini round 1 ----
            // Alice: (HANDMAIDEN), GUARD, Bob: SYCOPHANT, Charlie: COUNT, David: PRIEST, Eve: GUARD
            "0",        // Alice plays Handmaiden

            // Alice: GUARD, Bob: (SYCOPHANT), BARON, Charlie: COUNT, David: PRIEST, Eve: GUARD
            "0",        // Bob plays Sycophant
            "Eve",      // Target Eve

            // Alice: GUARD, Bob: BARON, Charlie: COUNT, (SYCOPHANT), David: PRIEST, Eve: GUARD
            "1",        // Charlie plays Sycophant, Targets Eve automatically

            // Alice: GUARD, Bob: BARON, Charlie: COUNT, David: PRIEST, (GUARD), Eve: GUARD
            "1",        // David plays Guard
            "Priest",   // Guess Priest (Target Eve automatically, wrong)

            // Alice: GUARD, Bob: BARON, Charlie: COUNT, David: PRIEST, Eve: GUARD, (BARONESS)
            "1",        // Eve plays Baroness, select 1 to 2 opponents
            "Bob",      // Target Bob
            "yes",      // Confirm selecting another opponent
            "Charlie",  // Target Charlie

            // ---- mini round 2 ----
            // Alice: (GUARD), PRIEST, Bob: BARON, Charlie: COUNT, David: PRIEST, Eve: GUARD
            "0",        // Alice plays GUARD
            "Priest",   // Guess Priest
            "Eve",      // Target Eve (wrong)

            // Alice: PRIEST, Bob: (BARON), CARDINAL, Charlie: COUNT, David: PRIEST, Eve: GUARD
            "0",        // Bob plays Baron
            "Alice",    // Target Alice (Tie, nothing happens)

            // Alice: PRIEST, Bob: CARDINAL, Charlie: (COUNT), GUARD, David: PRIEST, Eve: GUARD
            "0",        // Charlie plays Count (Nothing happens)

            // Alice: PRIEST, Bob: CARDINAL, Charlie: GUARD, David: (PRIEST), BISHOP, Eve: GUARD
            "0",        // David plays Priest
            "Alice",    // Target Alice

            // Alice: PRIEST, Bob: CARDINAL, Charlie: GUARD, David: BISHOP, Eve: GUARD, (HANDMAIDEN)
            "1",        // Eve plays Handmaiden

            // ---- mini round 3 ----
            // Alice: (PRIEST), CARDINAL, Bob: CARDINAL, Charlie: GUARD, David: BISHOP, Eve: GUARD
            "0",        // Alice plays Priest
            "Bob",      // Target Bob

            // Alice: CARDINAL, Bob: CARDINAL, (GUARD), Charlie: GUARD, David: BISHOP, Eve: GUARD
            "1",        // Bob plays Guard
            "Cardinal", // Guess Cardinal
            "Alice",    // Target Alice (Correct, Alice is eliminated)

            // Bob: CARDINAL, Charlie: GUARD, (COUNT), David: BISHOP, Eve: GUARD
            "1",        // Charlie plays Count (Nothing happens)

            // Bob: CARDINAL, Charlie: GUARD, David: (BISHOP), GUARD, Eve: GUARD
            "0",        // David plays Bishop
            "2",        // Guess card value 2
            "Bob",      // Target Bob (Correct, Bob discards Cardinal and gets Guard, David gets a Token)
            "y",        // Bob decides to discard hand and draw a new card

            // Bob: GUARD, Charlie: GUARD, David: GUARD, Eve: (GUARD), KING
            "0",        // Eve plays Guard
            "Prince",   // Guess Prince
            "Bob",      // Target Bob (Wrong, nothing happens)

            // ---- mini round 4 ----
            // Bob: (GUARD), BARONESS, Charlie: GUARD, David: GUARD, Eve: KING
            "0",        // Bob plays Guard
            "Constable", // Guess Constable
            "Charlie",  // Target Charlie (wrong, nothing happens)

            // Bob: BARONESS, Charlie: (GUARD), BARON, David: GUARD, Eve: KING
            "0",        // Charlie plays Guard
            "Prince",   // Guess Prince
            "Bob",      // Target Bob (Wrong, nothing happens)

            // Bob: BARONESS, Charlie: BARON, David: GUARD, (PRINCE), Eve: KING
            "1",        // David plays Prince
            "Charlie",  // Target Charlie (Charlie discards Baron and gets Countess)

            // Bob: BARONESS, Charlie: COUNTESS, David: GUARD, Eve: KING, (PRINCE)
            "1",        // Eve plays Prince
            "David",    // Target David (David discards Guard and gets Princess)

            // ---- mini round 5 ----
            // Bob: BARONESS, (Constable), Charlie: COUNTESS, David: PRINCESS, Eve: KING
            "1",        // Bob plays Constable

            // Bob: BARONESS, Charlie: COUNTESS, (GUARD), David: PRINCESS, Eve: KING
            "1",        // Charlie plays Guard
            "Princess", // Guess Princess
            "Bob",      // Target Bob (Wrong, nothing happens)

            // Charlie Wins Round 1 by comparing hand values
            // Tokens Result: Alice: 0, Bob: 0, Charlie: 1, David: 1, Eve: 0
        },

        // Deck cards (in order of drawing)
        new Card[]{
            // ======== Round 1 ========
            // dummy card
            Card.ASSASSIN,

            // Initial cards (dealt to players)
            Card.HANDMAIDEN,         // Alice's initial card
            Card.SYCOPHANT,          // Bob's initial card
            Card.COUNT,              // Charlie's initial card
            Card.PRIEST,             // David's initial card
            Card.GUARD,              // Eve's initial card

            // mini round 1
            Card.GUARD,              // Alice draws
            Card.BARON,              // Bob draws
            Card.SYCOPHANT,          // Charlie draws
            Card.GUARD,              // David draws
            Card.BARONESS,           // Eve draws

            // mini round 2
            Card.PRIEST,             // Alice draws
            Card.CARDINAL,           // Bob draws
            Card.GUARD,              // Charlie draws
            Card.BISHOP,             // David draws
            Card.HANDMAIDEN,         // Eve draws

            // mini round 3
            Card.CARDINAL,           // Alice draws
            Card.GUARD,              // Bob draws
            Card.COUNT,              // Charlie draws
            Card.GUARD,              // David draws
            Card.GUARD,              // Bob draws by David's Bishop
            Card.KING,               // Eve draws

            // mini round 4
            Card.BARONESS,           // Bob draws
            Card.BARON,              // Charlie draws
            Card.PRINCE,             // David draws
            Card.COUNTESS,           // Charlie draws by David's Prince
            Card.PRINCE,             // Eve draws
            Card.PRINCESS,           // David draws by Eve's Prince

            // mini round 5
            Card.CONSTABLE,          // Bob draws
            Card.GUARD,              // Charlie draws
        }
    );

    private static final RoundScenario ROUND_2_SCENARIO = new RoundScenario(
        new String[]{
            // ======== Round 2 (Charlie goes first) ========
            // initial state:
            // Charlie: BARONESS, David: JESTER, Eve: COUNTESS, Alice: PRIEST, Bob: PRINCESS

            // ---- mini round 1 ----
            // Charlie: (GUARD), BARONESS, David: JESTER, Eve: COUNTESS, Alice: PRIEST, Bob: PRINCESS
            "1",        // Charlie plays GUARD
            "Priest",   // Guess PRIEST
            "David",    // Target David (wrong)

            // Charlie: BARONESS, David: (JESTER), SYCOPHANT, Eve: COUNTESS, Alice: PRIEST, Bob: PRINCESS
            "0",        // David plays JESTER
            "Eve",      // Target Eve (David thinks Eve will win this round)

            // Charlie: BARONESS, David: SYCOPHANT, Eve: (COUNTESS), KING, Alice: PRIEST, Bob: PRINCESS
            "0",        // Eve plays COUNTESS (becasue Eve has King in hand)

            // Charlie: BARONESS, David: SYCOPHANT, Eve: KING, Alice: (PRIEST), CARDINAL, Bob: PRINCESS
            "0",        // Alice plays PRIEST
            "Bob",      // Target Bob

            // Charlie: BARONESS, David: SYCOPHANT, Eve: KING, Alice: CARDINAL, Bob: (PRINCESS), QUEEN
            "0",        // Bob plays PRINCESS (Bob is eliminated)

            // ---- mini round 2 ----
            // Charlie: (GUARD), BARONESS, David: SYCOPHANT, Eve: KING, Alice: CARDINAL
            "1",        // Charlie plays GUARD
            "Cardinal", // Guess CARDINAL
            "Alice",    // Target Alice (correct, Alice is eliminated)

            // Charlie: BARONESS, David: (SYCOPHANT), BARONESS, Eve: KING
            "0",        // David plays SYCOPHANT
            "Charlie",  // Target Charlie (Charlie will be the target for the next card)

            // Charlie: BARONESS, David: BARONESS, Eve: (HANDMAIDEN), KING 
            "1",        // Eve plays HANDMAIDEN (Eve is protected until Eve's next turn)

            // ---- mini round 3 ----
            // Charlie: (HANDMAIDEN), BARONESS, David: BARONESS, Eve: KING
            "1",        // Charlie plays HANDMAIDEN (Charlie is protected until Charlie's next turn)

            // Charlie: BARONESS, David: (PRINCE), BARONESS, Eve: KING
            "1",        // David plays PRINCE 
            "David",    // Target self (David is forced to choose himself because all are protected, 
                        //              David discards its hand card and draws a new card)

            // Charlie: BARONESS, David: BISHOP, Eve: (GUARD), KING
            "1",        // Eve plays GUARD 
            "Bishop",   // Guess BISHOP
            "David",    // Target David (correct, David is eliminated)

            // ---- mini round 4 ----
            // Charlie: (PRIEST), BARONESS, Eve: KING
            "1",        // Charlie plays PRIEST
            "Eve",      // Target Eve 

            // Charlie: BARONESS, Eve: (BARON), KING
            "1",        // Eve plays BARON
            "Charlie",  // Target Charlie (KING(6) > BARONESS(3), Charlie is eliminated)

            // Eve is the last player, round ends, Eve wins and gets a token
            // David guesses correctly(Eve is the final round winner), David also gets a token
        },
        new Card[]{
            // dummy card
            Card.GUARD,

            // initial cards
            Card.BARONESS,          // Charlie's initial card
            Card.JESTER,            // David's initial card
            Card.COUNTESS,          // Eve's initial card
            Card.PRIEST,            // Alice's initial card
            Card.PRINCESS,          // Bob's initial card

            // mini round 1
            Card.GUARD,             // Charlie draws
            Card.SYCOPHANT,         // David draws
            Card.KING,              // Eve draws
            Card.CARDINAL,          // Alice draws
            Card.QUEEN,             // Bob draws

            // mini round 2
            Card.GUARD,             // Charlie draws
            Card.BARONESS,          // David draws
            Card.HANDMAIDEN,        // Eve draws

            // mini round 3
            Card.HANDMAIDEN,        // Charlie draws
            Card.PRINCE,            // David draws
            Card.BISHOP,            // David draws second card becasue of PRINCE
            Card.GUARD,             // Eve draws

            // mini round 4
            Card.PRIEST,            // Charlie draws
            Card.BARON,             // Eve draws
        }
    );

    private static final RoundScenario ROUND_3_SCENARIO = new RoundScenario(
        // Player inputs
        new String[]{
            // ======== Round 3 (Eve goes first) ========
            // initial state:
            // Eve: Handmaiden, Alice: Guard, Bob: Priest, Charlie: Guard, David: Constable 

            // ==== mini round 1 ====
            // Eve: (Handmaiden), Alice: Guard, Bob: Priest, Charlie: Guard, David: Constable 
            "0",        // Eve plays Handmaiden 

            // Eve: Prince, Alice: (Guard), Bob: Priest, Charlie: Guard, David: Constable 
            "0",        // Alice plays Guard
            "Guard",    // Guess Guard
            "Bob",      // Target Bob

            // Eve: Prince, Alice: King, Bob: (Priest), Charlie: Guard, David: Constable
            "0",        // Bob plays Priest
            "David",    // Target David

            // Eve: Prince, Alice: King, Bob: Countess, Charlie: (Guard), David: Constable 
            "0",        // Charlie plays Guard 
            "Guard",    // Guess Guard
            "David",    // Target David

            // Eve: Prince, Alice: King, Bob: Countess, Charlie: Priest, David: (Constable) 
            "0",        // David plays Constable

            // ==== mini round 2 ====
            // Eve: (Prince), Alice: King, Bob: Countess, Charlie: Priest, David: Guard 
            "0",        // Eve plays Prince
            "Charlie",  // Target Charlie, Charlie discards Priest

            // Eve: Count, Alice: (King), Bob: Countess, Charlie: Guard, David: Guard 
            "0",        // Alice plays King
            "Bob",      // Target Bob (switch hands)

            // Eve: Count, Alice: Countess, Bob: (Guard), Charlie: Guard, David: Guard
            "1",        // Bob plays Handmaiden
            
            // Eve: Count, Alice: Countess, Bob: Guard, Charlie: (Guard), David: Guard
            "0",        // Charlie plays Guard
            "Guard",    // guess Guard
            "Eve",      // Target Eve

            // Eve: Count, Alice: Countess, Bob: Guard, Charlie: Sycophant, David: (Guard)
            "0",        // David plays Guard
            "Countess", // guess Countess
            "Charlie",  // Target Charlie

            // ==== mini round 3 ====
            // Eve: (Count), Alice: Countess, Bob: Guard, Charlie: Sycophant, David: Cardinal 
            "0",        // Eve plays Count

            // Eve: Baron, Alice: (Countess), Bob: Guard, Charlie: Sycophant, David: Cardinal 
            "0",        // Alice plays Countess

            // Eve: Baron, Alice: Guard, Bob: (Guard), Charlie: Sycophant, David: Cardinal
            "0",        // Bob plays Guard 
            "Princess", // Guess Princess
            "Charlie",  // Target Charlie

            // Eve: Baron, Alice: Guard, Bob: Sycophant, Charlie: (Sycophant), David: Cardinal
            "0",        // Charlie plays Sycophant
            "Charlie",  // Target Charlie

            // Eve: Baron, Alice: Guard, Bob: Sycophant, Charlie: Baroness, David: (Cardinal)
            "0",        // David plays Cardinal
            "Eve",      // Target Eve (Charlie is forced to be the target)
            "Eve",    // Choose to look at Eve's hand

            // ==== mini round 4 ====
            // Eve: (Baroness), Alice: Guard, Bob: Sycophant, Charlie: Baron, David: Count
            "0",        // Eve plays Baroness
            "Alice",    // Target Alice
            "no",

            // Eve: Assassin, Alice: (Guard), Bob: Sycophant, Charlie: Baron, David: Count
            "0",        // Alice plays Guard
            "Count",    // Guess Count
            "David",    // Target David

            // Eve: Assassin, Alice: Baroness, Bob: (Sycophant), Charlie: Baron
            "0",        // Bob plays Sycophant
            "Charlie",  // Target Charlie 

            // Eve: Assassin, Alice: Baroness, Bob: Guard, Charlie: (Baron)
            "0",        // Charlie plays Baron, forced to target himself, discarded without effect 

            // ==== mini round 5 ====
            // Eve: (Assassin), Alice: Baroness, Bob: Guard, Charlie: Prince
            "0",        // Eve plays Assassin

            // Eve: Baron, Alice: (Baroness), Bob: Guard, Charlie: Prince
            "0",        // Alice plays Baroness
            "Bob",      // Target Bob
            "no",

            // Eve: Baron, Alice: Princess, Bob: (Guard), Charlie: Prince
            "0",        // Bob plays Guard
            "Queen",    // Guess Queen
            "Alice",    // Target Alice 

            // Eve: Baron, Alice: Princess, Bob: Cardinal, Charlie: (Prince)
            "0",        // Charlie plays Prince
            "Eve",      // Target Eve

            // ==== mini round 6 ====
            // Eve: (Bishop), Alice: Princess, Bob: Cardinal, Charlie: Queen
            "1",        // Eve plays Guard
            "Cardinal", // Guess Cardinal
            "Alice",    // Target Alice

            // Eve: Bishop, Alice: Princess, Bob: Cardinal, Charlie: Queen 
            // Alice wins Round 3 by comparing hands
            // Tokens Result: Eve: 0, Alice: 1, Bob: 0, Charlie: 0, David: 1
        },

        // Deck cards (in order of drawing)
        new Card[]{
            // dummy card
            Card.JESTER,

            // Initial cards (dealt to players)
            Card.HANDMAIDEN,        // Eve's initial card
            Card.GUARD,             // Alice's initial card
            Card.PRIEST,            // Bob's initial card
            Card.GUARD,             // Charlie's initial card
            Card.CONSTABLE,         // David's initial card

            // mini round 1
            Card.PRINCE,            // Eve draws
            Card.KING,              // Alice draws
            Card.COUNTESS,          // Bob draws
            Card.PRIEST,            // Charlie draws
            Card.GUARD,             // David draws

            // mini round 2
            Card.COUNT,             // Eve draws
            Card.GUARD,             // Charlie draws (Eve plays Prince targetting Charlie)
            Card.GUARD,             // Alice draws
            Card.HANDMAIDEN,        // Bob draws
            Card.SYCOPHANT,         // Charlie draws
            Card.CARDINAL,          // David draws

            // mini round 3
            Card.BARON,             // Eve draws
            Card.GUARD,             // Alice draws
            Card.SYCOPHANT,         // Bob draws
            Card.BARONESS,          // Charlie draws
            Card.COUNT,             // David draws

            // mini round 4
            Card.ASSASSIN,          // Eve draws
            Card.BARONESS,          // Alice draws
            Card.GUARD,             // Bob draws
            Card.PRINCE,            // Charlie draws

            // mini round 5
            Card.BARON,             // Eve draws
            Card.PRINCESS,          // Alice draws
            Card.CARDINAL,          // Bob draws
            Card.QUEEN,             // Charlie draws
            Card.BISHOP,            // Eve draws (Charlie plays Prince against Eve)

            // mini round 6
            Card.GUARD             // Eve draws
        }
    );

    private static final RoundScenario ROUND_4_SCENARIO = new RoundScenario(
        // Player inputs
        new String[]{
            // initial state:
            // Alice: GUARD, Bob: HANDMAIDEN, Charlie: PRIEST, David: JESTER, Eve: CONSTABLE

            // ==== mini round 1 ====
            // Alice: (GUARD), Bob: HANDMAIDEN, Charlie: PRIEST, David: JESTER, Eve: CONSTABLE
            "0",        // Alice plays Guard
            "Priest",   // Guess Priest
            "Bob",      // Target Bob (wrong)

            // Alice: GUARD, Bob: (HANDMAIDEN), KING, Charlie: PRIEST, David: JESTER, Eve: CONSTABLE
            "0",        // Bob plays Handmaiden

            // Alice: GUARD, Bob: KING, Charlie: (PRIEST), PRINCESS, David: JESTER, Eve: CONSTABLE
            "0",        // Charlie plays Priest
            "David",    // Target David

            // Alice: GUARD, Bob: KING, Charlie: COUNTESS, David: (JESTER), Eve: CONSTABLE
            "0",        // David plays Jester
            "David",    // Target David

            // Alice: GUARD, Bob: KING, Charlie: COUNTESS, David: BARON, Eve: (CONSTABLE)
            "0",        // Eve plays Constable

            // ==== mini round 2 ====
            // Alice: (GUARD), Bob: KING, Charlie: COUNTESS, David: BARON, Eve: HANDMAIDEN 
            "0",        // Alice plays Guard
            "Priest",   // Guess Priest
            "David",      // Target David (wrong) 

            // Alice: GUARD, Bob: (KING), Charlie: COUNTESS, David: BARON, Eve: HANDMAIDEN 
            "0",        // Bob plays KING
            "Eve",    // Target Eve

            // Alice: GUARD, Bob: HANDMAIDEN, Charlie: (COUNTESS), David: BARON, Eve: GUARD
            "0",        // Charlie plays COUNTESS

            // Alice: GUARD, Bob: HANDMAIDEN, Charlie: CARDINAL, David: (BARON), Eve: GUARD
            "0",        // David plays BARON
            "Eve",      // Target Eve (tie)

            // Alice: GUARD, Bob: HANDMAIDEN, Charlie: CARDINAL, David: GUARD, Eve: (GUARD)
            "0",        // Eve plays Guard
            "Priest",   // Guess Priest
            "Alice",    // Target Alice (wrong)

            // ==== mini round 3 ====
            // (Alice): GUARD, Bob: HANDMAIDEN, Charlie: CARDINAL, David: GUARD, Eve: PRIEST
            "1",        // Alice plays Cardinal
            "Eve",      // Target Eve
            "Bob",      // Target Bob
            "Bob",      // Target Bob

            // Alice: GUARD, Bob: (PRIEST), Charlie: CARDINAL, David: GUARD, Eve: HANDMAIDEN
            "0",        // Bob plays Priest
            "Eve",      // Target Eve

            // Alice: GUARD, Bob: PRINCE, Charlie: (CARDINAL), David: GUARD, Eve: HANDMAIDEN
            "0",        // Charlie plays Cardinal
            "Eve",      // Target Eve
            "Alice",    // Target Alice
            "Alice",    // Target Alice

            // Alice: HANDMAIDEN, Bob: PRINCE, Charlie: COUNT, David: (GUARD), Eve: GUARD
            "0",        // David plays Guard
            "Priest",   // Guess Priest
            "Eve",      // Target Eve (wrong) 

            // Alice: HANDMAIDEN, Bob: PRINCE, Charlie: COUNT, David: GUARD, Eve: (GUARD)
            "0",        // Eve plays Guard
            "Priest",   // Guess Priest
            "David",      // Target David (wrong) 

            // ==== mini round 4 ====
            // Alice: (HANDMAIDEN), Bob: PRINCE, Charlie: COUNT, David: GUARD, Eve: QUEEN
            "0",        // Alice plays Handmaiden
            
            // Alice: BARONESS, Bob: (PRINCE), Charlie: COUNT, David: GUARD, Eve: QUEEN
            "0",        // Bob plays Prince
            "Eve",      // Target Eve

            // Alice: BARONESS, Bob: BARONESS, Charlie: (COUNT), David: GUARD, Eve: PRINCE
            "0",        // Charlie plays Count

            // Alice: BARONESS, Bob: BARONESS, Charlie: SYCOPHANT, David: (GUARD), Eve: PRINCE
            "0",        // David plays Guard
            "Sycophant", // Guess Sycophant
            "Charlie",    // Target Charlie (killed)

            // Alice: BARONESS, Bob: BARONESS, David: BISHOP, Eve: (PRINCE)
            "0",        // Eve plays Prince
            "David",    // Target David

            // ==== mini round 5 ====
            // Alice: (BARONESS), Bob: BARONESS, David: GUARD, Eve: SYCOPHANT
            "0",        // Alice plays Baroness
            "David",    // Target David
            "no",       // Target no one

            // Alice: BARON, Bob: (BARONESS), David: GUARD, Eve: SYCOPHANT
            "0",        // Bob plays Baroness
            "David",    // Target David
            "no",       // Target no one

            // Alice: BARON, Bob: COUNT, David: (GUARD), Eve: SYCOPHANT
            "0",        // David plays Guard
            "Baron",    // Guess Baron
            "Eve",      // Target Eve

            // Alice: BARON, Bob: COUNT, David: ASSASSIN, (Eve): SYCOPHANT
            "1",        // Eve plays Guard
            "Count",    // Guess Count
            "David",    // Target David
        },

        // Deck cards (in order of drawing)
        new Card[]{
            // dummy card
            Card.PRINCESS,

            // Initial cards (dealt to players)
            Card.GUARD,             // Alice's initial card
            Card.HANDMAIDEN,        // Bob's initial card
            Card.PRIEST,            // Charlie's initial card
            Card.JESTER,         // David's initial card
            Card.CONSTABLE,      // Eve's initial card

            // mini round 1
            Card.GUARD,             // Alice draws
            Card.KING,              // Bob draws
            Card.COUNTESS,          // Charlie draws
            Card.BARON,             // David draws
            Card.HANDMAIDEN,        // Eve draws

            // mini round 2
            Card.GUARD,             // Alice draws
            Card.GUARD,             // Bob draws
            Card.CARDINAL,          // Charlie draws
            Card.GUARD,             // David draws
            Card.PRIEST,            // Eve draws

            // mini round 3
            Card.CARDINAL,          // Alice draws
            Card.PRINCE,            // Bob draws
            Card.COUNT,             // Charlie draws
            Card.GUARD,             // David draws
            Card.QUEEN,             // Eve draws

            // mini round 4
            Card.BARONESS,          // Alice draws
            Card.BARONESS,          // Bob draws
            Card.PRINCE,            // Eve draws after discard
            Card.SYCOPHANT,         //Charlie draws 
            Card.BISHOP,            //David draws
            Card.SYCOPHANT,         //Eve draws
            Card.GUARD,             //David draws after discard

            // mini round 5
            Card.BARON,          //Alice draws
            Card.COUNT,             //Bob draws
            Card.ASSASSIN,          //David draws
            Card.GUARD,              //Eve draws
        }
    );

    private static final GameScenario GAME_SCENARIO = new GameScenario(
        new String[]{"Alice", "Bob", "Charlie", "David", "Eve"},
        new RoundScenario[]{ROUND_1_SCENARIO, ROUND_2_SCENARIO, ROUND_3_SCENARIO, ROUND_4_SCENARIO}
    );

    private class MockDeck extends Deck {
        private final Card[] deckCards;

        public MockDeck(Card[] deckCards) {
            this.deckCards = deckCards;
        }

        @Override
        public void build32Cards() {
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

    /**
     * Test the whole game scenario with 5 players
     * David the winner will win the game by getting 4 tokens after 4 rounds
     * 
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testWholeGameScenario() throws NoSuchFieldException, IllegalAccessException {
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
        List<Player> winners = players.getGameWinner();
        assertEquals(1, winners.size());
        assertEquals(winnerName, winners.get(0).getName());
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
