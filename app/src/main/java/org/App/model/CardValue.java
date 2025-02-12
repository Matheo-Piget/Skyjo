package org.App.model;

/**
 * The {@code CardValue} enum represents the possible values of a card in the
 * game.
 * Each card value is associated with an integer value.
 * <p>
 * The possible card values are:
 * <ul>
 * <li>{@link #MOINS_DEUX} (-2)</li>
 * <li>{@link #MOINS_UN} (-1)</li>
 * <li>{@link #ZERO} (0)</li>
 * <li>{@link #UN} (1)</li>
 * <li>{@link #DEUX} (2)</li>
 * <li>{@link #TROIS} (3)</li>
 * <li>{@link #QUATRE} (4)</li>
 * <li>{@link #CINQ} (5)</li>
 * <li>{@link #SIX} (6)</li>
 * <li>{@link #SEPT} (7)</li>
 * <li>{@link #HUIT} (8)</li>
 * <li>{@link #NEUF} (9)</li>
 * <li>{@link #DIX} (10)</li>
 * <li>{@link #ONZE} (11)</li>
 * <li>{@link #DOUZE} (12)</li>
 * </ul>
 * <p>
 * Each enum constant has an associated integer value that can be retrieved
 * using the {@link #getValue()} method.
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