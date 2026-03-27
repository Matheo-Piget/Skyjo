package org.App.model.player;

import java.util.ArrayList;
import java.util.List;

import org.App.model.game.Card;

/**
 * Base implementation for all player types.
 * Holds the common state (id, name, cards, score) and default behavior.
 *
 * @see HumanPlayer
 * @see AIPlayer
 *
 * @author Mathéo Piget
 * @version 3.0
 */
public abstract class AbstractPlayer implements Player {
    private final int id;
    private final String name;
    private final List<Card> cartes;
    private int cumulativeScore = 0;

    protected AbstractPlayer(int id, String name) {
        this.id = id;
        this.name = name;
        this.cartes = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Card> getCartes() {
        return cartes;
    }

    @Override
    public void setCards(List<Card> cards) {
        this.cartes.clear();
        this.cartes.addAll(cards);
    }

    @Override
    public void piocher(Card card) {
        cartes.add(card);
    }

    @Override
    public int getCumulativeScore() {
        return cumulativeScore;
    }

    @Override
    public void addScore(int score) {
        cumulativeScore += score;
    }
}
