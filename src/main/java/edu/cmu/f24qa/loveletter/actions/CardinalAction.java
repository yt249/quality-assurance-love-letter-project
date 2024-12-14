package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import edu.cmu.f24qa.loveletter.Card;
import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class CardinalAction implements CardAction {
    /**
     * Execute the Cardinal action.
     * 
     * @param context the game context
     */
    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }

        // Get two opponents and perform swap if both are selected (can includes user itself)
        List<Player> opponents = context.selectOpponents(2, 2, true);

        if (opponents.size() == 2) {
            Player opponent1 = opponents.get(0);
            Player opponent2 = opponents.get(1);
            
            System.out.println("Swapping cards between " + opponent1.getName() + " and " + opponent2.getName());
            
            // Perform the swap
            Card card1 = opponent1.removeCardFromHand(0);
            Card card2 = opponent2.removeCardFromHand(0);
            opponent1.addCard(card2);
            opponent2.addCard(card1);
            
            System.out.println("Card swap complete between opponents.");
            
            // Allow current user to then select one player from the previous selected two players
            // to see the card
            Player selectedOpponent = null;

            while (selectedOpponent == null) {
                System.out.print("Which player's hand would you like to look at: ");
                String inputOpponentName = context.readLine();
            
                if (inputOpponentName.equals(opponent1.getName())) {
                    selectedOpponent = opponent1;
                    break;
                } else if (inputOpponentName.equals(opponent2.getName())) {
                    selectedOpponent = opponent2;
                    break;
                }

                if (selectedOpponent == null) {
                    System.out.println("Please select one of the two players whose cards were swapped.");
                }
            }   

            @SuppressWarnings("null")
            Card opponentCard = selectedOpponent.getHand().peek(0);
            System.out.println(selectedOpponent.getName() + " shows you a " + opponentCard.toString());
        }
    }
}