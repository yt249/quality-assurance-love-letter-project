package edu.cmu.f24qa.loveletter.actions;

import java.util.List;

import edu.cmu.f24qa.loveletter.GameContext;
import edu.cmu.f24qa.loveletter.Player;

public class JesterAction implements CardAction {
    @Override
    public void execute(GameContext context) {
        Player user = context.getCurrentUser();
        if (user == null) {
            System.out.println("No current user found");
            return;
        }

        List<Player> opponents = context.selectOpponents(1, 1, true);
        if (opponents == null || opponents.isEmpty()) {
            System.out.println("Jester is discarded without effect.");
            return;
        }
        Player guessedPlayer = opponents.get(0);
        context.setGuessedPlayer(guessedPlayer);
        context.setJesterPlayer(user);
        System.out.println(user.getName() + " has guessed that " + guessedPlayer.getName() + " will win the round.");
    }
}