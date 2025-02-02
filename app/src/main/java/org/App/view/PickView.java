package org.App.view;

import org.App.controller.GameController;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class PickView extends StackPane {
    public PickView(int remainingCards) {
        Rectangle cardBackground = new Rectangle(50, 75);
        cardBackground.setFill(Color.GRAY);
        cardBackground.setStroke(Color.BLACK);

        Text cardCount = new Text(String.valueOf(remainingCards));
        cardCount.setStyle("-fx-font-size: 24px;");

        getChildren().addAll(cardBackground, cardCount);

        setOnMouseClicked(event -> handleClick());
    }

    private void handleClick() {
        GameController.getInstance().handlePickClick();
    }
}