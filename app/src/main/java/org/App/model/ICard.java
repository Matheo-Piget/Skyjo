package org.App.model;

/**
 * Interface representing a card in the game.
 * A card has a value, a visibility state (face up or face down), and can be flipped.
 * 
 * @see Card
 * @see CardValue
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public interface ICard {

    /**
     * Gets the value of the card.
     *
     * @return The value of the card as a {@link CardValue}.
     */
    CardValue valeur();

    /**
     * Checks if the card is face up (visible).
     *
     * @return True if the card is face up, false otherwise.
     */
    boolean faceVisible();

    /**
     * Flips the card to change its visibility state.
     *
     * @return The card itself after flipping.
     */
    ICard retourner();
}