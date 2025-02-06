package org.App.view;

import java.util.List;

import javafx.scene.layout.GridPane;

public class BoardView extends GridPane {

    public BoardView(List<CardView> cardViews) {
        int cols = cardViews.size() / 3;

        for (int i = 0; i < cardViews.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            this.add(cardViews.get(i), col, row);
        }

        // Horizontal and vertical gaps between cards
        this.setHgap(10);
        this.setVgap(10);
        this.setMaxSize(100, 75);
        this.setPrefSize(100, 75);
    }
}
