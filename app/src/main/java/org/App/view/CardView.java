package org.App.view;

import org.App.controller.GameController;
import org.App.model.Card;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CardView extends StackPane {
    private Card value;
    private final int index;

    public CardView(Card value, int index) {
        if (value.faceVisible()){
            this.value = value;
            this.index = index;
            Rectangle cardBackground = new Rectangle(100, 150);
            cardBackground.setFill(Color.WHITE);
            cardBackground.setStroke(Color.BLACK);

            Text cardValue = new Text(String.valueOf((int) switch (value.valeur()) {
                case MOINS_DEUX -> -2;
                case MOINS_UN -> -1;
                default -> value.valeur().getValue();
            }));
            cardValue.setStyle("-fx-font-size: 24px;");

            getChildren().addAll(cardBackground, cardValue);
        } else {
            this.value = value;
            this.index = index;
            Rectangle cardBackground = new Rectangle(100, 150);
            cardBackground.setFill(Color.BLACK);
            cardBackground.setStroke(Color.BLACK);

            Text cardValue = new Text("?");
            cardValue.setStyle("-fx-font-size: 24px;");

            getChildren().addAll(cardBackground, cardValue);
        }

        setOnMouseClicked(event -> handleClick());
    }

    private void handleClick() {
        System.out.println("Carte cliquée, index = " + getIndex());
        if (GameController.getInstance() == null) {
            System.out.println("GameController est null !");
        } else {
            GameController.getInstance().handleCardClick(this);
        }
    }
    

    public Card getValue() {
        return value;
    }

    public void setValue(Card value) {
        this.value = value;
        ((Text) getChildren().get(1)).setText(value.faceVisible() ? String.valueOf(value.valeur().getValue()) : "?");
        ((Rectangle) getChildren().get(0)).setFill(value.faceVisible() ? Color.WHITE : Color.BLACK);
    }

    public int getIndex() {
        return index;
    }
}
