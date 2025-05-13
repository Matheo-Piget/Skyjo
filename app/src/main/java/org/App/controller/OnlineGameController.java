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
 * 
 * Handles game state updates, player actions, and UI updates.
 * 
 * @see GameViewInterface
 * @see NetworkEventListener
 * 
 * @author Mathéo Piget
 * @version 1.1
 */
public class OnlineGameController implements NetworkEventListener {
    private GameViewInterface view;
    private int playerId;
    private boolean isMyTurn = false;
    private boolean hasPickedCard = false;
    private boolean gameEnded = false;
    private Map<Integer, String> playerNames = new HashMap<>();
    
    // Store last game state to handle any UI transitions
    private GameState lastGameState;
    
    // Card that's currently being held after picking
    private Card heldCard;

    /**
     * Creates a new OnlineGameController with the specified view and player ID.
     * 
     * @param view The game view interface.
     * @param playerId The local player's ID.
     */
    public OnlineGameController(GameViewInterface view, int playerId) {
        this.view = view;
        this.playerId = playerId;
        this.playerNames = new HashMap<>();
        
        System.out.println("OnlineGameController initialisé avec l'ID de joueur: " + playerId);
        
        // Register this controller as the network event listener
        NetworkManager.getInstance().getClient().setListener(this);

        // Initial UI setup while waiting for server state
        Platform.runLater(() -> {
            if (view != null) {
                view.showMessageBox("Connexion au serveur en cours...");
            }
        });
    }

    /**
     * Sets the view for this controller and updates it with any saved game state.
     * 
     * @param view The game view interface.
     */
    public void setView(GameViewInterface view) {
        this.view = view;
        
        // Process any saved game state
        if (lastGameState != null) {
            updateViewWithGameState(lastGameState);
        }
    }

    @Override
    public void onGameStateUpdated(GameState gameState) {
        if (gameState == null) {
            System.err.println("Received null game state");
            return;
        }
        
        // Store the game state for future reference
        lastGameState = gameState;
        
        // Vérification de sécurité: s'assurer que l'ID du joueur est correctement défini
        String localPlayerName = NetworkManager.getInstance().getLocalPlayerName();
        for (NetworkPlayerState playerState : gameState.getPlayers()) {
            if (playerState.getName().equals(localPlayerName) && this.playerId != playerState.getId()) {
                System.out.println("⚠️ Correction d'ID détectée: L'ID local " + this.playerId + 
                                   " ne correspond pas à l'ID du serveur " + playerState.getId() + 
                                   " pour le joueur " + localPlayerName);
                this.playerId = playerState.getId();
                NetworkManager.getInstance().setLocalPlayerId(this.playerId);
            }
        }
        
        // Update isMyTurn based on the current player ID
        isMyTurn = (gameState.getCurrentPlayerId() == playerId);
        System.out.println("Current player ID: " + gameState.getCurrentPlayerId() + ", My ID: " + playerId + ", Is my turn: " + isMyTurn);
        
        Platform.runLater(() -> {
            try {
                // Check if there's a view to update
                if (view != null) {
                    updateViewWithGameState(gameState);
                    
                    // Check if it's the final round
                    if (gameState.isFinalRound()) {
                        handleFinalRound();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error updating view with game state: " + e.getMessage());
                e.printStackTrace();
                if (view != null) {
                    view.showMessageBox("Erreur lors de la mise à jour du jeu: " + e.getMessage());
                }
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
        
        // Check if any cards are face visible - if so, we need to animate the initial flips
        boolean hasVisibleCards = false;
        for (Player player : modelPlayers) {
            for (Card card : player.getCartes()) {
                if (card.faceVisible()) {
                    hasVisibleCards = true;
                    break;
                }
            }
            if (hasVisibleCards) break;
        }
        
        // Update the view
        view.showPlaying(
                modelPlayers,
                getPlayerName(gameState.getCurrentPlayerId()),
                gameState.getRemainingCards(),
                topDiscard);
                
        // If we have visible cards, animate the flips
        if (hasVisibleCards) {
            // Use a small delay to ensure the views are fully updated
            addDelay(0.2, () -> animateInitialFlips(modelPlayers));
        }
    }
    
    /**
     * Called after receiving game state with revealed cards.
     * Finds the CardViews that need to be flipped and animates them.
     * 
     * @param players List of players with their current cards
     */
    private void animateInitialFlips(List<Player> players) {
        List<CardView> allCardViews = view.getAllCardViews();
        List<CardView> toFlip = new ArrayList<>();

        for (Player player : players) {
            for (int i = 0; i < player.getCartes().size(); i++) {
                Card card = player.getCartes().get(i);
                if (card.faceVisible()) {
                    for (CardView cardView : allCardViews) {
                        if (cardView.getIndex() == i && cardView.getPlayerId() == player.getId() 
                                && !cardView.isFlipped()) {
                            toFlip.add(cardView);
                            break;
                        }
                    }
                }
            }
        }
        
        if (!toFlip.isEmpty()) {
            System.out.println("🎴 Animation de retournement pour " + toFlip.size() + " cartes");
            animateCardFlipsSequentially(toFlip, () -> {
                System.out.println("🎴 Animation de retournement terminée");
            });
        }
    }

    /**
     * Animates the flip for each CardView in the provided list sequentially.
     * 
     * @param cardViews  The list of CardViews to flip.
     * @param onFinished The action to execute after all flips are completed.
     */
    private void animateCardFlipsSequentially(List<CardView> cardViews, Runnable onFinished) {
        if (cardViews.isEmpty()) {
            onFinished.run();
            return;
        }
        CardView cv = cardViews.remove(0);
        cv.flipCard(() -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.1));
            delay.setOnFinished(event -> animateCardFlipsSequentially(cardViews, onFinished));
            delay.play();
        });
    }
    
    /**
     * Adds a delay before executing an action.
     * 
     * @param seconds The delay in seconds.
     * @param action  The action to execute.
     */
    private void addDelay(double seconds, Runnable action) {
        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(event -> action.run());
        delay.play();
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
            List<Card> cards = new ArrayList<>();
            if (netPlayer.getCards() != null) {
                for (NetworkCardState netCard : netPlayer.getCards()) {
                    Card card = convertNetworkCardToModelCard(netCard);
                    if (card != null) {
                        cards.add(card);
                    }
                }
            }

            player.setCards(cards);
            
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
        // Debug logs pour comprendre les problèmes d'ID
        System.out.println("🎲 onPlayerTurnChanged: serveur dit que c'est le tour du joueur ID=" + currentPlayerId);
        System.out.println("🎲 Mon ID est: " + this.playerId);
        
        // Update turn status and notify the player if it's their turn
        boolean previousTurnState = isMyTurn;
        isMyTurn = (currentPlayerId == this.playerId);
        
        if (previousTurnState != isMyTurn) {
            System.out.println("🎲 Changement d'état de tour: " + previousTurnState + " -> " + isMyTurn);
        }
        
        Platform.runLater(() -> {
            if (isMyTurn) {
                System.out.println("🎲 C'EST MON TOUR!");
                view.showMessageBox("C'est votre tour!");
                
                // Refresh the view with the latest game state to highlight current player
                if (lastGameState != null) {
                    view.showPlaying(
                            convertNetworkPlayersToModelPlayers(lastGameState.getPlayers()),
                            getPlayerName(currentPlayerId),
                            lastGameState.getRemainingCards(),
                            convertNetworkCardToModelCard(lastGameState.getTopDiscard()));
                }
            } else {
                String playerName = getPlayerName(currentPlayerId);
                System.out.println("🎲 Tour du joueur " + playerName + " (ID=" + currentPlayerId + ")");
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
                alert.setTitle("Déconnexion");
                alert.setHeaderText("Connexion perdue");
                alert.showAndWait();
                
                returnToMainMenu();
            }
        });
    }

    @Override
    public void onGameEnd(String winnerName, Map<String, Integer> scores) {
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
     * Handles a click on a card.
     * If a card has been picked, this will try to exchange it.
     * Otherwise, it will try to reveal the card.
     * 
     * @param cardView The clicked card view.
     */
    public void handleCardClick(CardView cardView) {
        if (!isMyTurn) {
            System.out.println("❌ Tentative d'action hors tour. Mon ID=" + playerId + 
                              ", Joueur actuel=" + (lastGameState != null ? lastGameState.getCurrentPlayerId() : "?"));
            view.showMessageBox("Ce n'est pas votre tour!");
            return;
        }

        System.out.println("✅ Action validée: c'est bien mon tour (ID=" + playerId + ")");
        
        // Card clicks can either reveal or exchange a card
        // depending on the game state (if a card has been picked or not)
        if (hasPickedCard) {
            // Exchange card
            NetworkManager.getInstance().getClient().sendMessage(
                    Protocol.formatMessage(Protocol.CARD_EXCHANGE, playerId, String.valueOf(cardView.getIndex())));
            
            view.showMessageBox("Échange de carte...");
            hasPickedCard = false; // Reset state
            heldCard = null;
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
            view.showMessageBox("Ce n'est pas votre tour!");
            return;
        }
        
        if (hasPickedCard) {
            view.showMessageBox("Vous avez déjà pioché une carte!");
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
            view.showMessageBox("Ce n'est pas votre tour!");
            return;
        }
        
        if (!hasPickedCard) {
            view.showMessageBox("Vous devez d'abord piocher une carte!");
            return;
        }

        NetworkManager.getInstance().getClient().sendMessage(
                Protocol.formatMessage(Protocol.CARD_DISCARD, playerId));
        
        hasPickedCard = false;
        heldCard = null;
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
                    heldCard = new Card(cardValue, true, -1);
                    view.showMessageBox("Vous avez pioché une carte: " + cardValue.getValue());
                } else {
                    view.showMessageBox("Vous avez pioché une carte.");
                }
                hasPickedCard = true;
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
                view.showMessageBox("Vous avez révélé un " + cardValue.getValue());
            } else {
                view.showMessageBox(playerName + " a révélé un " + cardValue.getValue());
            }
            
            // Request a refresh of the game state after a card is revealed
            if (lastGameState != null) {
                updateViewWithGameState(lastGameState);
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
                view.showMessageBox("Vous avez échangé un " + discardedValue.getValue() +
                                   " contre un " + receivedValue.getValue());
                hasPickedCard = false;
                heldCard = null;
            } else {
                view.showMessageBox(playerName + " a échangé une carte");
            }
            
            // Request a refresh of the game state after a card exchange
            if (lastGameState != null) {
                updateViewWithGameState(lastGameState);
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur s'est produite");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    /**
     * Handles the start of the final round.
     */
    public void handleFinalRound() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dernier tour");
            alert.setHeaderText("C'est le dernier tour!");
            alert.setContentText("Un joueur a retourné toutes ses cartes. C'est le dernier tour de jeu!");
            alert.show();
        });
    }
    
    /**
     * Allows manual disconnection from the server.
     */
    public void disconnect() {
        NetworkManager.getInstance().disconnect();
    }

    @Override
    public void onGameStarted() {
        System.out.println("🎮 Partie en ligne démarrée!");
        Platform.runLater(() -> {
            view.showMessageBox("La partie commence!");
        });
    }

    /**
     * Retourne au menu principal.
     */
    private void returnToMainMenu() {
        // Return to the main menu
        NetworkManager.getInstance().disconnect();
        App.getINSTANCE().restart();
    }
}