package org.App.model.player;

/**
 * A class representing a human player in the game.
 *
 * @see Player
 * @see AbstractPlayer
 *
 * @author Mathéo Piget
 * @version 3.0
 */
public final class HumanPlayer extends AbstractPlayer {

    /**
     * Constructs a new HumanPlayer with the specified ID and name.
     *
     * @param id   The unique identifier.
     * @param name The name of the human player.
     */
    public HumanPlayer(int id, String name) {
        super(id, name);
    }
}
