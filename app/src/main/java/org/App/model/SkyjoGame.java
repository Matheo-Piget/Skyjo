package org.App.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class SkyjoGame {
    private final List<Player> players;
    private final List<Card> pick;
    private final List<Card> discard;
    private int indexActualPlayer = 0;
    private final Random random = new Random();

    public SkyjoGame(final List<Player> players) {
        this.players = List.copyOf(players);
        this.pick = createPick();
        this.discard = new ArrayList<>();
    }

    private List<Card> createPick() {
        List<Card> cards = new ArrayList<>();
        for (CardValue value : CardValue.values()) {
            int count = switch (value) {
                case ZERO -> 15;
                case MOINS_DEUX -> 5;
                default -> 10;
            };
            cards.addAll(Collections.nCopies(count, new Card(value, false)));
        }
        Collections.shuffle(cards);
        return cards;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Card pickCard() {
        return pick.isEmpty() ? null : pick.remove(0);
    }

    public Card pickDiscard() {
        return discard.isEmpty() ? null : discard.remove(discard.size() - 1).retourner();
    }

    public void addToDiscard(final Card card) {
        discard.add(card.faceVisible() ? card : card.retourner());
    }

    public Player getActualPlayer() {
        return players.get(indexActualPlayer);
    }

    public void nextPlayer() {
        indexActualPlayer = (indexActualPlayer + 1) % players.size();
    }

    private boolean hasPlayerRevealedAllCards() {
        return players.stream().anyMatch(player -> player.getCartes().stream().allMatch(Card::faceVisible));
    }

    public void revealAllCards() {
        players.forEach(player -> player.getCartes().replaceAll(card -> card.faceVisible() ? card : card.retourner()));
    }

    public Map<Player, Integer> getRanking() {
        Map<Player, Integer> ranking = new HashMap<>();
        players.forEach(player -> ranking.put(player, player.getCartes().stream().mapToInt(c -> c.valeur().getValue()).sum()));
        return ranking;
    }

    public boolean isFinished() {
        return hasPlayerRevealedAllCards();
    }

    public List<Card> getPick() {
        return List.copyOf(pick);
    }

    public List<Card> getDiscard() {
        return List.copyOf(discard);
    }

    public void startGame() {
        players.forEach(player -> player.getCartes().addAll(pick.subList(0, 12)));
        pick.subList(0, 12).clear();
        discard.add(pick.remove(0).retourner());
    }

    public void exchangeOrRevealCard(final Player player, final Card newCard, final int cardIndex) {
        if (cardIndex == -1) {
            addToDiscard(newCard);
        } else {
            Card oldCard = player.getCartes().set(cardIndex, newCard);
            addToDiscard(oldCard);
            revealCard(player, cardIndex);
        }
    }

    public void revealCard(final Player player, final int cardIndex) {
        player.getCartes().set(cardIndex, player.getCartes().get(cardIndex).retourner());
    }

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


    public Card getTopDiscard() {
        return discard.isEmpty() ? null : discard.get(discard.size() - 1);
    }
}