package org.App.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer implements Player {
    private final String nom;
    private final List<Card> cartes;
    private final Difficulty difficulty;
    private final Random random = new Random();

    public AIPlayer(String nom, Difficulty difficulty) {
        this.nom = nom;
        this.cartes = new ArrayList<>();
        this.difficulty = difficulty;
    }

    @Override
    public String getName() {
        return nom;
    }

    @Override
    public List<Card> getCartes() {
        return cartes;
    }

    @Override
    public void piocher(Card card) {
        cartes.add(card);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void playTurn(SkyjoGame game) {
        switch (difficulty) {
            case EASY -> playEasyTurn(game);
            case MEDIUM -> playMediumTurn(game);
            case HARD -> playHardTurn(game);
        }
    }

    private void playEasyTurn(SkyjoGame game) {
        // Randomly decide to pick from draw pile or discard pile
        boolean pickFromDiscard = random.nextBoolean();
        Card pickedCard;

        if (pickFromDiscard && game.getTopDiscard() != null) {
            pickedCard = game.pickDiscard();
        } else {
            pickedCard = game.pickCard();
        }

        if (pickedCard != null) {
            // Randomly decide to replace a card or reveal a card
            int cardIndex = random.nextInt(cartes.size());
            game.exchangeOrRevealCard(this, pickedCard, cardIndex);
        }

        // End the turn
        game.nextPlayer();
    }

    private void playMediumTurn(SkyjoGame game) {
        // Prefer to pick from discard if the top card is beneficial
        Card topDiscard = game.getTopDiscard();
        Card pickedCard;

        if (topDiscard != null && topDiscard.valeur().getValue() <= 0) {
            pickedCard = game.pickDiscard();
        } else {
            pickedCard = game.pickCard();
        }

        if (pickedCard != null) {
            // Replace the highest value card if possible
            int highestValueIndex = findHighestValueCardIndex();
            game.exchangeOrRevealCard(this, pickedCard, highestValueIndex);
        }

        // End the turn
        game.nextPlayer();
    }

    private void playHardTurn(SkyjoGame game) {
        // Strategic decision making
        Card topDiscard = game.getTopDiscard();
        Card pickedCard;

        if (topDiscard != null && isCardBeneficial(topDiscard)) {
            pickedCard = game.pickDiscard();
        } else {
            pickedCard = game.pickCard();
        }

        if (pickedCard != null) {
            // Replace the card that minimizes the total hand value
            int bestIndex = findBestCardToReplace(pickedCard);
            game.exchangeOrRevealCard(this, pickedCard, bestIndex);
        }

        // End the turn
        game.nextPlayer();
    }

    private int findHighestValueCardIndex() {
        int highestIndex = 0;
        int highestValue = cartes.get(0).valeur().getValue();

        for (int i = 1; i < cartes.size(); i++) {
            int currentValue = cartes.get(i).valeur().getValue();
            if (currentValue > highestValue) {
                highestValue = currentValue;
                highestIndex = i;
            }
        }

        return highestIndex;
    }

    private boolean isCardBeneficial(Card card) {
        // Implement logic to determine if the card is beneficial
        return card.valeur().getValue() <= 0;
    }

    private int findBestCardToReplace(Card newCard) {
        int bestIndex = 0;
        int bestValueDifference = Integer.MAX_VALUE;

        for (int i = 0; i < cartes.size(); i++) {
            int currentValue = cartes.get(i).valeur().getValue();
            int newValue = newCard.valeur().getValue();
            int difference = newValue - currentValue;

            if (difference < bestValueDifference) {
                bestValueDifference = difference;
                bestIndex = i;
            }
        }

        return bestIndex;
    }
}