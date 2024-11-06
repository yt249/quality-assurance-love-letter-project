package edu.cmu.f24qa.loveletter.actions;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class GuardAction implements CardAction {
    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        System.out.print("Which card would you like to guess: ");
        String guessedCardName = context.readLine();

        context.selectOpponent().ifPresent(opponent -> {
            if (opponent.getHand().peek(0).getName().equals(guessedCardName)) {
                System.out.println("You have guessed correctly!");
                opponent.eliminate();
            } else {
                System.out.println("You have guessed incorrectly.");
            }
        });
    }
}