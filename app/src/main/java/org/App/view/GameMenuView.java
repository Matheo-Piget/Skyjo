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

public class GameMenuView {
    private final TextField aiCountField;
    private final Stage stage;
    private final VBox playerInputs;
    private final List<TextField> nameFields;
    private final List<ComboBox<Player.Difficulty>> difficultyBoxes;

    private boolean hasPressOnGenerateFieldsButton;

    public GameMenuView(Stage stage) {
        this.stage = stage;
        this.nameFields = new ArrayList<>();
        this.difficultyBoxes = new ArrayList<>();
        this.playerInputs = new VBox(10);
        this.playerInputs.setAlignment(Pos.CENTER);
        this.aiCountField = new TextField("1");
        this.hasPressOnGenerateFieldsButton = false;

        stage.setFullScreen(true);

        setupMenu();
    }

    private void setupMenu() {

        // Menu principal avec un style moderne
        VBox menuContainer = new VBox(20);
        menuContainer.setPadding(new Insets(30));
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setStyle("-fx-background-color: #34495e; -fx-padding: 40px; -fx-border-radius: 15px;");

        Label title = new Label("Bienvenu dans : Skyjo !");
        title.getStyleClass().add("skyjo-title"); // Assurez-vous que cette classe est bien dans votre fichier CSS
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(5, Color.BLACK));

        Label playerCountLabel = new Label("Nombre de joueurs :");
        playerCountLabel.setTextFill(Color.WHITE);

        TextField playerCountField = new TextField("2");
        playerCountField.setPrefWidth(50);

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
            nameFields.add(nameField);
            playerInputs.getChildren().add(nameField);
        }

        // Si un seul joueur humain, ajouter les champs pour les IA
        if (numPlayers == 1) {
            Label aiLabel = new Label("Nombre d'IA :");
            aiLabel.setTextFill(Color.WHITE);

            aiCountField.setPrefWidth(50);
            aiCountField.setMaxWidth(250);
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

    private void clearAIFields() {
        playerInputs.getChildren().removeIf(node -> node instanceof Label && ((Label) node).getText().startsWith("IA"));
        playerInputs.getChildren().removeAll(difficultyBoxes);
        difficultyBoxes.clear();
    }

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
            styleComboBox(difficultyBox);
            difficultyBoxes.add(difficultyBox);

            HBox aiBox = new HBox(10, new Label("IA " + i + " :"), difficultyBox);
            aiBox.setAlignment(Pos.CENTER);
            playerInputs.getChildren().add(aiBox);
        }
    }

    private void startGame() {
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

        stage.setScene(gameView.getScene());
        stage.setFullScreen(true);

        gameView.show();
        controller.startGame();
    }

    private void openOptionsMenu() {
        OptionsView optionsView = new OptionsView(stage);
        stage.setScene(optionsView.getScene());
        stage.setFullScreen(true);
        optionsView.show(); 
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button"); // Assurez-vous que cette classe est bien dans votre fichier CSS
        button.setPrefSize(200, 40);
        button.setStyle("-fx-background-radius: 20px; -fx-border-color: #ecf0f1;");
        return button;
    }

    private void styleTextField(TextField textField) {
        textField.getStyleClass().add("text-field"); // Assurez-vous que cette classe est bien dans votre fichier CSS
    }

    private void styleComboBox(ComboBox<Player.Difficulty> comboBox) {
        comboBox.getStyleClass().add("combo-box"); // Assurez-vous que cette classe est bien dans votre fichier CSS
    }

    public Scene getScene() {
        return stage.getScene();
    }

    public void show() {
        stage.show();
    }
}
