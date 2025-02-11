package org.App.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.App.model.AIPlayer;
import org.App.model.Card;
import org.App.model.Player;
import org.App.model.SkyjoGame;
import org.App.view.CardView;
import org.App.view.GameView;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

/**
 * The GameController class is responsible for handling the game logic and
 * updating the view based on the game state. It acts as the intermediary between
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
    private final GameView view;
    private Card pickedCard;
    private boolean hasDiscard;
    private int count_reveal = 0;
    private CardView pickedCardView;

    /**
     * Constructs a new GameController with the specified view and players.
     * 
     * @param view     The {@link GameView} instance representing the game's UI.
     * @param players  A list of {@link Player} instances participating in the game.
     * 
     * @see GameView
     * @see Player
     */
    public GameController(GameView view, List<Player> players) {
        this.view = view;
        this.game = new SkyjoGame(players);
        instance = this;
    }

    /**
     * Returns the singleton instance of the GameController.
     * 
     * @return The singleton instance of {@link GameController}.
     */
    public static GameController getInstance() {
        return instance;
    }

    /**
     * Starts the game by initializing the game state, distributing cards with
     * animations, and setting up the board. If the current player is an AI,
     * it automatically plays its turn after a short delay.
     * 
     * @see SkyjoGame#startGame()
     * @see GameView#distributeCardsWithAnimation(List, List, Runnable)
     * @see GameView#fadeInGameplayElements(javafx.scene.layout.Pane, Runnable)
     * @see AIPlayer#playTurn(SkyjoGame)
     */
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

                    // Step 5: Start the game logic after the animation is complete
                    game.revealInitialCards();

                    PauseTransition pause = new PauseTransition(Duration.seconds(1));
                    pause.setOnFinished(e -> {
                        updateView();
                    });
                    pause.play();

                    // Step 6: Check if the current player is an AI and play their turn
                    if (game.getActualPlayer() instanceof AIPlayer aIPlayer) {
                        PauseTransition aiDelay = new PauseTransition(Duration.seconds(1)); // Délai de 1 seconde
                        aiDelay.setOnFinished(aiEvent -> {
                            aIPlayer.playTurn(game);
                            updateView();
                            endTurn(); // Passer au tour suivant après que l'IA a joué
                        });
                        aiDelay.play();
                    }
                });
            });
            delay.play();
        });
    }

    /**
     * Creates a list of {@link CardView} instances for all cards held by the players.
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
        if (pickedCard != null) {
            game.exchangeOrRevealCard(game.getActualPlayer(), pickedCard, cardView.getIndex());
            cardView.setValue(cardView.getValue().retourner()); // Retourne la carte
            view.getRootPane().getChildren().remove(pickedCardView); // Supprime la carte piochée
            pickedCard = null;
            pickedCardView = null;
            endTurn();
        }
        if (hasDiscard && count_reveal < 1) {
            game.revealCard(game.getActualPlayer(), cardView.getIndex());
            cardView.setValue(cardView.getValue().retourner()); // Retourne la carte
            updateView();
            count_reveal++;
        }

        if (count_reveal == 1) {
            count_reveal = 0;
            hasDiscard = false;
            endTurn();
        }
    }

    /**
     * Ends the current player's turn and checks if the game is finished. If the game
     * is finished, it displays the ranking. Otherwise, it proceeds to the next
     * player's turn.
     * 
     * @see SkyjoGame#checkColumns()
     * @see SkyjoGame#isFinished()
     * @see SkyjoGame#revealAllCards()
     * @see SkyjoGame#getRanking()
     * @see GameView#showRanking(Map)
     * @see SkyjoGame#nextPlayer()
     */
    private void endTurn() {
        game.checkColumns();
        if (game.isFinished()) {
            game.revealAllCards();
            Map<Player, Integer> ranking = game.getRanking();
            ranking = ranking.entrySet()
                .stream()
                .sorted(Map.Entry.<Player, Integer>comparingByValue())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
            view.showRanking(ranking);
        } else {
            updateView();
            game.nextPlayer(); // Passer au joueur suivant
            updateView();

            // Vérifier si le prochain joueur est une IA
            if (game.getActualPlayer() instanceof AIPlayer aIPlayer) {
                // Ajouter un délai avant que l'IA ne joue son tour
                PauseTransition delay = new PauseTransition(Duration.seconds(0.1)); // Délai de 1 seconde
                delay.setOnFinished(event -> {
                    aIPlayer.playTurn(game);
                    updateView();
                    endTurn(); // Passer au tour suivant après que l'IA a joué
                });
                delay.play();
            }
            // Si c'est un joueur humain, ne rien faire (attendre l'interaction utilisateur)
        }
    }
}