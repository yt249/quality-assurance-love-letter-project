package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class BaronAction implements CardAction {
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
            int userCardValue = user.getHand().peek(0).getValue();
            int opponentCardValue = opponent.getHand().peek(0).getValue();

            if (userCardValue > opponentCardValue) {
                System.out.println("You have won the comparison!");
                opponent.eliminate();
            } else if (userCardValue < opponentCardValue) {
                System.out.println("You have lost the comparison.");
                user.eliminate();
            } else {
                System.out.println("Tie! No one is eliminated.");
            }
        }
    }
}