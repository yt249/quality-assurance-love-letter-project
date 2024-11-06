package edu.cmu.f24qa.loveletter;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Game {
    private PlayerList players;
    private Deck deck;
    private Scanner in;

    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "For future game logic.")
    int round;

    public Game(PlayerList playerList, Deck deck, InputStream inputStream) {
        this.players = new PlayerList(playerList);
        this.deck = new Deck(deck);
        this.in = new Scanner(inputStream, StandardCharsets.UTF_8);  // Initialize Scanner here to control its lifecycle
        this.round = 0;
    }

    public void promptForPlayers() {
        System.out.print("Enter player name (empty when done): ");
        String name = in.nextLine();
        while (!name.equals("")) {
            this.players.addPlayer(name);
            System.out.print("Enter player name (empty when done): ");
            name = in.nextLine();
        }
    }

    /**
     * The main game loop.
     */
    public void start() {
        while (players.getGameWinner() == null) {
            players.reset();
            initializeDeck();
            players.dealCards(deck);
            while (!players.checkForRoundWinner() && deck.hasMoreCards()) {
                Player turn = players.getCurrentPlayer();

                if (turn.getHand().hasCards()) {
                    players.printUsedPiles();
                    System.out.println("\n" + turn.getName() + "'s turn:");
                    if (turn.getIsProtected()) {
                        turn.switchProtection();
                    }
                    turn.getHand().add(deck.draw());

                    int royaltyPos = turn.getHand().royaltyPos();
                    if (royaltyPos == -1) {
                        playCard(getCard(turn), turn);
                    } else {
                        if (royaltyPos == 0 && turn.getHand().peek(1).getValue() == 7) {
                            playCard(turn.getHand().remove(1), turn);
                        } else if (royaltyPos == 1 && turn.getHand().peek(0).getValue() == 7) {
                            playCard(turn.getHand().remove(0), turn);
                        } else {
                            playCard(getCard(turn), turn);
                        }
                    }
                }
            }

            Player winner;
            if (players.checkForRoundWinner() && players.getRoundWinner() != null) {
                winner = players.getRoundWinner();
            } else {
                winner = players.compareUsedPiles();
                winner.addToken();
            }

            if (winner != null) {
                winner.addToken();
                System.out.println(winner.getName() + " has won this round!");
                players.print();
            }
        }
        Player gameWinner = players.getGameWinner();
        System.out.println(gameWinner + " has won the game and the heart of the princess!");

    }

    private void initializeDeck() {
        this.deck.build();
        this.deck.shuffle();
    }

    /**
     * Plays a card from the user's hand.
     *
     * @param card - the played card
     * @param user - the player of the card
     */
    private void playCard(Card card, Player user) {
        String name = card.getName();
        int value = card.getValue();
        user.getDiscarded().add(card);

        if (value < 4 || value == 5 || value == 6) {
            Player opponent = getOpponent(in, players, user);
            if (opponent == null) {
                System.out.println("No such player found");
            } else if (name.equals("guard")) {
                useGuard(in, opponent);
            } else if (name.equals("preist")) {
                Card opponentCard = opponent.getHand().peek(0);
                System.out.println(opponent.getName() + " shows you a " + opponentCard);
            } else if (name.equals("baron")) {
                useBaron(user, opponent);
            } else if (name.equals("prince")) {
                opponent.eliminate();
            } else if (name.equals("king")) {
                useKing(opponent, user);
            }
        } else {
            if (value == 4) {
                System.out.println("You are now protected until your next turn");
            } else {
                if (value == 8) {
                    user.eliminate();
                }
            }
        }
    }

    /**
     * Allows for the user to pick a card from their hand to play.
     *
     * @param user - the current player
     *
     * @return the chosen card
     */
    private Card getCard(Player user) {
        user.getHand().print();
        System.out.println();
        System.out.print("Which card would you like to play (0 for first, 1 for second): ");
        String cardPosition = in.nextLine();
        int idx = Integer.parseInt(cardPosition);
        return user.getHand().remove(idx);
    }

    /**
     * Allows the user to guess a card that a player's hand contains (excluding
     * another guard).
     * If the user is correct, the opponent loses the round and must lay down their
     * card.
     * If the user is incorrect, the opponent is not affected.
     *
     * @param in - the input stream
     * @param opponent - the targeted player
     */
    private void useGuard(Scanner useGuardIn, Player opponent) {
        System.out.print("Which card would you like to guess: ");
        String cardName = useGuardIn.nextLine();

        Card opponentCard = opponent.getHand().peek(0);
        if (opponentCard.getName().equals(cardName)) {
            System.out.println("You have guessed correctly!");
            opponent.eliminate();
        } else {
            System.out.println("You have guessed incorrectly");
        }
        return;
    }

    /**
     * Allows the user to compare cards with an opponent.
     * If the user's card is of higher value, the opposing player loses the round
     * and their card.
     * If the user's card is of lower value, the user loses the round and their
     * card.
     * If the two players have the same card, their used pile values are compared in
     * the same manner.
     *
     * @param user - the initiator of the comparison
     * @param opponent - the targeted player
     */
    private void useBaron(
            Player user, Player opponent) {
        Card userCard = user.getHand().peek(0);
        Card opponentCard = opponent.getHand().peek(0);

        int cardComparison = Integer.compare(userCard.getValue(), opponentCard.getValue());
        if (cardComparison > 0) {
            System.out.println("You have won the comparison!");
            opponent.eliminate();
        } else if (cardComparison < 0) {
            System.out.println("You have lost the comparison");
            user.eliminate();
        } else {
            System.out.println("You have the same card!");
            if (opponent.getDiscarded().value() > user.getDiscarded().value()) {
                System.out.println("You have lost the used pile comparison");
                opponent.eliminate();
            } else {
                System.out.println("You have won the used pile comparison");
                user.eliminate();
            }
        }
    }

    /**
     * Allows the user to switch cards with an opponent.
     * Swaps the user's hand for the opponent's.
     *
     * @param user - the initiator of the swap
     * @param opponent - the targeted player
     */
    private void useKing(Player user, Player opponent) {
        Card userCard = user.getHand().remove(0);
        Card opponentCard = opponent.getHand().remove(0);
        user.getHand().add(opponentCard);
        opponent.getHand().add(userCard);
    }

    /**
     * Useful method for obtaining a chosen target from the player list.
     *
     * @param in - the input stream
     * @param playerList - the list of players
     * @param user - the player choosing an opponent
     * @return the chosen target player
     */
    private @Nullable Player getOpponent(Scanner getOpponentIn, PlayerList playerList, Player user) {
        System.out.print("Who would you like to target: ");
        String opponentName = getOpponentIn.nextLine();
        Player opponent = playerList.getPlayer(opponentName);
        if (opponent == null) {
            System.out.println("No such player found");
            return null;
        }
        return opponent;
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

}
