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

        getChildren().addAll(cardBackground, cardValue);
        StackPane.setAlignment(cardValue, javafx.geometry.Pos.CENTER);

        setOnMouseClicked(event -> handleClick());
    }

    /**
     * Met à jour l'apparence de la carte en fonction de son état (face visible ou non).
     */
    private void updateCardAppearance() {
        if (value.faceVisible()) {
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
        flip.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.greaterThanOrEqualTo(Duration.seconds(0.25)) && newValue.lessThan(Duration.seconds(0.5))) {
                updateCardAppearance(); // Met à jour l'apparence à 90 degrés
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
        updateCardAppearance(); // Mettre à jour l'apparence de la carte
    }

    /**
     * Indicates whether the card is flipped.
     *
     * @return {@code true} if the card is flipped, {@code false} otherwise.
     */
    public boolean isFlipped() {
        return value.faceVisible();
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