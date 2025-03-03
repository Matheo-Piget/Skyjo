package org.App.view.components;

import org.App.controller.GameController;
import org.App.model.game.Card;
import org.App.view.utils.SoundManager;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
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
 * The card view supports flipping animations and hover effects.
 * </p>
 * 
 * @see Card
 * @author Mathéo Piget
 * @version 1.0
 */
public class CardView extends StackPane {
    private final int playerId;
    private final int cardId;
    private Card value;
    private final int index;
    private final Rectangle cardBackground = new Rectangle(40, 60);
    private final Text frontText = new Text(); // Texte pour la face avant
    private final Text backText = new Text("?"); // Texte pour la face arrière

    public CardView(Card value, int index, int playerId) {

        this.playerId = playerId;
        this.value = value;
        this.index = index;
        this.cardId = value.id();

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
     * Gets the ID of the player who owns the card.
     * 
     * @return The ID of the player.
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Gets the ID of the card.
     * 
     * @return The ID of the card.
     */
    public int getCardId() {
        return cardId;
    }

    /**
     * Indicates whether the back of the card is visible.
     * 
     * @return True if the back is visible, false otherwise.
     */
    public boolean isBackVisible() {
        return backText.isVisible();
    }

    /**
     * Animates the card with a shaking effect.
     */
    @SuppressWarnings("unused")
    private void shake(CardView cardView) {
        TranslateTransition shakeTransition = new TranslateTransition(Duration.millis(100), cardView);
        shakeTransition.setFromX(0);
        shakeTransition.setToX(10);
        shakeTransition.setAutoReverse(true);
        shakeTransition.setCycleCount(4);
        shakeTransition.setInterpolator(Interpolator.EASE_BOTH);
        shakeTransition.play();
    }

    /**
     * Met à jour l'apparence de la carte en fonction de son état (face visible ou
     * non).
     */
    private void updateCardAppearance() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.6));

        cardBackground.setEffect(dropShadow);
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
     * Animate the card flipping.
     * 
     * @param onFinished Runnable to execute after the animation is finished.
     */
    public void flipCard(Runnable onFinished) {
        // Première étape : rotation de 0 à 90 degrés
        RotateTransition firstHalf = new RotateTransition(Duration.seconds(0.25), this);
        firstHalf.setAxis(Rotate.Y_AXIS);
        firstHalf.setFromAngle(0);
        firstHalf.setToAngle(90);
        firstHalf.setInterpolator(Interpolator.EASE_IN);

        // Inverser le texte pendant la rotation
        frontText.setScaleX(-1);

        firstHalf.setOnFinished(event -> {
            // Actualise la référence de la carte avec la nouvelle instance retournée
            setValue(value.retourner());
            // Met à jour l'apparence de la carte après la mise à jour
            updateCardAppearance();

            // Lance la deuxième étape de l'animation : rotation de 90 à 180 degrés
            RotateTransition secondHalf = new RotateTransition(Duration.seconds(0.25), this);
            secondHalf.setAxis(Rotate.Y_AXIS);
            secondHalf.setFromAngle(90);
            secondHalf.setToAngle(180);
            secondHalf.setInterpolator(Interpolator.EASE_OUT);
            secondHalf.setOnFinished(e -> {
                if (onFinished != null) {
                    onFinished.run();
                }
            });
            secondHalf.play();
        });

        firstHalf.play();

        SoundManager.playFlipSound();
    }

    /**
     * Scales up the card background when the mouse enters.
     * 
     * @param cardBackground The card background to scale up.
     */
    private void scaleUp(Rectangle cardBackground) {
        Scale scale = new Scale(1.15, 1.15, cardBackground.getWidth() / 2, cardBackground.getHeight() / 2);
        cardBackground.getTransforms().add(scale);
    }

    /**
     * Scales down the card background when the mouse exits.
     * 
     * @param cardBackground The card background to scale down.
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
     * @return The card value.
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
        updateCardAppearance();
    }

    /**
     * Indicates whether the card is flipped.
     * 
     * @return True if the card is flipped, false otherwise.
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