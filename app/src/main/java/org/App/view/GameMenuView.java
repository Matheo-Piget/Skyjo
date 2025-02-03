package org.App.view;

import java.util.ArrayList;
import java.util.List;

import org.App.controller.GameController;
import org.App.model.HumanPlayer;
import org.App.model.Player;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GameMenuView {
    private final Stage stage;
    private final VBox playerInputs;
    private final List<TextField> nameFields;

    public GameMenuView(Stage stage) {
        this.stage = stage;
        this.nameFields = new ArrayList<>();
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
        styleTextField(playerCountField);

        Button generateFieldsButton = createStyledButton("Configurer joueurs");
        generateFieldsButton.setOnAction(e -> generatePlayerFields(playerCountField));

        Button startButton = createStyledButton("Démarrer la partie");
        startButton.setOnAction(e -> startGame());

        menuContainer.getChildren().addAll(title, playerCountLabel, playerCountField, generateFieldsButton, playerInputs, startButton);
        Scene scene = new Scene(menuContainer, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void generatePlayerFields(TextField playerCountField) {
        playerInputs.getChildren().clear();
        nameFields.clear();
        
        int numPlayers;
        try {
            numPlayers = Integer.parseInt(playerCountField.getText());
            if (numPlayers < 2 || numPlayers > 6) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Label errorLabel = new Label("Veuillez entrer un nombre entre 2 et 6.");
            errorLabel.setTextFill(Color.RED);
            playerInputs.getChildren().add(errorLabel);
            return;
        }
        
        for (int i = 1; i <= numPlayers; i++) {
            TextField nameField = new TextField("Joueur " + i);
            nameField.setPromptText("Nom du Joueur " + i);
            styleTextField(nameField);
            nameFields.add(nameField);
            playerInputs.getChildren().add(nameField);
        }
    }

    private void startGame() {
        List<Player> players = new ArrayList<>();
        for (TextField nameField : nameFields) {
            players.add(new HumanPlayer(nameField.getText()));
        }
    
        GameView gameView = new GameView(stage);
        GameController controller = new GameController(gameView, players);
        controller.startGame();
    
        stage.setScene(gameView.getScene());
        stage.setFullScreen(true);
    
        gameView.showPlaying(players, players.get(0).getName(), 50, null); 
    
        gameView.show();
        controller.startGame();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"));
        return button;
    }

    private void styleTextField(TextField textField) {
        textField.setStyle("-fx-font-size: 14px; -fx-padding: 5px; -fx-border-color: #3498db; -fx-border-radius: 5px;");
    }
}