package edu.cmu.f24qa.loveletter.actions;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class AssassinAction implements CardAction {
    /**
     * Executes the Assassin card action, which currently takes no action.
     *
     * @param context The current game context.
     */
    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        System.out.println(user.getName() + " played the Assassin card. No action taken.");
    }

    /**
     * Handles the scenario where the Assassin card is guessed.
     * The guessing player is eliminated, and the opponent discards their card and draws a new one.
     *
     * @param context  The current game context.
     * @param user     The player who guessed the Assassin card.
     * @param opponent The opponent player.
     */
    public void handleAssassinGuessed(GameContext context, @Nullable Player user, Player opponent) {
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        System.out.println("You guessed Assassin! You are eliminated.");
        user.eliminate();
        opponent.addCardToDiscarded(opponent.removeCardFromHand(0));
        context.drawNewCardForPlayerByDeckStatus(opponent);
    }
} 