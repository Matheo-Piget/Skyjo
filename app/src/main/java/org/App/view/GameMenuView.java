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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GameMenuView {
    private final Stage stage;
    private final VBox playerInputs;
    private final List<TextField> nameFields;
    private final List<ComboBox<Player.Difficulty>> difficultyBoxes;

    public GameMenuView(Stage stage) {
        this.stage = stage;
        this.nameFields = new ArrayList<>();
        this.difficultyBoxes = new ArrayList<>();
        this.playerInputs = new VBox(10);
        this.playerInputs.setAlignment(Pos.CENTER);

        stage.setFullScreen(true);

        setupMenu();
    }

    private void setupMenu() {
        VBox menuContainer = new VBox(15);
        menuContainer.setPadding(new Insets(20));
        menuContainer.setAlignment(Pos.CENTER);
        menuContainer.setStyle("-fx-background-color: #2c3e50; -fx-padding: 30px; -fx-border-radius: 10px;");

        Label title = new Label("Configuration de la Partie");
        title.setFont(new Font("Arial", 28));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(3, Color.BLACK));

        Label playerCountLabel = new Label("Nombre de joueurs :");
        playerCountLabel.setTextFill(Color.WHITE);

        TextField playerCountField = new TextField("2");
        playerCountField.setPrefWidth(50);
        playerCountField.setMaxWidth(250);
        styleTextField(playerCountField);

        Button generateFieldsButton = createStyledButton("Configurer joueurs");
        generateFieldsButton.setOnAction(e -> generatePlayerFields(playerCountField));

        Button startButton = createStyledButton("Démarrer la partie");
        startButton.setOnAction(e -> startGame());

        menuContainer.getChildren().addAll(title, playerCountLabel, playerCountField, generateFieldsButton,
                playerInputs, startButton);
        Scene scene = new Scene(menuContainer, 600, 400);
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
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Nombre d'IA");
            dialog.setHeaderText("Contre combien d'IA voulez-vous jouer ?");
            dialog.setContentText("Nombre d'IA (1-7):");

            dialog.showAndWait().ifPresent(response -> {
                try {
                    int numAI = Integer.parseInt(response);
                    if (numAI < 1 || numAI > 7) {
                        throw new NumberFormatException();
                    }

                    // Ajouter les champs pour les IA
                    for (int i = 1; i <= numAI; i++) {
                        HBox aiBox = new HBox(10);
                        aiBox.setAlignment(Pos.CENTER_LEFT);

                        Label aiLabel = new Label("IA " + i + " - Difficulté :");
                        aiLabel.setTextFill(Color.WHITE);

                        ComboBox<Player.Difficulty> difficultyBox = new ComboBox<>();
                        difficultyBox.getItems().addAll(Player.Difficulty.values());
                        difficultyBox.setValue(Player.Difficulty.MEDIUM); // Valeur par défaut
                        styleComboBox(difficultyBox);

                        difficultyBoxes.add(difficultyBox);

                        aiBox.getChildren().addAll(aiLabel, difficultyBox);
                        playerInputs.getChildren().add(aiBox);
                    }
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Nombre d'IA invalide");
                    alert.setContentText("Veuillez entrer un nombre entre 1 et 7.");
                    alert.showAndWait();
                }
            });
        }
    }

    private void startGame() {
        List<Player> players = new ArrayList<>();
        for (TextField nameField : nameFields) {
            players.add(new HumanPlayer(nameField.getText()));
        }

        // Ajouter les IA avec leur difficulté
        for (int i = 0; i < difficultyBoxes.size(); i++) {
            ComboBox<Player.Difficulty> difficultyBox = difficultyBoxes.get(i);
            Player.Difficulty difficulty = difficultyBox.getValue();
            players.add(new AIPlayer("IA " + (i + 1), difficulty));
        }

        // Démarrer le jeu avec les joueurs configurés
        GameView gameView = new GameView(stage);
        GameController controller = new GameController(gameView, players);

        stage.setScene(gameView.getScene());
        stage.setFullScreen(true);

        gameView.show();
        controller.startGame();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white;  -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"));
        button.setPrefSize(200, 40);
        return button;
    }

    private void styleTextField(TextField textField) {
        textField.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-border-color: #3498db; -fx-border-radius: 5px;");
    }

    private void styleComboBox(ComboBox<Player.Difficulty> comboBox) {
        comboBox.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-border-color: #3498db; -fx-border-radius: 5px;");
    }
}