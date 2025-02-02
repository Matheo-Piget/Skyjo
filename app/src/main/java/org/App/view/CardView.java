package org.App.view;

import org.App.controller.GameController;
import org.App.model.Card;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class CardView extends StackPane {
    private Card value;
    private final int index;

    public CardView(Card value, int index) {
        this.value = value;
        this.index = index;

        Rectangle cardBackground = new Rectangle(40, 65);
        cardBackground.setStroke(Color.BLACK);

        Text cardValue = new Text();

        if (value.faceVisible()) {
            switch (value.valeur()) {
                case MOINS_DEUX, MOINS_UN -> cardBackground.setFill(Color.MAGENTA);
                case ZERO -> cardBackground.setFill(Color.CYAN);
                case UN, DEUX, TROIS, QUATRE -> cardBackground.setFill(Color.GREENYELLOW);
                case CINQ, SIX, SEPT, HUIT -> cardBackground.setFill(Color.YELLOW);
                case NEUF, DIX, ONZE , DOUZE -> cardBackground.setFill(Color.RED);
                default -> throw new AssertionError();
            }
            cardValue.setText(String.valueOf((int) switch (value.valeur()) {
                case MOINS_DEUX -> -2;
                case MOINS_UN -> -1;
                default -> value.valeur().getValue();
            }));
        } else {
            cardBackground.setFill(Color.BLACK);
            cardValue.setText("?");
            cardValue.setFill(Color.WHITE); // Assurez-vous que le texte est visible sur fond noir
        }

        cardValue.setStyle("-fx-font-size: 24px;");

        getChildren().addAll(cardBackground, cardValue);
        StackPane.setAlignment(cardValue, Pos.CENTER); // Aligner le texte après l'ajout

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
