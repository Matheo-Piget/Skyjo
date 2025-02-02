package org.App.model;

import java.util.List;
/**
 * Represents a player in the game.
 */
public interface Player {

    /**
     * Gets the name of the player.
     *
     * @return the name of the player.
     */
    String getNom();

    /**
     * Gets the list of cards the player has.
     *
     * @return the list of cards.
     */
    List<Card> getCartes();

    /**
     * Draws a card and adds it to the player's hand.
     *
     * @param carte the card to be drawn.
     */
    void piocher(Card carte);
}
