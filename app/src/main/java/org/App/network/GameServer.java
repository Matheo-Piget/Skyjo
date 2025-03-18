package org.App.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;
import org.App.model.player.HumanPlayer;
import org.App.model.player.Player;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GameServer {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>(); // Thread-safe list
    private SkyjoGame game;
    private boolean gameStarted = false;
    private boolean isRunning = true;
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
            while (isRunning && !serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    String name = "Player" + (clients.size() + 1); // Nom par défaut
                    ClientHandler handler = new ClientHandler(clientSocket, this, name);
                    new Thread(handler).start();
                    System.out.println("New client connected: " + name);
                } catch (IOException e) {
                    if (isRunning) {
                        System.err.println("Error accepting client: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            // Notify all clients that server is shutting down
            broadcast(Protocol.formatMessage(Protocol.ERROR, -1, "Server shutting down"));
            
            // Close all client connections
            for (ClientHandler client : clients) {
                try {
                    client.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Server closed"));
                } catch (Exception e) {
                    // Ignore errors during shutdown
                }
            }
            
            // Clear client list
            clients.clear();
            
            // Close server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }

    public synchronized void startGame() {
        if (clients.size() < 2) {
            broadcast(Protocol.formatMessage(Protocol.ERROR, -1, "Not enough players"));
            return;
        }

        List<Player> players = new ArrayList<>();
        for (ClientHandler client : clients) {
            players.add(new HumanPlayer(client.getId(), client.getName()));
        }

        game = new SkyjoGame(players);
        game.startGame();
        gameStarted = true;

        // Envoyer l'état initial à tous les joueurs
        broadcastGameState();
        
        // Notify players that the game has started
        broadcast(Protocol.formatMessage(Protocol.GAME_START, -1));

        // Désigner le premier joueur
        game.revealInitialCards();
        broadcastGameState();
        broadcast(Protocol.formatMessage(Protocol.PLAYER_TURN, game.getActualPlayer().getId()));
    }

    /**
     * Broadcast a message to all connected clients.
     * This method is synchronized to ensure thread safety when modifying the client list.
     * @param message The message to broadcast.
     */
    public synchronized void broadcast(String message) {
        System.out.println("SERVER BROADCASTING: " + message);
        List<ClientHandler> disconnectedClients = new ArrayList<>();
        
        for (ClientHandler client : clients) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                System.err.println("Error broadcasting to client: " + e.getMessage());
                disconnectedClients.add(client);
            }
        }
        
        // Remove disconnected clients
        clients.removeAll(disconnectedClients);
    }

    public synchronized void handleClientDisconnect(ClientHandler client) {
        clients.remove(client);
        broadcast(Protocol.formatMessage(Protocol.PLAYER_LEFT, -1, client.getName()));
        
        // If game has started and a player disconnects, we may need to handle that
        if (gameStarted && game != null) {
            // Check if we need to end the game due to too few players
            if (clients.size() < 2) {
                broadcast(Protocol.formatMessage(Protocol.GAME_END, -1, "Not enough players remaining"));
                gameStarted = false;
            } else {
                // Otherwise, we might need to skip this player's turn if it's their turn
                if (game.getActualPlayer().getId() == client.getId()) {
                    game.nextPlayer();
                    broadcastGameState();
                    broadcast(Protocol.formatMessage(Protocol.PLAYER_TURN, game.getActualPlayer().getId()));
                }
            }
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

    public synchronized void onClientMessage(ClientHandler sender, String message) {
        try {
            String[] parts = Protocol.parseMessage(message);
            if (parts.length < 2) {
                sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Invalid message format"));
                return;
            }
            
            String type = parts[0];
            int playerId = Integer.parseInt(parts[1]);

            // Verify that the sender is allowed to send messages for this player ID
            if (playerId != -1 && playerId != sender.getId() && type != Protocol.PLAYER_JOIN) {
                sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Unauthorized player ID"));
                return;
            }

            switch (type) {
                case Protocol.PLAYER_JOIN:
                    if (parts.length >= 3) {
                        handlePlayerJoin(sender, parts[2]);
                    } else {
                        sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Invalid player join message"));
                    }
                    break;
                case Protocol.CARD_PICK:
                    handleCardPick(sender);
                    break;
                case Protocol.CARD_DISCARD:
                    handleCardDiscard(sender);
                    break;
                case Protocol.CARD_REVEAL:
                    if (parts.length >= 3) {
                        int cardIndex = Integer.parseInt(parts[2]);
                        handleCardReveal(sender, playerId, cardIndex);
                    } else {
                        sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Invalid card reveal message"));
                    }
                    break;
                case Protocol.CARD_EXCHANGE:
                    if (parts.length >= 3) {
                        int cardIndex = Integer.parseInt(parts[2]);
                        handleCardExchange(sender, playerId, cardIndex);
                    } else {
                        sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Invalid card exchange message"));
                    }
                    break;
                default:
                    sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Unknown message type: " + type));
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Error processing message"));
        }
    }

    public synchronized int getClientId(ClientHandler client) {
        int index = clients.indexOf(client);
        return index >= 0 ? index : -1;
    }
    
    // Helper method to find player by ID
    private Player findPlayerById(int playerId) {
        if (game == null) return null;
        
        for (Player p : game.getPlayers()) {
            if (p.getId() == playerId) {
                return p;
            }
        }
        return null;
    }
    
    // Helper method to check if it's the player's turn
    private boolean isPlayerTurn(int playerId) {
        return game != null && game.getActualPlayer().getId() == playerId;
    }

    private void handleCardDiscard(ClientHandler sender) {
        if (!gameStarted || game == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Game not started"));
            return;
        }

        int playerId = sender.getId();
        if (!isPlayerTurn(playerId)) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Not your turn"));
            return;
        }

        Player player = findPlayerById(playerId);
        if (player == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Player not found"));
            return;
        }

        try {
            // Implement the discard logic in your game model
            game.addToDiscard(game.getTopDiscard());
            
            // Broadcast updated state
            broadcastGameState();

            // Move to next player's turn
            game.nextPlayer();
            broadcast(Protocol.formatMessage(Protocol.PLAYER_TURN, game.getActualPlayer().getId()));
        } catch (Exception e) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Error discarding card: " + e.getMessage()));
        }
    }

    private void handleCardReveal(ClientHandler sender, int playerId, int cardIndex) {
        if (!gameStarted || game == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Game not started"));
            return;
        }

        if (!isPlayerTurn(playerId)) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Not your turn"));
            return;
        }

        Player player = findPlayerById(playerId);
        if (player == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Player not found"));
            return;
        }

        try {
            // Check if the card index is valid
            if (cardIndex < 0 || cardIndex >= player.getCartes().size()) {
                sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Invalid card index"));
                return;
            }
            
            // Implement the reveal card logic in your game model
            game.revealCard(player, cardIndex);
            
            // Broadcast the updated game state to all clients
            broadcastGameState();
            
            // Check if the game is finished after this move
            if (game.isFinished()) {
                // Handle game end
                broadcast(Protocol.formatMessage(Protocol.GAME_END, -1));
                gameStarted = false;
            } else {
                // Move to next player
                game.nextPlayer();
                broadcast(Protocol.formatMessage(Protocol.PLAYER_TURN, game.getActualPlayer().getId()));
            }
        } catch (Exception e) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Error revealing card: " + e.getMessage()));
        }
    }

    private void handleCardExchange(ClientHandler sender, int playerId, int cardIndex) {
        if (!gameStarted || game == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Game not started"));
            return;
        }

        if (!isPlayerTurn(playerId)) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Not your turn"));
            return;
        }

        Player player = findPlayerById(playerId);
        if (player == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Player not found"));
            return;
        }

        try {
            // Check if the card index is valid
            if (cardIndex < 0 || cardIndex >= player.getCartes().size()) {
                sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Invalid card index"));
                return;
            }
            
            // Check if player has a picked card to exchange
            if(game.getPickedCard() == null) {
                sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "No card picked to exchange"));
                return;
            }
            
            // Implement the exchange card logic
            game.exchangeOrRevealCard(player, game.getPickedCard(), cardIndex);
            
            // Broadcast updated state
            broadcastGameState();
            
            // Check if the game is finished after this move
            if (game.isFinished()) {
                broadcast(Protocol.formatMessage(Protocol.GAME_END, -1));
                gameStarted = false;
            } else {
                // Move to next player
                game.nextPlayer();
                broadcast(Protocol.formatMessage(Protocol.PLAYER_TURN, game.getActualPlayer().getId()));
            }
        } catch (Exception e) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Error exchanging card: " + e.getMessage()));
        }
    }

    private void handlePlayerJoin(ClientHandler sender, String playerName) {
        if (gameStarted) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Game already started"));
            return;
        }

        // Set the player's name
        sender.setName(playerName);
        
        // Assign a unique ID to the client
        if (!clients.contains(sender)) {
            sender.setId(playerIdCounter++);
            clients.add(sender);
        }
        
        // Notify all clients about the new player
        broadcast(Protocol.formatMessage(Protocol.PLAYER_JOIN, sender.getId(), playerName));
        
        // Send the current player list to the new player
        for (ClientHandler client : clients) {
            if (client != sender) {
                sender.sendMessage(Protocol.formatMessage(
                    Protocol.PLAYER_JOIN, client.getId(), client.getName()));
            }
        }
    }

    private void handleCardPick(ClientHandler sender) {
        if (!gameStarted || game == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Game not started"));
            return;
        }

        int playerId = sender.getId();
        if (!isPlayerTurn(playerId)) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Not your turn"));
            return;
        }

        Player player = findPlayerById(playerId);
        if (player == null) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Player not found"));
            return;
        }

        try {
            // Check if pick pile has cards
            if (game.getPick().isEmpty()) {
                sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Pick pile is empty"));
                return;
            }
            
            // Implement the pick card logic
            Card pickedCard = game.pickCard();
            
            if (pickedCard != null) {
                // Notify all clients about the picked card
                broadcast(Protocol.formatMessage(Protocol.CARD_PICK, playerId));
                
                // Send the updated game state
                broadcastGameState();
            } else {
                sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Failed to pick a card"));
            }
        } catch (Exception e) {
            sender.sendMessage(Protocol.formatMessage(Protocol.ERROR, -1, "Error picking card: " + e.getMessage()));
        }
    }
}