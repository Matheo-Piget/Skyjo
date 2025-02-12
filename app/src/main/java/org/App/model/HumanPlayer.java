package org.App.model;

import java.util.ArrayList;
import java.util.List;

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
    private final String nom;
    private final List<Card> cartes;
    private int commutativeScore = 0;

    /**
     * Constructs a new HumanPlayer with the specified name.
     *
     * @param nom The name of the human player.
     */
    public HumanPlayer(String nom) {
        this.nom = nom;
        this.cartes = new ArrayList<>();
        this.commutativeScore = 0;
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