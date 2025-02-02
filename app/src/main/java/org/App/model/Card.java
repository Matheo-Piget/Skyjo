package org.App.model;

public record Card(CardValue valeur, boolean faceVisible) implements ICard {

    @Override
    public Card retourner() {
        return new Card(valeur, !faceVisible);
    }
}
