package org.App.model.player;

import java.util.ArrayList;
import java.util.List;

import org.App.model.game.Card;

/**
 * A class representing a human player in the game.
 * A human player has a name and a list of cards in their hand.
 *
 * @see Player
 * @see Card
 *
 * @author Mathéo Piget
 * @version 2.0
 */
public final class HumanPlayer implements Player {
    private final int id;
    private final String name;
    private final List<Card> cartes;
    private int cumulativeScore = 0;

    /**
     * Constructs a new HumanPlayer with the specified ID and name.
     *
     * @param id   The unique identifier.
     * @param name The name of the human player.
     */
    public HumanPlayer(int id, String name) {
        this.id = id;
        this.name = name;
        this.cartes = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void addScore(int score) {
        cumulativeScore += score;
    }

    @Override
    public void setCards(List<Card> cards) {
        this.cartes.clear();
        this.cartes.addAll(cards);
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
    public List<Card> getCartes() {
        return cartes;
    }

    @Override
    public void piocher(Card card) {
        cartes.add(card);
    }
}
