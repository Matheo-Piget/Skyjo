package org.App.view.screens;

import org.App.controller.OnlineGameController;
import org.App.network.GameClient;
import org.App.network.GameServer;
import org.App.network.GameState;
import org.App.network.NetworkManager;
import org.App.network.Protocol;
import org.App.view.utils.MusicManager;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LobbyView {
    private final Stage stage;
    private final VBox container;
    private final ListView<String> playersList;
    private final TextField serverAddressField;
    private final MusicManager musicManager;
    private GameServer server; // Stockage de l'instance du serveur
    private boolean isHost = false; // Indique si le joueur est l'hôte
    private Button startGameButton; // Bouton pour démarrer la partie

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
            // Fermer le serveur s'il est actif
            if (server != null) {
                // Idéalement, il faudrait avoir une méthode stop() dans GameServer
                // server.stop();
            }
            stage.setScene(new GameMenuView(stage, musicManager).getScene());
        });

        // Bouton pour démarrer la partie (initialement désactivé)
        startGameButton = new Button("Démarrer la partie");
        startGameButton.getStyleClass().add("button-primary");
        startGameButton.setDisable(true); // Désactivé jusqu'à ce qu'on soit l'hôte
        startGameButton.setOnAction(e -> startGame());

        // Label pour indiquer si on est l'hôte
        Label hostLabel = new Label("En attente de connexion...");
        hostLabel.setTextFill(Color.LIGHTGRAY);

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
                hostLabel,
                new Text("Joueurs connectés:"),
                playersList,
                startGameButton);

        // Style global
        Scene scene = new Scene(container, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/themes/menu.css").toExternalForm());

        stage.setScene(scene);
    }

    public Scene getScene() {
        return stage.getScene();
    }

    // Méthode pour héberger une partie
    private void hostGame() {
        try {
            server = new GameServer(5555);
            server.start();
            isHost = true;
            startGameButton.setDisable(false); // Activer le bouton de démarrage

            // Mettre à jour le label
            for (int i = 0; i < container.getChildren().size(); i++) {
                if (container.getChildren().get(i) instanceof Label) {
                    Label hostLabel = (Label) container.getChildren().get(i);
                    hostLabel.setText("Vous êtes l'hôte de la partie");
                    hostLabel.setTextFill(Color.GREEN);
                    break;
                }
            }
        } catch (Exception e) {
            showError("Erreur lors du démarrage du serveur: " + e.getMessage());
        }
    }

    // Méthode pour démarrer la partie
    private void startGame() {
        if (isHost && server != null) {
            // Envoyer un message au serveur pour démarrer la partie
            // Le serveur doit avoir une méthode startGame()
            server.startGame();
        } else {
            showError("Vous n'êtes pas l'hôte ou le serveur n'est pas démarré");
        }
    }

    // Méthode pour se connecter au serveur
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

            // Désactiver les boutons après la connexion
            for (int i = 0; i < container.getChildren().size(); i++) {
                if (container.getChildren().get(i) instanceof HBox) {
                    HBox hbox = (HBox) container.getChildren().get(i);
                    for (int j = 0; j < hbox.getChildren().size(); j++) {
                        if (hbox.getChildren().get(j) instanceof Button) {
                            Button button = (Button) hbox.getChildren().get(j);
                            if (button.getText().equals("Se connecter") || button.getText().equals("Héberger")) {
                                button.setDisable(true);
                            }
                        }
                    }
                }
            }

            // Afficher un message de succès
            showMessage("Connecté avec succès! En attente d'autres joueurs...");
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

    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Ajouter cette classe interne pour gérer les événements réseau
    private class LobbyNetworkListener implements GameClient.NetworkEventListener {
        @Override
        public void onPlayerJoined(String playerName) {
            Platform.runLater(() -> {
                if (!playerName.equals(NetworkManager.getInstance().getLocalPlayerName())) {
                    addPlayer(playerName);
                    showMessage("Nouveau joueur connecté: " + playerName);
                }
            });
        }

        @Override
        public void onGameStateUpdated(GameState gameState) {
            System.out.println("LobbyView received game state update");
            if (gameState == null) {
                System.err.println("Received null game state");
                return;
            }

            Platform.runLater(() -> {
                try {
                    // Only transition if we haven't already
                    if (stage.getScene() != null && stage.getScene() == LobbyView.this.getScene()) {
                        System.out.println("Transitioning to game view");

                        // Stop the lobby music
                        if (musicManager != null) {
                            musicManager.stop();
                        }

                        // Create new GameView with minimal initialization
                        GameView gameView = new GameView(stage);

                        // Set up controller and transition to game view
                        OnlineGameController controller = new OnlineGameController(gameView,
                                NetworkManager.getInstance().getLocalPlayerId());

                        // Important: Set the listener on the network manager before changing scene
                        NetworkManager.getInstance().getClient().setListener(controller);

                        // Switch to game scene
                        stage.setScene(gameView.getScene());
                        gameView.show();
                    }
                } catch (Exception e) {
                    System.err.println("Error transitioning to game view: " + e.getMessage());
                    e.printStackTrace();
                    showError("Failed to start game: " + e.getMessage());
                }
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
                // Réactiver les boutons en cas de déconnexion
                for (int i = 0; i < container.getChildren().size(); i++) {
                    if (container.getChildren().get(i) instanceof HBox) {
                        HBox hbox = (HBox) container.getChildren().get(i);
                        for (int j = 0; j < hbox.getChildren().size(); j++) {
                            if (hbox.getChildren().get(j) instanceof Button) {
                                Button button = (Button) hbox.getChildren().get(j);
                                button.setDisable(false);
                            }
                        }
                    }
                }
                playersList.getItems().clear();
                isHost = false;
                startGameButton.setDisable(true);
            });
        }
    }

    public void addPlayer(String playerName) {
        if (!playersList.getItems().contains(playerName)) {
            playersList.getItems().add(playerName);
        }
    }

    public void show() {
        stage.show();
    }
}