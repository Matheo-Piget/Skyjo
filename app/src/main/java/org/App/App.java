package org.App;

import org.App.view.screens.GameMenuView;
import org.App.view.utils.MusicManager;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Represents the main application class for the Skyjo game.
 * This class is responsible for starting the game and handling the primary
 * stage.
 * 
 * <p>
 * The application class is a subclass of {@link Application} and overrides the
 * {@link Application#start(Stage)} method.
 * </p>
 * 
 * @see GameMenuView
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public final class App extends Application {

    public static App INSTANCE;

    /**
     * Starts the game by displaying the main menu.
     *
     * @param primaryStage The primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) {

        MusicManager musicManager = new MusicManager("src/main/resources/musics/menu_music.mp3");

        // Passer le MusicManager à GameMenuView
        GameMenuView gameMenuView = new GameMenuView(primaryStage, musicManager);
        primaryStage.setScene(gameMenuView.getScene());
        primaryStage.show();

        INSTANCE = this;
    }

    /**
     * Returns the singleton instance of the application.
     *
     * @return The singleton instance of the application.
     */
    public static App getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Restarts the game by creating a new primary stage.
     */
    public void restart() {
        Stage primaryStage = new Stage();
        start(primaryStage);
    }

    /**
     * Launches the application by calling the {@link Application#launch(String...)}
     * method.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
