package org.App.model.player;

import java.util.ArrayList;
import java.util.List;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;

/**
 * The AIPlayer class represents an AI-controlled player in the Skyjo game.
 * It implements the {@link Player} interface and delegates decision-making
 * to an {@link AIStrategy} based on the selected {@link Difficulty}.
 * <p>
 * The AI player can perform actions such as picking cards, exchanging cards,
 * and revealing cards, depending on the game state and its strategy.
 * </p>
 *
 * @see Player
 * @see AIStrategy
 * @see Difficulty
 * @see SkyjoGame
 *
 * @author Mathéo Piget
 * @version 2.0
 */
public class AIPlayer implements Player {
    private final int id;
    private final String name;
    private final List<Card> cartes;
    private final Difficulty difficulty;
    private final AIStrategy strategy;
    private int cumulativeScore = 0;

    /**
     * Constructs a new AIPlayer with the specified name and difficulty level.
     * The difficulty is automatically mapped to the corresponding {@link AIStrategy}.
     *
     * @param id         The unique identifier of the AI player.
     * @param name       The name of the AI player.
     * @param difficulty The difficulty level of the AI player.
     */
    public AIPlayer(int id, String name, Difficulty difficulty) {
        this.id = id;
        this.name = name;
        this.cartes = new ArrayList<>();
        this.difficulty = difficulty;
        this.strategy = switch (difficulty) {
            case EASY -> new EasyStrategy();
            case MEDIUM -> new MediumStrategy();
            case HARD -> new HardStrategy();
        };
    }

    /**
     * Constructs a new AIPlayer with a custom strategy (for dependency injection).
     *
     * @param id       The unique identifier of the AI player.
     * @param name     The name of the AI player.
     * @param strategy The AI strategy to use.
     */
    public AIPlayer(int id, String name, AIStrategy strategy) {
        this.id = id;
        this.name = name;
        this.cartes = new ArrayList<>();
        this.difficulty = null;
        this.strategy = strategy;
    }

    @Override
    public void addScore(int score) {
        cumulativeScore += score;
    }

    @Override
    public int getCumulativeScore() {
        return cumulativeScore;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setCards(List<Card> cards) {
        this.cartes.clear();
        this.cartes.addAll(cards);
    }

    @Override
    public List<Card> getCartes() {
        return cartes;
    }

    @Override
    public void piocher(Card card) {
        cartes.add(card);
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

    @Override
    public int getId() {
        return id;
    }

    /**
     * Plays a turn for the AI player by delegating to its strategy.
     * This method only modifies model state — the controller handles view updates.
     *
     * @param game The current game instance.
     */
    public void playTurn(SkyjoGame game) {
        strategy.playTurn(game, this);
    }
}
