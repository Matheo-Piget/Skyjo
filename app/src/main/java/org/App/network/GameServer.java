package org.App.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.App.model.game.SkyjoGame;
import org.App.model.player.HumanPlayer;
import org.App.model.player.Player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameServer {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();
    private SkyjoGame game;
    private boolean gameStarted = false;
    private int playerIdCounter = 0;

    // Jackson ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Game server started on port " + port);
        } catch (IOException e) {
            System.err.println("Cannot start server: " + e.getMessage());
        }
    }

    public void start() {
        new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String name = "Player" + (clients.size() + 1); // Nom par défaut
                    ClientHandler handler = new ClientHandler(clientSocket, this, name);
                    new Thread(handler).start();
                    System.out.println("New client connected: " + name);
                } catch (IOException e) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }).start();
    }

    public synchronized void startGame() {
        if (clients.size() < 2) {
            broadcast(Protocol.formatMessage(Protocol.ERROR, -1, "Not enough players"));
            return;
        }

        List<Player> players = new ArrayList<>();
        for (ClientHandler client : clients) {
            players.add(new HumanPlayer(playerIdCounter++, client.getName()));
        }

        game = new SkyjoGame(players);
        game.startGame();
        gameStarted = true;

        // Envoyer l'état initial à tous les joueurs
        broadcastGameState();

        // Désigner le premier joueur
        game.revealInitialCards();
        broadcastGameState();
        broadcast(Protocol.formatMessage(Protocol.PLAYER_TURN, game.getActualPlayer().getId()));
    }

    // Broadcast message to all connected clients
    public synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private void broadcastGameState() {
        // Convertir l'état du jeu en chaîne JSON ou format personnalisé
        String gameState = serializeGameState(game);
        broadcast(Protocol.formatMessage(Protocol.GAME_STATE, -1, gameState));
    }

    private String serializeGameState(SkyjoGame game) {
        try {
            // Créez un objet qui contient uniquement les informations nécessaires
            GameState gameState = new GameState(
                    game.getPlayers(),
                    game.getDiscard().isEmpty() ? null : game.getTopDiscard(),
                    game.getPick().size(),
                    game.getActualPlayer().getId(),
                    game.isFinalRound());

            return objectMapper.writeValueAsString(gameState);
        } catch (JsonProcessingException e) {
            System.err.println("Error serializing game state: " + e.getMessage());
            return "{}"; // Retourner un objet JSON vide en cas d'erreur
        }
    }

    // Receives messages from clients and met à jour l'état du jeu en se basant sur
    // le protocole défini.
    public synchronized void onClientMessage(ClientHandler sender, String message) {
        String[] parts = Protocol.parseMessage(message);
        String type = parts[0];

        switch (type) {
            case Protocol.PLAYER_JOIN:
                handlePlayerJoin(sender, parts[1]); // parts[1] est le nom du joueur
                break;
            case Protocol.CARD_PICK:
                handleCardPick(sender);
                break;
            // Gérer les autres types de messages
        }
    }

    private void handlePlayerJoin(ClientHandler sender, String playerName) {
        if (gameStarted) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Game already started"));
            return;
        }

        clients.add(sender);
        broadcast(Protocol.formatMessage(Protocol.PLAYER_JOIN, -1, playerName));
    }

    private void handleCardPick(ClientHandler sender) {
        if (!gameStarted) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Game not started"));
            return;
        }
    }

    // Dans GameServer.java
    public static void main(String[] args) {
        GameServer server = new GameServer(5555);
        server.start(); // Ajoutez cette ligne pour démarrer l'écoute des clients
        System.out.println("Serveur démarré sur le port 5555");

        // Attendre que des joueurs se connectent avant de démarrer la partie
        Scanner scanner = new Scanner(System.in);
        System.out.println("Appuyez sur Entrée pour démarrer la partie...");
        scanner.nextLine();
        server.startGame();
    }
}