package org.App.view;

import org.App.controller.GameController;
import org.App.model.Card;

import javafx.scene.layout.StackPane;

public class DiscardView extends StackPane {
    private Card topCard;

    public DiscardView(Card topCard) {
        this.topCard = topCard;
        updateView();

        setOnMouseClicked(event -> handleClick());
    }

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
        updateView();
    }

    private void updateView() {
        getChildren().clear();
        if (topCard != null) {
            CardView cardView = new CardView(topCard, -1); // -1 indicates it's not part of the player's board
            getChildren().add(cardView);
        }
    }

    private void handleClick() {
        // Handle discard view click logic
        GameController.getInstance().handleDiscardClick();
    }
}