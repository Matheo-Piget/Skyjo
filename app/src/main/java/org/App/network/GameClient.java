package org.App.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread listenerThread;
    private NetworkEventListener listener;

    // Jackson ObjectMapper for JSON serialization
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
    
    public GameClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            out    = new PrintWriter(socket.getOutputStream(), true);
            in     = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            startListening();
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    public void setListener(NetworkEventListener listener) {
        this.listener = listener;
    }
    
    private void startListening() {
        listenerThread = new Thread(() -> {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println("CLIENT RECEIVED: " + message);  // Add this debug line
                    String[] parts = Protocol.parseMessage(message);
                    String type = parts[0];
                    
                    if (listener != null) {
                        switch (type) {
                            case Protocol.GAME_START:
                                System.out.println("📱 CLIENT: La partie commence!");
                                // Informer le listener que la partie commence
                                if (listener != null) {
                                    try {
                                        Platform.runLater(() -> {
                                            listener.onGameStarted();
                                        });
                                    } catch (Exception e) {
                                        System.err.println("Error notifying game start: " + e.getMessage());
                                    }
                                }
                                break;
                            case Protocol.GAME_STATE:
                                System.out.println("Received GAME_STATE message");  // Add this debug line
                                try {
                                    GameState updatedGame = deserializeGameState(parts[2]);
                                    
                                    // Au premier état de jeu, identifions notre ID en inspectant le tableau de joueurs
                                    String localName = NetworkManager.getInstance().getLocalPlayerName();
                                    int localId = NetworkManager.getInstance().getLocalPlayerId();
                                    
                                    // Si notre ID est encore incertain ou si plusieurs joueurs ont le même nom
                                    if (localId == -1 || needsIdVerification(updatedGame, localName, localId)) {
                                        findOurIdInGameState(updatedGame, localName);
                                    }
                                    
                                    listener.onGameStateUpdated(updatedGame);
                                } catch (Exception e) {
                                    System.err.println("Error processing game state: " + e);
                                    e.printStackTrace();
                                }
                                break;
                            case Protocol.PLAYER_TURN:
                                int playerId = Integer.parseInt(parts[1]);
                                listener.onPlayerTurnChanged(playerId);
                                break;
                            case Protocol.PLAYER_JOIN:
                                String playerName = parts[2];
                                int joinerId = Integer.parseInt(parts[1]);
                                if (playerName.equals(NetworkManager.getInstance().getLocalPlayerName())) {
                                    NetworkManager.getInstance().setLocalPlayerId(joinerId);
                                    System.out.println("📱 CLIENT: Mon ID a été défini à " + joinerId + " (nom: " + playerName + ")");
                                } else {
                                    System.out.println("📱 CLIENT: Joueur " + playerName + " a rejoint avec ID " + joinerId);
                                }
                                listener.onPlayerJoined(playerName);
                                break;
                            case Protocol.GAME_END:
                                if (parts.length > 2) {
                                    try {
                                        // Parse the results JSON
                                        String resultsJson = parts[2];
                                        com.fasterxml.jackson.databind.JsonNode root = objectMapper.readTree(resultsJson);
                                        
                                        // Get the winner
                                        String winnerName = root.get("winner").asText();
                                        
                                        // Build the scores map
                                        java.util.Map<String, Integer> scores = new java.util.HashMap<>();
                                        com.fasterxml.jackson.databind.JsonNode ranking = root.get("ranking");
                                        for (com.fasterxml.jackson.databind.JsonNode playerNode : ranking) {
                                            String name = playerNode.get("name").asText();
                                            int score = playerNode.get("score").asInt();
                                            scores.put(name, score);
                                        }
                                        
                                        listener.onGameEnd(winnerName, scores);
                                    } catch (Exception e) {
                                        System.err.println("Error processing game end: " + e);
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            default:
                                System.out.println("Unhandled message type: " + type);
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Listener error: " + e.getMessage());
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        });
        listenerThread.start();
    }

    public void setLocalPlayerName(String name) {
        
    }


    private GameState deserializeGameState(String jsonState) {
        try {
            return objectMapper.readValue(jsonState, GameState.class);
        } catch (IOException e) {
            System.err.println("Error deserializing game state: " + e.getMessage());
            return new GameState(); // Retourner un état vide en cas d'erreur
        }
    }
    
    public void sendMessage(String message) {
        System.out.println("CLIENT SENDING: " + message);
        if (out != null) {
            out.println(message);
            out.flush(); // Important to flush!
        } else {
            System.err.println("Cannot send message - not connected");
        }
    }
    
    public void disconnect() {
        try { socket.close(); } catch(IOException e) { }
    }
    
    public interface NetworkEventListener {
        void onGameStateUpdated(GameState gameState);
        void onPlayerTurnChanged(int playerId);
        void onPlayerJoined(String playerName);
        void onDisconnected();
        void onGameEnd(String winnerName, java.util.Map<String, Integer> scores);
        void onGameStarted();
    }

    /**
     * Vérifie si nous devons vérifier notre ID
     * @param gameState l'état actuel du jeu
     * @param localName le nom du joueur local
     * @param currentId l'ID actuellement stocké
     * @return true si l'ID semble incorrect ou ambigu
     */
    private boolean needsIdVerification(GameState gameState, String localName, int currentId) {
        if (gameState.getPlayers() == null) return false;
        
        // Compter combien de joueurs ont notre nom
        int playersWithSameName = 0;
        int correctIdForName = -1;
        
        for (NetworkPlayerState player : gameState.getPlayers()) {
            if (localName.equals(player.getName())) {
                playersWithSameName++;
                if (player.getId() == currentId) {
                    correctIdForName = currentId;
                }
            }
        }
        
        // Si plusieurs joueurs ont le même nom ou si notre ID ne correspond pas
        return playersWithSameName > 1 || (playersWithSameName == 1 && correctIdForName != currentId);
    }
    
    /**
     * Essaie de trouver notre ID dans l'état de jeu
     * @param gameState l'état actuel du jeu
     * @param localName le nom du joueur local
     */
    private void findOurIdInGameState(GameState gameState, String localName) {
        if (gameState.getPlayers() == null) return;
        
        int currentId = NetworkManager.getInstance().getLocalPlayerId();
        
        // Stratégie 1: Vérifier si notre ID actuel est valide
        boolean idFound = false;
        
        for (NetworkPlayerState player : gameState.getPlayers()) {
            if (player.getId() == currentId && localName.equals(player.getName())) {
                idFound = true;
                break;
            }
        }
        
        // Si l'ID est valide, on le garde
        if (idFound) {
            System.out.println("📱 CLIENT: ID vérifié et confirmé: " + currentId);
            return;
        }
        
        // Stratégie 2: Si un seul joueur a notre nom, c'est probablement nous
        int sameNameCount = 0;
        int potentialId = -1;
        
        for (NetworkPlayerState player : gameState.getPlayers()) {
            if (localName.equals(player.getName())) {
                sameNameCount++;
                potentialId = player.getId();
            }
        }
        
        if (sameNameCount == 1) {
            NetworkManager.getInstance().setLocalPlayerId(potentialId);
            System.out.println("📱 CLIENT: ID trouvé par nom: " + potentialId);
            return;
        }
        
        // Stratégie 3: Si plusieurs joueurs ont le même nom, on génère un avertissement
        if (sameNameCount > 1) {
            System.err.println("⚠️ ATTENTION: Plusieurs joueurs (" + sameNameCount + 
                               ") ont le même nom '" + localName + "'. Impossible de déterminer avec certitude l'ID.");
        }
    }
}