package org.App.model.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.App.model.player.Player;

/**
 * Represents the main game logic for the Skyjo game.
 * This class manages the game state, including the players, the deck, and the
 * discard pile.
 * <p>
 * The model is fully independent: it does NOT reference any controller or view.
 * State changes are propagated by the controller polling this class after each action.
 * </p>
 *
 * @see Player
 * @see Card
 *
 * @author Mathéo Piget
 * @version 2.0
 */
public final class SkyjoGame implements Serializable {
    private static final long serialVersionUID = 1L;

    private int startingPlayerIndex = 0;
    private final List<Player> players;
    private List<Card> pick;
    private final List<Card> discard;
    private int indexActualPlayer = 0;
    private final Random random = new Random();

    private Card pickedCard;
    private boolean hasDiscard;
    private int countReveal;
    private boolean hasPickedFromDiscard;
    private Player firstPlayerToRevealAllCards;
    private boolean isFinalRound = false;

    /**
     * Constructs a new SkyjoGame with no players.
     * Used for serialization purposes.
     */
    public SkyjoGame() {
        this.players = new ArrayList<>();
        this.pick = new ArrayList<>();
        this.discard = new ArrayList<>();
    }

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

    // ─── Getters / Setters ───────────────────────────────────────────────

    public int getStartingPlayerIndex() {
        return startingPlayerIndex;
    }

    public void setIndexActualPlayer(int indexActualPlayer) {
        this.indexActualPlayer = indexActualPlayer;
    }

    public Card getPickedCard() {
        return pickedCard;
    }

    public void setPickedCard(Card pickedCard) {
        this.pickedCard = pickedCard;
    }

    public boolean hasDiscard() {
        return hasDiscard;
    }

    public void setHasDiscard(boolean hasDiscard) {
        this.hasDiscard = hasDiscard;
    }

    public boolean isFinalRound() {
        return isFinalRound;
    }

    public void setFinalRound(boolean isFinalRound) {
        this.isFinalRound = isFinalRound;
    }

    public int getCountReveal() {
        return countReveal;
    }

    public void incrementCountReveal() {
        this.countReveal++;
    }

    public void resetCountReveal() {
        this.countReveal = 0;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getPick() {
        return List.copyOf(pick);
    }

    public List<Card> getDiscard() {
        return List.copyOf(discard);
    }

    public Player getActualPlayer() {
        return players.get(indexActualPlayer);
    }

    /**
     * Gets the top card of the discard pile.
     *
     * @return The top card of the discard pile, or null if the pile is empty.
     */
    public Card getTopDiscard() {
        return discard.isEmpty() ? null : discard.get(discard.size() - 1);
    }

    // ─── Deck management ────────────────────────────────────────────────

    /**
     * Creates and shuffles the deck of cards for the game.
     * IDs are generated locally per game session (no global static counter).
     *
     * @return A shuffled list of {@link Card} instances representing the deck.
     */
    private List<Card> createPick() {
        List<Card> cards = new ArrayList<>();
        int id = 0;
        for (CardValue value : CardValue.values()) {
            int count = switch (value) {
                case ZERO -> 15;
                case MOINS_DEUX -> 5;
                default -> 10;
            };
            for (int i = 0; i < count; i++) {
                cards.add(new Card(value, false, id++));
            }
        }
        Collections.shuffle(cards);
        return cards;
    }

    /**
     * Picks a card from the deck.
     * Removes from the end of the list for O(1) performance.
     *
     * @return The picked card, or null if the deck is empty.
     */
    public Card pickCard() {
        hasPickedFromDiscard = false;
        return pick.isEmpty() ? null : pick.remove(pick.size() - 1);
    }

    /**
     * Picks a card from the discard pile.
     *
     * @return The picked card, or null if the discard pile is empty.
     */
    public Card pickDiscard() {
        if (discard.isEmpty()) {
            return null;
        }
        Card topCard = discard.remove(discard.size() - 1);
        hasPickedFromDiscard = true;
        return topCard.retourner();
    }

    /**
     * Adds a card to the discard pile (always face-up).
     *
     * @param card The card to add to the discard pile.
     */
    public void addToDiscard(final Card card) {
        discard.add(card.faceVisible() ? card : card.retourner());
        hasPickedFromDiscard = false;
    }

    /**
     * If the deck is empty, reshuffles the discard pile into the deck,
     * keeping one card face-up as the new discard top.
     */
    public void pickEmpty() {
        if (pick.isEmpty() && !discard.isEmpty()) {
            List<Card> newPick = new ArrayList<>(discard);
            Collections.shuffle(newPick);
            pick = newPick;
            discard.clear();

            if (!pick.isEmpty()) {
                Card firstCard = pick.remove(pick.size() - 1);
                discard.add(firstCard.faceVisible() ? firstCard : firstCard.retourner());
            }
        }
    }

    // ─── Turn management ────────────────────────────────────────────────

    /**
     * Moves to the next player in the game.
     */
    public void nextPlayer() {
        indexActualPlayer = (indexActualPlayer + 1) % players.size();
    }

    // ─── Final round detection (Command-Query Separation) ───────────────

    /**
     * Checks whether any player has revealed all their cards.
     * Pure query — no side effects.
     *
     * @return true if at least one player has all cards face-up.
     */
    private boolean hasAnyPlayerRevealedAllCards() {
        return players.stream().anyMatch(
                player -> player.getCartes().stream().allMatch(Card::faceVisible));
    }

    /**
     * Command: checks if the final round should start and triggers it.
     * Call this after each turn, before checking {@link #isGameOver()}.
     *
     * @return true if the final round was <em>just</em> triggered by this call.
     */
    public boolean checkAndEnterFinalRound() {
        if (!isFinalRound && hasAnyPlayerRevealedAllCards()) {
            isFinalRound = true;
            if (firstPlayerToRevealAllCards == null) {
                firstPlayerToRevealAllCards = players.stream()
                        .filter(p -> p.getCartes().stream().allMatch(Card::faceVisible))
                        .findFirst()
                        .orElse(null);
            }
            return true;
        }
        return false;
    }

    /**
     * Pure query: checks if the game is over.
     * The game ends when the final round is active and we've reached the last player.
     *
     * @return true if the game is over.
     */
    public boolean isGameOver() {
        return isFinalRound && indexActualPlayer == players.size() - 1;
    }

    // ─── Card actions ───────────────────────────────────────────────────

    /**
     * Reveals all cards for all players (called at end of round).
     */
    public void revealAllCards() {
        for (Player player : players) {
            List<Card> cartes = player.getCartes();
            for (int i = 0; i < cartes.size(); i++) {
                if (!cartes.get(i).faceVisible()) {
                    cartes.set(i, cartes.get(i).retourner());
                }
            }
        }
    }

    /**
     * Exchanges or reveals a card for the specified player.
     *
     * @param player    The player performing the action.
     * @param newCard   The new card to exchange or reveal.
     * @param cardIndex The index of the card to replace, or -1 to discard.
     * @throws InvalidMoveException if the card index is out of bounds.
     */
    public void exchangeOrRevealCard(final Player player, final Card newCard, final int cardIndex) {
        if (cardIndex == -1) {
            addToDiscard(newCard);
            return;
        }
        if (cardIndex < 0 || cardIndex >= player.getCartes().size()) {
            throw new InvalidMoveException("Invalid card index: " + cardIndex);
        }
        if (hasPickedFromDiscard) {
            Card oldCard = player.getCartes().set(cardIndex, newCard.retourner());
            addToDiscard(oldCard);
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
     * @throws InvalidMoveException if the card index is out of bounds.
     */
    public void revealCard(final Player player, final int cardIndex) {
        if (cardIndex < 0 || cardIndex >= player.getCartes().size()) {
            throw new InvalidMoveException("Invalid card index: " + cardIndex);
        }
        player.getCartes().set(cardIndex, player.getCartes().get(cardIndex).retourner());
    }

    /**
     * Checks for completed columns in the current player's hand and removes them.
     * A column is complete when all 3 cards in a vertical column are face-up
     * and have the same value.
     */
    public void checkColumns() {
        Player player = players.get(indexActualPlayer);
        List<Card> cartes = player.getCartes();
        int columns = cartes.size() / 3;
        int rows = 3;

        for (int col = 0; col < columns; col++) {
            CardValue firstValue = null;
            boolean allSame = true;
            List<Integer> indexes = new ArrayList<>();

            for (int row = 0; row < rows; row++) {
                int index = row * columns + col;
                if (index >= cartes.size()) {
                    allSame = false;
                    break;
                }

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
                for (int i = indexes.size() - 1; i >= 0; i--) {
                    int index = indexes.get(i);
                    addToDiscard(cartes.get(index));
                    cartes.remove(index);
                }
            }
        }
    }

    // ─── Scoring ────────────────────────────────────────────────────────

    /**
     * Gets the ranking of players based on round scores + cumulative.
     * If the first player to reveal all cards doesn't have the lowest score,
     * their score is doubled as a penalty.
     *
     * @return A sorted map of players to their total scores (ascending).
     */
    public Map<Player, Integer> getRanking() {
        Map<Player, Integer> ranking = new java.util.HashMap<>();
        players.forEach(player -> ranking.put(player,
                player.getCartes().stream().mapToInt(c -> c.valeur().getValue()).sum()
                        + player.getCumulativeScore()));

        if (firstPlayerToRevealAllCards != null) {
            int minScore = Collections.min(ranking.values());
            int firstPlayerScore = ranking.get(firstPlayerToRevealAllCards);
            if (firstPlayerScore != minScore) {
                ranking.put(firstPlayerToRevealAllCards, firstPlayerScore * 2);
            }
        }

        return sortedRanking(ranking);
    }

    /**
     * Gets the final ranking (cumulative scores only, for display after 100 pts).
     *
     * @return A sorted map of players to their cumulative scores (ascending).
     */
    public Map<Player, Integer> getFinalRanking() {
        Map<Player, Integer> ranking = new java.util.HashMap<>();
        players.forEach(player -> ranking.put(player, player.getCumulativeScore()));
        return sortedRanking(ranking);
    }

    /**
     * Checks if any player has reached 100 cumulative points (game-ending condition).
     *
     * @return true if any player has &gt;= 100 cumulative points.
     */
    public boolean hasPlayerReached100Points() {
        return players.stream().anyMatch(player -> player.getCumulativeScore() >= 100);
    }

    /**
     * Sorts a ranking map by value in ascending order.
     */
    private Map<Player, Integer> sortedRanking(Map<Player, Integer> ranking) {
        return ranking.entrySet()
                .stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    // ─── Game lifecycle ─────────────────────────────────────────────────

    /**
     * Starts (or restarts) the game by creating a new deck, distributing
     * 12 cards to each player, and placing one card face-up in the discard.
     */
    public void startGame() {
        firstPlayerToRevealAllCards = null;
        isFinalRound = false;

        pick.clear();
        discard.clear();
        players.forEach(player -> player.getCartes().clear());

        pick = createPick();
        players.forEach(player -> {
            int size = pick.size();
            player.getCartes().addAll(pick.subList(size - 12, size));
            pick.subList(size - 12, size).clear();
        });
        if (!pick.isEmpty()) {
            Card firstCard = pick.remove(pick.size() - 1).retourner();
            discard.add(firstCard);
        }
    }

    /**
     * Reveals two random cards for each player and determines who starts
     * (the player with the highest total of their two revealed cards).
     */
    public void revealInitialCards() {
        int highestTotal = -1;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int total = 0;
            Set<Integer> revealedIndices = new HashSet<>();

            while (revealedIndices.size() < 2) {
                int rand = random.nextInt(player.getCartes().size());
                if (revealedIndices.add(rand)) {
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
    }
}
