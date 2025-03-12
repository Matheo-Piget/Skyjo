package org.App.network;

public class NetworkManager {
    private static NetworkManager instance;
    private GameClient client;
    
    private NetworkManager() {
        // Connectez-vous au serveur (par ex., localhost, port 5555)
        client = new GameClient("localhost", 5555);
    }
    
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    public GameClient getClient() {
        return client;
    }
    
    public void disconnect() {
        client.disconnect();
    }
}