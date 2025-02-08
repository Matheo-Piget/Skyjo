package org.App.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.App.model.AIPlayer;
import org.App.model.Card;
import org.App.model.Player;
import org.App.model.SkyjoGame;
import org.App.view.CardView;
import org.App.view.GameView;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

public final class GameController {
    private static GameController instance;
    private final SkyjoGame game;
    private final GameView view;
    private Card pickedCard;
    private boolean hasDiscard;
    private int count_reveal = 0;
    private CardView pickedCardView;

    public GameController(GameView view, List<Player> players) {
        this.view = view;
        this.game = new SkyjoGame(players);
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    public void startGame() {
        game.startGame();

        // Step 1: Create CardView instances without associating them with BoardView
        List<CardView> cardViews = createCardViews();

        // Step 2: Distribute Cards with Animation
        view.distributeCardsWithAnimation(game.getPlayers(), cardViews, () -> {
            // Add a small delay before fading in the gameplay elements
            PauseTransition delay = new PauseTransition(Duration.seconds(0.5));
            delay.setOnFinished(event -> {
                // Step 3: After delay, fade in the gameplay elements
                view.fadeInGameplayElements(view.getRootPane(), () -> {
                    // Step 4: Setup the board and update the view
                    view.setupBoardViews(game.getPlayers());
                    updateView(); // Ensure UI is properly refreshed
                });
            });
            delay.play();
        });

        game.revealInitialCards();

        if (game.getActualPlayer() instanceof AIPlayer aIPlayer) {
            aIPlayer.playTurn(game);
            endTurn();
        }
    }

    private List<CardView> createCardViews() {
        List<CardView> cardViews = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            for (int i = 0; i < player.getCartes().size(); i++) {
                cardViews.add(new CardView(player.getCartes().get(i), i));
            }
        }
        return cardViews;
    }

    public void handlePickClick() {
        pickedCard = game.pickCard();
        pickedCard = pickedCard.retourner();
        if (pickedCard != null) {
            System.out.println(game.getActualPlayer().getName() + " a pioché " + pickedCard.valeur());
            // Create a CardView for the picked card
            pickedCardView = new CardView(pickedCard, -1);
            view.getRootPane().getChildren().add(pickedCardView);

            // Set up mouse movement tracking
            view.getScene().setOnMouseMoved(event -> {
                if (pickedCardView != null) {
                    pickedCardView.setLayoutX(event.getX() - pickedCardView.getWidth());
                    pickedCardView.setLayoutY(event.getY() - pickedCardView.getHeight());
                }
            });
        }
    }

    public void updateView() {
        view.showPlaying(game.getPlayers(), game.getActualPlayer().getName(), game.getPick().size(),
                game.getTopDiscard());
    }

    public void handleDiscardClick() {
        if (pickedCard != null) {
            game.addToDiscard(pickedCard);
            view.getRootPane().getChildren().remove(pickedCardView); // Remove the card view
            pickedCard = null;
            pickedCardView = null; // Reset the picked card view
            hasDiscard = true;
        } else {
            pickedCard = game.pickDiscard();
            pickedCard = pickedCard.retourner();
            pickedCardView = new CardView(pickedCard, -1);
            view.getRootPane().getChildren().add(pickedCardView);

            // Set up mouse movement tracking
            view.getScene().setOnMouseMoved(event -> {
                if (pickedCardView != null) {
                    pickedCardView.setLayoutX(event.getX() - pickedCardView.getWidth());
                    pickedCardView.setLayoutY(event.getY() - pickedCardView.getHeight());
                }
            });
            if (pickedCard == null) {
                view.showMessageBox("La défausse est vide !");
            }
        }
    }

    public void handleCardClick(CardView cardView) {
        if (pickedCard != null) {
            game.exchangeOrRevealCard(game.getActualPlayer(), pickedCard, cardView.getIndex());
            view.getRootPane().getChildren().remove(pickedCardView); // Remove the card view
            pickedCard = null;
            pickedCardView = null; // Reset the picked card view
            endTurn();
        }
        if (hasDiscard && count_reveal < 1) {
            game.revealCard(game.getActualPlayer(), cardView.getIndex());
            updateView();
            count_reveal++;
        }

        if (count_reveal == 1) {
            count_reveal = 0;
            hasDiscard = false;
            endTurn();
        }
    }

    private void endTurn() {
        game.checkColumns();
        if (game.isFinished()) {
            game.revealAllCards();
            Map<Player, Integer> ranking = game.getRanking();
            view.showRanking(ranking);
        } else {
            if (game.getActualPlayer() instanceof AIPlayer aIPlayer) {
                aIPlayer.playTurn(game);
                endTurn();
            } else {
                game.nextPlayer();
                updateView();
            }
            updateView();
        }
    }
}