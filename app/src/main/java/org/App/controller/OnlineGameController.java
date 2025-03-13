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
import org.App.view.components.CardView;
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
                topDiscard);
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
        if (networkCard == null)
            return null;
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

    public void handleDiscardClick() {
        if (!isMyTurn)
            return;

        NetworkManager.getInstance().getClient().sendMessage(
                Protocol.formatMessage(Protocol.CARD_DISCARD, playerId));
    }

    @Override
    public void onPlayerJoined(String playerName) {
        Platform.runLater(() -> {
            view.showMessageBox("Le joueur " + playerName + " a rejoint la partie");
        });
    }

    @Override
    public void onDisconnected() {
        Platform.runLater(() -> {
            view.showMessageBox("Déconnecté du serveur");
            // Retourner au menu principal
        });
    }

    // Dans OnlineGameController.java
    public void handleCardClick(CardView cardView) {
        if (!isMyTurn) {
            return;
        }

        // Les clics de carte peuvent soit révéler soit échanger une carte
        // selon l'état du jeu (si une carte a été piochée ou non)
        if (hasPickedCard) {
            // Échange de carte
            NetworkManager.getInstance().getClient().sendMessage(
                    Protocol.formatMessage(Protocol.CARD_EXCHANGE, playerId, String.valueOf(cardView.getIndex())));
            hasPickedCard = false; // Réinitialiser l'état
        } else {
            // Révélation de carte
            NetworkManager.getInstance().getClient().sendMessage(
                    Protocol.formatMessage(Protocol.CARD_REVEAL, playerId, String.valueOf(cardView.getIndex())));
        }
    }

    // Ajouter une variable pour suivre si le joueur a pioché une carte
    private boolean hasPickedCard = false;

    // Mettre à jour handlePickClick
    public void handlePickClick() {
        if (!isMyTurn)
            return;

        NetworkManager.getInstance().getClient().sendMessage(
                Protocol.formatMessage(Protocol.CARD_PICK, playerId));

        hasPickedCard = true; // Le joueur a maintenant une carte en main
    }

    // Autres méthodes adaptées...
}