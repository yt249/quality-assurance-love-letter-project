package edu.cmu.f24qa.loveletter;

public class Main {

    public static void main(String[] args) {
        Game g = new Game(new PlayerList(), new Deck(), System.in);
        g.promptForPlayers();
        g.start();
    }
}
