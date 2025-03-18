package org.App.view.components;

import org.App.App;
import org.App.controller.GameController;
import org.App.controller.OnlineGameController;
import org.App.network.NetworkManager;

import javafx.animation.ScaleTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Represents a view for the draw pile in the game.
 * This view displays the number of remaining cards and allows the user to
 * interact with the pile.
 * It includes animations for hover effects and handles click events to draw a
 * card.
 * 
 * @version 1.0
 * @author Piget Mathéo
 * @see StackPane
 * @see GameController
 * @see Rectangle
 * @see Text
 */
public class PickView extends StackPane {

    /**
     * Constructs a PickView with the specified number of remaining cards.
     * 
     * @param remainingCards The number of cards remaining in the draw pile.
     */
    public PickView(int remainingCards) {
        // Create the card background
        Rectangle cardBackground = new Rectangle(40, 60);
        cardBackground.setFill(Color.GRAY);
        cardBackground.setStroke(Color.BLACK);
        cardBackground.setArcWidth(10);
        cardBackground.setArcHeight(10);

        // Display the number of remaining cards
        Text cardCount = new Text(String.valueOf(remainingCards));
        cardCount.setStyle("-fx-font-size: 24px;");

        // Add a shadow effect for depth
        DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
        cardBackground.setEffect(shadow);

        // Add hover animations
        cardBackground.setOnMouseEntered(event -> scaleUp(cardBackground));
        cardBackground.setOnMouseExited(event -> scaleDown(cardBackground));

        // Add components to the view
        getChildren().addAll(cardBackground, cardCount);

        // Handle click events
        setOnMouseClicked(event -> handleClick());
    }

    /**
     * Animates the card background to scale up when the mouse enters.
     * 
     * @param cardBackground The rectangle representing the card background.
     */
    private void scaleUp(Rectangle cardBackground) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(200), cardBackground);
        transition.setToX(1.3);
        transition.setToY(1.3);
        transition.play();
    }

    /**
     * Animates the card background to scale down when the mouse exits.
     * 
     * @param cardBackground The rectangle representing the card background.
     */
    private void scaleDown(Rectangle cardBackground) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(200), cardBackground);
        transition.setToX(1);
        transition.setToY(1);
        transition.play();
    }

    /**
     * Handles the click event on the draw pile.
     * This method delegates the click handling to the GameController.
     */
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

    /**
     * Adds a card view to the draw pile.
     * 
     * @param cardView The card view to add.
     */
    public void addCard(CardView cardView) {
        this.getChildren().add(cardView);
    }
}