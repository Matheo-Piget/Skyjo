package org.App.view;

import org.App.controller.GameController;
import org.App.model.Card;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Represents the discard pile view in the Skyjo game.
 * This class is responsible for displaying the top card of the discard pile and handling its interactions.
 * 
 * <p>
 * The discard pile supports a shadow effect and updates its appearance when the top card changes.
 * </p>
 * 
 * @see Card
 * @see GameController
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public class DiscardView extends StackPane {
    private Card topCard;

    /**
     * Constructs a new DiscardView with the specified top card.
     *
     * @param topCard The top card of the discard pile.
     */
    public DiscardView(Card topCard) {
        this.topCard = topCard;
        updateView();

        // Adding shadow effect
        DropShadow shadow = new DropShadow(5, 3, 3, Color.GRAY);
        this.setEffect(shadow);

        setOnMouseClicked(event -> handleClick(event));
    }

    /**
     * Sets the top card of the discard pile and updates the view.
     *
     * @param topCard The new top card.
     */
    public void setTopCard(Card topCard) {
        this.topCard = topCard;
        updateView();
    }

    /**
     * Updates the view to reflect the current top card.
     */
    private void updateView() {
        getChildren().clear();
        if (topCard != null) {
            CardView cardView = new CardView(topCard, -1);
            cardView.setMouseTransparent(true); // Disable interactions to prevent multiple clicks
            getChildren().add(cardView);
        }
    }

    /**
     * Adds a card view to the discard pile.
     *
     * @param cardView The card view to add.
     */
    public void addCard(CardView cardView) {
        this.getChildren().add(cardView);
    }

    /**
     * Handles the click event on the discard pile.
     *
     * @param event The mouse event.
     */
    private void handleClick(javafx.scene.input.MouseEvent event) {
        GameController.getInstance().handleDiscardClick();
        // Prevent the event from propagating to children
        event.consume();
    }
}