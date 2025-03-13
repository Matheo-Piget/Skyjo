package org.App.network;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import org.App.model.player.Player;

public class NetworkPlayerState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private int score;
    private List<NetworkCardState> cards;
    
    // Constructeur sans arguments
    public NetworkPlayerState() {
    }
    
    public static NetworkPlayerState fromPlayer(Player player) {
        NetworkPlayerState state = new NetworkPlayerState();
        state.id = player.getId();
        state.name = player.getName();
        state.score = player.getCommutativeScore();
        state.cards = player.getCartes().stream()
            .map(NetworkCardState::fromCard)
            .collect(Collectors.toList());
        return state;
    }
    
    // Getters et setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<NetworkCardState> getCards() {
        return cards;
    }
}