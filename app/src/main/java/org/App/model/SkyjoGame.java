package org.App.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.App.controller.GameController;

/**
 * Represents the main game logic for the Skyjo game.
 * This class manages the game state, including the players, the deck, and the discard pile.
 * 
 * <p>
 * The game involves players drawing and discarding cards to minimize the total value of their hands.
 * The game ends when a player reveals all their cards or the deck is empty.
 * </p>
 * 
 * @see Player
 * @see Card
 * @see GameController
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public final class SkyjoGame {
    private final List<Player> players;
    private List<Card> pick;
    private final List<Card> discard;
    private int indexActualPlayer = 0;
    private final Random random = new Random();

    /**
     * Constructs a new SkyjoGame with the specified players.
     *
     * @param players The list of players participating in the game.
     */
    public SkyjoGame(final List<Player> players) {
        this.players = List.copyOf(players);
        this.pick = createPick();
        this.discard = new ArrayList<>();
    }

    /**
     * Creates and shuffles the deck of cards for the game.
     *
     * @return A shuffled list of {@link Card} instances representing the deck.
     */
    private List<Card> createPick() {
        List<Card> cards = new ArrayList<>();
        for (CardValue value : CardValue.values()) {
            int count = switch (value) {
                case ZERO -> 15;
                case MOINS_DEUX -> 5;
                default -> 10;
            };
            for (int i = 0; i < count; i++) {
                cards.add(new Card(value, false)); // Crée et ajoute chaque carte
            }
        }
        Collections.shuffle(cards);
        System.out.println("Nombre total de cartes générées : " + cards.size()); // DEBUG
        return cards;
    }

    /**
     * Gets the list of players in the game.
     *
     * @return The list of players as a {@link List} of {@link Player} instances.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Picks a card from the deck.
     *
     * @return The picked card, or null if the deck is empty.
     */
    public Card pickCard() {
        return pick.isEmpty() ? null : pick.remove(0);
    }

    /**
     * Picks a card from the discard pile.
     *
     * @return The picked card, or null if the discard pile is empty.
     */
    public Card pickDiscard() {
        if (discard.isEmpty()) {
            return null; // Si la défausse est vide
        } else {
            Card topCard = discard.remove(discard.size() - 1);
            System.out.println("Joueur " + indexActualPlayer + " a pioché de la défausse un " + topCard.valeur());
            return topCard.retourner(); // On retourne la carte
        }
    }

    /**
     * Adds a card to the discard pile.
     *
     * @param card The card to add to the discard pile.
     */
    public void addToDiscard(final Card card) {
        System.out.println("Joueur " + indexActualPlayer + " a mit dans la défausse un " + card.valeur());
        discard.add(card.faceVisible() ? card : card.retourner()); // On ajoute la carte retournée si elle est cachée
    }

    /**
     * Gets the current player.
     *
     * @return The current player as a {@link Player} instance.
     */
    public Player getActualPlayer() {
        return players.get(indexActualPlayer);
    }

    /**
     * Moves to the next player in the game.
     */
    public void nextPlayer() {
        indexActualPlayer = (indexActualPlayer + 1) % players.size();
    }

    /**
     * Checks if any player has revealed all their cards.
     *
     * @return True if any player has revealed all their cards, false otherwise.
     */
    private boolean hasPlayerRevealedAllCards() {
        return players.stream().anyMatch(player -> player.getCartes().stream().allMatch(Card::faceVisible));
    }

    /**
     * Reveals all cards for all players.
     */
    public void revealAllCards() {
        for (Player player : players) {
            for (int i = 0; i < player.getCartes().size(); i++) {
                if (!player.getCartes().get(i).faceVisible()) {
                    player.getCartes().set(i, player.getCartes().get(i).retourner());
                }
            }
        }
        GameController.getInstance().updateView();
    }

    /**
     * Gets the ranking of players based on the total value of their cards.
     *
     * @return A map of players to their total card values.
     */
    public Map<Player, Integer> getRanking() {
        Map<Player, Integer> ranking = new HashMap<>();
        players.forEach(
                player -> ranking.put(player, player.getCartes().stream().mapToInt(c -> c.valeur().getValue()).sum()));
        return ranking;
    }

    /**
     * Checks if any player has reached 100 points.
     *
     * @return True if any player has reached 100 points, false otherwise.
     */
    public boolean hasPlayerReached100Points() {
        return players.stream().anyMatch(player -> player.getCommutativeScore() >= 100);
    }

    /**
     * Checks if the game is finished.
     *
     * @return True if the game is finished, false otherwise.
     */
    public boolean isFinished() {
        return hasPlayerRevealedAllCards() || pick.isEmpty();
    }

    /**
     * Gets the current deck of cards.
     *
     * @return The deck as a {@link List} of {@link Card} instances.
     */
    public List<Card> getPick() {
        return List.copyOf(pick);
    }

    /**
     * Gets the current discard pile.
     *
     * @return The discard pile as a {@link List} of {@link Card} instances.
     */
    public List<Card> getDiscard() {
        return List.copyOf(discard);
    }

    /**
     * Starts the game by initializing the deck, distributing cards to players,
     * and setting up the discard pile.
     */
    public void startGame() {
        if (!pick.isEmpty()) pick.clear();
        if (!discard.isEmpty()) discard.clear();
        players.forEach(player -> player.getCartes().clear());
    
        pick = createPick(); // Créer une nouvelle pioche
        players.forEach(player -> {
            player.getCartes().addAll(pick.subList(0, 12)); // Donner 12 cartes à chaque joueur
            pick.subList(0, 12).clear(); // Retirer ces cartes de la pioche
        });
        if (!pick.isEmpty()) {
            Card firstCard = pick.remove(0).retourner(); // La retourner face visible
            discard.add(firstCard);
        }
    }

    /**
     * Exchanges or reveals a card for the specified player.
     *
     * @param player    The player performing the action.
     * @param newCard   The new card to exchange or reveal.
     * @param cardIndex The index of the card to replace or reveal.
     */
    public void exchangeOrRevealCard(final Player player, final Card newCard, final int cardIndex) {
        if (cardIndex == -1) {
            addToDiscard(newCard);
        } else {
            Card oldCard = player.getCartes().set(cardIndex, newCard);
            addToDiscard(oldCard);
        }
    }

    /**
     * Reveals a specific card for the specified player.
     *
     * @param player    The player whose card is being revealed.
     * @param cardIndex The index of the card to reveal.
     */
    public void revealCard(final Player player, final int cardIndex) {
        player.getCartes().set(cardIndex, player.getCartes().get(cardIndex).retourner());
    }

    /**
     * Checks for completed columns in the current player's hand and removes them.
     */
    public void checkColumns() {
        Player player = players.get(indexActualPlayer);

        int columns = player.getCartes().size() / 3;
        int rows = 3;
        List<Card> cartes = player.getCartes();
        boolean columnRemoved = false;

        for (int col = 0; col < columns; col++) {
            CardValue firstValue = null;
            boolean allSame = true;
            List<Integer> indexes = new ArrayList<>();

            for (int row = 0; row < rows; row++) {
                int index = row * columns + col;
                if (index >= cartes.size())
                    continue;

                Card card = cartes.get(index);
                if (!card.faceVisible()) {
                    allSame = false;
                    break;
                }

                if (firstValue == null) {
                    firstValue = card.valeur();
                } else if (!firstValue.equals(card.valeur())) {
                    allSame = false;
                    break;
                }

                indexes.add(index);
            }

            if (allSame && firstValue != null) {
                // Ajouter les cartes à la défausse et supprimer les cartes du plateau
                for (int i = indexes.size() - 1; i >= 0; i--) {
                    int index = indexes.get(i);
                    addToDiscard(cartes.get(index));
                    cartes.remove(index);
                }
                columnRemoved = true;
            }
        }

        if (columnRemoved) {
            GameController.getInstance().updateView();
        }
    }

    /**
     * Reveals two random cards for each player to determine the starting player.
     */
    public void revealInitialCards() {
        int highestTotal = -1;
        int startingPlayerIndex = 0;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int total = 0;
            Set<Integer> revealedIndices = new HashSet<>();

            while (revealedIndices.size() < 2) { // Assurer que deux cartes différentes sont retournées
                int rand = random.nextInt(player.getCartes().size());
                if (revealedIndices.add(rand)) { // Ajoute uniquement si l'index n'était pas déjà sélectionné
                    Card card = player.getCartes().get(rand);
                    player.getCartes().set(rand, card.retourner());
                    total += card.valeur().getValue();
                }
            }

            if (total > highestTotal) {
                highestTotal = total;
                startingPlayerIndex = i;
            }
        }
        indexActualPlayer = startingPlayerIndex;
    }

    /**
     * Gets the top card of the discard pile.
     *
     * @return The top card of the discard pile, or null if the pile is empty.
     */
    public Card getTopDiscard() {
        return discard.isEmpty() ? null : discard.get(discard.size() - 1);
    }
}