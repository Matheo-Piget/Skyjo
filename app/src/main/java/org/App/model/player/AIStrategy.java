package org.App.model.player;

import java.util.List;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;

/**
 * Strategy interface for AI player decision-making.
 * Each difficulty level implements this interface with its own logic.
 * <p>
 * Follows the Strategy design pattern to allow interchangeable AI behaviors
 * without modifying the {@link AIPlayer} class.
 * </p>
 *
 * @see AIPlayer
 * @see EasyStrategy
 * @see MediumStrategy
 * @see HardStrategy
 *
 * @author Mathéo Piget
 * @version 1.0
 */
public interface AIStrategy {

    /**
     * Executes the AI's turn: picks a card, then exchanges or reveals.
     * This method only modifies model state — it does NOT call any controller or view.
     *
     * @param game   The current game instance.
     * @param player The AI player whose turn it is.
     */
    void playTurn(SkyjoGame game, AIPlayer player);

    /**
     * Checks if a card is beneficial (low or negative value).
     *
     * @param card The card to evaluate.
     * @return true if the card value is &lt;= 0.
     */
    default boolean isCardBeneficial(Card card) {
        return card.valeur().getValue() <= 0;
    }

    /**
     * Finds the index of the card whose replacement by {@code newCard}
     * yields the greatest score reduction.
     *
     * @param cartes  The player's current hand.
     * @param newCard The candidate card.
     * @return The best index to replace (always &gt;= 0 if hand is non-empty).
     */
    default int findBestCardToReplace(List<Card> cartes, Card newCard) {
        int bestIndex = 0;
        int bestDifference = Integer.MAX_VALUE;
        for (int i = 0; i < cartes.size(); i++) {
            int difference = newCard.valeur().getValue() - cartes.get(i).valeur().getValue();
            if (difference < bestDifference) {
                bestDifference = difference;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    /**
     * Finds the index of the first hidden (face-down) card.
     *
     * @param cartes The player's current hand.
     * @return The index of a hidden card, or -1 if all cards are visible.
     */
    default int findHiddenCardIndex(List<Card> cartes) {
        for (int i = 0; i < cartes.size(); i++) {
            if (!cartes.get(i).faceVisible()) {
                return i;
            }
        }
        return -1;
    }
}
