package org.App.model.player;

import java.util.List;

import org.App.model.game.Card;

/**
 * Represents a player in the Skyjo game.
 * A player has a name, a unique ID, a list of cards, and a cumulative score.
 * <p>
 * This interface defines the common behavior for both human and AI players.
 * </p>
 *
 * @see HumanPlayer
 * @see AIPlayer
 * @see Card
 *
 * @author Mathéo Piget
 * @version 2.0
 */
public interface Player {

    /**
     * Sets the list of cards for the player.
     *
     * @param cards The list of cards to set.
     */
    void setCards(List<Card> cards);

    /**
     * Gets the unique identifier of the player.
     *
     * @return The unique identifier of the player.
     */
    int getId();

    /**
     * Gets the cumulative score of the player across all rounds.
     *
     * @return The cumulative score of the player.
     */
    int getCumulativeScore();

    /**
     * Adds a round score to the player's cumulative total.
     *
     * @param score The score to add.
     */
    void addScore(int score);

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
