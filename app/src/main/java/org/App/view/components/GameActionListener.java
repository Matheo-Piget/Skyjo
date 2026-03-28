package org.App.view.components;

/**
 * Listener interface for user interactions on game components.
 * Decouples the view layer from controllers / singletons.
 * Both local and online controllers implement this interface.
 */
public interface GameActionListener {

    /** Called when the player clicks a card in their board. */
    void onCardClicked(CardView cardView);

    /** Called when the player clicks the draw pile. */
    void onPickClicked();

    /** Called when the player clicks the discard pile. */
    void onDiscardClicked();

    /** Called when the player clicks "next round" after the ranking screen. */
    default void onNextRoundRequested() {}
}
