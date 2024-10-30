package edu.cmu.f24qa.loveletter;

public enum Card {
    GUARD("Guard", 1),
    PRIEST("Priest", 2),
    BARON("Baron", 3),
    HANDMAIDEN("Handmaiden", 4),
    PRINCE("Prince", 5),
    KING("King", 6),
    COUNTESS("Countes", 7),
    PRINCESS("Princess", 8);

    private String name;
    private int value;

    /**
     * All possible card names.
     */
    public static final String[] CARD_NAMES = {
        "guard",
        "priest",
        "baron",
        "handmaiden",
        "prince",
        "king",
        "countess",
        "princess"
    };

    /**
     * Constructor for a card object.
     *
     * @param name
     *          the name of the card
     * @param value
     *          the value of the card
     */
    Card(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name + " (" + value + ")";
    }
}
