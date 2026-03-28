package org.App.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.App.model.player.HumanPlayer;
import org.App.model.player.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkyjoGameTest {

    private SkyjoGame game;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        players = List.of(
                new HumanPlayer(0, "Alice"),
                new HumanPlayer(1, "Bob"));
        game = new SkyjoGame(players);
        game.startGame();
    }

    // ─── Card distribution ──────────────────────────────────────────

    @Test
    void startGame_distributes12CardsPerPlayer() {
        for (Player p : game.getPlayers()) {
            assertEquals(12, p.getCartes().size());
        }
    }

    @Test
    void startGame_deckHasCorrectRemainingCount() {
        // 150 total - 12*2 dealt - 1 discard = 125
        assertEquals(125, game.getPickSize());
    }

    @Test
    void startGame_discardHasOneCardFaceUp() {
        assertNotNull(game.getTopDiscard());
        assertTrue(game.getTopDiscard().faceVisible());
    }

    @Test
    void startGame_allPlayerCardsAreFaceDown() {
        for (Player p : game.getPlayers()) {
            for (Card c : p.getCartes()) {
                assertFalse(c.faceVisible(), "Cards should be face-down after deal");
            }
        }
    }

    @Test
    void deckContains150Cards() {
        // Start a game with 0 dealt cards by checking the total
        // 12 per player * 2 players + remaining + 1 discard = 150
        int total = game.getPickSize() + 24 + 1;
        assertEquals(150, total);
    }

    // ─── Pick / Discard ──────────────────────────────────────────────

    @Test
    void pickCard_removesFromDeck() {
        int before = game.getPickSize();
        Card picked = game.pickCard();
        assertNotNull(picked);
        assertEquals(before - 1, game.getPickSize());
    }

    @Test
    void pickDiscard_returnsCardFaceUp() {
        Card picked = game.pickDiscard();
        assertNotNull(picked);
        assertTrue(picked.faceVisible());
    }

    @Test
    void pickDiscard_removesTopCard() {
        Card top = game.getTopDiscard();
        Card picked = game.pickDiscard();
        assertEquals(top.id(), picked.id());
        assertNotEquals(top, game.getTopDiscard());
    }

    @Test
    void addToDiscard_cardIsFaceUp() {
        Card faceDown = new Card(CardValue.CINQ, false, 999);
        game.addToDiscard(faceDown);
        assertTrue(game.getTopDiscard().faceVisible());
    }

    // ─── Exchange / Reveal ───────────────────────────────────────────

    @Test
    void exchangeOrRevealCard_placesCardFaceUp() {
        Player p = game.getPlayers().get(0);
        Card newCard = new Card(CardValue.TROIS, true, 500);
        game.exchangeOrRevealCard(p, newCard, 0);
        assertTrue(p.getCartes().get(0).faceVisible());
    }

    @Test
    void exchangeOrRevealCard_faceDownCardGetsFlipped() {
        Player p = game.getPlayers().get(0);
        Card faceDown = new Card(CardValue.DEUX, false, 501);
        game.exchangeOrRevealCard(p, faceDown, 0);
        assertTrue(p.getCartes().get(0).faceVisible(),
                "Face-down card should be flipped face-up when placed");
    }

    @Test
    void exchangeOrRevealCard_discardedCardFromExchangeIsOnDiscard() {
        Player p = game.getPlayers().get(0);
        Card oldCard = p.getCartes().get(0);
        Card newCard = new Card(CardValue.UN, true, 502);
        game.exchangeOrRevealCard(p, newCard, 0);
        assertEquals(oldCard.id(), game.getTopDiscard().id());
    }

    @Test
    void exchangeOrRevealCard_minusOneDiscardsCard() {
        Player p = game.getPlayers().get(0);
        Card card = new Card(CardValue.QUATRE, true, 503);
        game.exchangeOrRevealCard(p, card, -1);
        assertEquals(card.id(), game.getTopDiscard().id());
    }

    @Test
    void exchangeOrRevealCard_invalidIndexThrows() {
        Player p = game.getPlayers().get(0);
        Card card = new Card(CardValue.UN, true, 504);
        assertThrows(InvalidMoveException.class,
                () -> game.exchangeOrRevealCard(p, card, 99));
    }

    @Test
    void exchangeOrRevealCard_negativeIndexThrows() {
        Player p = game.getPlayers().get(0);
        Card card = new Card(CardValue.UN, true, 505);
        assertThrows(InvalidMoveException.class,
                () -> game.exchangeOrRevealCard(p, card, -2));
    }

    @Test
    void revealCard_flipsCard() {
        Player p = game.getPlayers().get(0);
        assertFalse(p.getCartes().get(0).faceVisible());
        game.revealCard(p, 0);
        assertTrue(p.getCartes().get(0).faceVisible());
    }

    @Test
    void revealCard_invalidIndexThrows() {
        Player p = game.getPlayers().get(0);
        assertThrows(InvalidMoveException.class,
                () -> game.revealCard(p, 50));
    }

    // ─── Column check ────────────────────────────────────────────────

    @Test
    void checkColumns_removesMatchingColumn() {
        Player p = game.getPlayers().get(0);
        // Set up a column: indices 0, 4, 8 (column 0 with 4 columns = indices 0, 4, 8)
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cards.add(new Card(CardValue.SEPT, false, i));
        }
        // Make column 0 all the same value and face-up
        int cols = 4;
        cards.set(0, new Card(CardValue.CINQ, true, 100));
        cards.set(cols, new Card(CardValue.CINQ, true, 101));
        cards.set(2 * cols, new Card(CardValue.CINQ, true, 102));
        p.setCards(cards);

        game.checkColumns();
        assertEquals(9, p.getCartes().size(), "Column of 3 matching cards should be removed");
    }

    @Test
    void checkColumns_doesNotRemoveNonMatchingColumn() {
        Player p = game.getPlayers().get(0);
        // Use distinct values per column so no full column matches
        CardValue[] vals = {CardValue.UN, CardValue.DEUX, CardValue.TROIS, CardValue.QUATRE};
        List<Card> cards = new ArrayList<>();
        int id = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++) {
                // Each column has different values across rows
                cards.add(new Card(vals[(row + col) % 4], true, id++));
            }
        }
        p.setCards(cards);

        game.checkColumns();
        assertEquals(12, p.getCartes().size(), "Non-matching column should not be removed");
    }

    @Test
    void checkColumns_doesNotRemoveHiddenColumn() {
        Player p = game.getPlayers().get(0);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            cards.add(new Card(CardValue.CINQ, false, i)); // all same but hidden
        }
        p.setCards(cards);

        game.checkColumns();
        assertEquals(12, p.getCartes().size(), "Hidden columns should not be removed");
    }

    // ─── Score ───────────────────────────────────────────────────────

    @Test
    void computeHandScore_sumsAllCards() {
        Player p = game.getPlayers().get(0);
        List<Card> hand = List.of(
                new Card(CardValue.CINQ, true, 0),
                new Card(CardValue.TROIS, true, 1),
                new Card(CardValue.MOINS_DEUX, true, 2));
        p.setCards(new ArrayList<>(hand));
        assertEquals(6, game.computeHandScore(p)); // 5 + 3 + (-2) = 6
    }

    @Test
    void getRanking_doublesFirstRevealerScoreIfNotLowest() {
        Player alice = game.getPlayers().get(0);
        Player bob = game.getPlayers().get(1);

        // Give Alice all face-up cards (she reveals all)
        List<Card> aliceCards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            aliceCards.add(new Card(CardValue.CINQ, true, i)); // score = 60
        }
        alice.setCards(aliceCards);

        // Give Bob lower cards
        List<Card> bobCards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            bobCards.add(new Card(CardValue.ZERO, true, 100 + i)); // score = 0
        }
        bob.setCards(bobCards);

        // Trigger final round (Alice has all revealed)
        game.checkAndEnterFinalRound();

        Map<Player, Integer> ranking = game.getRanking();
        // Alice should have doubled score (60 * 2 = 120) because she's not lowest
        assertEquals(120, ranking.get(alice));
        assertEquals(0, ranking.get(bob));
    }

    @Test
    void getRanking_doesNotDoubleIfFirstRevealerHasLowest() {
        Player alice = game.getPlayers().get(0);
        Player bob = game.getPlayers().get(1);

        List<Card> aliceCards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            aliceCards.add(new Card(CardValue.MOINS_DEUX, true, i)); // score = -24
        }
        alice.setCards(aliceCards);

        List<Card> bobCards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            bobCards.add(new Card(CardValue.DOUZE, true, 100 + i)); // score = 144
        }
        bob.setCards(bobCards);

        game.checkAndEnterFinalRound();

        Map<Player, Integer> ranking = game.getRanking();
        assertEquals(-24, ranking.get(alice)); // not doubled
        assertEquals(144, ranking.get(bob));
    }

    // ─── Final round / Game over ─────────────────────────────────────

    @Test
    void checkAndEnterFinalRound_triggersWhenAllCardsRevealed() {
        Player p = game.getPlayers().get(0);
        List<Card> allRevealed = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            allRevealed.add(new Card(CardValue.UN, true, i));
        }
        p.setCards(allRevealed);

        assertTrue(game.checkAndEnterFinalRound());
        assertTrue(game.isFinalRound());
    }

    @Test
    void checkAndEnterFinalRound_doesNotTriggerWhenCardsHidden() {
        assertFalse(game.checkAndEnterFinalRound());
        assertFalse(game.isFinalRound());
    }

    @Test
    void isGameOver_afterAllTurnsSpent() {
        Player p = game.getPlayers().get(0);
        List<Card> allRevealed = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            allRevealed.add(new Card(CardValue.UN, true, i));
        }
        p.setCards(allRevealed);

        game.checkAndEnterFinalRound();
        assertFalse(game.isGameOver()); // still 1 remaining turn (players.size() - 1)

        game.decrementFinalRoundTurns();
        assertTrue(game.isGameOver());
    }

    @Test
    void nextPlayer_wrapsAround() {
        game.setIndexActualPlayer(1);
        game.nextPlayer();
        assertEquals(game.getPlayers().get(0), game.getActualPlayer());
    }

    // ─── Reshuffle ───────────────────────────────────────────────────

    @Test
    void pickEmpty_reshufflesWhenDeckIsEmpty() {
        // Exhaust the deck
        while (game.getPickSize() > 0) {
            game.addToDiscard(game.pickCard());
        }
        assertEquals(0, game.getPickSize());

        game.pickEmpty();
        assertTrue(game.getPickSize() > 0, "Deck should be refilled from discard");
    }

    // ─── hasPlayerReached100Points ───────────────────────────────────

    @Test
    void hasPlayerReached100Points_falseAtStart() {
        assertFalse(game.hasPlayerReached100Points());
    }

    @Test
    void hasPlayerReached100Points_trueAfterAddingScore() {
        game.getPlayers().get(0).addScore(100);
        assertTrue(game.hasPlayerReached100Points());
    }

    // ─── revealAllCards ──────────────────────────────────────────────

    @Test
    void revealAllCards_flipsEverything() {
        game.revealAllCards();
        for (Player p : game.getPlayers()) {
            for (Card c : p.getCartes()) {
                assertTrue(c.faceVisible());
            }
        }
    }
}
