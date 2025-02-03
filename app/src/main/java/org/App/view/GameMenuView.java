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
import javafx.scene.layout.VBox;
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

        setupMenu();
    }

    private void setupMenu() {
        VBox menuContainer = new VBox(15);
        menuContainer.setPadding(new Insets(20));
        menuContainer.setAlignment(Pos.CENTER);

        Label title = new Label("Configuration de la Partie");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label playerCountLabel = new Label("Nombre de joueurs :");
        TextField playerCountField = new TextField("2");

        Button generateFieldsButton = new Button("Configurer joueurs");
        generateFieldsButton.setOnAction(e -> generatePlayerFields(playerCountField));

        Button startButton = new Button("Démarrer la partie");
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
            playerInputs.getChildren().add(new Label("Veuillez entrer un nombre entre 2 et 6."));
            return;
        }
        
        for (int i = 1; i <= numPlayers; i++) {
            TextField nameField = new TextField("Joueur " + i);
            nameField.setPromptText("Nom du Joueur " + i);
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
    
        // Mise à jour de la scène de la fenêtre
        stage.setScene(gameView.getScene());
    
        // Affichage de la partie (ajout important)
        gameView.showPlaying(players, players.get(0).getName(), 50, null); // Mettre une vraie carte ici
    
        gameView.show();
    }
    
    
}
