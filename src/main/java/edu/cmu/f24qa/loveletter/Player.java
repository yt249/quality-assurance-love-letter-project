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

    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
        this.discarded = new DiscardPile();
        this.isProtected = false;
        this.tokens = 0;
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

    /**
     * Switches the user's level of protection.
     */
    public void switchProtection() {
        this.isProtected = !this.isProtected;
    }

    public Hand getHand() {
        return this.hand.copy();
    }

    public DiscardPile getDiscarded() {
        return this.discarded.copy();
    }

    /**
     * Checks to see if the user is protected by a handmaiden.
     *
     * @return true, if the player is protected, false if not
     */
    public boolean isProtected() {
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
