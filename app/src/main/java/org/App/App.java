package org.App;

import org.App.view.screens.GameMenuView;
import org.App.view.screens.LobbyView;
import org.App.view.utils.FloatingCardsFactory;
import org.App.view.utils.MusicManager;
import org.App.view.utils.OptionsManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Main application class for the Skyjo game.
 */
public final class App extends Application {

    public static App INSTANCE;
    public boolean isOnlineGame = false;

    @Override
    public void start(Stage primaryStage) {
        MusicManager musicManager = new MusicManager("/musics/menu_music.mp3");

        // Background layer with floating card decorations
        Pane backgroundLayer = new Pane();
        backgroundLayer.setMouseTransparent(true);
        backgroundLayer.setPickOnBounds(false);
        FloatingCardsFactory.create(backgroundLayer, 8, 900, 700);

        VBox mainMenu = new VBox(24);
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setPadding(new Insets(60));

        // Title
        Label title = new Label("SKYJO");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 56));
        title.setTextFill(Color.web("#f1f5f9"));
        title.setStyle(
                "-fx-background-color: rgba(99, 102, 241, 0.1); " +
                "-fx-border-color: rgba(129, 140, 248, 0.3); " +
                "-fx-border-width: 2; -fx-border-radius: 16; " +
                "-fx-background-radius: 16; -fx-padding: 16 40;");

        Label subtitle = new Label("Le jeu de cartes");
        subtitle.setFont(Font.font("Segoe UI", 16));
        subtitle.setTextFill(Color.web("#94a3b8"));

        // Buttons
        Button localGameButton = createMenuButton("Jeu Local");
        localGameButton.setOnAction(e -> startLocalGame(primaryStage, musicManager));

        Button onlineGameButton = createMenuButton("Jeu En Ligne");
        onlineGameButton.setOnAction(e -> startOnlineGame(primaryStage, musicManager));

        mainMenu.getChildren().addAll(title, subtitle, localGameButton, onlineGameButton);

        StackPane root = new StackPane(backgroundLayer, mainMenu);

        Scene scene = new Scene(root, 800, 600);

        // Apply theme
        String cssPath = OptionsManager.getTheme().equals("Sombre")
                ? "/themes/menu.css"
                : "/themes/menu_light.css";
        try {
            scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load CSS: " + e.getMessage());
        }

        primaryStage.setTitle("Skyjo");
        primaryStage.setScene(scene);
        primaryStage.show();

        INSTANCE = this;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().addAll("button", "button-primary");
        button.setPrefSize(240, 48);
        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        return button;
    }

    private void startLocalGame(Stage stage, MusicManager musicManager) {
        GameMenuView gameMenuView = new GameMenuView(stage, musicManager);
        stage.setScene(gameMenuView.getScene());
    }

    private void startOnlineGame(Stage stage, MusicManager musicManager) {
        isOnlineGame = true;
        LobbyView lobbyView = new LobbyView(stage, musicManager);
        stage.setScene(lobbyView.getScene());
        stage.show();
    }

    public static App getINSTANCE() {
        return INSTANCE;
    }

    public void restart() {
        Stage primaryStage = new Stage();
        start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
