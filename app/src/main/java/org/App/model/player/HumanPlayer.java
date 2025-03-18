package org.App.model.player;

import java.util.ArrayList;
import java.util.List;

import org.App.model.game.Card;

/**
 * A class representing a human player in the game.
 * A human player has a name and a list of cards in their hand.
 * 
 * <p>
 * This class implements the {@link Player} interface.
 * </p>
 * 
 * @see Player
 * @see Card
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public final class HumanPlayer implements Player {
    private final int id;
    private final String nom;
    private final List<Card> cartes;
    private int commutativeScore = 0;

    /**
     * Constructs a new HumanPlayer with the specified name.
     *
     * @param nom The name of the human player.
     */
    public HumanPlayer(int id, String nom) {
        this.id = id;
        this.nom = nom;
        this.cartes = new ArrayList<>();
        this.commutativeScore = 0;
    }

    /**
     * Returns the unique identifier of the human player.
     * 
     * @return The unique identifier of the human player.
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Adds a score to the commutative score of the AI player.
     * 
     * @param score The score to add.
     */
    @Override
    public void addScore(int score) {
        commutativeScore += score;
    }

    /**
     * Sets the list of cards for the AI player.
     * 
     * @param carte The list of cards to set.
     * 
     * @see Card
     */
    @Override
    public void setCards(List<Card> carte) {
        this.cartes.clear();
        this.cartes.addAll(carte);
    }

    /**
     * Returns the commutative score of the AI player.
     * 
     * @return The commutative score of the AI player.
     */
    @Override
    public int getCommutativeScore() {
        return commutativeScore;
    }

    /**
     * Returns the name of the human player.
     *
     * @return The name of the human player.
     */
    @Override
    public String getName() {
        return nom;
    }

    /**
     * Returns the list of cards held by the human player.
     *
     * @return A list of {@link Card} instances.
     */
    @Override
    public List<Card> getCartes() {
        return cartes;
    }

    /**
     * Adds a card to the human player's hand.
     *
     * @param card The card to add to the player's hand.
     * 
     * @see Card
     */
    @Override
    public void piocher(Card card) {
        cartes.add(card);
    }
}