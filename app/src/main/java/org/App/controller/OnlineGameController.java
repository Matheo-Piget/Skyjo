package org.App.controller;

import java.util.ArrayList;
import java.util.List;

import org.App.model.game.Card;
import org.App.model.player.HumanPlayer;
import org.App.model.player.Player;
import org.App.network.GameClient.NetworkEventListener;
import org.App.network.GameState;
import org.App.network.NetworkCardState;
import org.App.network.NetworkManager;
import org.App.network.NetworkPlayerState;
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
    public void onGameStateUpdated(GameState gameState) {
        Platform.runLater(() -> {
            // Conversion des objets réseau en objets modèle si nécessaire
            updateViewWithGameState(gameState);
        });
    }

    private void updateViewWithGameState(GameState gameState) {
        // Mettez à jour la vue avec les informations du GameState
        // Par exemple, affichez le plateau, les cartes, etc.
        
        // Vérifiez si c'est le tour du joueur actuel
        isMyTurn = (gameState.getCurrentPlayerId() == playerId);
        
        // Créez les cartes visibles en fonction de gameState.players
        List<Player> modelPlayers = convertNetworkPlayersToModelPlayers(gameState.getPlayers());
        
        Card topDiscard = convertNetworkCardToModelCard(gameState.getTopDiscard());
        
        // Mise à jour de la vue
        view.showPlaying(
            modelPlayers,
            gameState.getCurrentPlayerName(),
            gameState.getRemainingCards(),
            topDiscard
        );
    }

    private List<Player> convertNetworkPlayersToModelPlayers(List<NetworkPlayerState> networkPlayers) {
    List<Player> modelPlayers = new ArrayList<>();
    for (NetworkPlayerState netPlayer : networkPlayers) {
        // Créer un nouveau joueur avec l'ID et le nom
        Player player = new HumanPlayer(netPlayer.getId(), netPlayer.getName());
        
        // Ajouter les cartes au joueur
        for (NetworkCardState netCard : netPlayer.getCards()) {
            player.piocher(new Card(netCard.getValue(), netCard.isFaceVisible()));
        }
        
        modelPlayers.add(player);
    }
    return modelPlayers;
}

private Card convertNetworkCardToModelCard(NetworkCardState networkCard) {
    if (networkCard == null) return null;
    return new Card(networkCard.getValue(), networkCard.isFaceVisible());
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