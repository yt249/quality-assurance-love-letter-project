package edu.cmu.f24qa.loveletter.actions;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class PriestAction implements CardAction {
    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        context.selectOpponent().ifPresent(opponent -> {
            System.out.println(opponent.getName() + " shows you a " + opponent.getHand().peek(0));
        });
    }
}