package edu.cmu.f24qa.loveletter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class GameContext {
    private PlayerList players;
    private Deck deck;
    private Scanner inputScanner;
    private @Nullable Player currentUser;
    private @Nullable Player guessedPlayer;
    private @Nullable Player jesterPlayer;
    private @Nullable Player sycophantForcedPlayer;

    /**
     * Constructs a GameContext with the specified players, deck, and input scanner.
     *
     * @param players      The list of players in the game.
     * @param deck         The deck of cards used in the game.
     * @param inputScanner The input source for reading player input.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public GameContext(PlayerList players, Deck deck, Readable inputScanner) {
        this.players = players;
        this.deck = deck;
        this.inputScanner = new Scanner(inputScanner);
        this.sycophantForcedPlayer = null;
    }

    /**
     * Returns a copy of the player list.
     *
     * @return A new PlayerList containing the players in the game.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public PlayerList getPlayers() {
        return players;
    }

    /**
     * Returns a copy of the deck.
     *
     * @return A new Deck containing the cards in the game.
     */
    public Deck getDeck() {
        return new Deck(deck);
    }

    /**
     * Draws a card from the deck.
     *
     * @return The card drawn from the deck.
     */
    public Card drawCard() {
        return deck.draw();
    }

    /**
     * Sets the current user of the game.
     *
     * @param user The player to set as the current user.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public void setCurrentUser(Player user) {
        this.currentUser = user;
    }

    /**
     * Returns the current user of the game.
     *
     * @return The current user, or null if no user is set.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public @Nullable Player getCurrentUser() {
        return currentUser;
    }

    /**
     * Selects an opponent for the current user to target.
     * If all opponents are protected, informs the user and returns an empty Optional.
     * Otherwise, prompts the user to select an opponent by name.
     *
     * @return An Optional containing the selected opponent, or empty if no valid opponent is selected.
     *
     * Sets the player chosen by the Sycophant card.
     * @param player The player who must be chosen in the next card effect.
     */
    public void setSycophantForcedPlayer(@Nullable Player player) {
        this.sycophantForcedPlayer = player;
    }

    /**
     * Resets the Sycophant effect after the next card is played.
     */
    public void resetSycophantForcedPlayer() {
        this.sycophantForcedPlayer = null;
    }

    /**
     * Select opponents, considering Sycophant effect if active.
     * @param min Minimum number of opponents to select.
     * @param max Maximum number of opponents to select.
     * @param includeSelf whether it is allowed to target the currentPlayer itself 
     * @return A list of selected opponents.
     */
    public List<Player> selectOpponents(int min, int max, boolean includeSelf) {
        List<Player> selectedOpponents = new ArrayList<>();
        List<Player> availableOpponents = new ArrayList<>();
        Player currentPlayer = getCurrentUser();

        // Build a list of valid opponents
        for (Player player: players.getPlayers()) {
            boolean isCurrentPlayer = player.equals(currentPlayer);
            if ((!isCurrentPlayer && !player.getIsProtected() && !player.isEliminated()) ||
                (isCurrentPlayer && includeSelf)) {
                availableOpponents.add(player);
            }
        }

        // check if there are enough available players to satisfy the card's requirements
        if (selectedOpponents.size() + availableOpponents.size() < min) {
            System.out.println("Not enough available players can be selected to satisfy " +
                               "the requirement of targeting at least " + min + " player(s).");
            selectedOpponents.clear();
            return selectedOpponents;
        }

        // enforce Sycophant effect
        if (sycophantForcedPlayer != null) {
            // Check if the Sycophant-enforced player violates the targeting rules of the card
            if (sycophantForcedPlayer.equals(currentPlayer) && !includeSelf) {
                System.out.println("The Sycophant effect enforces targeting yourself, " +
                                   "but current card cannot target yourself.");
                return selectedOpponents; // Empty list indicates the card is discarded
            }
            System.out.println("The Sycophant effect enforces targeting " + sycophantForcedPlayer.getName() + ".");
            selectedOpponents.add(sycophantForcedPlayer);
            availableOpponents.remove(sycophantForcedPlayer);
        }
        
        // Let player manually select additional opponents if needed
        while (selectedOpponents.size() < Math.min(max, availableOpponents.size() + selectedOpponents.size())) {
            // Display available opponents excluding already selected ones
            System.out.println("Available opponents: ");
            for (Player opponent : availableOpponents) {
                System.out.println(opponent.getName());
            }
            System.out.print("Who would you like to target: ");
            String opponentName = this.readLine();
            Optional<Player> selectedOpponent = Optional.ofNullable(players.getPlayer(opponentName)); 
            if (selectedOpponent.isEmpty()) {
                System.out.println("No such player found. Please try again.");
            } else if (!availableOpponents.contains(selectedOpponent.get())) {
                System.out.println("Please target a player within available opponents");
            } else {
                Player opponent = selectedOpponent.get();
                selectedOpponents.add(opponent);
                availableOpponents.remove(opponent);

                if (selectedOpponents.size() >= min && selectedOpponents.size() < max) {
                    System.out.println("Would you like to select another player? (yes/no): ");
                    if (this.readLine().equalsIgnoreCase("no")) {
                        break;
                    }
                }
            }
        }
        return selectedOpponents;
    }

    /**
     * Reads a line of input from the input scanner.
     *
     * @return The line of input read.
     */
    public String readLine() {
        return inputScanner.nextLine();
    }

    /**
     * Sets the guessed player in the game context.
     *
     * @param player The player to set as the guessed player.
     */
    public void setGuessedPlayer(Player player) {
        this.guessedPlayer = player;
    }

    /**
     * Returns the guessed player in the game context.
     *
     * @return The guessed player, or null if no player is set.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public @Nullable Player getGuessedPlayer() {
        return guessedPlayer;
    }

    /**
     * Sets the jester player in the game context.
     *
     * @param player The player to set as the jester player.
     */
    public void setJesterPlayer(Player player) {
        this.jesterPlayer = player;
    }

    /**
     * Returns the jester player in the game context.
     *
     * @return The jester player, or null if no player is set.
     */
    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public @Nullable Player getJesterPlayer() {
        return jesterPlayer;
    }

    public Card getRemovedTopCard() {
        return deck.getRemovedTopCard();
    }

    /**
     * Draws a new card for the specified player from the deck or the removed top card.
     *
     * @param player The player who will draw a new card.
     */
    public void drawNewCardForPlayerByDeckStatus(Player player) {
        if (deck.hasMoreCards()) {
            player.addCard(drawCard());
        } else {
            player.addCard(getRemovedTopCard());
        }
    }

    /*
     * Initializes the deck based on the number of players.
     */
    public void initializeDeck() {
        int playerCount = players.getPlayers().size();
        if (playerCount >= 2 && playerCount <= 4) {
            this.deck.build16Cards();
        } else if (playerCount >= 5 && playerCount <= 8) {
            this.deck.build32Cards();
        } else {
            throw new IllegalStateException("Invalid number of players. Only 2-8 players are allowed.");
        }
        this.deck.shuffle();
        this.deck.clearRemovedTopCard();
    }

    /**
     * In the beginning of the game: 
     *  - remove 1 top card from deck and store it 
     *  - remove additional 3 cards from deck face up in a 2-player game
     */
    public void removeCardFromDeck() {
        int playerSize = players.getPlayers().size();

        // Remove 1 card from deck and store it
        deck.removeCardFromDeck();
        // Remove additional 3 cards from deck face up in a 2-player game
        if (playerSize == 2) {
            for (int i = 0; i < 3; i++) {
                Card drawnCard = deck.draw();
                System.out.println(drawnCard + " was removed from the deck.");
            }
        }
    }

    /**
     * Resets the game context.
     * 1. reset players' hands and discard piles
     * 2. deck formation (shuffle)
     * 3. remove card(s)
     * 4. reset game states
     */
    public void reset() {
        this.players.reset();
        this.initializeDeck();
        this.removeCardFromDeck();
        this.currentUser = null;
        this.guessedPlayer = null;
        this.jesterPlayer = null;
        this.sycophantForcedPlayer = null;
    }
}