package org.App.model.player;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;

/**
 * AI strategy for HARD difficulty.
 * Strategically minimizes the total hand value.
 * Picks from the discard pile when beneficial, otherwise picks from the deck
 * and replaces the card that yields the largest score reduction.
 *
 * @author Mathéo Piget
 * @version 1.0
 */
public final class HardStrategy implements AIStrategy {

    @Override
    public void playTurn(SkyjoGame game, AIPlayer player) {
        Card topDiscard = game.getTopDiscard();
        Card pickedCard;

        if (topDiscard != null && isCardBeneficial(topDiscard)) {
            pickedCard = game.pickDiscard();
            if (pickedCard != null) {
                int cardIndex = findBestCardToReplace(player.getCartes(), pickedCard);
                if (cardIndex != -1) {
                    game.exchangeOrRevealCard(player, pickedCard, cardIndex);
                }
            }
        } else {
            pickedCard = game.pickCard();
            if (pickedCard != null) {
                int bestIndex = findBestCardToReplace(player.getCartes(), pickedCard);
                pickedCard = pickedCard.retourner();
                game.exchangeOrRevealCard(player, pickedCard, bestIndex);
            }
        }
    }
}
