package org.App.view;

import java.util.List;
import java.util.Map;

import org.App.model.Card;
import org.App.model.Player;

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
}