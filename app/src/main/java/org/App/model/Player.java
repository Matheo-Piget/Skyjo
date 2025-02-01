package org.App.model;

import java.util.List;

public interface Player {
    String getNom();
    List<Card> getCartes();
    void piocher(Card carte);
}
