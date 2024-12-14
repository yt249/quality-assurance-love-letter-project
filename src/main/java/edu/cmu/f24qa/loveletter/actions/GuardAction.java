package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.Card;
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

        List<Player> opponents = context.selectOpponents(1, 1, false);
        if (!opponents.isEmpty()) {
            Player opponent = opponents.get(0);
            String opponentCardName = opponent.getHand().peek(0).getName();
            if (opponentCardName.equals(Card.ASSASSIN.getName())) {
                AssassinAction assassinAction = new AssassinAction();
                assassinAction.handleAssassinGuessed(context, user, opponent);
            } else if (opponentCardName.equals(guessedCardName)) {
                System.out.println("You have guessed correctly!");
                opponent.eliminate();
            } else {
                System.out.println("You have guessed incorrectly.");
            }
        } 
    }
}