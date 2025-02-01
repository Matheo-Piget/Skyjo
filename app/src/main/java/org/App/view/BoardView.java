package org.App.view;

import java.util.List;

import javafx.scene.layout.GridPane;

public class BoardView extends GridPane {

    public BoardView(List<CardView> cardViews) {
        int rows = 4; // 4x4 grid, ajuster si nécessaire
        int cols = 4;

        for (int i = 0; i < cardViews.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            this.add(cardViews.get(i), col, row);
        }

        this.setHgap(10); // Horizontal gap between cards
        this.setVgap(10); // Vertical gap between cards
    }
}
