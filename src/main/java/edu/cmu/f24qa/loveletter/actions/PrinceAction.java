package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class PrinceAction implements CardAction {
    /**
     * Executes the Prince card action, forcing a player to discard their card.
     * If the discarded card is the Princess, the player is eliminated.
     * Otherwise, the player draws a new card from the deck or the removed top card.
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
        
        // Select an opponent to target with the Prince card, including self as valid target
        List<Player> opponents = context.selectOpponents(1, 1, true);
        Player opponent = opponents.isEmpty() ? user : opponents.get(0);

        System.out.println(opponent.getName() + " discards their card.");
        
        // Check if the opponent has the Princess card
        if (opponent.getHand().peek(0).getValue() == 8) { // Assuming 8 is the Princess
            opponent.eliminate();
            System.out.println(opponent.getName() + " had the Princess and is eliminated.");
        } else {
            // Discard the current card and draw a new one
            opponent.addCardToDiscarded(opponent.getHand().peek(0));
            opponent.getHand().remove(0); // Discard current card
            context.drawNewCardForPlayerByDeckStatus(opponent);
            System.out.println(opponent.getName() + " draws a new card.");
        }
    }
}