package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Hand;
import edu.cmu.f24qa.loveletter.Player;

public class KingAction implements CardAction {
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
            System.out.println("Swapping cards with " + opponent.getName());
            Hand userHand = user.getHand();
            Hand opponentHand = opponent.getHand();

            Card userCard = userHand.remove(0);
            Card opponentCard = opponentHand.remove(0);

            userHand.add(opponentCard);
            opponentHand.add(userCard);

            System.out.println("Card swap complete.");
        }
    }
}