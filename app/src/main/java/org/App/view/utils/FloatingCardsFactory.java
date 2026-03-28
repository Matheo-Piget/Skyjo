package org.App.view.utils;

import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Factory for floating card-shaped background decorations.
 * Shared between App and GameMenuView.
 */
public final class FloatingCardsFactory {

    private FloatingCardsFactory() {}

    /**
     * Creates animated floating card decorations in the given pane.
     *
     * @param layer       The pane to add cards to.
     * @param count       Number of floating cards.
     * @param areaWidth   Maximum X spawn range.
     * @param areaHeight  Maximum Y spawn range.
     */
    public static void create(Pane layer, int count, double areaWidth, double areaHeight) {
        final Random rng = new Random();
        final boolean isDark = OptionsManager.getTheme().equals("Sombre");

        for (int i = 0; i < count; i++) {
            Rectangle card = new Rectangle(35 + rng.nextInt(25), 50 + rng.nextInt(30));
            card.setArcWidth(8);
            card.setArcHeight(8);
            card.setFill(isDark
                    ? Color.web("#818cf8", 0.03 + rng.nextDouble() * 0.04)
                    : Color.web("#6366f1", 0.04 + rng.nextDouble() * 0.05));
            card.setStroke(isDark
                    ? Color.web("#818cf8", 0.06)
                    : Color.web("#6366f1", 0.07));
            card.setStrokeWidth(1);

            card.setLayoutX(rng.nextDouble() * areaWidth);
            card.setLayoutY(rng.nextDouble() * areaHeight);
            card.setRotate(rng.nextDouble() * 360);

            layer.getChildren().add(card);

            TranslateTransition tt = new TranslateTransition(
                    Duration.seconds(14 + rng.nextDouble() * 16), card);
            tt.setFromY(0);
            tt.setToY(-25 - rng.nextDouble() * 50);
            tt.setFromX(0);
            tt.setToX(-15 + rng.nextDouble() * 30);
            tt.setAutoReverse(true);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.setInterpolator(Interpolator.EASE_BOTH);
            tt.play();

            RotateTransition rt = new RotateTransition(
                    Duration.seconds(22 + rng.nextDouble() * 20), card);
            rt.setByAngle(-12 + rng.nextDouble() * 24);
            rt.setAutoReverse(true);
            rt.setCycleCount(RotateTransition.INDEFINITE);
            rt.setInterpolator(Interpolator.EASE_BOTH);
            rt.play();
        }
    }
}
