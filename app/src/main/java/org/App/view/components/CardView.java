package org.App.view.components;

import org.App.controller.GameController;
import org.App.model.game.Card;

import javafx.animation.RotateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * Represents the view of a card in the Skyjo game.
 * This class is responsible for displaying a card and handling its
 * interactions.
 * 
 * <p>
 * The card can be face up or face down, and its appearance changes based on its
 * value.
 * It also supports animations for flipping and scaling.
 * </p>
 * 
 * @see Card
 * @see GameController
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public class CardView extends StackPane {
    private Card value;
    private final int index;
    private final Rectangle cardBackground = new Rectangle(40, 60);
    private final Text cardValue = new Text();

    /**
     * Constructs a new CardView with the specified card value and index.
     *
     * @param value The card value to display.
     * @param index The index of the card in the player's hand.
     */
    public CardView(Card value, int index) {
        this.value = value;
        this.index = index;

        cardBackground.setStroke(Color.BLACK);
        cardBackground.setArcWidth(15); // Rounded corners
        cardBackground.setArcHeight(15);

        // Apply gradient background and rounded corners
        this.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.2));"
                        +
                        "-fx-background-radius: 10px; -fx-border-radius: 10px; -fx-border-color: rgba(255, 255, 255, 0.3);");

        // Card color styles
        if (value.faceVisible()) {
            // Update the card color logic based on value
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

            cardValue.setFont(new javafx.scene.text.Font(24));
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

    /**
     * Scales up the card background when the mouse enters.
     *
     * @param cardBackground The rectangle representing the card background.
     */
    private void scaleUp(Rectangle cardBackground) {
        Scale scale = new Scale(1.15, 1.15, cardBackground.getWidth() / 2, cardBackground.getHeight() / 2);
        cardBackground.getTransforms().add(scale);
    }

    /**
     * Scales down the card background when the mouse exits.
     *
     * @param cardBackground The rectangle representing the card background.
     */
    private void scaleDown(Rectangle cardBackground) {
        cardBackground.getTransforms().clear();
    }

    /**
     * Updates the card's appearance based on its value and visibility.
     */
    private void updateCardAppearance() {
        if (value.faceVisible()) {
            // Update the card color logic based on value
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

    /**
     * Flips the card with a rotation animation.
     */
    public void flipCard() {
        // Crée une animation de rotation pour simuler le retournement
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), this);
        rotateTransition.setAxis(javafx.scene.transform.Rotate.Y_AXIS); // Rotation autour de l'axe Y
        rotateTransition.setFromAngle(0); // Commence à 0 degrés
        rotateTransition.setToAngle(180); // Termine à 180 degrés
    
        // Change l'apparence de la carte à mi-chemin de l'animation (90 degrés)
        rotateTransition.setOnFinished(event -> {
            // À mi-chemin, met à jour l'apparence de la carte
            if (this.getRotate() >= 90) {
                updateCardAppearance();
            }
        });
    
        // Démarre l'animation
        rotateTransition.play();
    }

    /**
     * Handles the click event on the card.
     */
    private void handleClick() {
        System.out.println("Carte cliquée, index = " + getIndex());
        if (GameController.getInstance() == null) {
            System.out.println("GameController est null !");
        } else {
            GameController.getInstance().handleCardClick(this);
        }
    }

    /**
     * Gets the card value.
     *
     * @return The card value as a {@link Card}.
     */
    public Card getValue() {
        return value;
    }

    /**
     * Sets the card value and triggers a flip animation.
     *
     * @param value The new card value.
     */
    public void setValue(Card value) {
        this.value = value;
        flipCard(); // Trigger the flip animation when the card is updated
    }

    /**
     * Gets the index of the card in the player's hand.
     *
     * @return The index of the card.
     */
    public int getIndex() {
        return index;
    }
}