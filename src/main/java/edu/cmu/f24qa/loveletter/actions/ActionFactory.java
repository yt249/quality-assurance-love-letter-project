package edu.cmu.f24qa.loveletter.actions;

public class ActionFactory {
    public CardAction getAction(String cardName) {
        return switch (cardName.toLowerCase()) {
            case "guard" -> new GuardAction();
            case "priest" -> new PriestAction();
            case "baron" -> new BaronAction();
            case "prince" -> new PrinceAction();
            case "king" -> new KingAction();
            case "handmaiden" -> new HandmaidenAction();
            case "princess" -> new PrincessAction();
            default -> throw new IllegalArgumentException("Invalid card name: " + cardName);
        };
    }
}