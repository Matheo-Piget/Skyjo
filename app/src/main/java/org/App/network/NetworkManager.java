package org.App.network;

import org.App.controller.OnlineGameController;

public class NetworkManager {
    private static NetworkManager instance;
    private GameClient client;

    private String localPlayerName;
    private int localPlayerId = -1;

    private OnlineGameController onlineController;

    public void setOnlineController(OnlineGameController controller) {
        this.onlineController = controller;
    }

    public OnlineGameController getOnlineController() {
        return onlineController;
    }

    public void setLocalPlayerName(String name) {
        this.localPlayerName = name;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public void setLocalPlayerId(int id) {
        this.localPlayerId = id;
    }

    public int getLocalPlayerId() {
        return localPlayerId;
    }

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

    public static void createInstance(String host, int port) {
        if (instance == null) {
            instance = new NetworkManager();
            instance.client = new GameClient(host, port);
        } else {
            instance.client = new GameClient(host, port);
        }
    }
}