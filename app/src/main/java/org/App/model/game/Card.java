package org.App.model.game;

import java.io.Serializable;

/**
 * A record representing a card in the game.
 * A card has a value, a visibility state (face up or face down), and a unique ID.
 * <p>
 * Cards are immutable: flipping a card returns a new instance with the
 * opposite visibility state, preserving the same ID.
 * </p>
 *
 * @see ICard
 * @see CardValue
 *
 * @param valeur      The value of the card.
 * @param faceVisible Whether the card is face-up (visible).
 * @param id          The unique identifier of the card within a game session.
 *
 * @author Mathéo Piget
 * @version 2.0
 */
public record Card(CardValue valeur, boolean faceVisible, int id) implements ICard, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Flips the card to change its visibility state.
     *
     * @return A new {@link Card} instance with the opposite visibility state.
     */
    @Override
    public Card retourner() {
        return new Card(valeur, !faceVisible, id);
    }
}
