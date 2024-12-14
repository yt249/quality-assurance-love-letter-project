package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;
import edu.umd.cs.findbugs.annotations.Nullable;

public class BishopAction implements CardAction {

    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        System.out.print("Which number would you like to guess: ");
        int guessedCardValue = Integer.parseInt(context.readLine());

        List<Player> opponents = context.selectOpponents(1, 1, false);
        if (opponents.isEmpty()) {
            System.out.println("No opponent found. Bishop is discarded without effect.");
            return;
        }
        Player opponent = opponents.get(0);
        if (opponent.getHand().peek(0).getValue() == guessedCardValue) {
            System.out.println("You have guessed correctly! You get a token.");
            user.addToken();
            List<Player> winners = context.getPlayers().getGameWinner();
            if (winners.size() == 1 && user == winners.get(0)) {
                return;
            }
            // opponent can optionally discard its hand and draw a new card
            System.out.println(opponent.getName() + 
                ", would you like to discard your hand and draw a new card? (y/n)");
            String discardChoice = context.readLine();
            if (discardChoice.equals("y")) {
                // check if opponent's hand is PRINCESS
                if (opponent.getHand().peek(0).equals(Card.PRINCESS)) {
                    // PRINCESS takes effect
                    context.setCurrentUser(opponent);
                    CardAction princessAction = new PrincessAction();
                    princessAction.execute(context);
                    return;
                }
                Card discardedCard = opponent.removeCardFromHand(0);  // Discard current card
                opponent.addCardToDiscarded(discardedCard);
                opponent.addCard(context.drawCard());  // Draw a new card
                System.out.println(opponent.getName() + " discards hand and draws a new card.");
            }
        } else {
            System.out.println("You have guessed incorrectly.");
        }
    }
}

