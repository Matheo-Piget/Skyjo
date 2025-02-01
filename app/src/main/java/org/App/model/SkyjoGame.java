package org.App.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SkyjoGame {
    private final List<Player> players;
    private final List<Card> pick;
    private int indexActualPlayer = 0;

    public SkyjoGame(List<Player> players) {
        this.players = List.copyOf(players);
        this.pick = creatPick();
    }

    private List<Card> creatPick() {
        List<Card> cartes = new ArrayList<>();
        for (CardValue valeur : CardValue.values()) {
            for (int i = 0; i < 10; i++) { 
                cartes.add(new Card(valeur, false));
            }
        }
        Collections.shuffle(cartes);
        return cartes;
    }

    public List<Player> getplayers() {
        return players;
    }

    public Card pickCard() {
        return pick.isEmpty() ? null : pick.remove(0);
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


}
