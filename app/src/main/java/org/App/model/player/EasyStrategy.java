package org.App.model.player;

import java.util.Random;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;

/**
 * AI strategy for EASY difficulty.
 * Makes random decisions between picking from discard or deck,
 * and randomly chooses to exchange or discard the picked card.
 *
 * @author Mathéo Piget
 * @version 1.0
 */
public final class EasyStrategy implements AIStrategy {

    private final Random random = new Random();

    @Override
    public void playTurn(SkyjoGame game, AIPlayer player) {
        boolean pickFromDiscard = random.nextBoolean();
        Card pickedCard;

        if (pickFromDiscard && game.getTopDiscard() != null) {
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
                if (random.nextBoolean()) {
                    pickedCard = pickedCard.retourner();
                    int cardIndex = findBestCardToReplace(player.getCartes(), pickedCard);
                    if (cardIndex != -1) {
                        game.exchangeOrRevealCard(player, pickedCard, cardIndex);
                    }
                } else {
                    game.addToDiscard(pickedCard);
                    int hiddenIndex = findHiddenCardIndex(player.getCartes());
                    if (hiddenIndex != -1) {
                        player.getCartes().set(hiddenIndex,
                                player.getCartes().get(hiddenIndex).retourner());
                    }
                }
            }
        }
    }
}
