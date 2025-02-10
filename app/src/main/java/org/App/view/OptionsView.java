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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Represents the options view of the application.
 * This view allows the user to select and save preferences such as theme and game mode.
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

    private MusicManager musicManager;

    /**
     * Constructs the options view.
     * 
     * @param stage The primary stage of the application.
     */
    public OptionsView(Stage stage) {
        this.stage = stage;
        this.musicManager = new MusicManager("C:\\Users\\mathe\\Documents\\Projet Perso\\Skyjo\\Skyjo\\app\\src\\main\\resources\\Kikou.mp3");

        // Create UI elements
        VBox optionsContainer = new VBox(20);
        optionsContainer.setPadding(new Insets(30));
        optionsContainer.setAlignment(Pos.CENTER);
        optionsContainer.setStyle("-fx-background-color: #34495e; -fx-padding: 40px; -fx-border-radius: 15px;");


        MusicManager.getINSTANCE().play();
        Slider volumeSlider = new Slider(0, 1, musicManager.getVolume());
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicManager.setVolume(newVal.doubleValue());
        });

        Label title = new Label("Options");
        title.setFont(new javafx.scene.text.Font("Arial", 32));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(5, Color.BLACK));

        // ComboBox for theme selection
        Label themeLabel = new Label("Select theme:");
        themeLabel.setTextFill(Color.WHITE);
        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Clair", "Sombre");

        // ComboBox for game mode selection
        Label modeLabel = new Label("Select game mode:");
        modeLabel.setTextFill(Color.WHITE);
        
        ComboBox<String> modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll("Classique", "Rapide");
        themeComboBox.setValue(OptionsManager.getTheme());
        modeComboBox.setValue(OptionsManager.getMode());

        // Button to save options
        Button saveButton = createStyledButton("Save");
        saveButton.setOnAction(e -> saveOptions(themeComboBox.getValue(), modeComboBox.getValue()));

        // Button to return to the main menu
        Button backButton = createStyledButton("Back");
        backButton.setOnAction(e -> goBackToMenu());

        optionsContainer.getChildren().addAll(title, volumeSlider, themeLabel, themeComboBox, modeLabel, modeComboBox, saveButton, backButton);

        this.scene = new Scene(optionsContainer, 700, 500);
        this.scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
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
     * Displays the options view on the stage.
     */
    public void show() {
        stage.show();
    }

    /**
     * Saves the selected theme and game mode.
     * 
     * @param theme The selected theme.
     * @param mode The selected game mode.
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
        GameMenuView gameMenuView = new GameMenuView(stage);
        stage.setScene(gameMenuView.getScene());
        stage.setFullScreen(true);
        gameMenuView.show();
    }

    /**
     * Creates a styled button with the specified text.
     * 
     * @param text The text to display on the button.
     * @return The styled button.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button"); // Ensure this class is defined in your CSS file
        button.setPrefSize(200, 40);
        return button;
    }
}