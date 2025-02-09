package org.App.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.App.controller.GameController;

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
        game.checkColumns(); // Vérifier les colonnes après chaque action
        GameController.getInstance().updateView();
    }
    
    private void playEasyTurn(SkyjoGame game) {
        // Choix aléatoire entre piocher ou prendre la défausse
        boolean pickFromDiscard = random.nextBoolean();
        Card pickedCard;
    
        if (pickFromDiscard && game.getTopDiscard() != null) {
            // Choix 1 : Prendre la carte de la défausse
            pickedCard = game.pickDiscard();
            if (pickedCard != null) {
                // Obligation d'échanger cette carte avec une carte du plateau
                pickedCard = pickedCard.retourner();
                replaceCard(game, pickedCard);
                GameController.getInstance().updateView();
            }
        } else {
            // Choix 2 : Prendre une carte de la pioche
            pickedCard = game.pickCard();
            if (pickedCard != null) {
                // Choix aléatoire entre échanger ou défausser
                if (random.nextBoolean()) {
                    // Échanger la carte piochée avec une carte du plateau
                    pickedCard = pickedCard.retourner();
                    replaceCard(game, pickedCard);
                    GameController.getInstance().updateView();
                } else {
                    // Défausser la carte piochée et retourner une carte cachée
                    game.addToDiscard(pickedCard);
                    GameController.getInstance().updateView();
                    revealRandomCard();
                    GameController.getInstance().updateView();
                }
            }
        }
    }
    
    private void playMediumTurn(SkyjoGame game) {
        // Préférer prendre la défausse si la carte est bénéfique
        Card topDiscard = game.getTopDiscard();
        Card pickedCard;
    
        if (topDiscard != null && isCardBeneficial(topDiscard)) {
            // Choix 1 : Prendre la carte de la défausse
            pickedCard = game.pickDiscard();
            if (pickedCard != null) {
                // Obligation d'échanger cette carte avec une carte du plateau
                pickedCard = pickedCard.retourner();
                replaceCard(game, pickedCard);
                GameController.getInstance().updateView();
            }
        } else {
            // Choix 2 : Prendre une carte de la pioche
            pickedCard = game.pickCard();
            if (pickedCard != null) {
                // Remplacer la carte de plus haute valeur si possible
                int highestValueIndex = findHighestValueCardIndex();
                pickedCard = pickedCard.retourner();
                game.exchangeOrRevealCard(this, pickedCard, highestValueIndex);
                GameController.getInstance().updateView();
            }
        }
    }
    
    private void playHardTurn(SkyjoGame game) {
        // Stratégie avancée : minimiser la valeur totale de la main
        Card topDiscard = game.getTopDiscard();
        Card pickedCard;
    
        if (topDiscard != null && isCardBeneficial(topDiscard)) {
            // Choix 1 : Prendre la carte de la défausse
            pickedCard = game.pickDiscard();
            if (pickedCard != null) {
                // Obligation d'échanger cette carte avec une carte du plateau
                pickedCard = pickedCard.retourner();
                replaceCard(game, pickedCard);
                GameController.getInstance().updateView();
            }
        } else {
            // Choix 2 : Prendre une carte de la pioche
            pickedCard = game.pickCard();
            if (pickedCard != null) {
                // Remplacer la carte qui minimise la valeur totale de la main
                int bestIndex = findBestCardToReplace(pickedCard);
                pickedCard = pickedCard.retourner();
                game.exchangeOrRevealCard(this, pickedCard, bestIndex);
                GameController.getInstance().updateView();
            }
        }
    }

    private void replaceCard(SkyjoGame game, Card newCard) {
        // Remplacer une carte cachée ou visible
        int cardIndex = findBestCardToReplace(newCard); // Trouver la meilleure carte à remplacer
        if (cardIndex != -1) {
            game.exchangeOrRevealCard(this, newCard, cardIndex);
            GameController.getInstance().updateView();
        }
    }
    
    private void revealRandomCard() {
        // Retourner une carte cachée aléatoire
        int cardIndex = findHiddenCardIndex();
        if (cardIndex != -1) {
            cartes.set(cardIndex, cartes.get(cardIndex).retourner());
            GameController.getInstance().updateView();
        }
    }
    
    private int findHiddenCardIndex() {
        // Trouver l'index d'une carte cachée
        for (int i = 0; i < cartes.size(); i++) {
            if (!cartes.get(i).faceVisible()) {
                return i;
            }
        }
        return -1; // Aucune carte cachée trouvée
    }
    
    private boolean isCardBeneficial(Card card) {
        // Une carte est bénéfique si sa valeur est faible ou négative
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
}