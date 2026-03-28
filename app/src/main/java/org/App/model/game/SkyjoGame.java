package org.App.model.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
 * @version 3.0
 */
public final class SkyjoGame implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Number of cards dealt to each player at the start. */
    private static final int CARDS_PER_PLAYER = 12;
    /** Number of rows in a player's grid. */
    private static final int ROWS = 3;
    /** Cumulative score threshold that ends the game. */
    private static final int SCORE_LIMIT = 100;

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
    private int finalRoundTurnsRemaining = 0;

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

    // ─── Getters ─────────────────────────────────────────────────────────

    public int getStartingPlayerIndex() { return startingPlayerIndex; }
    public Card getPickedCard()         { return pickedCard; }
    public boolean hasDiscard()         { return hasDiscard; }
    public boolean isFinalRound()       { return isFinalRound; }
    public int getCountReveal()         { return countReveal; }
    public List<Player> getPlayers()    { return players; }
    public Player getActualPlayer()     { return players.get(indexActualPlayer); }

    /** Returns the number of cards remaining in the draw pile. O(1). */
    public int getPickSize() { return pick.size(); }

    /** Returns an unmodifiable view of the draw pile — no defensive copy. */
    public List<Card> getPick() { return Collections.unmodifiableList(pick); }

    /** Returns an unmodifiable view of the discard pile. */
    public List<Card> getDiscard() { return Collections.unmodifiableList(discard); }

    /**
     * Gets the top card of the discard pile.
     *
     * @return The top card of the discard pile, or null if the pile is empty.
     */
    public Card getTopDiscard() {
        return discard.isEmpty() ? null : discard.getLast();
    }

    // ─── State mutators (used by controllers / server) ───────────────────

    public void setIndexActualPlayer(int indexActualPlayer) {
        this.indexActualPlayer = indexActualPlayer;
    }

    public void setPickedCard(Card pickedCard) {
        this.pickedCard = pickedCard;
    }

    public void setHasDiscard(boolean hasDiscard) {
        this.hasDiscard = hasDiscard;
    }

    public void setFinalRound(boolean isFinalRound) {
        this.isFinalRound = isFinalRound;
    }

    public void incrementCountReveal() { this.countReveal++; }

    public void resetCountReveal() { this.countReveal = 0; }

    // ─── Deck management ────────────────────────────────────────────────

    /**
     * Creates and shuffles the deck of cards for the game.
     * Uses streams for concise card generation.
     *
     * @return A shuffled list of {@link Card} instances representing the deck.
     */
    private List<Card> createPick() {
        final int[] idHolder = {0};
        final List<Card> cards = java.util.Arrays.stream(CardValue.values())
                .flatMap(value -> {
                    final int count = switch (value) {
                        case ZERO -> 15;
                        case MOINS_DEUX -> 5;
                        default -> 10;
                    };
                    return IntStream.range(0, count)
                            .mapToObj(i -> new Card(value, false, idHolder[0]++));
                })
                .collect(Collectors.toCollection(ArrayList::new));
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
        return pick.isEmpty() ? null : pick.removeLast();
    }

    /**
     * Picks a card from the discard pile (always returned face-up).
     *
     * @return The picked card, or null if the discard pile is empty.
     */
    public Card pickDiscard() {
        if (discard.isEmpty()) {
            return null;
        }
        final Card topCard = discard.removeLast();
        hasPickedFromDiscard = true;
        return topCard.faceVisible() ? topCard : topCard.retourner();
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
        if (!pick.isEmpty() || discard.isEmpty()) {
            return;
        }
        pick = new ArrayList<>(discard);
        Collections.shuffle(pick);
        discard.clear();

        if (!pick.isEmpty()) {
            final Card firstCard = pick.removeLast();
            discard.add(firstCard.faceVisible() ? firstCard : firstCard.retourner());
        }
    }

    // ─── Turn management ────────────────────────────────────────────────

    /** Moves to the next player in the game. */
    public void nextPlayer() {
        indexActualPlayer = (indexActualPlayer + 1) % players.size();
    }

    // ─── Final round detection ──────────────────────────────────────────

    /**
     * Checks whether any player has revealed all their cards.
     * Pure query — no side effects.
     */
    private boolean hasAnyPlayerRevealedAllCards() {
        return players.stream().anyMatch(
                player -> player.getCartes().stream().allMatch(Card::faceVisible));
    }

    /**
     * Command: checks if the final round should start and triggers it.
     * Every other player gets exactly one more turn after the trigger.
     *
     * @return true if the final round was <em>just</em> triggered by this call.
     */
    public boolean checkAndEnterFinalRound() {
        if (!isFinalRound && hasAnyPlayerRevealedAllCards()) {
            isFinalRound = true;
            finalRoundTurnsRemaining = players.size() - 1;
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

    /** Decrements the final round counter. Call after each turn in the final round. */
    public void decrementFinalRoundTurns() {
        if (isFinalRound && finalRoundTurnsRemaining > 0) {
            finalRoundTurnsRemaining--;
        }
    }

    /**
     * Pure query: the game is over when the final round is active
     * and all remaining turns are spent.
     */
    public boolean isGameOver() {
        return isFinalRound && finalRoundTurnsRemaining <= 0;
    }

    // ─── Card actions ───────────────────────────────────────────────────

    /** Reveals all cards for all players (called at end of round). */
    public void revealAllCards() {
        players.forEach(player -> {
            final List<Card> cartes = player.getCartes();
            IntStream.range(0, cartes.size())
                    .filter(i -> !cartes.get(i).faceVisible())
                    .forEach(i -> cartes.set(i, cartes.get(i).retourner()));
        });
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
        final Card placed = newCard.faceVisible() ? newCard : newCard.retourner();
        final Card oldCard = player.getCartes().set(cardIndex, placed);
        addToDiscard(oldCard);
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
        final Player player = players.get(indexActualPlayer);
        final List<Card> cartes = player.getCartes();
        final int columns = cartes.size() / ROWS;

        for (int col = columns - 1; col >= 0; col--) {
            final int c = col;
            final List<Integer> indexes = IntStream.range(0, ROWS)
                    .map(row -> row * columns + c)
                    .filter(idx -> idx < cartes.size())
                    .boxed()
                    .collect(Collectors.toList());

            if (indexes.size() != ROWS) continue;

            final boolean allVisible = indexes.stream().allMatch(i -> cartes.get(i).faceVisible());
            if (!allVisible) continue;

            final CardValue firstValue = cartes.get(indexes.getFirst()).valeur();
            final boolean allSame = indexes.stream()
                    .allMatch(i -> cartes.get(i).valeur() == firstValue);

            if (allSame) {
                // Remove in reverse order to preserve indices
                indexes.stream()
                        .sorted(Collections.reverseOrder())
                        .forEach(idx -> {
                            addToDiscard(cartes.get(idx));
                            cartes.remove((int) idx);
                        });
            }
        }
    }

    // ─── Scoring ────────────────────────────────────────────────────────

    /**
     * Computes the score for a single player's hand.
     *
     * @param player The player whose hand to score.
     * @return The sum of all card values.
     */
    public int computeHandScore(Player player) {
        return player.getCartes().stream()
                .mapToInt(c -> c.valeur().getValue())
                .sum();
    }

    /**
     * Gets the ranking of players based on round scores + cumulative.
     * If the first player to reveal all cards doesn't have the lowest score,
     * their score is doubled as a penalty.
     *
     * @return A sorted map of players to their total scores (ascending).
     */
    public Map<Player, Integer> getRanking() {
        final Map<Player, Integer> ranking = players.stream()
                .collect(Collectors.toMap(
                        p -> p,
                        p -> computeHandScore(p) + p.getCumulativeScore()));

        if (firstPlayerToRevealAllCards != null) {
            final int minScore = ranking.values().stream().mapToInt(Integer::intValue).min().orElse(0);
            final int firstPlayerScore = ranking.get(firstPlayerToRevealAllCards);
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
        return sortedRanking(players.stream()
                .collect(Collectors.toMap(p -> p, Player::getCumulativeScore)));
    }

    /**
     * Checks if any player has reached the score limit (game-ending condition).
     */
    public boolean hasPlayerReached100Points() {
        return players.stream().anyMatch(p -> p.getCumulativeScore() >= SCORE_LIMIT);
    }

    /** Sorts a ranking map by value in ascending order. */
    private Map<Player, Integer> sortedRanking(Map<Player, Integer> ranking) {
        return ranking.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    // ─── Game lifecycle ─────────────────────────────────────────────────

    /**
     * Starts (or restarts) the game by creating a new deck, distributing
     * cards to each player, and placing one card face-up in the discard.
     */
    public void startGame() {
        firstPlayerToRevealAllCards = null;
        isFinalRound = false;
        finalRoundTurnsRemaining = 0;

        pick.clear();
        discard.clear();
        players.forEach(player -> player.getCartes().clear());

        pick = createPick();
        players.forEach(player -> {
            final int size = pick.size();
            player.getCartes().addAll(pick.subList(size - CARDS_PER_PLAYER, size));
            pick.subList(size - CARDS_PER_PLAYER, size).clear();
        });
        if (!pick.isEmpty()) {
            discard.add(pick.removeLast().retourner());
        }
    }

    /**
     * Reveals two random cards for each player and determines who starts
     * (the player with the highest total of their two revealed cards).
     */
    public void revealInitialCards() {
        int highestTotal = Integer.MIN_VALUE;

        for (int i = 0; i < players.size(); i++) {
            final Player player = players.get(i);
            final int cardCount = player.getCartes().size();

            // Pick 2 distinct random indices
            final int idx1 = random.nextInt(cardCount);
            int idx2 = random.nextInt(cardCount - 1);
            if (idx2 >= idx1) idx2++;

            final Card c1 = player.getCartes().get(idx1);
            final Card c2 = player.getCartes().get(idx2);
            player.getCartes().set(idx1, c1.retourner());
            player.getCartes().set(idx2, c2.retourner());

            final int total = c1.valeur().getValue() + c2.valeur().getValue();
            if (total > highestTotal) {
                highestTotal = total;
                startingPlayerIndex = i;
            }
        }
    }
}
