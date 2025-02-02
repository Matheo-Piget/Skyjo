package org.App.model;

/**
 * Interface representing a card in the game.
 */
public interface ICard {

    /**
     * Gets the value of the card.
     *
     * @return the value of the card.
     */
    CardValue valeur();

    /**
     * Checks if the card is face up (visible).
     *
     * @return true if the card is face up, false otherwise.
     */
    boolean faceVisible();

    /**
     * Flips the card to change its visibility state.
     *
     * @return the card itself after flipping.
     */
    ICard retourner();
}