package org.App.view;

import java.util.ArrayList;
import java.util.List;

import org.App.controller.GameController;
import org.App.model.AIPlayer;
import org.App.model.HumanPlayer;
import org.App.model.Player;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Represents the main menu view for the Skyjo game.
 * This class is responsible for displaying the game menu, allowing players to configure the game,
 * and starting the game with the specified settings.
 * 
 * <p>
 * The menu includes options to set the number of players, configure AI players, and access game options.
 * </p>
 * 
 * @see GameController
 * @see GameView
 * @see AIPlayer
 * @see HumanPlayer
 * @see Player
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public class GameMenuView {
    private final TextField aiCountField;
    private final Stage stage;
    private final VBox playerInputs;
    private final List<TextField> nameFields;
    private final List<ComboBox<Player.Difficulty>> difficultyBoxes;
    private final MusicManager musicManager; // Ajout du MusicManager

    private boolean hasPressOnGenerateFieldsButton;

    /**
     * Constructs a new GameMenuView with the specified stage.
     *
     * @param stage The primary stage for the application.
     */
    public GameMenuView(Stage stage, MusicManager musicManager) {
        this.stage = stage;
        this.nameFields = new ArrayList<>();
        this.musicManager = musicManager; 
        this.difficultyBoxes = new ArrayList<>();
        this.playerInputs = new VBox(10);
        this.playerInputs.setAlignment(Pos.CENTER);
        this.aiCountField = new TextField("1");
        this.hasPressOnGenerateFieldsButton = false;

        stage.setFullScreen(true);

        setupMenu();

        // Appliquer les options enregistrées
        applySavedOptions();

        // Jouer la musique
        musicManager.play();

    }

    /**
     * Applies the saved options (theme and mode) to the game menu.
     */
    private void applySavedOptions() {
        String theme = OptionsManager.getTheme();
        String mode = OptionsManager.getMode();

        // Appliquer le thème
        if (theme.equals("Sombre")) {
            if (!stage.getScene().getStylesheets().isEmpty()) {
                stage.getScene().getStylesheets().clear();
            }
            stage.getScene().getStylesheets().add(getClass().getResource("/menu.css").toExternalForm());
        } else {
            if (!stage.getScene().getStylesheets().isEmpty()) {
                stage.getScene().getStylesheets().clear();
            }
            stage.getScene().getStylesheets().add(getClass().getResource("/lighttheme.css").toExternalForm());
        }

        // Appliquer le mode de jeu si nécessaire
        if (mode.equals("Action")) {
            // Démarrer le skyjo mais en action
        }
    }

    /**
     * Sets up the main menu with all its components.
     */
    private void setupMenu() {
        // Menu principal avec un style moderne
        VBox menuContainer = new VBox(20);
        menuContainer.setPadding(new Insets(30));
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setStyle("-fx-background-color: #34495e; -fx-padding: 40px; -fx-border-radius: 15px;");

        Label title = new Label("Bienvenu dans Skyjo !");
        title.getStyleClass().add("skyjo-title"); // Assurez-vous que cette classe est bien dans votre fichier CSS
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(5, Color.BLACK));

        Label playerCountLabel = new Label("Nombre de joueurs :");
        playerCountLabel.setTextFill(Color.WHITE);
        playerCountLabel.getStyleClass().add("number-of-players-label"); // Assurez-vous que cette classe est bien dans votre fichier CSS

        TextField playerCountField = new TextField("2");
        playerCountField.setPrefWidth(50);
        playerCountField.setMaxWidth(200);

        Button generateFieldsButton = createStyledButton("Configurer joueurs");
        generateFieldsButton.setOnAction(e -> {
            hasPressOnGenerateFieldsButton = true;
            generatePlayerFields(playerCountField);
        });

        Button startButton = createStyledButton("Démarrer la partie");
        startButton.setOnAction(e -> {
            if (hasPressOnGenerateFieldsButton) {
                startGame();
            }
        });

        Button optionsButton = createStyledButton("Options");
        optionsButton.setOnAction(e -> openOptionsMenu());

        Button quitButton = createStyledButton("Quitter");
        quitButton.setOnAction(e -> stage.close());

        // Agencement du menu
        HBox buttonBox = new HBox(15, generateFieldsButton, startButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox optionsBox = new VBox(15, optionsButton, quitButton);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPadding(new Insets(20));

        menuContainer.getChildren().addAll(title, playerCountLabel, playerCountField, buttonBox, playerInputs,
                optionsBox);

        // Scene
        Scene scene = new Scene(menuContainer, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Generates input fields for the specified number of players.
     *
     * @param playerCountField The text field containing the number of players.
     */
    private void generatePlayerFields(TextField playerCountField) {
        playerInputs.getChildren().clear();
        nameFields.clear();
        difficultyBoxes.clear();

        int numPlayers;
        try {
            numPlayers = Integer.parseInt(playerCountField.getText());
            if (numPlayers < 1 || numPlayers > 8) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Label errorLabel = new Label("Veuillez entrer un nombre entre 1 et 8.");
            errorLabel.setTextFill(Color.RED);
            playerInputs.getChildren().add(errorLabel);
            return;
        }

        // Ajouter les champs pour les joueurs humains
        for (int i = 1; i <= numPlayers; i++) {
            TextField nameField = new TextField("Joueur " + i);
            nameField.setPromptText("Nom du Joueur " + i);
            styleTextField(nameField);
            nameField.setPrefWidth(200);
            nameField.setMaxWidth(200);
            nameFields.add(nameField);
            playerInputs.getChildren().add(nameField);
        }

        // Si un seul joueur humain, ajouter les champs pour les IA
        if (numPlayers == 1) {
            Label aiLabel = new Label("Nombre d'IA :");
            aiLabel.setTextFill(Color.WHITE);

            aiCountField.setPrefWidth(35);
            aiCountField.setMaxWidth(240);
            styleTextField(aiCountField);

            Button confirmAIButton = createStyledButton("Ajouter IA");
            confirmAIButton.setOnAction(e -> {
                clearAIFields();
                generateAIFields();
            });

            HBox aiSelectionBox = new HBox(10, aiLabel, aiCountField, confirmAIButton);
            aiSelectionBox.setAlignment(Pos.CENTER);
            playerInputs.getChildren().add(aiSelectionBox);
        }
    }

    /**
     * Clears the AI input fields.
     */
    private void clearAIFields() {
        playerInputs.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().startsWith("IA"));
        playerInputs.getChildren().removeAll(difficultyBoxes);
        difficultyBoxes.clear();
    }

    /**
     * Generates input fields for AI players.
     */
    private void generateAIFields() {
        playerInputs.getChildren().remove(aiCountField);

        int numAI;
        try {
            numAI = Integer.parseInt(aiCountField.getText());
            if (numAI < 1 || numAI > 7) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Label errorLabel = new Label("Veuillez entrer un nombre entre 1 et 7.");
            errorLabel.setTextFill(Color.RED);
            playerInputs.getChildren().add(errorLabel);
            return;
        }

        for (int i = 1; i <= numAI; i++) {
            ComboBox<Player.Difficulty> difficultyBox = new ComboBox<>();
            difficultyBox.getItems().addAll(Player.Difficulty.values());
            difficultyBox.setValue(Player.Difficulty.MEDIUM);
            difficultyBox.setPrefWidth(150);
            difficultyBox.setMaxWidth(150);
            difficultyBox.setMaxHeight(40);
            difficultyBox.setPrefHeight(40);
            styleComboBox(difficultyBox);
            difficultyBoxes.add(difficultyBox);

            Label lab = new Label("IA " + i + " :");
            lab.setTextFill(Color.WHITE);

            HBox aiBox = new HBox(10, lab, difficultyBox);
            aiBox.setAlignment(Pos.CENTER);
            playerInputs.getChildren().add(aiBox);
        }
    }

    /**
     * Starts the game with the configured players.
     */
    private void startGame() {

        musicManager.stop();
        List<Player> players = new ArrayList<>();
        for (TextField nameField : nameFields) {
            players.add(new HumanPlayer(nameField.getText()));
        }

        for (int i = 0; i < difficultyBoxes.size(); i++) {
            ComboBox<Player.Difficulty> difficultyBox = difficultyBoxes.get(i);
            Player.Difficulty difficulty = difficultyBox.getValue();
            players.add(new AIPlayer("IA " + (i + 1), difficulty));
        }

        GameView gameView = new GameView(stage);
        GameController controller = new GameController(gameView, players);

        // Appliquer le mode enregistré
        String mode = OptionsManager.getMode();
        if (mode.equals("Rapide")) {
            //controller.enableFastMode(); // Ajoute une méthode pour gérer ce mode dans GameController
        }

        stage.setScene(gameView.getScene());
        stage.setFullScreen(true);

        gameView.show();
        controller.startGame();
    }

    /**
     * Opens the options menu.
     */
    private void openOptionsMenu() {
        OptionsView optionsView = new OptionsView(stage, musicManager);
        stage.setScene(optionsView.getScene());
        stage.setFullScreen(true);

        optionsView.show();

        // Appliquer les nouvelles options après la fermeture du menu options
        stage.setOnCloseRequest(event -> applySavedOptions());
    }

    /**
     * Creates a styled button with the specified text.
     *
     * @param text The text to display on the button.
     * @return The styled button.
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button"); // Assurez-vous que cette classe est bien dans votre fichier CSS
        button.setPrefSize(200, 40);
        return button;
    }

    /**
     * Applies a style to the specified text field.
     *
     * @param textField The text field to style.
     */
    private void styleTextField(TextField textField) {
        textField.setPrefWidth(200);
        textField.getStyleClass().add("text-field"); // Assurez-vous que cette classe est bien dans votre fichier CSS
    }

    /**
     * Applies a style to the specified combo box.
     *
     * @param comboBox The combo box to style.
     */
    private void styleComboBox(ComboBox<Player.Difficulty> comboBox) {
        comboBox.getStyleClass().add("combo-box"); // Assurez-vous que cette classe est bien dans votre fichier CSS
    }

    /**
     * Gets the scene associated with this view.
     *
     * @return The scene.
     */
    public Scene getScene() {
        return stage.getScene();
    }

    /**
     * Displays the game menu.
     */
    public void show() {
        stage.show();
    }
}