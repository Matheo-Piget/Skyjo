package org.App.model.game;

/**
 * Exception thrown when a player attempts an invalid move during the game.
 * Examples: exchanging a card at an out-of-bounds index, revealing an already
 * visible card, or acting out of turn.
 *
 * @author Mathéo Piget
 * @version 1.0
 */
public class InvalidMoveException extends RuntimeException {

    /**
     * Constructs a new InvalidMoveException with the specified detail message.
     *
     * @param message The detail message describing the invalid move.
     */
    public InvalidMoveException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidMoveException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public InvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
