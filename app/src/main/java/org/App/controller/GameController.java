package org.App.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.App.model.game.Card;
import org.App.model.game.SkyjoGame;
import org.App.model.player.AIPlayer;
import org.App.model.player.Player;
import org.App.view.components.CardView;
import org.App.view.screens.GameView;
import org.App.view.screens.GameViewInterface;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * The GameController class is responsible for handling the game logic and
 * updating the view based on the game state. It acts as the intermediary
 * between
 * the model ({@link SkyjoGame}) and the view ({@link GameView}).
 * 
 * <p>
 * This class follows the Singleton design pattern to ensure only one instance
 * exists during the application's lifecycle.
 * </p>
 * 
 * @see SkyjoGame
 * @see GameView
 * @see CardView
 * @see Player
 * @see AIPlayer
 * 
 * @author Mathéo Piget
 * @version 1.0
 */
public final class GameController {
    private static GameController instance;
    private final SkyjoGame game;
    private final GameViewInterface view;
    private CardView pickedCardView;

    /**
     * Constructs a new GameController with the specified view and players.
     * 
     * @param view    The {@link GameView} instance representing the game's UI.
     * @param players A list of {@link Player} instances participating in the game.
     * 
     * @see GameView
     * @see Player
     */
    public GameController(GameViewInterface view, List<Player> players) {
        this.view = view;
        this.game = new SkyjoGame(players);
        instance = this;
    }

    /**
     * Returns the singleton instance of the GameController.
     * 
     * @return The singleton instance of the GameController.
     */
    public static GameController getInstance() {
        return instance;
    }

    /**
     * Starts the game by distributing the cards to the players and initializing the
     */
    public void startGame() {
        game.startGame();
        List<CardView> cardViews = createCardViews();
        view.distributeCardsWithAnimation(game.getPlayers(), cardViews, this::initializeGameBoard);
    }

    /**
     * Initializes the game board by fading in the gameplay elements and revealing
     * 
     */
    private void initializeGameBoard() {
        addDelay(0.5, () -> {
            view.fadeInGameplayElements(view.getRootPane(), () -> {
                view.firstShowPlaying(game.getPlayers(), game.getActualPlayer().getName(),
                        game.getPick().size(), game.getTopDiscard());
                addDelay(1, () -> {
                    // Update the model: reveal the designated cards
                    game.revealInitialCards();
                    // Animate the flips for the corresponding CardViews
                    animateInitialFlips(() -> {
                        // After the flips are complete, update the view and proceed normally.
                        updateView();
                        game.setIndexActualPlayer(game.getStartingPlayerIndex());
                        updateViewWithDelay(2);
                        handleAITurn();
                    });
                });
            });
        });
    }

    /**
     * Creates a list of {@link CardView} instances for all cards held by the
     * players.
     * 
     * @return A list of {@link CardView} instances.
     * 
     * @see CardView
     * @see Player#getCartes()
     */
    private List<CardView> createCardViews() {
        List<CardView> cardViews = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            for (int i = 0; i < player.getCartes().size(); i++) {
                cardViews.add(new CardView(player.getCartes().get(i), i, player.getId()));
            }
        }
        return cardViews;
    }

    /**
     * Handles the action when the player clicks the "Pick" button. It picks a card
     * from the deck, flips it, and displays it on the screen.
     * 
     * @see SkyjoGame#pickCard()
     * @see Card#retourner()
     */
    public void handlePickClick() {

        if (game.getPickedCard() != null) {
            return;
        }

        Card pickedCard = game.pickCard();

        if (!pickedCard.faceVisible()) {
            pickedCard = pickedCard.retourner();
        }

        game.setPickedCard(pickedCard);
        if (pickedCard != null) {
            pickedCardView = new CardView(pickedCard, -1, -1);
            view.getRootPane().getChildren().add(pickedCardView);

            // Set up mouse movement tracking
            view.getScene().setOnMouseMoved(event -> {
                if (pickedCardView != null) {
                    pickedCardView.toFront();
                    pickedCardView.setLayoutX(event.getX() - pickedCardView.getWidth());
                    pickedCardView.setLayoutY(event.getY() - pickedCardView.getHeight());
                }
            });
        }
    }

    /**
     * Updates the game view to reflect the current state of the game.
     * 
     * @see GameView#showPlaying(List, String, int, Card)
     */
    public void updateView() {
        view.showPlaying(game.getPlayers(), game.getActualPlayer().getName(), game.getPick().size(),
                game.getTopDiscard());
    }

    /**
     * Handles the action when the player clicks the "Discard" button. It either
     * discards the currently picked card or picks a card from the discard pile.
     * 
     * @see SkyjoGame#addToDiscard(Card)
     * @see SkyjoGame#pickDiscard()
     * @see Card#retourner()
     */
    public void handleDiscardClick() {
        if (game.getDiscard().isEmpty()) {
            view.showMessageBox("La défausse est vide !");
            return;
        }
        if (game.getPickedCard() != null) {
            game.addToDiscard(game.getPickedCard());
            view.getRootPane().getChildren().remove(pickedCardView); // Remove the card view
            game.setPickedCard(null);
            pickedCardView = null; // Reset the picked card view
            game.setHasDiscard(true);
        } else {
            Card pickedCard = game.pickDiscard();
            game.setPickedCard(pickedCard);
            if (pickedCard != null) {
                pickedCard = pickedCard.retourner();
                pickedCardView = new CardView(pickedCard, -1, -1);
                view.getRootPane().getChildren().add(pickedCardView);

                // Set up mouse movement tracking
                view.getScene().setOnMouseMoved(event -> {
                    if (pickedCardView != null) {
                        pickedCardView.toFront();
                        pickedCardView.setLayoutX(event.getX() - pickedCardView.getWidth());
                        pickedCardView.setLayoutY(event.getY() - pickedCardView.getHeight());
                    }
                });
            } else {
                view.showMessageBox("La défausse est vide !");
            }
        }
    }

    /**
     * Handles the action when a card is clicked. It either exchanges the clicked
     * card with the picked card or reveals the clicked card, depending on the game
     * state.
     * 
     * @param cardView The {@link CardView} instance representing the clicked card.
     * 
     * @see SkyjoGame#exchangeOrRevealCard(Player, Card, int)
     * @see SkyjoGame#revealCard(Player, int)
     * @see Card#retourner()
     */
    public void handleCardClick(CardView cardView) {
        if (game.getPickedCard() != null) {
            game.exchangeOrRevealCard(game.getActualPlayer(), game.getPickedCard(), cardView.getIndex());
            view.getRootPane().getChildren().remove(pickedCardView);
            resetPickState();
            updateView(); // Mettre à jour la vue après l'échange
            addDelay(0.5, () -> {});
            endTurn();
        } else if (game.hasDiscard() && game.getCountReveal() < 1) {
            cardView.flipCard(() -> {
                game.revealCard(game.getActualPlayer(), cardView.getIndex());
                updateView(); // Mettre à jour la vue après avoir révélé la carte
                game.incrementCountReveal();
                if (game.getCountReveal() == 1) {
                    resetRevealState();
                    endTurn();
                }
            });
        }
    }

    /**
     * Handles the action when the player clicks the "End Turn" button. It ends the
     * player's turn and proceeds to the next player.
     * 
     * @see #endTurn()
     */
    private void resetPickState() {
        game.setPickedCard(null);
        pickedCardView = null;
    }

    /**
     * Handles the action when the player clicks the "Reveal" button. It reveals the
     * clicked card and ends the player's turn.
     * 
     * @see #resetRevealState()
     * @see #endTurn()
     */
    private void resetRevealState() {
        game.resetCountReveal();
        game.setHasDiscard(false);
    }

    /**
     * Ends the player's turn and proceeds to the next player. It checks if the game
     * is finished and either concludes the game or proceeds to the next turn.
     * 
     * @see SkyjoGame#checkColumns()
     * @see SkyjoGame#isFinished()
     * @see #concludeGame()
     * @see #updateView()
     * @see #handleAITurn()
     */
    private void endTurn() {
        game.checkColumns();
        if (game.getPick().isEmpty()) {
            game.pickEmpty();
        }

        if (game.isFinished()) {
            if (game.isFinalRound()) {
                concludeGame();
            } else {
                game.setFinalRound(true);
                addDelay(0.2, () -> {
                    game.nextPlayer();
                    updateView();
                    handleAITurn();
                });
            }
        } else {
            addDelay(0.2, () -> {
                game.nextPlayer();
                updateView();
                handleAITurn();
            });
        }
    }

    /**
     * Fades out the current player's view and executes the provided action when
     * finished.
     * 
     * @param onFinished The action to execute after
     *                   fading out the current player's view.
     */
    @SuppressWarnings("unused")
    private void fadeOutCurrentPlayer(Runnable onFinished) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), view.getRootPane());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> onFinished.run());
        fadeOut.play();
    }

    /**
     * Fades in the next player's view and executes the provided action when
     * finished.
     * 
     * @param onFinished The action to execute after fading in the next player's
     *                   view.
     */
    @SuppressWarnings("unused")
    private void fadeInNextPlayer(Runnable onFinished) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), view.getRootPane());
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setOnFinished(event -> onFinished.run());
        fadeIn.play();
    }

    /**
     * Concludes the game by revealing all cards and displaying the final ranking.
     * If no player has reached 100 points, it restarts the round after a short
     * delay.
     * 
     * @see SkyjoGame#revealAllCards()
     * @see SkyjoGame#getSortedRanking()
     * @see GameView#showRanking(Map)
     * @see SkyjoGame#hasPlayerReached100Points()
     * @see #restartRoundWithDelay(double)
     */
    private void concludeGame() {
        game.revealAllCards();
        Map<Player, Integer> ranking = game.getRanking();
        ranking.forEach((player, score) -> player.addScore(score));
        addDelay(2, () -> view.showRanking(ranking));

        if (game.hasPlayerReached100Points()) {
            Map<Player, Integer> finalRanking = game.getFinalRanking();
            finalRanking.forEach((player, score) -> player.addScore(score));
            view.showFinalRanking(finalRanking);
        }
    }

    /**
     * Restarts the round after a short delay.
     * 
     * @param seconds The delay in seconds.
     * 
     * @see #restartRoundWithDelay(double)
     * @see #addDelay(double, Runnable)
     */
    public void restartRoundWithDelay(double seconds) {
        addDelay(seconds, () -> {
            view.clearAll();
            startGame();
        });
    }

    /**
     * Handles the AI player's turn by playing the turn after a short delay.
     * 
     * @see AIPlayer#playTurn(SkyjoGame)
     * @see #addDelay(double, Runnable)
     */
    private void handleAITurn() {
        if (game.getActualPlayer() instanceof AIPlayer aIPlayer) {
            addDelay(0.1, () -> {
                aIPlayer.playTurn(game);
                updateView();
                endTurn();
            });
        }
    }

    /**
     * Updates the game view with a delay.
     * 
     * @param seconds The delay in seconds.
     * 
     * @see #updateView()
     * @see #addDelay(double, Runnable)
     */
    private void updateViewWithDelay(double seconds) {
        addDelay(seconds, this::updateView);
    }

    /**
     * Adds a delay before executing an action.
     * 
     * @param seconds The delay in seconds.
     * @param action  The action to execute.
     * 
     * @see PauseTransition
     */
    private void addDelay(double seconds, Runnable action) {
        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(event -> action.run());
        delay.play();
    }

    /**
     * Called after the distribution phase. Once the game model has updated the
     * cards
     * (via revealInitialCards), this method finds the CardViews that still show
     * their back
     * and animates a flip for each to reveal the correct card.
     * 
     * @param onFinished The action to execute after all flips are completed.
     */
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

    /**
     * Animates the flip for each CardView in the provided list sequentially.
     * 
     * @param cardViews  The list of CardViews to flip.
     * @param onFinished The action to execute after all flips are completed.
     */
    private void animateCardFlipsSequentially(List<CardView> cardViews, Runnable onFinished) {
        if (cardViews.isEmpty()) {
            onFinished.run();
            return;
        }
        CardView cv = cardViews.remove(0);
        cv.flipCard(() -> {
            PauseTransition delay = new PauseTransition(Duration.seconds(0.1));
            delay.setOnFinished(event -> animateCardFlipsSequentially(cardViews, onFinished));
            delay.play();
        });
    }
}