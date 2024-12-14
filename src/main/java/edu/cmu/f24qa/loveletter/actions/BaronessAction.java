package edu.cmu.f24qa.loveletter.actions;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

import java.util.List;

public class BaronessAction implements CardAction {

    /*
     * The Baroness allows the user to select up to 2 opponents to look at their hands.
     */
    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }

        // Select up to 2 opponents
        List<Player> opponents = context.selectOpponents(1, 2, false);

        if (!opponents.isEmpty()) {
            // Print the cards in the opponents' hands
            for (Player opponent : opponents) {
                System.out.println(opponent.getName() + " shows you a " + opponent.getHand().peek(0));
            }
        }
    }
}