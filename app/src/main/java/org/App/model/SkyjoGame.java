package org.App.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class SkyjoGame {
    private final List<Player> players;
    private final List<Card> pick;
    private final List<Card> discard;
    private int indexActualPlayer = 0;

    private final Random random = new Random();

    public SkyjoGame(List<Player> players) {
        this.players = List.copyOf(players);
        this.pick = creatPick();
        this.discard = new ArrayList<>();
    }

    private List<Card> creatPick() {
        List<Card> cartes = new ArrayList<>();
        for (CardValue valeur : CardValue.values()) {
            switch (valeur) {
                case ZERO -> {
                    for (int i = 0; i < 15; i++) {
                        cartes.add(new Card(valeur, false));
                    }
                }
                case MOINS_DEUX -> {
                    for (int i = 0; i < 5; i++) {
                        cartes.add(new Card(valeur, false));
                    }
                }
                default -> {
                    for (int i = 0; i < 10; i++) {
                        cartes.add(new Card(valeur, false));
                    }
                }
            }
        }
        Collections.shuffle(cartes);
        return cartes;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Card pickCard() {
        return pick.isEmpty() ? null : pick.remove(0);
    }

    public Card discardCard(Card card) {
        if (!card.faceVisible()) {
            card = card.retourner();
        }
        discard.add(card);
        return card;

    }

    public Card pickDiscard() {
        if (discard.isEmpty()) {
            return null;
        }
        Card card = discard.remove(discard.size() - 1);
        card = card.retourner();
        return card;
    }

    public void addToDiscard(Card card) {
        if (!card.faceVisible()) {
            card = card.retourner();  // Retourne la carte si elle est face cachée
        }
        discard.add(card);
    }
    

    public Player getActualPlayer() {
        return players.get(indexActualPlayer);
    }

    public void nextPlayer() {
        indexActualPlayer = (indexActualPlayer + 1) % players.size();
    }

    public boolean isFinished() {
        return pick.isEmpty();
    }

    public List<Card> getPick() {
        return List.copyOf(pick);
    }

    public List<Card> getDiscard() {
        return List.copyOf(discard);
    }

    public void startGame() {
        for (Player player : players) {
            for (int i = 0; i < 12; i++) {
                player.piocher(pickCard());
            }
        }
        discard.add(pickCard().retourner());
    }

    public void exchangeCard(Player player, Card newCard, int cardIndex) {
        Card oldCard = player.getCartes().get(cardIndex);
        player.getCartes().set(cardIndex, newCard);
        addToDiscard(oldCard);
        revealCard(player, cardIndex);
    }

    public void revealCard(Player player, int cardIndex) {
        Card card = player.getCartes().get(cardIndex);
        player.getCartes().set(cardIndex, card.retourner());
    }

    public void revealInitialCards() {
        int highestTotal = -1;
        int startingPlayerIndex = 0;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int total = 0;
            for (int j = 0; j < 2; j++) {
                int rand = random.nextInt(player.getCartes().size());
                Card card = player.getCartes().get(rand);
                player.getCartes().set(rand, card.retourner());
                total += card.valeur().getValue();
            }
            if (total > highestTotal) {
                highestTotal = total;
                startingPlayerIndex = i;
            }
        }

        indexActualPlayer = startingPlayerIndex;
    }
}