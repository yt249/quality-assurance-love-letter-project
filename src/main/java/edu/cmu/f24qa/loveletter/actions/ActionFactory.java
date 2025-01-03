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
            case "countess" -> new CountessAction();
            case "assassin" -> new AssassinAction();
            case "jester" -> new JesterAction();
            case "count" -> new CountAction();
            case "constable" -> new ConstableAction();
            case "sycophant" -> new SycophantAction();
            case "queen" -> new QueenAction();
            case "cardinal" -> new CardinalAction();
            case "bishop" -> new BishopAction();
            case "baroness" -> new BaronessAction();
            default -> throw new IllegalArgumentException("Invalid card name: " + cardName);
        };
    }
}