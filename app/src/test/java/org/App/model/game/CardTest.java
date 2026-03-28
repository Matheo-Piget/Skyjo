package org.App.model.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void retourner_flipsVisibility() {
        Card card = new Card(CardValue.CINQ, false, 1);
        Card flipped = card.retourner();
        assertTrue(flipped.faceVisible());
    }

    @Test
    void retourner_preservesValueAndId() {
        Card card = new Card(CardValue.DOUZE, false, 42);
        Card flipped = card.retourner();
        assertEquals(CardValue.DOUZE, flipped.valeur());
        assertEquals(42, flipped.id());
    }

    @Test
    void retourner_doubleFlipRestoresState() {
        Card card = new Card(CardValue.TROIS, false, 7);
        Card doubleFlipped = card.retourner().retourner();
        assertEquals(card.faceVisible(), doubleFlipped.faceVisible());
    }

    @Test
    void cardIsImmutable() {
        Card card = new Card(CardValue.UN, true, 10);
        Card flipped = card.retourner();
        // Original should not change
        assertTrue(card.faceVisible());
        assertFalse(flipped.faceVisible());
    }
}
