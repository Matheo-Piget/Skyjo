package org.App;

import org.App.view.screens.GameMenuView;
import org.App.view.screens.LobbyView;
import org.App.view.utils.MusicManager;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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

        // Menu principal modifié avec options de jeu
        VBox mainMenu = new VBox(20);
        mainMenu.setAlignment(Pos.CENTER);

        Button localGameButton = new Button("Jeu Local");
        localGameButton.setOnAction(e -> startLocalGame(primaryStage, musicManager));

        Button onlineGameButton = new Button("Jeu En Ligne");
        onlineGameButton.setOnAction(e -> startOnlineGame(primaryStage, musicManager));

        mainMenu.getChildren().addAll(
                new Text("Skyjo"),
                localGameButton,
                onlineGameButton);

        Scene scene = new Scene(mainMenu, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startLocalGame(Stage stage, MusicManager musicManager) {
        // Code existant qui lance le jeu local
        GameMenuView gameMenuView = new GameMenuView(stage, musicManager);
        stage.setScene(gameMenuView.getScene());
    }

    private void startOnlineGame(Stage stage, MusicManager musicManager) {
        // Nouveau code qui lance le lobby pour le jeu en ligne
        LobbyView lobbyView = new LobbyView(stage, musicManager);
        stage.setScene(lobbyView.getScene());
        stage.show();
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
