package edu.cmu.f24qa.loveletter;

public class Player {
    private String name;
    private Hand hand;

    private DiscardPile discarded;

    /**
     * True if the player is protected by a handmaiden, false if not.
     */
    private boolean isProtected;

    /**
     * The number of blocks the player has won.
     */
    private int tokens;

    public Player(String name, Hand hand, DiscardPile discarded, boolean isProtected, int tokens) {
        this.name = name;
        this.hand = new Hand(hand);
        this.discarded = new DiscardPile(discarded);
        this.isProtected = isProtected;
        this.tokens = tokens;
    }

    public Player(Player player) {
        this.name = player.name;
        this.hand = new Hand(player.hand);
        this.discarded = new DiscardPile(player.discarded);
        this.isProtected = player.getIsProtected();
        this.tokens = player.tokens;

    }

    public void addToken() {
        this.tokens++;
    }

    /**
     * Eliminates the player from the round by discarding their hand.
     */
    public void eliminate() {
        this.discarded.add(this.hand.remove(0));
    }

    public boolean isEliminated() {
        return this.hand.getHand().isEmpty();
    }

    /**
     * Switches the user's level of protection.
     */
    public void switchProtection() {
        this.isProtected = !this.isProtected;
    }

    public Hand getHand() {
        return this.hand.copy();
    }

    public void addCard(Card card) {
        this.hand.add(card);
    }

    public DiscardPile getDiscarded() {
        return this.discarded.copy();
    }

    public void addCardToDiscarded(Card card) {
        this.discarded.add(card);
    }

    public Card removeCardFromHand(int index) {
        return this.hand.remove(index);
    }

    /**
     * Checks to see if the user is protected by a handmaiden.
     *
     * @return true, if the player is protected, false if not
     */
    public boolean getIsProtected() {
        return this.isProtected;
    }

    public int getTokens() {
        return this.tokens;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name + " (" + this.tokens + " tokens)";
    }
}
