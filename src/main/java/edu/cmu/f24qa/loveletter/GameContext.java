package edu.cmu.f24qa.loveletter;

import java.util.Optional;
import java.util.Scanner;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class GameContext {
    private PlayerList players;
    private Deck deck;
    private Scanner inputScanner;
    private @Nullable Player currentUser;

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public GameContext(PlayerList players, Deck deck, Readable inputScanner) {
        this.players = players;
        this.deck = deck;
        this.inputScanner = new Scanner(inputScanner);
    }

    public PlayerList getPlayers() {
        return new PlayerList(players);
    }

    public Deck getDeck() {
        return new Deck(deck);
    }

    public Card drawCard() {
        return deck.draw();
    }

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP2", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public void setCurrentUser(Player user) {
        this.currentUser = user;
    }

    @SuppressFBWarnings(
        value = "EI_EXPOSE_REP", 
        justification = "Game-Specific Business Logic Requires Direct Reference"
    )
    public @Nullable Player getCurrentUser() {
        return currentUser;
    }

    public Optional<Player> selectOpponent() {
        Optional<Player> selectedOpponent = Optional.empty();
        boolean isAllOpponentProtected = true;
        for (Player player: players.getPlayers()) {
            if (!player.equals(this.getCurrentUser()) && !player.getIsProtected()) {
                isAllOpponentProtected = false;
            }
        }
        if (isAllOpponentProtected) {
            System.out.println("All opponents are protected. Move to the next player.");
            return selectedOpponent;
        }
        while (selectedOpponent.isEmpty()) {
            System.out.print("Who would you like to target: ");
            String opponentName = inputScanner.nextLine();
            selectedOpponent = Optional.ofNullable(players.getPlayer(opponentName));
            
            if (selectedOpponent.isEmpty()) {
                System.out.println("No such player found. Please try again.");
            } else if (selectedOpponent.get().getIsProtected()) {
                System.out.println(selectedOpponent.get().getName() + " is protected. Please select another opponent.");
            }
        }
    
        return selectedOpponent;
    }

    public String readLine() {
        return inputScanner.nextLine();
    }
}