package org.App.model;

/**
 * An enum representing the possible values of a card in the game.
 * Each value has an associated integer value.
 * 
 * @see Card
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public enum CardValue {

    MOINS_DEUX(-2), MOINS_UN(-1), ZERO(0), UN(1), DEUX(2), TROIS(3), QUATRE(4),
    CINQ(5), SIX(6), SEPT(7), HUIT(8), NEUF(9), DIX(10), ONZE(11), DOUZE(12);

    private final int valeur;

    /**
     * Constructs a CardValue with the specified integer value.
     *
     * @param valeur The integer value associated with the card value.
     */
    CardValue(int valeur) {
        this.valeur = valeur;
    }

    /**
     * Gets the integer value of the card value.
     *
     * @return The integer value of the card value.
     */
    public int getValue() {
        return valeur;
    }
}