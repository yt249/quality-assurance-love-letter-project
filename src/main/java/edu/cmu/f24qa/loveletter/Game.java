package edu.cmu.f24qa.loveletter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.f24qa.loveletter.actions.ActionFactory;
import edu.cmu.f24qa.loveletter.actions.CardAction;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class Game {
    private PlayerList players;
    private Deck deck;
    private GameContext context;
    private ActionFactory actionFactory;
    private int round;
    private List<Player> lastRoundWinners;

    public Game(PlayerList playerList, Deck deck, InputStream inputStream) {
        this.players = new PlayerList(playerList);
        this.deck = new Deck(deck);
        this.context = new GameContext(players, deck, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.actionFactory = new ActionFactory();
        this.round = 0;
        this.lastRoundWinners = new ArrayList<>();
    }

    public Game(PlayerList playerList, Deck deck, InputStream inputStream, ActionFactory actionFactory) {
        this.players = new PlayerList(playerList);
        this.deck = new Deck(deck);
        this.context = new GameContext(players, deck, new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        this.actionFactory = actionFactory;
        this.round = 0;
        this.lastRoundWinners = new ArrayList<>();
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

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    /*
     * Gets last round's winners.
     */
    public List<Player> getLastRoundWinners() {
        return lastRoundWinners;
    }

    public void promptForPlayers() {
        System.out.print("Enter player name (empty when done): ");
        String name = context.readLine();
        while (!name.equals("")) {
            this.players.addPlayer(name);
            System.out.print("Enter player name (empty when done): ");
            name = context.readLine();
        }
        
        int playerCount = this.players.getPlayers().size();
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalStateException("Invalid number of players. Only 2-4 players are allowed.");
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
        if (round > 0) {
            keepLastRoundWinnerFirst();
        }
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
        lastRoundWinners.clear();
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
            lastRoundWinners.add(winner);
            winner.addToken();
            System.out.println(winner.getName() + " has won this round!");
            players.print();
        } else if (!tiedWinners.isEmpty()) {
            lastRoundWinners.addAll(tiedWinners);
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
            for (int i = 0; i < 3; i++) {
                Card drawnCard = deck.draw();
                System.out.println(drawnCard + " was removed from the deck.");
            }
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

    /**
    *  Ensure winner of last round is first in the player list 
    *  - rotate the list so the order of players won't change 
    */
    private void keepLastRoundWinnerFirst() {
        // Preserve the previous round winner and move them to the front
        // if there's a tie in the last round, always pick the first player to start first
        if (lastRoundWinners.isEmpty()) {
            throw new IllegalStateException("There is no winner in the last round");
        }
        Player roundWinner = lastRoundWinners.get(0);
        // Rotate the list so the winner becomes the first player
        players.rotatePlayerList(roundWinner);
    }

}
