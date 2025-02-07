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

public final class SkyjoGame {
    private final List<Player> players;
    private List<Card> pick;
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
            for (int i = 0; i < count; i++) {
                cards.add(new Card(value, false)); // Crée et ajoute chaque carte
            }
        }
        Collections.shuffle(cards);
        System.out.println("Nombre total de cartes générées : " + cards.size()); // DEBUG
        return cards;
    }
    

    public List<Player> getPlayers() {
        return players;
    }

    public Card pickCard() {
        return pick.isEmpty() ? null : pick.remove(0);
    }

    public Card pickDiscard() {
        if (discard.isEmpty()) {
            return null; // Si la défausse est vide
        } else {
            Card topCard = discard.remove(discard.size() - 1);
            System.out.println("Joueur " + indexActualPlayer + " a pioché de la défausse un " + topCard.valeur()); 
            return topCard.retourner(); // On retourne la carte
        }
    }

    public void addToDiscard(final Card card) {
        System.out.println("Joueur " + indexActualPlayer + " a mit dans la défausse un " + card.valeur());
        discard.add(card.faceVisible() ? card : card.retourner()); // On ajoute la carte retournée si elle est cachée
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
        players.forEach(
                player -> ranking.put(player, player.getCartes().stream().mapToInt(c -> c.valeur().getValue()).sum()));
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
        if (!pick.isEmpty()) pick.clear();
        if (!discard.isEmpty()) discard.clear();
        players.forEach(player -> player.getCartes().clear());
    
        pick = createPick(); // Créer une nouvelle pioche
        players.forEach(player -> {
            player.getCartes().addAll(pick.subList(0, 12)); // Donner 12 cartes à chaque joueur
            pick.subList(0, 12).clear(); // Retirer ces cartes de la pioche
        });
    
        // Vérifier si la distribution s'est bien passée
        System.out.println("Cartes restantes dans la pioche après distribution : " + pick.size());
        players.forEach(player -> System.out.println(player.getName() + " a " + player.getCartes().size() + " cartes"));
    
        // Ajoute la première carte de la pioche à la défausse
        if (!pick.isEmpty()) {
            Card firstCard = pick.remove(0).retourner(); // La retourner face visible
            discard.add(firstCard);
            System.out.println("Première carte dans la défausse : " + firstCard.valeur());
        }
    }
    

    public void exchangeOrRevealCard(final Player player, final Card newCard, final int cardIndex) {
        if (cardIndex == -1) {
            addToDiscard(newCard);
        } else {
            Card oldCard = player.getCartes().set(cardIndex, newCard);
            addToDiscard(oldCard);
        }
    }

    public void revealCard(final Player player, final int cardIndex) {
        player.getCartes().set(cardIndex, player.getCartes().get(cardIndex).retourner());
    }

    public void checkColumns() {
        Player player = players.get(indexActualPlayer);

        int columns = player.getCartes().size()/3; 
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