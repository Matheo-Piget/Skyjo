package org.App.controller;

import org.App.model.game.SkyjoGame;
import org.App.network.GameClient.NetworkEventListener;
import org.App.network.NetworkManager;
import org.App.network.Protocol;
import org.App.view.screens.GameViewInterface;

import javafx.application.Platform;

public class OnlineGameController implements NetworkEventListener {
    private final GameViewInterface view;
    private final int playerId;
    private boolean isMyTurn = false;
    
    public OnlineGameController(GameViewInterface view, int playerId) {
        this.view = view;
        this.playerId = playerId;
        
        NetworkManager.getInstance().getClient().setListener(this);
    }
    
    @Override
    public void onGameStateUpdated(SkyjoGame game) {
        Platform.runLater(() -> view.showPlaying(
            game.getPlayers(), 
            game.getActualPlayer().getName(),
            game.getPick().size(), 
            game.getTopDiscard()
        ));
    }
    
    @Override
    public void onPlayerTurnChanged(int currentPlayerId) {
        isMyTurn = (currentPlayerId == playerId);
        Platform.runLater(() -> {
            if (isMyTurn) {
                view.showMessageBox("C'est votre tour!");
            }
        });
    }
    
    // Adapter les méthodes du GameController original pour envoyer des messages au serveur
    public void handlePickClick() {
        if (!isMyTurn) return;
        
        NetworkManager.getInstance().getClient().sendMessage(
            Protocol.formatMessage(Protocol.CARD_PICK, playerId)
        );
    }

    public void handleDiscardClick() {
        if (!isMyTurn) return;
        
        NetworkManager.getInstance().getClient().sendMessage(
            Protocol.formatMessage(Protocol.CARD_DISCARD, playerId)
        );
    }

    @Override
    public void onPlayerJoined(String playerName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onPlayerJoined'");
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onDisconnected'");
    }
    
    // Autres méthodes adaptées...
}