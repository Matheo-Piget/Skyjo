package org.App.network;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.App.model.game.Card;
import org.App.model.player.Player;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private List<NetworkPlayerState> players;
    private NetworkCardState topDiscard;
    private int remainingCards;
    private int currentPlayerId;
    private boolean isFinalRound;
    
    // Constructeur sans arguments pour la désérialisation
    public GameState() {
    }
    
    public GameState(List<Player> players, Card topDiscard, int remainingCards, 
                    int currentPlayerId, boolean isFinalRound) {
        this.players = players.stream()
            .map(NetworkPlayerState::fromPlayer)
            .collect(Collectors.toList());
        
        this.topDiscard = topDiscard != null ? NetworkCardState.fromCard(topDiscard) : null;
        this.remainingCards = remainingCards;
        this.currentPlayerId = currentPlayerId;
        this.isFinalRound = isFinalRound;
    }
    
    // Getters et setters
    public List<NetworkPlayerState> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<NetworkPlayerState> players) {
        this.players = players;
    }
    
    public NetworkCardState getTopDiscard() {
        return topDiscard;
    }
    
    public void setTopDiscard(NetworkCardState topDiscard) {
        this.topDiscard = topDiscard;
    }
    
    public int getRemainingCards() {
        return remainingCards;
    }
    
    public void setRemainingCards(int remainingCards) {
        this.remainingCards = remainingCards;
    }
    
    public int getCurrentPlayerId() {
        return currentPlayerId;
    }
    
    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }


    public String getCurrentPlayerName() {
        return "";
    }
    
    public boolean isFinalRound() {
        return isFinalRound;
    }
    
    public void setFinalRound(boolean finalRound) {
        isFinalRound = finalRound;
    }
}