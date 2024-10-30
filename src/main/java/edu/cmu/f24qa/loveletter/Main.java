package edu.cmu.f24qa.loveletter;

public class Main {

    public static void main(String[] args) {
        Game g = new Game(System.in);
        g.setPlayers();
        g.start();
    }
}
