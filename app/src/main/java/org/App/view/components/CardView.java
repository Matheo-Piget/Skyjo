package org.App.view.components;

import org.App.model.game.Card;
import org.App.view.utils.OptionsManager;
import org.App.view.utils.SoundManager;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Represents the view of a card in the Skyjo game.
 * Decoupled from controllers — delegates clicks to an injected {@link GameActionListener}.
 */
public class CardView extends StackPane {
    static GameActionListener globalListener;

    private final int playerId;
    private final int cardId;
    private Card value;
    private final int index;
    private final Rectangle cardBackground = new Rectangle(50, 72);
    private final Text frontText = new Text();
    private final Text backText = new Text("?");

    /**
     * Sets the global action listener for all card views.
     * Called once when the controller initialises the game view.
     */
    public static void setGlobalListener(GameActionListener listener) {
        globalListener = listener;
    }

    public static GameActionListener getGlobalListener() {
        return globalListener;
    }

    public CardView(Card value, int index, int playerId) {
        this.playerId = playerId;
        this.value = value;
        this.index = index;
        this.cardId = value.id();

        cardBackground.setStroke(Color.web("#475569"));
        cardBackground.setStrokeWidth(1.5);
        cardBackground.setArcWidth(12);
        cardBackground.setArcHeight(12);

        updateCardAppearance();

        setCursor(Cursor.HAND);
        setOnMouseEntered(event -> scaleUp());
        setOnMouseExited(event -> scaleDown());

        getChildren().addAll(cardBackground, frontText, backText);
        StackPane.setAlignment(frontText, javafx.geometry.Pos.CENTER);
        StackPane.setAlignment(backText, javafx.geometry.Pos.CENTER);

        backText.setVisible(true);

        setOnMouseClicked(event -> {
            if (globalListener != null) {
                globalListener.onCardClicked(this);
            }
        });
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getCardId() {
        return cardId;
    }

    public boolean isBackVisible() {
        return backText.isVisible();
    }

    private void updateCardAppearance() {
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(8.0);
        dropShadow.setOffsetX(0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.35));
        cardBackground.setEffect(dropShadow);

        if (value.faceVisible()) {
            switch (value.valeur()) {
                case MOINS_DEUX, MOINS_UN -> {
                    cardBackground.setFill(Color.web("#22c55e"));
                    cardBackground.setStroke(Color.web("#16a34a"));
                }
                case ZERO -> {
                    cardBackground.setFill(Color.web("#3b82f6"));
                    cardBackground.setStroke(Color.web("#2563eb"));
                }
                case UN, DEUX, TROIS, QUATRE -> {
                    cardBackground.setFill(Color.web("#eab308"));
                    cardBackground.setStroke(Color.web("#ca8a04"));
                }
                case CINQ, SIX, SEPT, HUIT -> {
                    cardBackground.setFill(Color.web("#f97316"));
                    cardBackground.setStroke(Color.web("#ea580c"));
                }
                case NEUF, DIX, ONZE, DOUZE -> {
                    cardBackground.setFill(Color.web("#ef4444"));
                    cardBackground.setStroke(Color.web("#dc2626"));
                }
                default -> throw new AssertionError();
            }
            frontText.setText(String.valueOf(value.valeur().getValue()));
            backText.setText("");
            backText.setVisible(false);
            frontText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
            frontText.setFill(Color.WHITE);
            frontText.setEffect(new DropShadow(2, 0, 1, Color.color(0, 0, 0, 0.3)));
        } else {
            backText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
            cardBackground.setFill(Color.web("#1e293b"));
            cardBackground.setStroke(Color.web("#334155"));
            backText.setFill(Color.web("#64748b"));
        }
    }

    public void flipCard(Runnable onFinished) {
        double speed = OptionsManager.getAnimationSpeed();

        RotateTransition firstHalf = new RotateTransition(Duration.seconds(0.2 * speed), this);
        firstHalf.setAxis(Rotate.Y_AXIS);
        firstHalf.setFromAngle(0);
        firstHalf.setToAngle(90);
        firstHalf.setInterpolator(Interpolator.EASE_IN);

        frontText.setScaleX(-1);

        firstHalf.setOnFinished(event -> {
            setValue(value.retourner());
            updateCardAppearance();

            RotateTransition secondHalf = new RotateTransition(Duration.seconds(0.2 * speed), this);
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

    private void scaleUp() {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), this);
        st.setToX(1.12);
        st.setToY(1.12);
        st.setInterpolator(Interpolator.EASE_OUT);
        st.play();
    }

    private void scaleDown() {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), this);
        st.setToX(1.0);
        st.setToY(1.0);
        st.setInterpolator(Interpolator.EASE_OUT);
        st.play();
    }

    public Card getValue() {
        return value;
    }

    public void setValue(Card value) {
        this.value = value;
        updateCardAppearance();
    }

    public boolean isFlipped() {
        return value.faceVisible();
    }

    public int getIndex() {
        return index;
    }
}
