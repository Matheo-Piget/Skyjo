package org.App.view.components;

import org.App.controller.GameController;
import org.App.model.game.Card;
import org.App.view.utils.SoundManager;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * Represents the view of a card in the Skyjo game.
 * This class is responsible for displaying a card and handling its interactions.
 */
public class CardView extends StackPane {
    private Card value;
    private final int index;
    private final Rectangle cardBackground = new Rectangle(40, 60);
    private final Text frontText = new Text(); // Texte pour la face avant
    private final Text backText = new Text("?"); // Texte pour la face arrière

    public CardView(Card value, int index) {
        this.value = value;
        this.index = index;

        cardBackground.setStroke(Color.WHITE);
        cardBackground.setArcWidth(15);
        cardBackground.setArcHeight(15);

        // Appliquer le style de la carte
        updateCardAppearance();

        // Effet de survol
        cardBackground.setOnMouseEntered(event -> scaleUp(cardBackground));
        cardBackground.setOnMouseExited(event -> scaleDown(cardBackground));

        // Ajouter les éléments à la carte
        getChildren().addAll(cardBackground, frontText, backText);
        StackPane.setAlignment(frontText, javafx.geometry.Pos.CENTER);
        StackPane.setAlignment(backText, javafx.geometry.Pos.CENTER);

        // Masquer la face arrière initialement
        backText.setVisible(true);

        setOnMouseClicked(event -> handleClick());
    }

    /**
     * Met à jour l'apparence de la carte en fonction de son état (face visible ou non).
     */
    private void updateCardAppearance() {
        if (value.faceVisible()) {
            // Face avant
            switch (value.valeur()) {
                case MOINS_DEUX, MOINS_UN -> cardBackground.setFill(Color.MAGENTA);
                case ZERO -> cardBackground.setFill(Color.CYAN);
                case UN, DEUX, TROIS, QUATRE -> cardBackground.setFill(Color.GREENYELLOW);
                case CINQ, SIX, SEPT, HUIT -> cardBackground.setFill(Color.YELLOW);
                case NEUF, DIX, ONZE, DOUZE -> cardBackground.setFill(Color.RED);
                default -> throw new AssertionError();
            }
            frontText.setText(String.valueOf((int) switch (value.valeur()) {
                case MOINS_DEUX -> -2;
                case MOINS_UN -> -1;
                default -> value.valeur().getValue();
            }));
            backText.setText("");
            backText.setVisible(false);
            frontText.setFont(new javafx.scene.text.Font(24));
            frontText.setFill(Color.BLACK);
        } else {
            // Face arrière
            backText.setFont(new javafx.scene.text.Font(24));
            cardBackground.setFill(Color.BLACK);
            backText.setFill(Color.WHITE);
        }
    }

    /**
     * Anime le retournement de la carte.
     */
    public void flipCard(Runnable onFinished) {
        RotateTransition flip = new RotateTransition(Duration.seconds(0.5), this);
        flip.setAxis(Rotate.Y_AXIS);
        flip.setFromAngle(0);
        flip.setToAngle(180);
        flip.setInterpolator(Interpolator.EASE_BOTH);

        // Gestion de la visibilité des textes pendant l'animation
        flip.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.greaterThanOrEqualTo(Duration.seconds(0.25))) {
                // À 90 degrés, on change l'apparence de la carte
                updateCardAppearance();
                frontText.setVisible(value.faceVisible());
                backText.setVisible(!value.faceVisible());
            }
        });

        flip.setOnFinished(event -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });

        flip.play();
        SoundManager.playFlipSound();
    }

    /**
     * Scales up the card background when the mouse enters.
     */
    private void scaleUp(Rectangle cardBackground) {
        Scale scale = new Scale(1.15, 1.15, cardBackground.getWidth() / 2, cardBackground.getHeight() / 2);
        cardBackground.getTransforms().add(scale);
    }

    /**
     * Scales down the card background when the mouse exits.
     */
    private void scaleDown(Rectangle cardBackground) {
        cardBackground.getTransforms().clear();
    }

    /**
     * Handles the click event on the card.
     */
    private void handleClick() {
        if (GameController.getInstance() == null) {
            System.out.println("GameController est null !");
        } else {
            GameController.getInstance().handleCardClick(this);
        }
    }

    /**
     * Gets the card value.
     */
    public Card getValue() {
        return value;
    }

    /**
     * Sets the card value and triggers a flip animation.
     */
    public void setValue(Card value) {
        this.value = value;
        updateCardAppearance();
    }

    /**
     * Indicates whether the card is flipped.
     */
    public boolean isFlipped() {
        return value.faceVisible();
    }

    /**
     * Gets the index of the card in the player's hand.
     */
    public int getIndex() {
        return index;
    }
}