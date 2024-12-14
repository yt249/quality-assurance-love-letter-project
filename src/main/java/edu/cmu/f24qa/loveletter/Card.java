package edu.cmu.f24qa.loveletter;

public enum Card {

    // Card values for 2-4 players
    GUARD("Guard", 1),
    PRIEST("Priest", 2),
    BARON("Baron", 3),
    HANDMAIDEN("Handmaiden", 4),
    PRINCE("Prince", 5),
    KING("King", 6),
    COUNTESS("Countess", 7),
    PRINCESS("Princess", 8),

    // Card values for 5-8 players
    JESTER("Jester", 0),
    ASSASSIN("Assassin", 0),
    CARDINAL("Cardinal", 2),
    BARONESS("Baroness", 3),
    SYCOPHANT("Sycophant", 4),
    COUNT("Count", 5),
    CONSTABLE("Constable", 6),
    QUEEN("Queen", 7),
    BISHOP("Bishop", 9);

    private String name;
    private int value;

    /**
     * Constructor for a card object.
     *
     * @param name - the name of the card
     * @param value - the value of the card
     */
    Card(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Getter for the card's value.
     *
     * @return the value of the card.
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Getter for the card's name.
     *
     * @return the name of the card.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name + " (" + value + ")";
    }
}
