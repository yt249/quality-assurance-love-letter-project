package edu.cmu.f24qa.loveletter;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Game g = new Game(in);
        g.setPlayers();
        g.start();
    }
   // Example method to demonstrate a rule violation
   public void exampleMethod(String param) {
        // Violation: Reassigning the method parameter
        param = "New Value"; // This line violates the AvoidReassigningParameters rule
        System.out.println(param);
    } 
}
