package org.App.view;

import org.App.controller.GameController;
import org.App.model.Card;

import javafx.animation.RotateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

public class CardView extends StackPane {
    private Card value;
    private final int index;
    private final Rectangle cardBackground = new Rectangle(40, 60);
    private final Text cardValue = new Text();

    public CardView(Card value, int index) {
        this.value = value;
        this.index = index;

        cardBackground.setStroke(Color.BLACK);
        cardBackground.setArcWidth(15); // Rounded corners
        cardBackground.setArcHeight(15); 

        // Apply gradient background and rounded corners
        this.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.2));" +
                      "-fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: rgba(255, 255, 255, 0.3);");

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

    private void updateCardAppearance() {
        if (value.faceVisible()) {
            // Mettre à jour la couleur de la carte en fonction de sa valeur
            switch (value.valeur()) {
                case MOINS_DEUX, MOINS_UN -> cardBackground.setFill(Color.MAGENTA);
                case ZERO -> cardBackground.setFill(Color.CYAN);
                case UN, DEUX, TROIS, QUATRE -> cardBackground.setFill(Color.GREENYELLOW);
                case CINQ, SIX, SEPT, HUIT -> cardBackground.setFill(Color.YELLOW);
                case NEUF, DIX, ONZE, DOUZE -> cardBackground.setFill(Color.RED);
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
            cardValue.setFill(Color.WHITE);
        }
    }

    public void flipCard() {
        // Créer une animation de rotation pour simuler le retournement
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(2), this);
        rotateTransition.setAxis(javafx.scene.transform.Rotate.Y_AXIS); // Rotation autour de l'axe Y
        rotateTransition.setFromAngle(0); // Commence à 0 degré
        rotateTransition.setToAngle(180); // Termine à 180 degrés

        // Changer l'apparence de la carte à mi-chemin de l'animation
        rotateTransition.setOnFinished(event -> {
            updateCardAppearance(); // Mettre à jour l'apparence de la carte après le retournement
        });

        rotateTransition.play();
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
        flipCard(); // Lancer l'animation de retournement lorsque la carte est mise à jour
    }

    public int getIndex() {
        return index;
    }
}
