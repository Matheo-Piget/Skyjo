package org.App.view;

import org.App.controller.GameController;
import org.App.model.Card;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class DiscardView extends StackPane {
    private Card topCard;

    public DiscardView(Card topCard) {
        this.topCard = topCard;
        updateView();

        // Adding shadow effect
        DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
        this.setEffect(shadow);

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
