package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

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
        List<Player> opponents = context.selectOpponents(1, 1, false);
        if (!opponents.isEmpty()) {
            Player opponent = opponents.get(0);
            System.out.println(opponent.getName() + " shows you a " + opponent.getHand().peek(0));
        }
    }
}