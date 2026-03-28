package org.App.view.screens;

import java.util.ArrayList;
import java.util.List;

import org.App.controller.GameController;
import org.App.model.player.AIPlayer;
import org.App.model.player.Difficulty;
import org.App.model.player.HumanPlayer;
import org.App.model.player.Player;
import org.App.view.utils.FloatingCardsFactory;
import org.App.view.utils.MusicManager;
import org.App.view.utils.OptionsManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Main menu view for the Skyjo game.
 * Player configuration is preserved when navigating to/from options.
 */
public class GameMenuView {
    private final Stage stage;
    private Scene menuScene;
    private final VBox playerInputs;
    private final List<TextField> nameFields;
    private final List<ComboBox<Difficulty>> difficultyBoxes;
    private final MusicManager musicManager;
    private int idplayer = 0;
    private boolean hasPressOnGenerateFieldsButton;

    public GameMenuView(Stage stage, MusicManager musicManager) {
        this.stage = stage;
        stage.setFullScreen(true);
        this.nameFields = new ArrayList<>();
        this.musicManager = musicManager;
        this.difficultyBoxes = new ArrayList<>();
        this.playerInputs = new VBox(12);
        this.playerInputs.setAlignment(Pos.CENTER);
        this.hasPressOnGenerateFieldsButton = false;

        setupMenu();
        applySavedOptions();
        musicManager.play();
    }

    /**
     * Applies saved theme and volume. Public so OptionsView can call it on return.
     */
    public void applySavedOptions() {
        String theme = OptionsManager.getTheme();
        double volume = OptionsManager.getVolume();

        if (menuScene != null) {
            menuScene.getStylesheets().clear();
            String cssPath = theme.equals("Sombre")
                    ? "/themes/menu.css"
                    : "/themes/menu_light.css";
            menuScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        }

        if (volume != musicManager.getVolume()) {
            musicManager.setVolume(volume);
        }
    }

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
            errorLabel.setTextFill(Color.web("#ef4444"));
            errorLabel.setStyle("-fx-font-size: 14px;");
            playerInputs.getChildren().add(errorLabel);
            return;
        }

        // Human player fields
        GridPane playerGrid = new GridPane();
        playerGrid.setHgap(20);
        playerGrid.setVgap(12);
        int columns = Math.min(numPlayers, numPlayers <= 3 ? numPlayers : (numPlayers <= 6 ? 3 : 4));

        for (int i = 1; i <= numPlayers; i++) {
            TextField nameField = new TextField("Joueur " + i);
            nameField.setPrefWidth(180);
            nameField.setMaxWidth(180);
            nameField.getStyleClass().add("text-field");
            nameFields.add(nameField);

            Label lab = new Label("Joueur " + i + " :");
            lab.setTextFill(Color.web("#cbd5e1"));
            lab.setStyle("-fx-font-size: 15px;");

            HBox playerBox = new HBox(8, lab, nameField);
            playerBox.setAlignment(Pos.CENTER);

            int row = (i - 1) / columns;
            int col = (i - 1) % columns;
            playerGrid.add(playerBox, col, row);
        }

        playerGrid.setAlignment(Pos.CENTER);
        playerInputs.getChildren().add(playerGrid);

        // AI fields
        if (numAI > 0) {
            GridPane aiGrid = new GridPane();
            aiGrid.setHgap(20);
            aiGrid.setVgap(12);
            int aiColumns = Math.min(numAI, numAI <= 3 ? numAI : (numAI <= 6 ? 3 : 4));

            for (int i = 1; i <= numAI; i++) {
                ComboBox<Difficulty> difficultyBox = new ComboBox<>();
                difficultyBox.getItems().addAll(Difficulty.values());
                difficultyBox.setValue(Difficulty.MEDIUM);
                difficultyBox.setPrefWidth(150);
                difficultyBox.setMaxWidth(150);
                difficultyBox.setPrefHeight(36);
                difficultyBox.getStyleClass().add("combo-box");
                difficultyBoxes.add(difficultyBox);

                Label lab = new Label("IA " + i + " :");
                lab.setTextFill(Color.web("#cbd5e1"));
                lab.setStyle("-fx-font-size: 15px;");

                HBox aiBox = new HBox(8, lab, difficultyBox);
                aiBox.setAlignment(Pos.CENTER);

                int row = (i - 1) / aiColumns;
                int col = (i - 1) % aiColumns;
                aiGrid.add(aiBox, col, row);
            }
            aiGrid.setAlignment(Pos.CENTER);
            playerInputs.getChildren().add(aiGrid);
        }
    }

    private void setupMenu() {
        // Background layer with floating card decorations
        Pane backgroundLayer = new Pane();
        backgroundLayer.setMouseTransparent(true);
        backgroundLayer.setPickOnBounds(false);
        FloatingCardsFactory.create(backgroundLayer, 12, 1200, 800);

        // Content layer
        VBox menuContainer = new VBox(22);
        menuContainer.setPadding(new Insets(40, 40, 20, 40));
        menuContainer.setAlignment(Pos.CENTER);

        Label title = new Label("SKYJO");
        title.getStyleClass().add("skyjo-title");

        // Configuration panel with glass effect
        VBox configPanel = new VBox(12);
        configPanel.getStyleClass().add("glass-panel");
        configPanel.setAlignment(Pos.CENTER);
        configPanel.setMaxWidth(480);

        Label configLabel = new Label("CONFIGURATION");
        configLabel.getStyleClass().add("section-label");

        Label playerCountLabel = new Label("Nombre de joueurs humains :");
        playerCountLabel.getStyleClass().add("number-of-players-label");

        TextField playerCountField = new TextField("2");
        playerCountField.setPrefWidth(60);
        playerCountField.setMaxWidth(200);
        playerCountField.getStyleClass().add("text-field");

        Label aiCountLabel = new Label("Nombre d'IA :");
        aiCountLabel.getStyleClass().add("number-of-players-label");

        TextField aiCountField = new TextField("1");
        aiCountField.setPrefWidth(60);
        aiCountField.setMaxWidth(200);
        aiCountField.getStyleClass().add("text-field");

        Button generateFieldsButton = createStyledButton("Generer", "button-secondary");
        generateFieldsButton.setOnAction(e -> {
            hasPressOnGenerateFieldsButton = true;
            generatePlayerFields(playerCountField, aiCountField);
        });

        configPanel.getChildren().addAll(configLabel, playerCountLabel, playerCountField,
                aiCountLabel, aiCountField, generateFieldsButton);

        // Action buttons
        Button startButton = createStyledButton("Jouer", "button-primary");
        startButton.setOnAction(e -> {
            if (hasPressOnGenerateFieldsButton) {
                startGame();
            }
        });

        Button optionsButton = createStyledButton("Options", "button-secondary");
        optionsButton.setOnAction(e -> openOptionsMenu());

        Button quitButton = createStyledButton("Quitter", "button-danger");
        quitButton.setOnAction(e -> stage.close());

        HBox actionButtons = new HBox(12, startButton, optionsButton, quitButton);
        actionButtons.setAlignment(Pos.CENTER);

        // Footer
        Label footer = new Label("Skyjo  \u00b7  Jeu de cartes  \u00b7  v1.0");
        footer.getStyleClass().add("footer-label");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        menuContainer.getChildren().addAll(title, configPanel, playerInputs, actionButtons, spacer, footer);

        // Layered root
        StackPane root = new StackPane(backgroundLayer, menuContainer);

        String cssPath = OptionsManager.getTheme().equals("Sombre")
                ? "/themes/menu.css"
                : "/themes/menu_light.css";
        this.menuScene = new Scene(root, 700, 500);
        menuScene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        stage.setScene(menuScene);
    }

    private void startGame() {
        musicManager.stop();
        List<Player> players = new ArrayList<>();
        for (TextField nameField : nameFields) {
            players.add(new HumanPlayer(idplayer++, nameField.getText()));
        }

        for (int i = 0; i < difficultyBoxes.size(); i++) {
            ComboBox<Difficulty> difficultyBox = difficultyBoxes.get(i);
            Difficulty difficulty = difficultyBox.getValue();
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
     * Opens options menu, passing 'this' so the player list is preserved on return.
     */
    private void openOptionsMenu() {
        OptionsView optionsView = new OptionsView(stage, musicManager, this);
        stage.setScene(optionsView.getScene());
        stage.setFullScreen(true);
        optionsView.show();
    }

    private Button createStyledButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().addAll("button", styleClass);
        button.setPrefSize(180, 40);
        return button;
    }

    public Scene getScene() {
        return menuScene;
    }

    public void show() {
        stage.show();
    }
}
