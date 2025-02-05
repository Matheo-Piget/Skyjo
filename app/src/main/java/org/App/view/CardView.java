package org.App.view;

import org.App.controller.GameController;
import org.App.model.Card;

import javafx.animation.TranslateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class CardView extends StackPane {
    private Card value;
    private final int index;

    private double initialX = 0;
    private double initialY = -100; // Commence au-dessus de l'écran

    public CardView(Card value, int index) {
        this.value = value;
        this.index = index;

        Rectangle cardBackground = new Rectangle(40, 60);  // Larger card size
        cardBackground.setStroke(Color.BLACK);
        cardBackground.setArcWidth(15); // Rounded corners
        cardBackground.setArcHeight(15); 

        Text cardValue = new Text();

        // Card color styles
        if (value.faceVisible()) {
            // Update the card color logic based on value
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

            cardValue.setFont( new javafx.scene.text.Font(24));
            
        } else {
            cardBackground.setFill(Color.BLACK);
            cardValue.setText("?");
            cardValue.setFill(Color.WHITE); 
        }

        setTranslateX(initialX);
        setTranslateY(initialY);

        // Scaling transition for interaction
        cardBackground.setOnMouseEntered(event -> scaleUp(cardBackground));
        cardBackground.setOnMouseExited(event -> scaleDown(cardBackground));


        getChildren().addAll(cardBackground, cardValue);
        StackPane.setAlignment(cardValue, javafx.geometry.Pos.CENTER); 

        setOnMouseClicked(event -> handleClick());
    }

    private void scaleUp(Rectangle cardBackground) {
        Scale scale = new Scale(1.15, 1.15, cardBackground.getWidth() / 2, cardBackground.getHeight() / 2);
        cardBackground.getTransforms().add(scale);
    }

    private void scaleDown(Rectangle cardBackground) {
        cardBackground.getTransforms().clear();
    }

    public void animateCard(double targetX, double targetY) {
        // Animation de la carte vers sa position cible
        TranslateTransition translate = new TranslateTransition(Duration.seconds(0.5), this);
        translate.setToX(targetX);
        translate.setToY(targetY);
        translate.play();
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
