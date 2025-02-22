package org.App.view.components;

import java.util.List;

import javafx.scene.layout.GridPane;

/**
 * Represents the board view in the Skyjo game.
 * This class is responsible for displaying the cards in a grid layout.
 * 
 * <p>
 * The board is organized in rows and columns, with gaps between the cards for
 * better visibility.
 * </p>
 * 
 * @see CardView
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public class BoardView extends GridPane {

    /**
     * Constructs a new BoardView with the specified list of card views.
     *
     * @param cardViews The list of {@link CardView} instances to display on the
     *                  board.
     */
    public BoardView(List<CardView> cardViews) {
        int cols = cardViews.size() / 3;

        for (int i = 0; i < cardViews.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            this.add(cardViews.get(i), col, row);
        }

        // Horizontal and vertical gaps between cards
        this.setHgap(8);
        this.setVgap(8);
        this.setMaxSize(75, 60);
        this.setPrefSize(75, 60);
    }

    public CardView getCardViewAtIndex(int index) {
        return (CardView) this.getChildren().get(index);
    }
}