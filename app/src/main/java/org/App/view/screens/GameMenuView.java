package org.App.view.screens;

import java.util.ArrayList;
import java.util.List;

import org.App.controller.GameController;
import org.App.model.player.AIPlayer;
import org.App.model.player.HumanPlayer;
import org.App.model.player.Player;
import org.App.view.utils.MusicManager;
import org.App.view.utils.OptionsManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Represents the main menu view for the Skyjo game.
 * This class is responsible for displaying the game menu, allowing players to
 * configure the game,
 * and starting the game with the specified settings.
 * 
 * <p>
 * The menu includes options to set the number of players, configure AI players,
 * and access game options.
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
    private final Stage stage;
    private final VBox playerInputs;
    private final List<TextField> nameFields;
    private final List<ComboBox<Player.Difficulty>> difficultyBoxes;
    private final MusicManager musicManager;
    private int idplayer = 0;

    private boolean hasPressOnGenerateFieldsButton;

    /**
     * Constructs a new GameMenuView with the specified stage.
     *
     * @param stage The primary stage for the application.
     */
    public GameMenuView(Stage stage, MusicManager musicManager) {
        this.stage = stage;
        stage.setFullScreen(true);
        this.nameFields = new ArrayList<>();
        this.musicManager = musicManager;
        this.difficultyBoxes = new ArrayList<>();
        this.playerInputs = new VBox(10);
        this.playerInputs.setAlignment(Pos.CENTER);
        this.hasPressOnGenerateFieldsButton = false;

        setupMenu();

        applySavedOptions();

        musicManager.play();

    }

    /**
     * Applies the saved options (theme, mode and volume) to the game menu.
     */
    private void applySavedOptions() {
        String theme = OptionsManager.getTheme();
        String mode = OptionsManager.getMode();
        double volume = OptionsManager.getVolume();
        System.out.println("Applying saved options: theme=" + theme + ", mode=" + mode + ", volume=" + volume);

        // Appliquer le thème
        if (theme.equals("Sombre")) {
            if (!stage.getScene().getStylesheets().isEmpty()) {
                stage.getScene().getStylesheets().clear();
            }
            stage.getScene().getStylesheets().add(getClass().getResource("/themes/menu.css").toExternalForm());
        } else {
            if (!stage.getScene().getStylesheets().isEmpty()) {
                stage.getScene().getStylesheets().clear();
            }
            stage.getScene().getStylesheets().add(getClass().getResource("/themes/menu_light.css").toExternalForm());
        }

        if (volume != musicManager.getVolume()) {
            musicManager.setVolume(volume);
        }
    }

    /**
     * Generates input fields for the specified number of players and AI.
     *
     * @param playerCountField The text field containing the number of players.
     * @param aiCountField     The text field containing the number of AI players.
     */
    private void generatePlayerFields(TextField playerCountField, TextField aiCountField) {
        playerInputs.getChildren().clear();
        nameFields.clear();
        difficultyBoxes.clear();

        int numPlayers;
        int numAI;
        try {
            numPlayers = Integer.parseInt(playerCountField.getText());
            numAI = Integer.parseInt(aiCountField.getText());
            if (numPlayers < 1 || numPlayers + numAI > 8 || numAI < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Label errorLabel = new Label("Veuillez entrer un nombre valide de joueurs et d'IA (total max 8).");
            errorLabel.setTextFill(Color.RED);
            playerInputs.getChildren().add(errorLabel);
            return;
        }

        // Ajouter les champs pour les joueurs humains
        GridPane playerGrid = new GridPane();
        playerGrid.setHgap(20);
        playerGrid.setVgap(10);
        int columns = switch (numPlayers) {
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 2;
            case 5 -> 3;
            case 6 -> 3;
            case 7 -> 4;
            default -> 4;
        };

        for (int i = 1; i <= numPlayers; i++) {
            TextField nameField = new TextField("Joueur " + i);
            nameField.setPrefWidth(200);
            nameField.setMaxWidth(200);
            styleTextField(nameField);
            nameFields.add(nameField);

            Label lab = new Label("Joueur " + i + " :");
            lab.setTextFill(Color.WHITE);
            lab.setStyle("-fx-font-size: 16px;");
            lab.setEffect(new DropShadow(5, Color.BLACK));

            HBox playerBox = new HBox(10, lab, nameField);
            playerBox.setAlignment(Pos.CENTER);

            int row = (i - 1) / columns;
            int col = (i - 1) % columns;
            playerGrid.add(playerBox, col, row);
        }

        playerGrid.setAlignment(Pos.CENTER);
        playerInputs.getChildren().add(playerGrid);

        

        // Ajouter les champs pour les IA
        if (numAI > 0) {
            GridPane aiGrid = new GridPane();
            aiGrid.setHgap(20);
            aiGrid.setVgap(10);

            columns = switch (numAI) {
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 3;
                case 4 -> 2;
                case 5 -> 3;
                case 6 -> 3;
                case 7 -> 4;
                default -> 4;
            };

            for (int i = 1; i <= numAI; i++) {
                ComboBox<Player.Difficulty> difficultyBox = new ComboBox<>();
                difficultyBox.getItems().addAll(Player.Difficulty.values());
                difficultyBox.setValue(Player.Difficulty.MEDIUM);
                difficultyBox.setPrefWidth(150);
                difficultyBox.setMaxWidth(150);
                difficultyBox.setPrefHeight(40);
                difficultyBox.setMaxHeight(40);
                styleComboBox(difficultyBox);
                difficultyBoxes.add(difficultyBox);

                Label lab = new Label("IA " + i + " :");
                lab.setTextFill(Color.WHITE);
                lab.setStyle("-fx-font-size: 16px;");
                lab.setEffect(new DropShadow(5, Color.BLACK));
                

                HBox aiBox = new HBox(10, lab, difficultyBox);
                aiBox.setAlignment(Pos.CENTER);

                int row = (i - 1) / columns;
                int col = (i - 1) % columns;
                aiGrid.add(aiBox, col, row);
            }
            aiGrid.setAlignment(Pos.CENTER);
            playerInputs.getChildren().add(aiGrid);
        }
    }

    /**
     * Sets up the main menu with all its components.
     */
    private void setupMenu() {
        VBox menuContainer = new VBox(20);
        menuContainer.setPadding(new Insets(30));
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setStyle("-fx-background-color: #34495e; -fx-padding: 40px; -fx-border-radius: 15px;");

        Label title = new Label("Bienvenu dans Skyjo !");
        title.getStyleClass().add("skyjo-title");
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(5, Color.BLACK));

        Label playerCountLabel = new Label("Nombre de joueurs humains :");
        playerCountLabel.setTextFill(Color.WHITE);
        playerCountLabel.getStyleClass().add("number-of-players-label");

        TextField playerCountField = new TextField("2");
        playerCountField.setPrefWidth(50);
        playerCountField.setMaxWidth(200);

        Label aiCountLabel = new Label("Nombre d'IA :");
        aiCountLabel.setTextFill(Color.WHITE);
        aiCountLabel.getStyleClass().add("number-of-players-label");

        TextField aiCountField = new TextField("1");
        aiCountField.setPrefWidth(50);
        aiCountField.setMaxWidth(200);

        Button generateFieldsButton = createStyledButton("Générer", "button-secondary");
        generateFieldsButton.setOnAction(e -> {
            hasPressOnGenerateFieldsButton = true;
            generatePlayerFields(playerCountField, aiCountField);
        });

        Button startButton = createStyledButton("Start", "button-primary");
        startButton.setOnAction(e -> {
            if (hasPressOnGenerateFieldsButton) {
                startGame();
            }
        });

        Button optionsButton = createStyledButton("Options", "button-secondary");
        optionsButton.setOnAction(e -> openOptionsMenu());

        Button quitButton = createStyledButton("Quitter", "button-danger");
        quitButton.setOnAction(e -> stage.close());

        // Agencement du menu
        HBox buttonBox = new HBox(15, generateFieldsButton, startButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox optionsBox = new VBox(15, optionsButton, quitButton);
        optionsBox.setAlignment(Pos.CENTER);
        optionsBox.setPadding(new Insets(20));

        menuContainer.getChildren().addAll(title, playerCountLabel, playerCountField, aiCountLabel, aiCountField,
                buttonBox, playerInputs, optionsBox);

        // Scene
        if (OptionsManager.getTheme().equals("Sombre")) {
            Scene scene = new Scene(menuContainer, 700, 500);
            scene.getStylesheets().add(getClass().getResource("/themes/menu.css").toExternalForm());
            stage.setScene(scene);
        } else {
            Scene scene = new Scene(menuContainer, 700, 500);
            scene.getStylesheets().add(getClass().getResource("/themes/menu_light.css").toExternalForm());
            stage.setScene(scene);
        }
    }

    /**
     * Starts the game with the configured players.
     */
    private void startGame() {

        musicManager.stop();
        List<Player> players = new ArrayList<>();
        for (TextField nameField : nameFields) {
            players.add(new HumanPlayer(idplayer++, nameField.getText()));
        }

        for (int i = 0; i < difficultyBoxes.size(); i++) {
            ComboBox<Player.Difficulty> difficultyBox = difficultyBoxes.get(i);
            difficultyBox.setMaxSize(150, 40);
            difficultyBox.setMinSize(150, 40);
            Player.Difficulty difficulty = difficultyBox.getValue();
            players.add(new AIPlayer(idplayer++, "IA " + (i + 1), difficulty));
        }

        GameViewInterface gameView = new GameView(stage);
        GameController controller = new GameController(gameView, players);

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

        stage.setOnCloseRequest(event -> applySavedOptions());
    }

    /**
     * Creates a styled button with the specified text.
     *
     * @param text The text to display on the button.
     * @return The styled button.
     */
    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("button", styleClass);
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
        textField.getStyleClass().add("text-field");
    }

    /**
     * Applies a style to the specified combo box.
     *
     * @param comboBox The combo box to style.
     */
    private void styleComboBox(ComboBox<Player.Difficulty> comboBox) {
        comboBox.getStyleClass().add("combo-box");
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