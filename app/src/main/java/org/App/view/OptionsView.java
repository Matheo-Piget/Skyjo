package org.App.view;

import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Represents the options view of the application.
 * This view allows the user to select and save preferences such as theme, game
 * mode, and volume.
 * 
 * @version 1.0
 * @author Piget Mathéo
 * @see Stage
 * @see Scene
 * @see OptionsManager
 */
public class OptionsView {

    private final Stage stage;
    private final Scene scene;
    private final MusicManager musicManager;

    /**
     * Constructs the options view.
     * 
     * @param stage        The primary stage of the application.
     * @param musicManager The music manager to control background music.
     */
    /**
     * Constructs the options view.
     * 
     * @param stage        The primary stage of the application.
     * @param musicManager The music manager to control background music.
     */
    public OptionsView(Stage stage, MusicManager musicManager) {
        this.stage = stage;
        this.musicManager = musicManager;

        // Create UI elements
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #34495e, #2c3e50);");

        // Title
        Label title = new Label();
        title.setText("Options");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(10, Color.BLACK));
        title.setId("options-title");

        // Theme selection
        GridPane themePanel = createPanel("");
        Label themeLabel = new Label("Select theme:");
        themeLabel.setTextFill(Color.WHITE);
        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Clair", "Sombre");
        themeComboBox.setValue(OptionsManager.getTheme());
        themePanel.add(themeLabel, 0, 0);
        themePanel.add(themeComboBox, 1, 0);

        // Game mode selection
        GridPane modePanel = createPanel("");
        ComboBox<String> modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll("Classique", "Action");
        modeComboBox.setValue(OptionsManager.getMode());
        modePanel.add(new Label("Select game mode:"), 0, 0);
        modePanel.add(modeComboBox, 1, 0);

        // Volume control
        GridPane volumePanel = createPanel("");
        Label volumeLabel = new Label("Adjust volume:");
        volumeLabel.setTextFill(Color.WHITE);
        Slider volumeSlider = new Slider(0, 1, musicManager.getVolume());
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicManager.setVolume(newVal.doubleValue());
        });
        volumeSlider.getStyleClass().add("slider");
        volumePanel.add(volumeLabel, 0, 0);
        volumePanel.add(volumeSlider, 1, 0);

        // Buttons
        Button saveButton = createStyledButton("Save");
        saveButton.setOnAction(e -> {
            saveOptions(themeComboBox.getValue(), modeComboBox.getValue());
            showConfirmation("Options saved successfully!");
        });

        Button resetButton = createStyledButton("Reset to Default");
        resetButton.setOnAction(e -> {
            themeComboBox.setValue("Clair");
            modeComboBox.setValue("Classique");
            volumeSlider.setValue(0.5);
            showConfirmation("Options reset to default!");
        });

        Button backButton = createStyledButton("Back to Menu");
        backButton.setOnAction(e -> goBackToMenu());

        // Add elements to root
        root.getChildren().addAll(title, themePanel, modePanel, volumePanel, saveButton, resetButton, backButton);

        // Create scene
        this.scene = new Scene(root, 800, 600);
        this.scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    /**
     * Creates a styled panel with a title.
     * 
     * @param title The title of the panel.
     * @return The styled GridPane.
     */
    private GridPane createPanel(String title) {
        GridPane panel = new GridPane();
        panel.setHgap(20);
        panel.setVgap(10);
        panel.setPadding(new Insets(20));
        panel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-border-radius: 10; -fx-background-radius: 10;");

        Label panelTitle = new Label(title);
        panelTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        panelTitle.setTextFill(Color.WHITE);
        panel.add(panelTitle, 0, 0, 2, 1);

        return panel;
    }

    /**
     * Creates a styled button with the specified text.
     * 
     * @param text The text to display on the button.
     * @return The styled button.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.setPrefSize(200, 40);
        return button;
    }

    /**
     * Displays a confirmation message.
     * 
     * @param message The message to display.
     */
    private void showConfirmation(String message) {
        Label confirmation = new Label(message);
        confirmation.setTextFill(Color.LIGHTGREEN);
        confirmation.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ((VBox) scene.getRoot()).getChildren().add(confirmation);

        // Remove the message after 3 seconds
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> ((VBox) scene.getRoot()).getChildren().remove(confirmation));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Returns the scene associated with the options view.
     * 
     * @return The options view scene.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Saves the selected theme and game mode.
     * 
     * @param theme The selected theme.
     * @param mode  The selected game mode.
     */
    private void saveOptions(String theme, String mode) {
        try {
            OptionsManager.saveOptions(theme, mode);
        } catch (IOException e) {
            System.err.println("Error saving options: " + e.getMessage());
        }
    }

    /**
     * Navigates back to the main menu.
     */
    private void goBackToMenu() {
        GameMenuView gameMenuView = new GameMenuView(stage, musicManager);
        stage.setScene(gameMenuView.getScene());
        stage.setFullScreen(true);
        gameMenuView.show();
    }

    /**
     * Shows the options view.
     */
    public void show() {
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
