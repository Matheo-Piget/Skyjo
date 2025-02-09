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
        this.setHgap(5);
        this.setVgap(5);
        this.setMaxSize(75, 60);
        this.setPrefSize(75, 60);
    }
}
