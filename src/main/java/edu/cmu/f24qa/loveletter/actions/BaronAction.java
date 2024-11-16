package edu.cmu.f24qa.loveletter.actions;

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

        context.selectOpponent().ifPresent(opponent -> {
            int userCardValue = user.getHand().peek(0).getValue();
            int opponentCardValue = opponent.getHand().peek(0).getValue();

            if (userCardValue > opponentCardValue) {
                System.out.println("You have won the comparison!");
                opponent.eliminate();
            } else if (userCardValue < opponentCardValue) {
                System.out.println("You have lost the comparison.");
                user.eliminate();
            } else {
                System.out.println("You have the same card!");
                int userDiscardPile = user.getDiscarded().value();
                int opponentDiscardPile = opponent.getDiscarded().value();
                if (userDiscardPile > opponentDiscardPile) {
                    System.out.println("You have lost the used pile comparison");
                    opponent.eliminate();
                } else if (userDiscardPile < opponentDiscardPile) {
                    System.out.println("You have won the used pile comparison");
                    user.eliminate();
                }
            }
        });
    }
}