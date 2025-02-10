package org.App.model;

import java.util.List;

/**
 * Represents a player in the Skyjo game.
 * A player has a name, a list of cards, and a difficulty level (for AI players).
 * 
 * <p>
 * This interface defines the common behavior for both human and AI players.
 * </p>
 * 
 * @see HumanPlayer
 * @see AIPlayer
 * @see Card
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public interface Player {

    /**
     * Represents the difficulty levels for AI players.
     */
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    /**
     * Gets the name of the player.
     *
     * @return The name of the player.
     */
    String getName();

    /**
     * Gets the list of cards the player has.
     *
     * @return The list of cards as a {@link List} of {@link Card} instances.
     */
    List<Card> getCartes();

    /**
     * Draws a card and adds it to the player's hand.
     *
     * @param carte The card to be drawn.
     */
    void piocher(Card carte);
}