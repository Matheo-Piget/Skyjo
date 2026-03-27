package org.App.model.player;

import java.util.Comparator;
import java.util.stream.IntStream;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;

/**
 * AI strategy for MEDIUM difficulty.
 * Prefers to pick from the discard pile if the card is beneficial (value &lt;= 0).
 * Otherwise picks from the deck and replaces the highest-value visible card.
 *
 * @author Mathéo Piget
 * @version 1.0
 */
public final class MediumStrategy implements AIStrategy {

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
                pickedCard = pickedCard.retourner();
                int highestIndex = findHighestValueCardIndex(player);
                if (highestIndex != -1 && pickedCard.valeur().getValue() < player.getCartes().get(highestIndex).valeur().getValue()) {
                    game.exchangeOrRevealCard(player, pickedCard, highestIndex);
                } else {
                    // Card is not beneficial — discard and reveal a hidden card
                    game.addToDiscard(pickedCard);
                    int hiddenIndex = findHiddenCardIndex(player.getCartes());
                    if (hiddenIndex != -1) {
                        player.getCartes().set(hiddenIndex, player.getCartes().get(hiddenIndex).retourner());
                    }
                }
            }
        }
    }

    /**
     * Finds the index of the highest-value visible card in the player's hand.
     *
     * @param player The AI player.
     * @return The index of the highest-value visible card, or -1 if none visible.
     */
    private int findHighestValueCardIndex(AIPlayer player) {
        return IntStream.range(0, player.getCartes().size())
                .filter(i -> player.getCartes().get(i).faceVisible())
                .boxed()
                .max(Comparator.comparing(i -> player.getCartes().get(i).valeur().getValue()))
                .orElse(-1);
    }
}
