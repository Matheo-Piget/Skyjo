package org.App.view.screens;

import java.util.List;
import java.util.Map;

import org.App.model.game.Card;
import org.App.model.player.Player;
import org.App.view.components.CardView;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public interface GameViewInterface {
    void showPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard);

    void showFinalRanking(Map<Player, Integer> ranking);

    void showRanking(Map<Player, Integer> ranking);

    void showEndGame();

    void showMessageBox(String message);

    void distributeCardsWithAnimation(List<Player> players, List<CardView> cardViews, Runnable onComplete);

    void fadeInGameplayElements(Node node, Runnable onFinished);

    void setupBoardViews(List<Player> players);

    Scene getScene();

    Pane getRootPane();

    void show();

    List<CardView> getAllCardViews();

    void firstShowPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard);

    CardView getCardViewByIndex(int index);

    CardView findCardViewByCard(List<CardView> cardViews, Card card);

    void clearAll();

    /** Shows a non-intrusive toast message (fades in/out without clearing the board). */
    default void showToast(String message) {
        showMessageBox(message); // fallback for online view
    }

    /** Updates the status bar with a game phase hint (e.g. "Piochez une carte"). */
    default void showStatusMessage(String message) {}

    /** Shows a brief turn announcement animation ("Tour de [Player]"). */
    default void showTurnAnnouncement(String playerName) {}
}
