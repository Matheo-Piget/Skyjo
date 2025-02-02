package org.App.model;

import java.util.ArrayList;
import java.util.List;

public final class HumanPlayer implements Player {
    private final String nom;
    private final List<Card> cartes;

    public HumanPlayer(String nom) {
        this.nom = nom;
        this.cartes = new ArrayList<>();
    }

    @Override
    public String getNom() {
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
}
