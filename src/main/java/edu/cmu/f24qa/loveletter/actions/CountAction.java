package edu.cmu.f24qa.loveletter.actions;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;
import edu.umd.cs.findbugs.annotations.Nullable;

public class CountAction implements CardAction {

    @Override
    public void execute(GameContext context) {
        @Nullable Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }
        System.out.println(user.getName() + " played the Count card. No action taken.");
    }
    
}
