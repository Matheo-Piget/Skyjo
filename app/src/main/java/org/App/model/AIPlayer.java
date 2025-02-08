package org.App.model;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer implements Player {
    private final String nom;
    private final List<Card> cartes;
    private final Difficulty difficulty;

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
}