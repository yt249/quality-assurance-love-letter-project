package edu.cmu.f24qa.loveletter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.f24qa.loveletter.actions.ActionFactory;
import edu.cmu.f24qa.loveletter.actions.CardAction;

public class Game {
    private PlayerList players;
    private Deck deck;
    private GameContext context;
    private ActionFactory actionFactory;
    private int round;

    public Game(PlayerList playerList, Deck deck, InputStream inputStream) {
        this.players = new PlayerList(playerList);
        this.deck = new Deck(deck);
        this.context = new GameContext(players, deck, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.actionFactory = new ActionFactory();
        this.round = 0;
    }

    public Game(PlayerList playerList, Deck deck, InputStream inputStream, ActionFactory actionFactory) {
        this.players = new PlayerList(playerList);
        this.deck = new Deck(deck);
        this.context = new GameContext(players, deck, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.actionFactory = actionFactory;
        this.round = 0;
    }

    /**
     * Gets a copy of the players list.
     *
     * @return a copy of the PlayerList object to avoid exposing internal
     *         representation.
     */
    public PlayerList getPlayers() {
        return new PlayerList(this.players);
    }

    /**
     * Gets a copy of the deck to avoid exposing internal representation.
     *
     * @return a copy of the current Deck instance.
     */
    public Deck getDeck() {
        return new Deck(this.deck);
    }

    /**
     * Gets the current round number.
     *
     * @return the round number.
     */
    public int getRound() {
        return round;
    }

    public void promptForPlayers() {
        System.out.print("Enter player name (empty when done): ");
        String name = context.readLine();
        while (!name.equals("")) {
            this.players.addPlayer(name);
            System.out.print("Enter player name (empty when done): ");
            name = context.readLine();
        }
    }

    /**
     * The main game loop.
     */
    public void start() {
        while (players.getGameWinner() == null) {
            startRound();
        }
        announceGameWinner();

    }

    public void startRound() {
        setupNewGame();
        while (!players.checkForRoundWinner() && deck.hasMoreCards()) {
            Player turn = players.getCurrentPlayer();
            executeTurn(turn);
        }
        determineRoundWinner();
        round += 1;
    }

    public void setupNewGame() {
        players.reset();
        initializeDeck();
        removeCardFromDeck();
        players.dealCards(deck);
    }

    public void executeTurn(Player turn) {
        if (turn.getHand().hasCards()) {
            displayTurnInfo(turn);
            if (turn.getIsProtected()) {
                turn.switchProtection();
            }
            turn.getHand().add(deck.draw());
            playTurnCard(turn);
        }
    }

    public void displayTurnInfo(Player turn) {
        players.printUsedPiles();
        System.out.println("\n" + turn.getName() + "'s turn:");
    }

    public void playTurnCard(Player turn) {
        int cardIdx = getCardIdx(turn);
        playCard(turn.removeCardFromHand(cardIdx), turn);
    }

    /**
     * Allows for the user to pick a card from their hand to play.
     *
     * @param user
     *             the current player
     *
     * @return the index of the chosen card
     */
    public int getCardIdx(Player user) {
        int countessIdxWithRoyalty = getCountessIdxWithRoyalty(user);
        if (countessIdxWithRoyalty != -1) {
            return countessIdxWithRoyalty;
        }
        user.getHand().print();
        System.out.println();
        System.out.print("Which card would you like to play (0 for first, 1 for second): ");
        String cardPosition = context.readLine();
        int idx = Integer.parseInt(cardPosition);
        return idx;
    }

    public int getCountessIdxWithRoyalty(Player turn) {
        int royaltyPos = turn.getHand().royaltyPos();
        if (royaltyPos == -1) {
            return -1;
        }
        int otherCardPos = (royaltyPos == 0) ? 1 : 0;
        return turn.getHand().peek(otherCardPos).getValue() == 7 ? otherCardPos : -1;
    }

    public void determineRoundWinner() {
        Player winner = null;
        List<Player> tiedWinners = new ArrayList<>();
        if (players.checkForRoundWinner()) {
            winner = players.getRoundWinner();
        } else {
            List<Player> highestHandPlayers = players.compareHand();
            if (highestHandPlayers.size() == 1) {
                winner = highestHandPlayers.get(0);
            } else if (highestHandPlayers.size() > 1) {
                tiedWinners = players.compareUsedPiles(highestHandPlayers);
                if (tiedWinners.size() == 1) {
                    winner = tiedWinners.get(0);
                }
            }
        }

        if (winner != null) {
            winner.addToken();
            System.out.println(winner.getName() + " has won this round!");
            players.print();
        } else if (!tiedWinners.isEmpty()) {
            System.out.println("It's a tie! The following players have won this round:");
            for (Player player : tiedWinners) {
                player.addToken();
                System.out.println(player.getName());
            }
            players.print();
        }
    }

    public void announceGameWinner() {
        Player gameWinner = players.getGameWinner();
        System.out.println(gameWinner + " has won the game and the heart of the princess!");
    }

    public void initializeDeck() {
        this.deck.build();
        this.deck.shuffle();
    }

    /**
     * In the beginning of the game: 
     *  - remove 1 card from deck 
     *  - remove additional 3 cards from deck face up in a 2-player game
     */
    public void removeCardFromDeck(){
        int playerSize = players.getPlayers().size();

        // Remove 1 card from deck
        deck.draw();

        // Remove additional 3 cards from deck face up in a 2-player game
        if (playerSize == 2){
            Card drawnCard1 = deck.draw();
            Card drawnCard2 = deck.draw();
            Card drawnCard3 = deck.draw();

            // Print out the three cards to indicate it is facing up
            System.out.println(drawnCard1 + " was removed from the deck.");
            System.out.println(drawnCard2 + " was removed from the deck.");
            System.out.println(drawnCard3 + " was removed from the deck.");
        }
    }

    /**
     * Plays a card from the user's hand.
     *
     * @param card - the played card
     * @param user - the player of the card
     */
    private void playCard(Card card, Player user) {
        String cardName = card.getName();

        user.addCardToDiscarded(card);
        context.setCurrentUser(user);

        CardAction action = actionFactory.getAction(cardName);
        action.execute(context); // Execute the action with GameContext
    }

}
