package org.App.view.components;

import org.App.model.game.Card;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Represents the discard pile view in the Skyjo game.
 * Decoupled from controllers — delegates clicks to the global {@link GameActionListener}.
 */
public class DiscardView extends StackPane {
    private Card topCard;

    public DiscardView(Card topCard) {
        this.topCard = topCard;
        updateView();

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        shadow.setColor(Color.color(0, 0, 0, 0.3));
        this.setEffect(shadow);

        Text label = new Text("Defausse");
        label.setFont(Font.font("Segoe UI", 10));
        label.setFill(Color.web("#64748b"));
        label.setTranslateY(28);
        getChildren().add(label);

        setAlignment(Pos.CENTER);

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

        setOnMouseClicked(event -> {
            GameActionListener listener = CardView.globalListener;
            if (listener != null) {
                listener.onDiscardClicked();
            }
            event.consume();
        });
    }

    public void setTopCard(Card topCard) {
        this.topCard = topCard;
        updateView();
    }

    private void updateView() {
        getChildren().removeIf(node -> node instanceof CardView);
        if (topCard != null) {
            CardView cardView = new CardView(topCard, -1, -1);
            cardView.setMouseTransparent(true);
            getChildren().addFirst(cardView);
        }
    }

    public void addCard(CardView cardView) {
        this.getChildren().add(cardView);
    }
}
