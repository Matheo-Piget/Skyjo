package org.App.view.components;

import org.App.App;
import org.App.controller.GameController;
import org.App.controller.OnlineGameController;
import org.App.network.NetworkManager;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Represents the draw pile in the game.
 */
public class PickView extends StackPane {

    public PickView(int remainingCards) {
        Rectangle cardBackground = new Rectangle(50, 72);
        cardBackground.setFill(Color.web("#334155"));
        cardBackground.setStroke(Color.web("#475569"));
        cardBackground.setStrokeWidth(1.5);
        cardBackground.setArcWidth(12);
        cardBackground.setArcHeight(12);

        // Stacked card effect - second card behind
        Rectangle bgCard = new Rectangle(50, 72);
        bgCard.setFill(Color.web("#293548"));
        bgCard.setStroke(Color.web("#3d4f63"));
        bgCard.setStrokeWidth(1);
        bgCard.setArcWidth(12);
        bgCard.setArcHeight(12);
        bgCard.setTranslateX(3);
        bgCard.setTranslateY(3);

        Text cardCount = new Text(String.valueOf(remainingCards));
        cardCount.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        cardCount.setFill(Color.web("#94a3b8"));

        Text label = new Text("Pioche");
        label.setFont(Font.font("Segoe UI", 10));
        label.setFill(Color.web("#64748b"));
        label.setTranslateY(28);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        shadow.setColor(Color.color(0, 0, 0, 0.3));
        this.setEffect(shadow);

        setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), this);
            st.setToX(1.08);
            st.setToY(1.08);
            st.play();
        });
        setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(150), this);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        setAlignment(Pos.CENTER);
        getChildren().addAll(bgCard, cardBackground, cardCount, label);
        setOnMouseClicked(event -> handleClick());
    }

    private void handleClick() {
        if (App.getINSTANCE().isOnlineGame) {
            OnlineGameController controller = NetworkManager.getInstance().getOnlineController();
            if (controller != null) {
                controller.handlePickClick();
            }
        } else {
            GameController.getInstance().handlePickClick();
        }
    }

    public void addCard(CardView cardView) {
        this.getChildren().add(cardView);
    }
}
