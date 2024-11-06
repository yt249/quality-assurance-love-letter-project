package edu.cmu.f24qa.loveletter;

import java.util.Optional;
import java.util.Scanner;

import org.checkerframework.checker.nullness.qual.Nullable;

public class GameContext {
    private PlayerList players;
    private Deck deck;
    private Scanner inputScanner;
    private @Nullable Player currentUser;

    public GameContext(PlayerList players, Deck deck, Readable inputScanner) {
        this.players = new PlayerList(players);
        this.deck = new Deck(deck);;
        this.inputScanner = new Scanner(inputScanner);
    }

    public PlayerList getPlayers() {
        return new PlayerList(players);
    }

    public Deck getDeck() {
        return deck;
    }

    public void setCurrentUser(Player user) {
        this.currentUser = new Player(user);
    }

    public @Nullable Player getCurrentUser() {
        return (currentUser != null) ? new Player(currentUser) : null;
    }

    public Optional<Player> selectOpponent() {
        System.out.print("Who would you like to target: ");
        String opponentName = inputScanner.nextLine();
        return Optional.ofNullable(players.getPlayer(opponentName));
    }

    public String readLine() {
        return inputScanner.nextLine();
    }
}