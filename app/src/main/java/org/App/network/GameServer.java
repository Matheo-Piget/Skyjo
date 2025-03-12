package org.App.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();

    public GameServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Game server started on port " + port);
        } catch (IOException e) {
            System.err.println("Cannot start server: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start();
                System.out.println("New client connected.");
            } catch (IOException e) {
                System.err.println("Error accepting client: " + e.getMessage());
            }
        }
    }
    
    // Broadcast message to all connected clients
    public synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    
    // Receives messages from clients and met à jour l'état du jeu en se basant sur le protocole défini.
    public synchronized void onClientMessage(ClientHandler sender, String message) {
        // Traitez les commandes telles que "MOVE", "PICK", etc.
        // Ici, updatez le modèle de jeu avec l'action et diffusez le nouvel état
        System.out.println("Message received from client: " + message);
        broadcast(message);
    }
    
    public static void main(String[] args) {
        GameServer server = new GameServer(5555);
        server.start();
    }
}