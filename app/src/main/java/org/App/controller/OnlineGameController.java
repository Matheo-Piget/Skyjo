package org.App.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.App.App;
import org.App.model.game.Card;
import org.App.model.game.CardValue;
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

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

/**
 * Controller for online game functionality.
 * Manages the communication between the network layer and the view.
 */
public class OnlineGameController implements NetworkEventListener {
    private final GameViewInterface view;
    private final int playerId;
    private boolean isMyTurn = false;
    private boolean hasPickedCard = false;
    private boolean gameEnded = false;
    private Map<Integer, String> playerNames = new HashMap<>();
    
    // Store last game state to handle any UI transitions
    private GameState lastGameState;

    /**
     * Creates a new OnlineGameController with the specified view and player ID.
     * 
     * @param view The game view interface.
     * @param playerId The local player's ID.
     */
    public OnlineGameController(GameViewInterface view, int playerId) {
        this.view = view;
        this.playerId = playerId;
        
        // Register this controller as the network event listener
        NetworkManager.getInstance().getClient().setListener(this);
        
        // Initial UI setup while waiting for server state
        Platform.runLater(() -> {
            view.showMessageBox("Connexion au serveur en cours...");
        });
    }

    @Override
    public void onGameStateUpdated(GameState gameState) {
        Platform.runLater(() -> {
            try {
                // Store for reference
                lastGameState = gameState;
                
                // Convert network objects to model objects and update the view
                updateViewWithGameState(gameState);
                
                // Check if it's the final round and show that information
                if (gameState.isFinalRound()) {
                    view.showMessageBox("Tour final !");
                }
                
                // Update turn status
                isMyTurn = (gameState.getCurrentPlayerId() == playerId);
            } catch (Exception e) {
                e.printStackTrace();
                view.showMessageBox("Erreur lors de la mise à jour de l'interface : " + e.getMessage());
            }
        });
    }

    /**
     * Updates the view with the provided game state.
     * 
     * @param gameState The current state of the game.
     */
    private void updateViewWithGameState(GameState gameState) {
        // Create a list of model players from network players
        List<Player> modelPlayers = convertNetworkPlayersToModelPlayers(gameState.getPlayers());
        
        // Convert the top discard card to a model card
        Card topDiscard = convertNetworkCardToModelCard(gameState.getTopDiscard());

        // Store player names for future reference
        for (NetworkPlayerState player : gameState.getPlayers()) {
            playerNames.put(player.getId(), player.getName());
        }
        
        // Update the view
        view.showPlaying(
                modelPlayers,
                getPlayerName(gameState.getCurrentPlayerId()),
                gameState.getRemainingCards(),
                topDiscard);
    }
    
    /**
     * Gets a player's name from their ID.
     * 
     * @param playerId The player's ID.
     * @return The player's name, or "Joueur" if not found.
     */
    private String getPlayerName(int playerId) {
        return playerNames.getOrDefault(playerId, "Joueur " + playerId);
    }

    /**
     * Converts network player states to model players.
     * 
     * @param networkPlayers The list of network player states.
     * @return A list of model players.
     */
    private List<Player> convertNetworkPlayersToModelPlayers(List<NetworkPlayerState> networkPlayers) {
        List<Player> modelPlayers = new ArrayList<>();
        for (NetworkPlayerState netPlayer : networkPlayers) {
            // Create a new player with the ID and name
            Player player = new HumanPlayer(netPlayer.getId(), netPlayer.getName());
            
            // Add cards to the player
            if (netPlayer.getCards() != null) {
                for (NetworkCardState netCard : netPlayer.getCards()) {
                    player.piocher(new Card(netCard.getValue(), netCard.isFaceVisible(), netCard.getId()));
                }
            }
            
            modelPlayers.add(player);
        }
        return modelPlayers;
    }

    /**
     * Converts a network card state to a model card.
     * 
     * @param networkCard The network card state.
     * @return A model card, or null if the network card is null.
     */
    private Card convertNetworkCardToModelCard(NetworkCardState networkCard) {
        if (networkCard == null)
            return null;
        return new Card(networkCard.getValue(), networkCard.isFaceVisible(), networkCard.getId());
    }

    @Override
    public void onPlayerTurnChanged(int currentPlayerId) {
        // Update turn status and notify the player if it's their turn
        isMyTurn = (currentPlayerId == playerId);
        Platform.runLater(() -> {
            if (isMyTurn) {
                view.showMessageBox("C'est votre tour!");
                view.showPlaying(
                        convertNetworkPlayersToModelPlayers(lastGameState.getPlayers()),
                        getPlayerName(currentPlayerId),
                        lastGameState.getRemainingCards(),
                        convertNetworkCardToModelCard(lastGameState.getTopDiscard()));
            } else {
                String playerName = getPlayerName(currentPlayerId);
                view.showMessageBox("Tour de " + playerName);
            }
        });
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
            if (!gameEnded) {
                Alert alert = new Alert(Alert.AlertType.ERROR, 
                    "Déconnecté du serveur. Retour au menu principal.", 
                    ButtonType.OK);
                alert.showAndWait();
                
                // Return to the main menu
                NetworkManager.getInstance().disconnect();
                App.getINSTANCE().restart();
            }
        });
    }

    /**
     * Handles a click on a card.
     * If a card has been picked, this will try to exchange it.
     * Otherwise, it will try to reveal the card.
     * 
     * @param cardView The clicked card view.
     */
    public void handleCardClick(CardView cardView) {
        if (!isMyTurn) {
            view.showMessageBox("Ce n'est pas votre tour !");
            return;
        }

        // Card clicks can either reveal or exchange a card
        // depending on the game state (if a card has been picked or not)
        if (hasPickedCard) {
            // Exchange card
            NetworkManager.getInstance().getClient().sendMessage(
                    Protocol.formatMessage(Protocol.CARD_EXCHANGE, playerId, String.valueOf(cardView.getIndex())));
            
            view.showMessageBox("Échange de carte...");
            hasPickedCard = false; // Reset state
        } else {
            // Reveal card
            NetworkManager.getInstance().getClient().sendMessage(
                    Protocol.formatMessage(Protocol.CARD_REVEAL, playerId, String.valueOf(cardView.getIndex())));
            
            view.showMessageBox("Révélation de carte...");
        }
    }

    /**
     * Handles a click on the pick pile.
     * Requests to pick a card from the server.
     */
    public void handlePickClick() {
        if (!isMyTurn) {
            view.showMessageBox("Ce n'est pas votre tour !");
            return;
        }

        NetworkManager.getInstance().getClient().sendMessage(
                Protocol.formatMessage(Protocol.CARD_PICK, playerId));
        
        // This state is set before server confirmation, and will be reset on card exchange or card discard
        hasPickedCard = true;
        view.showMessageBox("Pioche d'une carte...");
    }
    
    /**
     * Handles a click on the discard pile.
     * Requests to discard the currently picked card.
     */
    public void handleDiscardClick() {
        if (!isMyTurn) {
            view.showMessageBox("Ce n'est pas votre tour !");
            return;
        }
        
        if (!hasPickedCard) {
            view.showMessageBox("Vous devez d'abord piocher une carte !");
            return;
        }

        NetworkManager.getInstance().getClient().sendMessage(
                Protocol.formatMessage(Protocol.CARD_DISCARD, playerId));
        
        hasPickedCard = false;
        view.showMessageBox("Défausse de la carte...");
    }
    
    /**
     * Handles the end of the game.
     * Displays final scores and returns to the main menu.
     * 
     * @param winnerName The name of the winning player.
     * @param scores A map of player names to scores.
     */
    public void handleGameEnd(String winnerName, Map<String, Integer> scores) {
        gameEnded = true;
        
        Platform.runLater(() -> {
            // Build results message
            StringBuilder results = new StringBuilder("Partie terminée !\n");
            results.append("Gagnant : ").append(winnerName).append("\n\n");
            results.append("Scores finaux :\n");
            
            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                results.append(entry.getKey()).append(": ").append(entry.getValue()).append(" points\n");
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                results.toString(), 
                ButtonType.OK);
            alert.setTitle("Fin de partie");
            alert.setHeaderText("Résultats");
            
            alert.showAndWait();
            
            // Delay a bit before returning to menu
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> {
                NetworkManager.getInstance().disconnect();
                App.getINSTANCE().restart();
            });
            delay.play();
        });
    }
    
    /**
     * Handles when a player leaves the game.
     * 
     * @param playerName The name of the player who left.
     */
    public void handlePlayerLeft(String playerName) {
        Platform.runLater(() -> {
            view.showMessageBox("Le joueur " + playerName + " a quitté la partie");
        });
    }
    
    /**
     * Method to update the UI with what card was picked by a player.
     * 
     * @param playerId The ID of the player who picked a card.
     * @param cardValue The picked card value (or null if hidden).
     */
    public void onCardPicked(int playerId, CardValue cardValue) {
        Platform.runLater(() -> {
            String playerName = getPlayerName(playerId);
            
            if (playerId == this.playerId) {
                if (cardValue != null) {
                    view.showMessageBox("Vous avez pioché une carte: " + cardValue.getValue());
                } else {
                    view.showMessageBox("Vous avez pioché une carte");
                }
            } else {
                view.showMessageBox(playerName + " a pioché une carte");
            }
        });
    }
    
    /**
     * Method to update the UI with what card was revealed by a player.
     * 
     * @param playerId The ID of the player who revealed a card.
     * @param cardValue The revealed card value.
     */
    public void onCardRevealed(int playerId, CardValue cardValue) {
        Platform.runLater(() -> {
            String playerName = getPlayerName(playerId);
            
            if (playerId == this.playerId) {
                view.showMessageBox("Vous avez révélé: " + cardValue.getValue());
            } else {
                view.showMessageBox(playerName + " a révélé: " + cardValue.getValue());
            }
        });
    }
    
    /**
     * Method to update the UI with what card exchange was made.
     * 
     * @param playerId The ID of the player who exchanged cards.
     * @param discardedValue The value of the card discarded.
     * @param receivedValue The value of the card received.
     */
    public void onCardExchanged(int playerId, CardValue discardedValue, CardValue receivedValue) {
        Platform.runLater(() -> {
            String playerName = getPlayerName(playerId);
            
            if (playerId == this.playerId) {
                view.showMessageBox("Vous avez échangé " + discardedValue.getValue() + 
                                    " pour " + receivedValue.getValue());
            } else {
                view.showMessageBox(playerName + " a échangé une carte");
            }
        });
    }
    
    /**
     * Handles errors received from the server.
     * 
     * @param message The error message.
     */
    public void handleError(String message) {
        Platform.runLater(() -> {
            view.showMessageBox("Erreur: " + message);
        });
    }
    
    /**
     * Handles the start of the final round.
     */
    public void handleFinalRound() {
        Platform.runLater(() -> {
            view.showMessageBox("Tour final ! Un joueur a retourné toutes ses cartes.");
        });
    }
    
    /**
     * Allows manual disconnection from the server.
     */
    public void disconnect() {
        NetworkManager.getInstance().disconnect();
    }
}