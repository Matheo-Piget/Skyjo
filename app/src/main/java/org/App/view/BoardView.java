package org.App.view;

import java.util.List;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class BoardView extends GridPane {

    public BoardView(List<CardView> cardViews) {
        int cols = cardViews.size()/3;

        for (int i = 0; i < cardViews.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            this.add(cardViews.get(i), col, row);
        }

        // Horizontal and vertical gaps between cards
        this.setHgap(10);
        this.setVgap(10);

        // Applying shadow effect to the whole grid
        DropShadow shadow = new DropShadow(10, 5, 5, Color.GRAY);
        this.setEffect(shadow);
    }
}
