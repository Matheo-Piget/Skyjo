package org.App.view.screens;

import org.App.controller.OnlineGameController;
import org.App.network.GameClient;
import org.App.network.GameServer;
import org.App.network.GameState;
import org.App.network.NetworkManager;
import org.App.network.NetworkPlayerState;
import org.App.network.Protocol;
import org.App.view.utils.MusicManager;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LobbyView {
    private final Stage stage;
    private final VBox container;
    private final ListView<String> playersList;
    private final TextField serverAddressField;
    private final MusicManager musicManager;

    // Dans LobbyView.java
    public LobbyView(Stage stage, MusicManager musicManager) {
        this.stage = stage;
        this.musicManager = musicManager;

        // En-tête
        Text title = new Text("Skyjo Online");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        // Conteneur principal avec style
        container = new VBox(20);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: #1E1E1E; -fx-padding: 30px;");

        // Champs pour nom du joueur et adresse du serveur
        TextField playerNameField = new TextField("Joueur");
        playerNameField.setPromptText("Votre nom");

        serverAddressField = new TextField("localhost:5555");
        serverAddressField.setPromptText("IP:port");

        // Boutons avec style CSS
        Button connectButton = new Button("Se connecter");
        connectButton.getStyleClass().add("button-primary");
        connectButton.setOnAction(e -> connectToServer(playerNameField.getText()));

        Button hostButton = new Button("Héberger");
        hostButton.getStyleClass().add("button-secondary");
        hostButton.setOnAction(e -> {
            hostGame();
            connectToServer(playerNameField.getText());
        });

        Button backButton = new Button("Retour");
        backButton.getStyleClass().add("button-danger");
        backButton.setOnAction(e -> {
            musicManager.stop();
            stage.setScene(new GameMenuView(stage, musicManager).getScene());
        });

        // Liste des joueurs connectés
        playersList = new ListView<>();
        playersList.setPrefHeight(200);

        // Assemblage
        HBox inputRow1 = new HBox(10, new Text("Nom:"), playerNameField);
        inputRow1.setAlignment(Pos.CENTER);

        HBox inputRow2 = new HBox(10, new Text("Serveur:"), serverAddressField);
        inputRow2.setAlignment(Pos.CENTER);

        HBox buttonRow = new HBox(20, connectButton, hostButton, backButton);
        buttonRow.setAlignment(Pos.CENTER);

        container.getChildren().addAll(
                title,
                inputRow1,
                inputRow2,
                buttonRow,
                new Text("Joueurs connectés:"),
                playersList);

        // Style global
        Scene scene = new Scene(container, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/themes/menu.css").toExternalForm());
    }

    public Scene getScene() {
        return stage.getScene();
    }

    // Méthode pour héberger une partie

    private void hostGame() {
        try {
            GameServer server = new GameServer(5555);
            server.start();
        } catch (Exception e) {
            showError("Erreur lors du démarrage du serveur: " + e.getMessage());
        }
    }

    // Méthode à modifier
    private void connectToServer(String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            showError("Veuillez entrer un nom valide");
            return;
        }

        String address = serverAddressField.getText();
        String[] parts = address.split(":");

        if (parts.length != 2) {
            showError("Format d'adresse invalide. Utilisez IP:port");
            return;
        }

        String host = parts[0];
        int port;
        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            showError("Format de port invalide");
            return;
        }

        try {
            NetworkManager.createInstance(host, port);

            // Stocker l'ID du joueur pour l'utiliser lors de la transition vers la partie
            NetworkManager.getInstance().setLocalPlayerName(playerName);

            NetworkManager.getInstance().getClient().setListener(new LobbyNetworkListener());
            NetworkManager.getInstance().getClient().sendMessage(
                    Protocol.formatMessage(Protocol.PLAYER_JOIN, -1, playerName));
            addPlayer(playerName + " (vous)");
        } catch (Exception e) {
            showError("Erreur de connexion: " + e.getMessage());
        }

    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Ajouter cette classe interne pour gérer les événements réseau
    private class LobbyNetworkListener implements GameClient.NetworkEventListener {
        @Override
        public void onPlayerJoined(String playerName) {
            Platform.runLater(() -> {
                addPlayer(playerName);
            });
        }

        // Dans la classe LobbyNetworkListener
        @Override
        public void onGameStateUpdated(GameState gameState) {
            Platform.runLater(() -> {
                // Trouver l'ID du joueur local
                for (NetworkPlayerState player : gameState.getPlayers()) {
                    if (player.getName().equals(NetworkManager.getInstance().getLocalPlayerName())) {
                        NetworkManager.getInstance().setLocalPlayerId(player.getId());
                        break;
                    }
                }

                // Transition vers l'écran de jeu
                GameViewInterface gameView = new GameView(stage);
                OnlineGameController controller = new OnlineGameController(
                        gameView,
                        NetworkManager.getInstance().getLocalPlayerId());
                stage.setScene(gameView.getScene());
            });
        }

        @Override
        public void onPlayerTurnChanged(int playerId) {
            // Ignoré dans le lobby
        }

        @Override
        public void onDisconnected() {
            Platform.runLater(() -> {
                showError("Déconnecté du serveur");
            });
        }
    }

    public void addPlayer(String playerName) {
        if (!playersList.getItems().contains(playerName)) {
            playersList.getItems().add(playerName);
        }
    }
}