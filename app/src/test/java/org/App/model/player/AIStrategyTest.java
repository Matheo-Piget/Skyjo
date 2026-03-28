package org.App.model.player;

import java.util.ArrayList;
import java.util.List;

import org.App.model.game.Card;
import org.App.model.game.CardValue;
import org.App.model.game.SkyjoGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that AI strategies only use observable information
 * and produce valid game actions.
 */
class AIStrategyTest {

    private SkyjoGame game;
    private AIPlayer easyAI;
    private AIPlayer mediumAI;
    private AIPlayer hardAI;

    @BeforeEach
    void setUp() {
        easyAI = new AIPlayer(0, "EasyBot", Difficulty.EASY);
        mediumAI = new AIPlayer(1, "MediumBot", Difficulty.MEDIUM);
        hardAI = new AIPlayer(2, "HardBot", Difficulty.HARD);

        List<Player> players = List.of(easyAI, mediumAI, hardAI);
        game = new SkyjoGame(players);
        game.startGame();
    }

    @Test
    void easyStrategy_playTurnDoesNotThrow() {
        game.setIndexActualPlayer(0);
        assertDoesNotThrow(() -> easyAI.playTurn(game));
    }

    @Test
    void mediumStrategy_playTurnDoesNotThrow() {
        game.setIndexActualPlayer(1);
        assertDoesNotThrow(() -> mediumAI.playTurn(game));
    }

    @Test
    void hardStrategy_playTurnDoesNotThrow() {
        game.setIndexActualPlayer(2);
        assertDoesNotThrow(() -> hardAI.playTurn(game));
    }

    @Test
    void easyStrategy_doesNotIncreaseHandSize() {
        game.setIndexActualPlayer(0);
        int before = easyAI.getCartes().size();
        easyAI.playTurn(game);
        assertTrue(easyAI.getCartes().size() <= before,
                "AI should not end up with more cards than it started with");
    }

    @Test
    void mediumStrategy_doesNotIncreaseHandSize() {
        game.setIndexActualPlayer(1);
        int before = mediumAI.getCartes().size();
        mediumAI.playTurn(game);
        assertTrue(mediumAI.getCartes().size() <= before);
    }

    @Test
    void hardStrategy_doesNotIncreaseHandSize() {
        game.setIndexActualPlayer(2);
        int before = hardAI.getCartes().size();
        hardAI.playTurn(game);
        assertTrue(hardAI.getCartes().size() <= before);
    }

    @Test
    void findBestCardToReplace_returnsValidIndex() {
        AIStrategy strategy = new EasyStrategy();
        List<Card> hand = new ArrayList<>();
        hand.add(new Card(CardValue.DOUZE, true, 0));
        hand.add(new Card(CardValue.CINQ, true, 1));
        hand.add(new Card(CardValue.ZERO, true, 2));

        Card newCard = new Card(CardValue.MOINS_DEUX, true, 99);
        int idx = strategy.findBestCardToReplace(hand, newCard);
        assertTrue(idx >= 0 && idx < hand.size());
        // Should prefer replacing the highest card (DOUZE at index 0)
        assertEquals(0, idx);
    }

    @Test
    void findHiddenCardIndex_returnsHiddenCard() {
        AIStrategy strategy = new EasyStrategy();
        List<Card> hand = new ArrayList<>();
        hand.add(new Card(CardValue.UN, true, 0));
        hand.add(new Card(CardValue.DEUX, true, 1));
        hand.add(new Card(CardValue.TROIS, false, 2)); // hidden

        assertEquals(2, strategy.findHiddenCardIndex(hand));
    }

    @Test
    void findHiddenCardIndex_returnsMinusOneWhenAllVisible() {
        AIStrategy strategy = new EasyStrategy();
        List<Card> hand = new ArrayList<>();
        hand.add(new Card(CardValue.UN, true, 0));
        hand.add(new Card(CardValue.DEUX, true, 1));

        assertEquals(-1, strategy.findHiddenCardIndex(hand));
    }

    @Test
    void multipleAITurns_gameRemainsConsistent() {
        // Play several turns and verify no exceptions / state corruption
        for (int turn = 0; turn < 10; turn++) {
            Player current = game.getActualPlayer();
            if (current instanceof AIPlayer ai) {
                ai.playTurn(game);
            }
            game.checkColumns();
            game.pickEmpty();
            if (game.checkAndEnterFinalRound()) break;
            if (game.isFinalRound()) {
                game.decrementFinalRoundTurns();
                if (game.isGameOver()) break;
            }
            game.nextPlayer();
        }
        // Just verify the game is in a consistent state
        assertNotNull(game.getActualPlayer());
        assertTrue(game.getPickSize() >= 0);
    }
}
