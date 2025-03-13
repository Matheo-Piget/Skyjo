package org.App.view.screens;

import org.App.network.GameServer;
import org.App.network.NetworkManager;
import org.App.network.Protocol;
import org.App.view.utils.MusicManager;

import javafx.scene.Scene;
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

    public LobbyView(Stage stage, MusicManager musicManager) {
        this.musicManager = musicManager;
        this.stage = stage;
        container = new VBox(10);
        playersList = new ListView<>();
        TextField playerNameField = new TextField("Joueur");
        serverAddressField = new TextField("localhost:5555");

        // Ajouter les éléments à la vue

        // Créer les éléments UI
        Button connectButton = new Button("Connect");
        connectButton.setOnAction(e -> connectToServer());

        Button hostButton = new Button("Host Game");
        hostButton.setOnAction(e -> hostGame());

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> startGame());

        container.getChildren().addAll(
                new Text("Skyjo Online"),
                new HBox(10, new Text("Nom:"), playerNameField),
                new HBox(10, new Text("Serveur:"), serverAddressField),
                new HBox(10, connectButton, hostButton, startButton),
                new Text("Joueurs connectés:"),
                playersList);

        // Construire la vue
        // ...
    }

    public Scene getScene() {
        return new Scene(container, 800, 600);
    }

    private void connectToServer() {
        String address = serverAddressField.getText();
        String[] parts = address.split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 5555;

        // Utiliser un nouveau client avec l'adresse spécifiée
        try {
            NetworkManager.createInstance(host, port);
            // S'identifier auprès du serveur
            NetworkManager.getInstance().getClient().sendMessage(
                    Protocol.formatMessage(Protocol.PLAYER_JOIN, -1, playerNameField.getText()));
        } catch (Exception e) {
            System.err.println("Erreur de connexion au serveur: " + e.getMessage());
            // Afficher un message d'erreur à l'utilisateur
            // ...
        }
    }

    private void hostGame() {
        // Lancer un serveur local
        new Thread(() -> {
            GameServer server = new GameServer(5555);
            server.startGame();
        }).start();

        // Se connecter au serveur local
        connectToServer();
    }

    private void startGame() {
        // Envoyer la commande de démarrage au serveur
        NetworkManager.getInstance().getClient().sendMessage(
                Protocol.formatMessage(Protocol.GAME_START, -1));
    }
}