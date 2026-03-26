package org.App.view.components;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;

/**
 * Represents the board view in the Skyjo game.
 * Displays cards in a grid layout.
 */
public class BoardView extends GridPane {

    public BoardView(List<CardView> cardViews) {
        int cols = cardViews.size() / 3;
        if (cols == 0) {
            cols = 1;
        }

        for (int i = 0; i < cardViews.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            this.add(cardViews.get(i), col, row);
        }

        this.setHgap(5);
        this.setVgap(5);
        this.setAlignment(Pos.CENTER);
    }

    public CardView getCardViewAtIndex(int index) {
        return (CardView) this.getChildren().get(index);
    }
}
