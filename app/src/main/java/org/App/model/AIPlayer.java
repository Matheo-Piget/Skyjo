package org.App.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.App.controller.GameController;

/**
 * The AIPlayer class represents an AI-controlled player in the Skyjo game.
 * It implements the {@link Player} interface and provides different strategies
 * based on the difficulty level (EASY, MEDIUM, HARD).
 * 
 * <p>
 * The AI player can perform actions such as picking cards, exchanging cards,
 * and revealing cards, depending on the game state and its difficulty level.
 * </p>
 * 
 * @see Player
 * @see Difficulty
 * @see SkyjoGame
 * @see GameController
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public class AIPlayer implements Player {
    private final String nom;
    private final List<Card> cartes;
    private final Difficulty difficulty;
    private final Random random = new Random();
    private int commutativeScore = 0;

    /**
     * Constructs a new AIPlayer with the specified name and difficulty level.
     * 
     * @param nom        The name of the AI player.
     * @param difficulty The difficulty level of the AI player.
     * 
     * @see Difficulty
     */
    public AIPlayer(String nom, Difficulty difficulty) {
        this.nom = nom;
        this.cartes = new ArrayList<>();
        this.difficulty = difficulty;
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
     * Returns the name of the AI player.
     * 
     * @return The name of the AI player.
     */
    @Override
    public String getName() {
        return nom;
    }

    /**
     * Returns the list of cards held by the AI player.
     * 
     * @return A list of {@link Card} instances.
     */
    @Override
    public List<Card> getCartes() {
        return cartes;
    }

    /**
     * Adds a card to the AI player's hand.
     * 
     * @param card The card to add to the player's hand.
     * 
     * @see Card
     */
    @Override
    public void piocher(Card card) {
        cartes.add(card);
    }

    /**
     * Returns the difficulty level of the AI player.
     * 
     * @return The difficulty level of the AI player.
     * 
     * @see Difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Plays a turn for the AI player based on its difficulty level.
     * 
     * @param game The current game instance.
     * 
     * @see SkyjoGame
     * @see GameController#updateView()
     */
    public void playTurn(SkyjoGame game) {
        switch (difficulty) {
            case EASY -> playEasyTurn(game);
            case MEDIUM -> playMediumTurn(game);
            case HARD -> playHardTurn(game);
        }
        game.checkColumns(); // Vérifier les colonnes après chaque action
        GameController.getInstance().updateView();
    }

    /**
     * Plays a turn for the AI player in EASY difficulty mode.
     * The AI makes random decisions between picking from the discard pile or the
     * deck,
     * and randomly chooses to exchange or discard the picked card.
     * 
     * @param game The current game instance.
     * 
     * @see SkyjoGame#pickDiscard()
     * @see SkyjoGame#pickCard()
     * @see SkyjoGame#addToDiscard(Card)
     * @see GameController#updateView()
     */
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

    /**
     * Plays a turn for the AI player in MEDIUM difficulty mode.
     * The AI prefers to pick from the discard pile if the card is beneficial.
     * Otherwise, it picks from the deck and replaces the highest-value card in its
     * hand.
     * 
     * @param game The current game instance.
     * 
     * @see SkyjoGame#pickDiscard()
     * @see SkyjoGame#pickCard()
     * @see SkyjoGame#exchangeOrRevealCard(Player, Card, int)
     * @see GameController#updateView()
     */
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

    /**
     * Plays a turn for the AI player in HARD difficulty mode.
     * The AI prefers to pick from the discard pile if the card is beneficial.
     * Otherwise, it picks from the deck and replaces the card that minimizes
     * the total value of its hand.
     * 
     * @param game The current game instance.
     * 
     * @see SkyjoGame#pickDiscard()
     * @see SkyjoGame#pickCard()
     * @see SkyjoGame#exchangeOrRevealCard(Player, Card, int)
     * @see GameController#updateView()
     */
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

    /**
     * Replaces a card in the AI player's hand with a new card.
     * 
     * @param game    The current game instance.
     * @param newCard The new card to replace an existing card.
     * 
     * @see SkyjoGame#exchangeOrRevealCard(Player, Card, int)
     * @see GameController#updateView()
     */
    private void replaceCard(SkyjoGame game, Card newCard) {
        // Remplacer une carte cachée ou visible
        int cardIndex = findBestCardToReplace(newCard); // Trouver la meilleure carte à remplacer
        if (cardIndex != -1) {
            game.exchangeOrRevealCard(this, newCard, cardIndex);
            GameController.getInstance().updateView();
        }
    }

    /**
     * Reveals a random hidden card in the AI player's hand.
     * 
     * @see GameController#updateView()
     */
    private void revealRandomCard() {
        // Retourner une carte cachée aléatoire
        int cardIndex = findHiddenCardIndex();
        if (cardIndex != -1) {
            cartes.set(cardIndex, cartes.get(cardIndex).retourner());
            GameController.getInstance().updateView();
        }
    }

    /**
     * Finds the index of a hidden card in the AI player's hand.
     * 
     * @return The index of a hidden card, or -1 if no hidden cards are found.
     */
    private int findHiddenCardIndex() {
        // Trouver l'index d'une carte cachée
        for (int i = 0; i < cartes.size(); i++) {
            if (!cartes.get(i).faceVisible()) {
                return i;
            }
        }
        return -1; // Aucune carte cachée trouvée
    }

    /**
     * Checks if a card is beneficial for the AI player.
     * A card is considered beneficial if its value is low or negative.
     * 
     * @param card The card to check.
     * @return True if the card is beneficial, false otherwise.
     */
    private boolean isCardBeneficial(Card card) {
        // Une carte est bénéfique si sa valeur est faible ou négative
        return card.valeur().getValue() <= 0;
    }

    /**
     * Finds the best card to replace in the AI player's hand to minimize the total
     * value.
     * 
     * @param newCard The new card to compare with existing cards.
     * @return The index of the best card to replace.
     */
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

    /**
     * Finds the index of the highest-value card in the AI player's hand.
     * 
     * @return The index of the highest-value card.
     */
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