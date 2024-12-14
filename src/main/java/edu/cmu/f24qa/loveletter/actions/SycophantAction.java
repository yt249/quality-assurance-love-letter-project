package edu.cmu.f24qa.loveletter.actions;

import org.checkerframework.checker.nullness.qual.Nullable;
import java.util.List;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class SycophantAction implements CardAction {
    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        List<Player> opponents = context.selectOpponents(1, 1, true);
        if (!opponents.isEmpty()) {
            Player opponent = opponents.get(0);
            context.setSycophantForcedPlayer(opponent);
            System.out.println(user.getName() + " played the Sycophant card and forced " + 
                opponent.getName() + " to be targeted when the next card is played.");
        } else {
            System.out.println(user.getName() + " played the Sycophant card but no opponent can be selected.");
        }
    }
}