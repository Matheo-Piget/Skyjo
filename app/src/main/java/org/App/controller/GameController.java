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
import org.App.view.GameViewInterface;

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
                view.setupBoardViews(game.getPlayers());
                game.revealInitialCards();
                updateViewWithDelay(1);
                handleAITurn();
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
                cardViews.add(new CardView(player.getCartes().get(i), i));
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
        Card pickedCard = game.pickCard().retourner();
        game.setPickedCard(pickedCard);
        if (pickedCard != null) {
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
                pickedCardView = new CardView(pickedCard, -1);
                view.getRootPane().getChildren().add(pickedCardView);

                // Set up mouse movement tracking
                view.getScene().setOnMouseMoved(event -> {
                    if (pickedCardView != null) {
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
            cardView.setValue(cardView.getValue().retourner()); // Retourne la carte
            view.getRootPane().getChildren().remove(pickedCardView); // Supprime la carte piochée
            resetPickState();
            endTurn();
        }
        if (game.hasDiscard() && game.getCountReveal() < 1) {
            game.revealCard(game.getActualPlayer(), cardView.getIndex());
            cardView.setValue(cardView.getValue().retourner()); // Retourne la carte
            updateView();
            game.incrementCountReveal();
        }

        if (game.getCountReveal() == 1) {
            resetRevealState();
            endTurn();
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
        if (game.isFinished()) {
            concludeGame();
        } else {
            game.nextPlayer();
            updateView();
            handleAITurn();
        }
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
        view.showRanking(ranking);

        if (game.hasPlayerReached100Points()) {
            Map<Player, Integer> finalRanking = game.getFinalRanking();
            finalRanking.forEach((player, score) -> player.addScore(score));
            view.showFinalRanking(finalRanking);
        } else {
            restartRoundWithDelay(15);
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
    private void restartRoundWithDelay(double seconds) {
        addDelay(seconds, () -> {
            game.startGame();
            view.setupBoardViews(game.getPlayers());
            game.revealInitialCards();
            updateView();
            handleAITurn();
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
}