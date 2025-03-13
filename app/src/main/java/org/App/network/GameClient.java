package org.App.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.App.model.game.SkyjoGame;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread listenerThread;
    private NetworkEventListener listener;
    
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
                    String[] parts = Protocol.parseMessage(message);
                    String type = parts[0];
                    
                    if (listener != null) {
                        switch (type) {
                            case Protocol.GAME_STATE:
                                SkyjoGame updatedGame = deserializeGameState(parts[2]);
                                listener.onGameStateUpdated(updatedGame);
                                break;
                            case Protocol.PLAYER_TURN:
                                int playerId = Integer.parseInt(parts[1]);
                                listener.onPlayerTurnChanged(playerId);
                                break;
                            // Gérer les autres types de messages
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


    private SkyjoGame deserializeGameState(String gameState) {
        // Implémentez la logique de désérialisation ici
        return null; // Remplacez par l'objet SkyjoGame désérialisé
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }
    
    public void disconnect() {
        try { socket.close(); } catch(IOException e) { }
    }
    
    public interface NetworkEventListener {
        void onGameStateUpdated(SkyjoGame game);
        void onPlayerTurnChanged(int playerId);
        void onPlayerJoined(String playerName);
        void onDisconnected();
        // Autres événements réseau
    }
}