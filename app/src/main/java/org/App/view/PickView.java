package org.App.view;


import org.App.controller.GameController;

import javafx.animation.ScaleTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PickView extends StackPane {
    public PickView(int remainingCards) {
        Rectangle cardBackground = new Rectangle(40, 60);
        cardBackground.setFill(Color.GRAY);
        cardBackground.setStroke(Color.BLACK);
        cardBackground.setArcWidth(10); 
        cardBackground.setArcHeight(10); 

        Text cardCount = new Text(String.valueOf(remainingCards));
        cardCount.setStyle("-fx-font-size: 24px;");

        // Adding shadow for depth
        DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
        cardBackground.setEffect(shadow);

        // Animation for scale up
        cardBackground.setOnMouseEntered(event -> scaleUp(cardBackground));
        cardBackground.setOnMouseExited(event -> scaleDown(cardBackground));

        getChildren().addAll(cardBackground, cardCount);

        setOnMouseClicked(event -> handleClick());
    }

    private void scaleUp(Rectangle cardBackground) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(200), cardBackground);
        transition.setToX(1.3);
        transition.setToY(1.3);
        transition.play();
    }

    private void scaleDown(Rectangle cardBackground) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(200), cardBackground);
        transition.setToX(1);
        transition.setToY(1);
        transition.play();
    }

    private void handleClick() {
        GameController.getInstance().handlePickClick();
    }

    public void addCard(CardView cardView) {
        this.getChildren().add(cardView);
    }
}
