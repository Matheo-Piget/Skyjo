package org.App.view.screens;

/*
 * Represents the interface for the game view.
 * This interface defines the methods that a game view must implement.
 * 
 * <p>
 * The game view is responsible for displaying the game state to the user.
 * </p>
 * 
 * @version 1.0
 * @see GameView
 * @see GameView
 */
@FunctionalInterface
interface CardAnimationTask {
    void run(Runnable onFinished);
}
