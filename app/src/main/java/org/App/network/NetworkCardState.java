package org.App.network;

import java.io.Serializable;

import org.App.model.game.Card;
import org.App.model.game.CardValue;

public class NetworkCardState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private CardValue value;
    private boolean faceVisible;
    private int id;
    
    // Constructeur sans arguments
    public NetworkCardState() {
    }
    
    public static NetworkCardState fromCard(Card card) {
        NetworkCardState state = new NetworkCardState();
        state.value = card.valeur();
        state.faceVisible = card.faceVisible();
        state.id = card.id();
        return state;
    }
    
    // Getters et setters
}