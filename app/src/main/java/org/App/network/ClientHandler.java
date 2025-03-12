package org.App.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch(IOException e) {
            System.err.println("Error setting up client handler: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        String inputLine;
        try {
            while ((inputLine = in.readLine()) != null) {
                server.onClientMessage(this, inputLine);
            }
        } catch(IOException e) {
            System.err.println("Error reading from client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch(IOException e) { }
        }
    }
    
    public void sendMessage(String message) {
        out.println(message);
    }
}