package edu.cmu.f24qa.loveletter.actions;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class PrinceAction implements CardAction {
    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        context.selectOpponent().ifPresent(opponent -> {
            System.out.println(opponent.getName() + " discards their card.");
            if (opponent.getHand().peek(0).getValue() == 8) { // Assuming 8 is the Princess
                opponent.eliminate();
                System.out.println(opponent.getName() + " had the Princess and is eliminated.");
            } else {
                opponent.getHand().remove(0);  // Discard current card
                opponent.getHand().add(context.getDeck().draw());  // Draw a new card
                System.out.println(opponent.getName() + " draws a new card.");
            }
        });
    }
}