package org.App.model.player;

import org.App.model.game.SkyjoGame;

/**
 * The AIPlayer class represents an AI-controlled player in the Skyjo game.
 * Delegates decision-making to an {@link AIStrategy} based on {@link Difficulty}.
 *
 * @see Player
 * @see AbstractPlayer
 * @see AIStrategy
 * @see Difficulty
 *
 * @author Mathéo Piget
 * @version 3.0
 */
public class AIPlayer extends AbstractPlayer {
    private final Difficulty difficulty;
    private final AIStrategy strategy;

    /**
     * Constructs a new AIPlayer with the specified name and difficulty level.
     *
     * @param id         The unique identifier of the AI player.
     * @param name       The name of the AI player.
     * @param difficulty The difficulty level of the AI player.
     */
    public AIPlayer(int id, String name, Difficulty difficulty) {
        super(id, name);
        this.difficulty = difficulty;
        this.strategy = switch (difficulty) {
            case EASY -> new EasyStrategy();
            case MEDIUM -> new MediumStrategy();
            case HARD -> new HardStrategy();
        };
    }

    /**
     * Constructs a new AIPlayer with a custom strategy (for dependency injection / testing).
     *
     * @param id       The unique identifier of the AI player.
     * @param name     The name of the AI player.
     * @param strategy The AI strategy to use.
     */
    public AIPlayer(int id, String name, AIStrategy strategy) {
        super(id, name);
        this.difficulty = null;
        this.strategy = strategy;
    }

    /**
     * Returns the difficulty level of the AI player.
     *
     * @return The difficulty level, or null if constructed with a custom strategy.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the strategy used by this AI player.
     *
     * @return The AI strategy instance.
     */
    public AIStrategy getStrategy() {
        return strategy;
    }

    /**
     * Plays a turn for the AI player by delegating to its strategy.
     *
     * @param game The current game instance.
     */
    public void playTurn(SkyjoGame game) {
        strategy.playTurn(game, this);
    }
}
