package org.App.model;

/**
 * A record representing a card in the game.
 * A card has a value and a visibility state (face up or face down).
 * 
 * <p>
 * This class implements the {@link ICard} interface.
 * </p>
 * 
 * @see ICard
 * @see CardValue
 * 
 * @param valeur      The value of the card.
 * @param faceVisible The visibility state of the card.
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public record Card(CardValue valeur, boolean faceVisible) implements ICard {

    /**
     * Flips the card to change its visibility state.
     *
     * @return A new {@link Card} instance with the opposite visibility state.
     */
    @Override
    public Card retourner() {
        return new Card(valeur, !faceVisible);
    }
}