package org.App.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;
import org.App.model.player.AIPlayer;
import org.App.model.player.Player;
import org.App.view.components.CardView;
import org.App.view.screens.GameViewInterface;
import org.App.view.utils.OptionsManager;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Game controller with smooth card following, turn announcements,
 * phase indicators, and polished UX.
 */
public final class GameController {
    private static GameController instance;
    private final SkyjoGame game;
    private final GameViewInterface view;
    private CardView pickedCardView;

    // Smooth mouse following
    private double mouseTargetX, mouseTargetY;
    private AnimationTimer cardFollower;

    // Card dimensions for centering
    private static final double CARD_HALF_W = 25;
    private static final double CARD_HALF_H = 36;

    public GameController(GameViewInterface view, List<Player> players) {
        this.view = view;
        this.game = new SkyjoGame(players);
        instance = this;
    }

    public static GameController getInstance() {
        return instance;
    }

    public void startGame() {
        game.startGame();
        List<CardView> cardViews = createCardViews();
        view.distributeCardsWithAnimation(game.getPlayers(), cardViews, this::initializeGameBoard);
    }

    private void initializeGameBoard() {
        double speed = OptionsManager.getAnimationSpeed();
        addDelay(0.4 * speed, () -> {
            view.fadeInGameplayElements(view.getRootPane(), () -> {
                view.firstShowPlaying(game.getPlayers(), game.getActualPlayer().getName(),
                        game.getPick().size(), game.getTopDiscard());
                addDelay(0.8 * speed, () -> {
                    game.revealInitialCards();
                    animateInitialFlips(() -> {
                        updateView();
                        game.setIndexActualPlayer(game.getStartingPlayerIndex());
                        updateViewWithDelay(1.5 * speed);
                        addDelay(1.5 * speed, this::onNewTurn);
                    });
                });
            });
        });
    }

    /** Called whenever it becomes a new player's turn. Shows announcement + status. */
    private void onNewTurn() {
        Player current = game.getActualPlayer();
        view.showTurnAnnouncement(current.getName());
        updatePhaseHint();
        handleAITurn();
    }

    /** Updates the status bar with the correct phase hint. */
    private void updatePhaseHint() {
        Player current = game.getActualPlayer();
        if (current instanceof AIPlayer) {
            view.showStatusMessage(current.getName() + " joue...");
        } else if (game.getPickedCard() != null) {
            view.showStatusMessage("Echangez avec une de vos cartes, ou cliquez sur la defausse pour defausser");
        } else if (game.hasDiscard() && game.getCountReveal() < 1) {
            view.showStatusMessage("Revelez une de vos cartes face cachee");
        } else {
            view.showStatusMessage("Piochez une carte ou prenez la defausse  -  Tour de " + current.getName());
        }
    }

    private List<CardView> createCardViews() {
        List<CardView> cardViews = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            for (int i = 0; i < player.getCartes().size(); i++) {
                cardViews.add(new CardView(player.getCartes().get(i), i, player.getId()));
            }
        }
        return cardViews;
    }

    // ==================== Pick / Discard / Card click ====================

    public void handlePickClick() {
        if (game.getPickedCard() != null) return;

        Card pickedCard = game.pickCard();
        if (pickedCard == null) return;

        if (!pickedCard.faceVisible()) {
            pickedCard = pickedCard.retourner();
        }

        game.setPickedCard(pickedCard);
        startHoldingCard(pickedCard);
        updatePhaseHint();
    }

    public void handleDiscardClick() {
        if (game.getDiscard().isEmpty()) {
            view.showToast("La defausse est vide !");
            return;
        }
        if (game.getPickedCard() != null) {
            // Discard the picked card
            game.addToDiscard(game.getPickedCard());
            stopHoldingCard();
            game.setPickedCard(null);
            game.setHasDiscard(true);
            updatePhaseHint();
        } else {
            // Pick from discard
            Card pickedCard = game.pickDiscard();
            game.setPickedCard(pickedCard);
            if (pickedCard != null) {
                pickedCard = pickedCard.retourner();
                startHoldingCard(pickedCard);
                updatePhaseHint();
            } else {
                view.showToast("La defausse est vide !");
            }
        }
    }

    public void handleCardClick(CardView cardView) {
        if (game.getPickedCard() != null) {
            game.exchangeOrRevealCard(game.getActualPlayer(), game.getPickedCard(), cardView.getIndex());
            stopHoldingCard();
            game.setPickedCard(null);
            updateView();
            endTurn();
        } else if (game.hasDiscard() && game.getCountReveal() < 1) {
            cardView.flipCard(() -> {
                game.revealCard(game.getActualPlayer(), cardView.getIndex());
                updateView();
                game.incrementCountReveal();
                if (game.getCountReveal() == 1) {
                    resetRevealState();
                    endTurn();
                }
            });
        }
    }

    // ==================== Smooth card holding ====================

    /**
     * Starts holding a card: adds it to the overlay centered on screen,
     * sets up smooth mouse following via AnimationTimer, changes cursor.
     */
    private void startHoldingCard(Card card) {
        pickedCardView = new CardView(card, -1, -1);
        pickedCardView.setMouseTransparent(true);

        // Elevated shadow for floating card
        DropShadow floatShadow = new DropShadow(20, 0, 8, Color.color(0, 0, 0, 0.45));
        pickedCardView.setEffect(floatShadow);

        // Slight tilt for "picked up" feel
        pickedCardView.setRotate(-4);
        pickedCardView.setScaleX(1.15);
        pickedCardView.setScaleY(1.15);

        // Start at center of scene (avoids top-left flash)
        double centerX = view.getScene().getWidth() / 2 - CARD_HALF_W;
        double centerY = view.getScene().getHeight() / 2 - CARD_HALF_H;
        pickedCardView.setLayoutX(centerX);
        pickedCardView.setLayoutY(centerY);
        mouseTargetX = centerX;
        mouseTargetY = centerY;

        view.getRootPane().getChildren().add(pickedCardView);

        // Track mouse target position
        view.getScene().setOnMouseMoved(event -> {
            mouseTargetX = event.getX() - CARD_HALF_W;
            mouseTargetY = event.getY() - CARD_HALF_H;
        });

        // Smooth interpolated following (lerp)
        cardFollower = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (pickedCardView != null) {
                    double currentX = pickedCardView.getLayoutX();
                    double currentY = pickedCardView.getLayoutY();
                    double lerp = 0.28;
                    pickedCardView.setLayoutX(currentX + (mouseTargetX - currentX) * lerp);
                    pickedCardView.setLayoutY(currentY + (mouseTargetY - currentY) * lerp);
                }
            }
        };
        cardFollower.start();

        // Change cursor to closed hand
        view.getScene().setCursor(Cursor.CLOSED_HAND);
    }

    /** Stops holding the card: removes from overlay, stops animation, restores cursor. */
    private void stopHoldingCard() {
        if (cardFollower != null) {
            cardFollower.stop();
            cardFollower = null;
        }
        if (pickedCardView != null) {
            view.getRootPane().getChildren().remove(pickedCardView);
            pickedCardView = null;
        }
        view.getScene().setOnMouseMoved(null);
        view.getScene().setCursor(Cursor.DEFAULT);
    }

    private void resetRevealState() {
        game.resetCountReveal();
        game.setHasDiscard(false);
    }

    // ==================== Turn management ====================

    public void updateView() {
        view.showPlaying(game.getPlayers(), game.getActualPlayer().getName(), game.getPick().size(),
                game.getTopDiscard());
    }

    private void endTurn() {
        game.checkColumns();
        game.pickEmpty();
        updateView();
        view.showStatusMessage("");

        boolean justEnteredFinalRound = game.checkAndEnterFinalRound();
        if (justEnteredFinalRound) {
            view.showToast("Dernier tour !");
        }

        if (!justEnteredFinalRound && game.isGameOver()) {
            concludeGame();
        } else {
            double speed = OptionsManager.getAnimationSpeed();
            addDelay(0.25 * speed, () -> {
                game.nextPlayer();
                updateView();
                onNewTurn();
            });
        }
    }

    private void concludeGame() {
        game.revealAllCards();
        updateView();
        Map<Player, Integer> ranking = game.getRanking();
        ranking.forEach((player, score) -> player.addScore(score));

        double speed = OptionsManager.getAnimationSpeed();
        view.showStatusMessage("Fin de la manche - Calcul des scores...");

        addDelay(2.5 * speed, () -> {
            if (game.hasPlayerReached100Points()) {
                Map<Player, Integer> finalRanking = game.getFinalRanking();
                view.showFinalRanking(finalRanking);
            } else {
                view.showRanking(ranking);
            }
        });
    }

    public void restartRoundWithDelay(double seconds) {
        addDelay(seconds, () -> {
            view.clearAll();
            startGame();
        });
    }

    private void handleAITurn() {
        if (game.getActualPlayer() instanceof AIPlayer aiPlayer) {
            double speed = OptionsManager.getAnimationSpeed();
            addDelay(0.6 * speed, () -> {
                aiPlayer.playTurn(game);
                updateView();
                endTurn();
            });
        }
    }

    private void updateViewWithDelay(double seconds) {
        addDelay(seconds, this::updateView);
    }

    private void addDelay(double seconds, Runnable action) {
        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(event -> action.run());
        delay.play();
    }

    // ==================== Initial flip animation ====================

    private void animateInitialFlips(Runnable onFinished) {
        List<CardView> allCardViews = view.getAllCardViews();
        List<CardView> toFlip = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            for (int i = 0; i < player.getCartes().size(); i++) {
                Card card = player.getCartes().get(i);
                if (card.faceVisible()) {
                    for (CardView cardView : allCardViews) {
                        if (cardView.getCardId() == card.id() && cardView.getIndex() == i
                                && cardView.getPlayerId() == player.getId()) {
                            toFlip.add(cardView);
                            break;
                        }
                    }
                }
            }
        }
        animateCardFlipsSequentially(toFlip, onFinished);
    }

    private void animateCardFlipsSequentially(List<CardView> cardViews, Runnable onFinished) {
        if (cardViews.isEmpty()) {
            onFinished.run();
            return;
        }
        CardView cv = cardViews.remove(0);
        cv.flipCard(() -> {
            double speed = OptionsManager.getAnimationSpeed();
            PauseTransition delay = new PauseTransition(Duration.seconds(0.08 * speed));
            delay.setOnFinished(event -> animateCardFlipsSequentially(cardViews, onFinished));
            delay.play();
        });
    }
}
