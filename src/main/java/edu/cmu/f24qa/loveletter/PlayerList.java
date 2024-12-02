package edu.cmu.f24qa.loveletter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public class PlayerList {

    private LinkedList<Player> players;

    public PlayerList() {
        this.players = new LinkedList<>();
    }

    /**
     * Copy constructor
     */
    public PlayerList(@Nullable PlayerList playerList) {
        this.players = (playerList != null && playerList.players != null)
        ? new LinkedList<>(playerList.players)
        : new LinkedList<>();
    }

    /**
     * Adds a new Player object with the given name to the PlayerList.
     *
     * @param name - the given player name
     *
     * @return true if the player is not already in the list and can be added, false
     *         if not
     */
    public boolean addPlayer(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        players.addLast(new Player(name, new Hand(), new DiscardPile(), false, 0));
        return true;
    }

    /**
     * Gets the first player in the list and adds them to end of the list.
     *
     * @return the first player in the list
     */
    public Player getCurrentPlayer() {
        Player current = players.removeFirst();
        players.addLast(current);
        return current;
    }

    /**
     * Resets all players within the list.
     */
    public void reset() {
        for (Player p : players) {
            p.clearHand();
            p.clearDiscarded();;
        }
    }

    /**
     * Prints the used pile of each Player in the list.
     */
    public void printUsedPiles() {
        for (Player p : players) {
            System.out.println("\n" + p.getName());
            p.getDiscarded().print();
        }
    }

    /**
     * Prints each Player in the list.
     */
    public void print() {
        System.out.println();
        for (Player p : players) {
            System.out.println(p);
        }
        System.out.println();
    }

    /**
     * Checks the list for a single Player with remaining cards.
     *
     * @return true if there is a winner, false if not
     */
    public boolean checkForRoundWinner() {
        int count = 0;
        for (Player p : players) {
            if (p.getHand().hasCards()) {
                count++;
            }
        }
        if (count == 0) {
            throw new IllegalStateException("No players have cards.");
        }
        return count == 1;
    }

    /**
     * Returns the winner of the round.
     *
     * @return the round winner
     */
    public @Nullable Player getRoundWinner() {
        for (Player p : players) {
            if (p.getHand().hasCards()) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns the winner of the game.
     *
     * @return the game winner
     */
    public @Nullable Player getGameWinner() {
        int playerCount = players.size();
        int tokensToWin;
        if (!(playerCount >= 2 && playerCount <= 4)) {
            throw new IllegalStateException("Invalid number of players");
        }
        if (playerCount == 2) {
            tokensToWin = 7;
        } else if (playerCount == 3) {
            tokensToWin = 5;
        } else {
            tokensToWin = 4;
        }
        for (Player p : players) {
            if (p.getTokens() == tokensToWin) {
                return p;
            }
        }
        return null;
    }

    /**
     * Deals a card to each Player in the list.
     *
     * @param deck - the deck of cards
     */
    public void dealCards(Deck deck) {
        for (Player p : players) {
            p.addCard(deck.draw());
        }
    }

    /**
     * Gets the player with the given name.
     *
     * @param name - the name of the desired player
     *
     * @return the player with the given name or null if there is no such player
     */
    public @Nullable Player getPlayer(String name) {
        if (players.isEmpty()) {
            throw new IllegalStateException("Player list is empty");
        }
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns the full list of players.
     *
     * @return the LinkedList of Player objects.
     */
    public LinkedList<Player> getPlayers() {
        return new LinkedList<>(players); // Defensive copy
    }

    /**
     * Returns the list of players with the highest used pile value.
     *
     * @return the list of players with the highest used pile value
     */
    public List<Player> compareUsedPiles(List<Player> tiedPlayers) {
        if (tiedPlayers.isEmpty()) {
            throw new IllegalStateException("Passed player list is empty");
        }
        List<Player> tiedWinners = new ArrayList<>();
        int highestDiscardPileValue = -1;

        for (Player player: players) {
            if (player.isEliminated()) {
                continue;
            }
            int discardPileValue = player.getDiscarded().value();
            if (discardPileValue > highestDiscardPileValue) {
                highestDiscardPileValue = discardPileValue;
                tiedWinners.clear();
                tiedWinners.add(player);
            } else if (discardPileValue == highestDiscardPileValue) {
                tiedWinners.add(player);
            }
        }
        return tiedWinners;
    }

    /*
     * Returns a list of players with the highest value hand card
     */
    public List<Player> compareHand() {
        if (players.isEmpty()) {
            throw new IllegalStateException("Player list is empty");
        }
        List<Player> tiedPlayers = new ArrayList<>();
        int highestHandValue = -1;

        for (Player player: players) {
            if (player.getHand().hasCards()) {
                int handValue = player.getHand().peek(0).getValue(); // Get the value of the card in hand
                if (handValue > highestHandValue) {
                    highestHandValue = handValue;
                    tiedPlayers.clear();
                    tiedPlayers.add(player);
                } else if (handValue == highestHandValue) {
                    tiedPlayers.add(player);
                }
            }
        }
        return tiedPlayers;
    }

    /* for testing purpose */
    public void addPlayer(Player player) {
        players.addLast(player);
    }
}
