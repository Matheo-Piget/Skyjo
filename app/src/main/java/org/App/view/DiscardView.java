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

        setOnMouseClicked(event -> handleClick(event));
    }

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
        updateView();
    }

    private void updateView() {
        getChildren().clear();
        if (topCard != null) {
            CardView cardView = new CardView(topCard, -1);
            cardView.setMouseTransparent(true); // Désactive les interactions pour éviter les clics multiples
            getChildren().add(cardView);
        }
    }

    private void handleClick(javafx.scene.input.MouseEvent event) {
        GameController.getInstance().handleDiscardClick();
        // Empêche la propagation du clic aux enfants

        event.consume();
    }
}
