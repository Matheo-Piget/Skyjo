package org.App.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread listenerThread;
    
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
    
    private void startListening() {
        listenerThread = new Thread(() -> {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // Traitez les messages envoyés par le serveur.
                    System.out.println("Message from server: " + message);
                    // Par exemple, mettez à jour la vue en fonction du message reçu.
                }
            } catch (IOException e) {
                System.err.println("Listener error: " + e.getMessage());
            }
        });
        listenerThread.start();
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }
    
    public void disconnect() {
        try { socket.close(); } catch(IOException e) { }
    }
    
    // Getter pour socket ou d'autres attributs si nécessaire
}