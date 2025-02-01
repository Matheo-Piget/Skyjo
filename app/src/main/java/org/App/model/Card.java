package org.App.model;

public record Card(CardValue valeur, boolean faceVisible) {
    public Card retourner() {
        return new Card(valeur, !faceVisible);
    }
}
