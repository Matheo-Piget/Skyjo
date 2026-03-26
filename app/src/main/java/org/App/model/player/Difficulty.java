package org.App.model.player;

/**
 * Represents the difficulty levels for AI players.
 *
 * @see AIPlayer
 * @see AIStrategy
 *
 * @author Mathéo Piget
 * @version 1.0
 */
public enum Difficulty {

    /** Easy difficulty: random decisions. */
    EASY,

    /** Medium difficulty: prefers beneficial discards, targets high-value cards. */
    MEDIUM,

    /** Hard difficulty: strategic optimization to minimize total hand value. */
    HARD
}
