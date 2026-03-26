package org.App.view.screens;

import java.io.IOException;

import org.App.view.utils.MusicManager;
import org.App.view.utils.OptionsManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Options view. Navigating back restores the parent menu view
 * without losing player configuration (persistence fix).
 */
public class OptionsView {

    private final Stage stage;
    private final Scene scene;
    private final MusicManager musicManager;
    private final GameMenuView parentMenu;

    /**
     * @param parentMenu The parent GameMenuView to return to (preserves player list).
     */
    public OptionsView(Stage stage, MusicManager musicManager, GameMenuView parentMenu) {
        this.stage = stage;
        this.musicManager = musicManager;
        this.parentMenu = parentMenu;

        VBox root = new VBox(24);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);

        // Title
        Label title = new Label("Options");
        title.setId("options-title");

        // Theme
        GridPane themePanel = createPanel("Theme");
        Label themeLabel = new Label("Theme :");
        themeLabel.getStyleClass().add("label");
        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Clair", "Sombre");
        themeComboBox.setValue(OptionsManager.getTheme());
        themeComboBox.getStyleClass().add("combo-box");
        themePanel.add(themeLabel, 0, 1);
        themePanel.add(themeComboBox, 1, 1);

        // Game mode
        GridPane modePanel = createPanel("Mode de jeu");
        Label modeLabel = new Label("Mode :");
        modeLabel.getStyleClass().add("label");
        ComboBox<String> modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll("Classique", "Action");
        modeComboBox.setValue(OptionsManager.getMode());
        modeComboBox.getStyleClass().add("combo-box");
        modePanel.add(modeLabel, 0, 1);
        modePanel.add(modeComboBox, 1, 1);

        // Volume
        GridPane volumePanel = createPanel("Audio");
        Label volumeLabel = new Label("Volume :");
        volumeLabel.getStyleClass().add("label");
        Slider volumeSlider = new Slider(0, 1, musicManager.getVolume());
        volumeSlider.getStyleClass().add("slider");
        volumeSlider.setPrefWidth(250);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                musicManager.setVolume(newVal.doubleValue()));
        volumePanel.add(volumeLabel, 0, 1);
        volumePanel.add(volumeSlider, 1, 1);

        // Animation speed
        GridPane speedPanel = createPanel("Animations");
        Label speedLabel = new Label("Vitesse :");
        speedLabel.getStyleClass().add("label");
        ComboBox<String> speedComboBox = new ComboBox<>();
        speedComboBox.getItems().addAll("Lent", "Normal", "Rapide");
        speedComboBox.setValue(OptionsManager.getAnimationSpeedLabel());
        speedComboBox.getStyleClass().add("combo-box");
        speedPanel.add(speedLabel, 0, 1);
        speedPanel.add(speedComboBox, 1, 1);

        // Buttons
        Button saveButton = createStyledButton("Sauvegarder");
        saveButton.setOnAction(e -> {
            saveOptions(themeComboBox.getValue(), modeComboBox.getValue(),
                    volumeSlider.getValue(), speedComboBox.getValue());
            showConfirmation("Options sauvegardees !");
        });

        Button resetButton = createStyledButton("Reinitialiser");
        resetButton.setOnAction(e -> {
            themeComboBox.setValue("Clair");
            modeComboBox.setValue("Classique");
            volumeSlider.setValue(0.5);
            speedComboBox.setValue("Normal");
            showConfirmation("Options reinitialisees !");
        });

        Button backButton = createStyledButton("Retour au menu");
        backButton.setOnAction(e -> goBackToMenu());

        root.getChildren().addAll(title, themePanel, modePanel, volumePanel, speedPanel,
                saveButton, resetButton, backButton);

        this.scene = new Scene(root, 800, 600);
        String cssPath = OptionsManager.getTheme().equals("Sombre")
                ? "/themes/option.css"
                : "/themes/option_light.css";
        scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
    }

    private GridPane createPanel(String title) {
        GridPane panel = new GridPane();
        panel.setHgap(16);
        panel.setVgap(10);
        panel.setPadding(new Insets(16));
        panel.getStyleClass().add("panel");
        Label panelTitle = new Label(title);
        panelTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        panelTitle.getStyleClass().add("label");
        panel.add(panelTitle, 0, 0, 2, 1);
        return panel;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.setPrefSize(200, 40);
        return button;
    }

    private void showConfirmation(String message) {
        Label confirmation = new Label(message);
        confirmation.setTextFill(Color.web("#22c55e"));
        confirmation.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        ((VBox) scene.getRoot()).getChildren().add(confirmation);

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                javafx.application.Platform.runLater(() ->
                        ((VBox) scene.getRoot()).getChildren().remove(confirmation));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public Scene getScene() {
        return scene;
    }

    private void saveOptions(String theme, String mode, double volume, String animationSpeed) {
        try {
            OptionsManager.saveOptions(theme, mode, volume, animationSpeed);
        } catch (IOException e) {
            System.err.println("Error saving options: " + e.getMessage());
        }
    }

    /**
     * Returns to the parent menu view without recreating it (preserves player list).
     */
    private void goBackToMenu() {
        parentMenu.applySavedOptions();
        stage.setScene(parentMenu.getScene());
        stage.setFullScreen(true);
    }

    public void show() {
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
}
