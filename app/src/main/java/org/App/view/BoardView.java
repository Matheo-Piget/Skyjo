package org.App.view;

import java.util.List;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class BoardView extends GridPane {

    public BoardView(List<CardView> cardViews) {
        int totalCards = cardViews.size();
        int rows = Math.min(3, totalCards); // On limite à 3 lignes max
        int cols = (int) Math.ceil((double) totalCards / rows); // Nombre de colonnes dynamiquement calculé
    
        for (int i = 0; i < totalCards; i++) {
            int row = i % rows;  // Répartition sur les 3 lignes max
            int col = i / rows;  // Augmente dynamiquement selon le nombre de cartes
            this.add(cardViews.get(i), col, row);
        }
    
        // Ajout d'espaces entre les cartes
        this.setHgap(10);
        this.setVgap(10);
    
        // Application d'un effet d'ombre sur la grille
        DropShadow shadow = new DropShadow(10, 5, 5, Color.GRAY);
        this.setEffect(shadow);
    }
    
}
