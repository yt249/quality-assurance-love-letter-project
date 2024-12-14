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
        while (players.getGameWinner().size() == 0) {
            startRound();
        }
        List<Player> winners = players.getGameWinner();
        // if there are multiple winners, all tied players should play another round to break the tie
        if (winners.size() > 1) {
            winners = startRoundForTiedWinners(winners);
        }
        announceGameWinner(winners);
    }

    public void startRound() {
        if (round > 0) {
            keepLastRoundWinnerFirst();
        }
        setupNewGame();
        while (!players.checkForRoundWinner() && deck.hasMoreCards()) {
            Player turn = players.getCurrentPlayer();
            executeTurn(turn);
            // check if someone won the game after the turn
            if (players.getGameWinner().size() > 0) {
                return;
            }
        }
        determineRoundWinner();
        round += 1;
    }

    /*
     * If there are multiple winners, start a new round for the tied winners
     * 
     * @return the winners of the new round (only 1 winner)
     */
    public List<Player> startRoundForTiedWinners(List<Player> winnerList) {
        System.out.println("Tie! Starting a new round for the tied winners:");
        PlayerList winners = new PlayerList();
        for (Player winner : winnerList) {
            winners.addPlayer(winner);
        }

        Game breakTieGame = new Game(winners, new Deck(), System.in);
        breakTieGame.startRound();
        List<Player> roundWinners = breakTieGame.getLastRoundWinners();
        if (roundWinners.size() != 1) {
            return startRoundForTiedWinners(roundWinners);
        }
        return roundWinners;
    }

    public void setupNewGame() {
        context.reset();
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
        return turn.getHand().peek(otherCardPos).getName() == Card.COUNTESS.getName() ? otherCardPos : -1;
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

        // Check Jester guess
        Player guessedPlayer = context.getGuessedPlayer();
        Player jesterPlayer = context.getJesterPlayer();
        if (guessedPlayer != null && jesterPlayer != null && lastRoundWinners.contains(guessedPlayer)) {
            jesterPlayer.addToken();
            System.out.println(jesterPlayer.getName() + " guessed correctly and gains a token!");
        }
    }

    public void announceGameWinner(List<Player> winners) {
        int winnerCount = winners.size();
        if (winnerCount == 0) {
            throw new IllegalStateException("There is no winner in the game");
        } else if (winners.size() > 1) {
            throw new IllegalStateException("There are multiple winners in the game");
        }
        System.out.println(winners.get(0).getName() + " has won the game and the heart of the princess!");
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
        // only reset the sycophant effect if the card played is not a sycophant card
        if (!card.equals(Card.SYCOPHANT)) {
            context.resetSycophantForcedPlayer();
        }
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
