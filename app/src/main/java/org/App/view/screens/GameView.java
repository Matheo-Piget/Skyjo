package org.App.view.screens;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.App.App;
import org.App.controller.GameController;
import org.App.model.game.Card;
import org.App.model.player.Player;
import org.App.view.components.BoardView;
import org.App.view.components.CardView;
import org.App.view.components.DiscardView;
import org.App.view.components.PickView;
import org.App.view.utils.MusicManager;
import org.App.view.utils.OptionsManager;
import org.App.view.utils.SoundManager;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Represents the main game view for the Skyjo game. This class is responsible
 * for displaying the game interface, including the cards, players, and game
 * controls.
 *
 * <p>
 * The view supports animations for card distribution, player boards, and game
 * transitions. It also provides methods to display rankings, end-game messages,
 * and other UI elements.
 * </p>
 *
 * @see Card
 * @see Player
 * @see BoardView
 * @see CardView
 * @see DiscardView
 * @see PickView
 *
 * @author Mathéo Piget
 * @version 1.0
 */
public class GameView implements GameViewInterface {

    private final Stage stage;
    private final VBox cardsContainer;
    private final Scene scene;
    private final Pane rootPane;
    private final MusicManager musicManager;

    /**
     * Constructs a new GameView with the specified stage.
     *
     * @param stage The primary stage for the application.
     */
    public GameView(Stage stage) {
        this.stage = stage;
        rootPane = new Pane();
        stage.setTitle("Skyjo");
        stage.setFullScreen(false);
        stage.setMaximized(true);

        musicManager = new MusicManager("src/main/resources/musics/game_music.mp3");

        if (OptionsManager.getVolume() != 0) {
            musicManager.play();
        }

        rootPane.getStyleClass().add("root1");

        this.cardsContainer = new VBox(20);
        this.cardsContainer.setAlignment(Pos.CENTER);
        this.cardsContainer.getStyleClass().add("vbox");

        MenuBar menuBar = createMenuBar();
        menuBar.getStyleClass().add("menu-bar");

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new BorderPane(menuBar));
        borderPane.setCenter(rootPane);
        borderPane.setBottom(cardsContainer);

        this.scene = new Scene(borderPane, 1400, 900);
        if (OptionsManager.getTheme().equals("Sombre")) {
            scene.getStylesheets().add("file:src/main/resources/themes/menu.css");
        } else {
            scene.getStylesheets().add("file:src/main/resources/themes/menu_light.css");
        }

        cardsContainer.prefHeightProperty().bind(scene.heightProperty().subtract(100));
        cardsContainer.prefWidthProperty().bind(scene.widthProperty().subtract(100));
    }

    /**
     * Gets the scene associated with this view.
     *
     * @return The scene.
     */
    @Override
    public Scene getScene() {
        return scene;
    }

    /**
     * Displays the game view.
     */
    @Override
    public void show() {
        stage.show();
    }

    /**
     * Creates the menu bar for the game.
     *
     * @return The menu bar.
     */
    private MenuBar createMenuBar() {
        Menu gameMenu = new Menu();
        Label menuIcon = new Label("☰");
        gameMenu.setGraphic(menuIcon);

        MenuItem startNewGame = new MenuItem("Start New Game");
        MenuItem toggleMusic = new MenuItem("Toggle Music");
        MenuItem increaseVolume = new MenuItem("Increase Volume");
        MenuItem decreaseVolume = new MenuItem("Decrease Volume");
        MenuItem exitGame = new MenuItem("Exit");

        // Appliquer des styles aux éléments du menu
        startNewGame.getStyleClass().add("menu-item");
        toggleMusic.getStyleClass().add("menu-item");
        increaseVolume.getStyleClass().add("menu-item");
        decreaseVolume.getStyleClass().add("menu-item");
        exitGame.getStyleClass().add("menu-item");

        // Gestion des événements
        startNewGame.setOnAction(event -> {
            SoundManager.dispose();
            musicManager.stop();
            scene.getStylesheets().clear();
            stage.close();
            App.getINSTANCE().restart();
        });

        toggleMusic.setOnAction(event -> {
            if (musicManager != null) {
                if (musicManager.isPlaying()) {
                    musicManager.pause();
                    toggleMusic.setText("Resume Music");
                } else {
                    musicManager.play();
                    toggleMusic.setText("Pause Music");
                }
            }
        });

        increaseVolume.setOnAction(event -> {
            if (musicManager != null) {
                double newVolume = Math.min(1.0, musicManager.getVolume() + 0.1);
                musicManager.setVolume(newVolume);
            }
        });

        decreaseVolume.setOnAction(event -> {
            if (musicManager != null) {
                double newVolume = Math.max(0.0, musicManager.getVolume() - 0.1);
                musicManager.setVolume(newVolume);
            }
        });

        exitGame.setOnAction(event -> stage.close());

        // Ajouter les éléments au menu
        gameMenu.getItems().addAll(startNewGame, toggleMusic, increaseVolume, decreaseVolume, exitGame);

        // Créer la barre de menu
        MenuBar menuBar = new MenuBar();
        menuBar.getStyleClass().add("menu-bar");
        menuBar.getMenus().add(gameMenu);

        return menuBar;
    }

    /**
     * Displays the game view with the specified players, current player,
     * remaining cards, and top discard card.
     *
     * @param players           The list of players.
     * @param currentPlayerName The name of the current player.
     * @param remainingCards    The number of remaining cards.
     * @param topDiscardCard    The top discard card.
     */
    @Override
    public void showPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard) {
        clearPreviousCards();
        cardsContainer.getChildren().clear();

        VBox centerPlayerContainer = createPlayerBoard(getPlayerByName(players, currentPlayerName), true);
        HBox topPlayersContainer = createSidePlayersContainer(players, currentPlayerName, true);
        HBox bottomPlayersContainer = createSidePlayersContainer(players, currentPlayerName, false);

        HBox centerArea = createCenterArea(remainingCards, topDiscardCard, centerPlayerContainer);
        VBox mainContainer = createMainContainer(topPlayersContainer, centerArea, bottomPlayersContainer);

        cardsContainer.getChildren().add(mainContainer);

        stage.show();

        System.out.println(getAllCardViews().size());
    }

    /**
     * Clears the previous cards from the root pane.
     */
    private void clearPreviousCards() {
        rootPane.getChildren().removeIf(node -> node instanceof CardView);
    }

    /**
     * Creates a container for side players based on the current player.
     *
     * @param players           The list of players.
     * @param currentPlayerName The name of the current player.
     * @param isTop             Whether the container is for top players.
     * @return The HBox container for side players.
     */
    private HBox createSidePlayersContainer(List<Player> players, String currentPlayerName, boolean isTop) {
        HBox container = new HBox(20);
        container.setAlignment(Pos.CENTER);

        List<VBox> sidePlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.getName().equals(currentPlayerName)) {
                sidePlayers.add(createPlayerBoard(player, false));
            }
        }

        for (int i = 0; i < sidePlayers.size(); i++) {
            if ((isTop && i % 2 == 0) || (!isTop && i % 2 != 0)) {
                container.getChildren().add(sidePlayers.get(i));
            }
        }

        return container;
    }

    /**
     * Creates the center area of the game view.
     *
     * @param remainingCards        The number of remaining cards.
     * @param topDiscardCard        The top discard card.
     * @param centerPlayerContainer The container for the center player.
     * @return The HBox container for the center area.
     */
    private HBox createCenterArea(int remainingCards, Card topDiscardCard, VBox centerPlayerContainer) {
        PickView pickView = new PickView(remainingCards);
        DiscardView discardView = new DiscardView(topDiscardCard);
        pickView.setPrefSize(150, 200);
        discardView.setPrefSize(150, 200);

        HBox centerArea = new HBox(40, pickView, centerPlayerContainer, discardView);
        centerArea.setAlignment(Pos.CENTER);
        return centerArea;
    }

    /**
     * Creates the main container for the game view.
     *
     * @param topPlayersContainer    The container for top players.
     * @param centerArea             The container for the center area.
     * @param bottomPlayersContainer The container for bottom players.
     * @return The main container as a VBox.
     */
    private VBox createMainContainer(HBox topPlayersContainer, HBox centerArea, HBox bottomPlayersContainer) {
        VBox mainContainer = new VBox(20, topPlayersContainer, centerArea, bottomPlayersContainer);
        mainContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(mainContainer, javafx.scene.layout.Priority.ALWAYS);
        return mainContainer;
    }

    /**
     * Gets the player by their name.
     *
     * @param players The list of players.
     * @param name    The name of the player to find.
     * @return The player with the specified name, or null if not found.
     */
    private Player getPlayerByName(List<Player> players, String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Creates a player board with the specified player and whether they are the
     * current player.
     *
     * @param player    The player to create the board for.
     * @param isCurrent Whether the player is the current player.
     * @return The VBox container for the player board.
     */
    private VBox createPlayerBoard(Player player, boolean isCurrent) {
        Text playerNameText = createPlayerNameText(player.getName(), isCurrent);
        List<CardView> cardViews = createCardViewsForPlayer(player);
        BoardView boardView = new BoardView(cardViews);
        boardView.setAlignment(Pos.CENTER);

        VBox playerContainer = new VBox(5, playerNameText, boardView);
        playerContainer.setAlignment(Pos.CENTER);
        playerContainer.setPrefSize(200, 300);
        playerContainer.setMaxSize(200, 300);

        if (isCurrent) {
            playerContainer.setStyle(
                    "-fx-background-color: #1E1E1E; -fx-border-color: gold; -fx-border-width: 3px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 10px;");
            DropShadow shadow = new DropShadow(10, Color.GOLD);
            playerContainer.setEffect(shadow);
        } else {
            playerContainer.setStyle(
                    "-fx-background-color: #1E1E1E; -fx-border-color: white; -fx-border-width: 3px; -fx-background-radius: 10px; -fx-border-radius: 10px; -fx-padding: 10px;");
        }

        return playerContainer;
    }

    /**
     * Creates a text element for the player name.
     *
     * @param playerName The name of the player.
     * @param isCurrent  Whether the player is the current player.
     * @return The text element for the player name.
     */
    private Text createPlayerNameText(String playerName, boolean isCurrent) {
        Text playerNameText = new Text(playerName);
        playerNameText.setFont(Font.font("Arial", isCurrent ? 22 : 18));
        playerNameText.setFill(isCurrent ? Color.GOLD : Color.WHITE);
        return playerNameText;
    }

    /**
     * Creates a list of card views for the specified player.
     *
     * @param player The player to create card views for.
     * @return A list of card views.
     */
    private List<CardView> createCardViewsForPlayer(Player player) {
        List<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < player.getCartes().size(); i++) {
            cardViews.add(new CardView(player.getCartes().get(i), i, player.getId()));
        }
        return cardViews;
    }


    /**
     * Clears all elements from the game view.
     */
    @Override
    public void clearAll() {
        rootPane.getChildren().clear();
        cardsContainer.getChildren().clear();
    }

    /**
     * Displays the final ranking of players.
     *
     * @param ranking The final ranking of players as a map of players to their
     *                scores.
     */
    @Override
    public void showFinalRanking(Map<Player, Integer> ranking) {
        cardsContainer.getChildren().clear();
        VBox rankingContainer = new VBox(20);
        rankingContainer.setAlignment(Pos.CENTER);
        rankingContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 20; -fx-border-radius: 15;");

        Text rankingTitle = new Text("Classement final :");
        rankingTitle.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
        rankingTitle.setFill(Color.web("#ff79c6"));
        rankingTitle.setEffect(new DropShadow(5, Color.BLACK));
        rankingContainer.getChildren().add(rankingTitle);

        ranking.forEach((player, score) -> {
            HBox playerScoreBox = new HBox(15);
            playerScoreBox.setAlignment(Pos.CENTER_LEFT);

            Rectangle playerIcon = new Rectangle(25, 25);
            playerIcon.setFill(Color.web("#bd93f9"));
            playerIcon.setArcWidth(10);
            playerIcon.setArcHeight(10);
            playerIcon.setEffect(new DropShadow(3, Color.BLACK));

            Text playerScore = new Text(player.getName() + ": " + score + " points");
            playerScore.setFont(Font.font("Roboto", 20));
            playerScore.setFill(Color.WHITE);

            playerScoreBox.getChildren().addAll(playerIcon, playerScore);
            rankingContainer.getChildren().add(playerScoreBox);
        });

        Button restartButton = new Button("Nouvelle partie");
        restartButton.setStyle(
                "-fx-background-color: #34495e; -fx-text-fill: white; -fx-font-size: 18px; -fx-padding: 10 20; -fx-border-radius: 10;");
        restartButton.setOnAction(event -> {
            stage.close();
            App.getINSTANCE().restart();
        });
        restartButton.setEffect(new DropShadow(5, Color.BLACK));

        rankingContainer.getChildren().add(restartButton);
        cardsContainer.getChildren().add(rankingContainer);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), rankingContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        stage.show();
    }

    /**
     * Displays the ranking of players.
     *
     * @param ranking The ranking of players as a map of players to their
     *                scores.
     */
    @Override
    public void showRanking(Map<Player, Integer> ranking) {
        cardsContainer.getChildren().clear();
        VBox rankingContainer = new VBox(20);
        rankingContainer.setAlignment(Pos.CENTER);
        rankingContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 20; -fx-border-radius: 15;");

        Text rankingTitle = new Text("Classement des joueurs:");
        rankingTitle.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
        rankingTitle.setFill(Color.web("#ff79c6"));
        rankingTitle.setEffect(new DropShadow(5, Color.BLACK));
        rankingContainer.getChildren().add(rankingTitle);

        ranking.forEach((player, score) -> {
            HBox playerScoreBox = new HBox(15);
            playerScoreBox.setAlignment(Pos.CENTER_LEFT);

            Rectangle playerIcon = new Rectangle(25, 25);
            playerIcon.setFill(Color.web("#bd93f9"));
            playerIcon.setArcWidth(10);
            playerIcon.setArcHeight(10);
            playerIcon.setEffect(new DropShadow(3, Color.BLACK));

            Text playerScore = new Text(player.getName() + ": " + score);
            playerScore.setFont(Font.font("Roboto", 20));
            playerScore.setFill(Color.WHITE);

            playerScoreBox.getChildren().addAll(playerIcon, playerScore);
            rankingContainer.getChildren().add(playerScoreBox);
        });


        Button nextButton = new Button("Next Round");

        nextButton.getStyleClass().add("button");
        nextButton.setOnAction(event -> {
            GameController.getInstance().restartRoundWithDelay(1);
        });
        nextButton.setEffect(new DropShadow(5, Color.BLACK));
        rankingContainer.getChildren().add(nextButton);

        rankingContainer.setAlignment(Pos.CENTER);
        cardsContainer.getChildren().add(rankingContainer);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), rankingContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        stage.show();
    }

    /**
     * Displays the end game message.
     */
    @Override
    public void showEndGame() {
        cardsContainer.getChildren().clear();
        Text endGameText = new Text("Fin de la partie !");
        endGameText.setFont(Font.font("Arial", 36));
        endGameText.setFill(Color.WHITE);
        cardsContainer.getChildren().add(endGameText);
        stage.show();
    }

    /**
     * Displays a message box with the specified message.
     *
     * @param message The message to display.
     */
    @Override
    public void showMessageBox(String message) {
        cardsContainer.getChildren().clear();
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);

        Text messageText = new Text(message);
        messageText.setFont(Font.font("Arial", 24));
        messageText.setFill(Color.WHITE);

        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 16px;");
        closeButton.setOnAction(event -> cardsContainer.getChildren().clear());

        messageBox.getChildren().addAll(messageText, closeButton);
        cardsContainer.getChildren().add(messageBox);
        stage.show();
    }

    /**
     * Recursively collects all card views from the root pane.
     *
     * @param node      The current node to check.
     * @param cardViews The list to collect card views.
     */
    private void collectCardViews(Node node, List<CardView> cardViews) {
        if (node instanceof CardView) {
            cardViews.add((CardView) node);
        } else if (node instanceof Pane) {
            for (Node child : ((Pane) node).getChildren()) {
                collectCardViews(child, cardViews);
            }
        }
    }

    /**
     * Gets all the card views in the root pane.
     *
     * @return A list of all card views.
     */
    @Override
    public List<CardView> getAllCardViews() {

        List<CardView> cardViews = new ArrayList<>();

        collectCardViews(cardsContainer, cardViews);

        System.out.println(cardViews.size());

        return cardViews;
    }

    /**
     * Distributes cards to players with an animation.
     *
     * @param players    The list of players.
     * @param cardViews  The list of card views to distribute.
     * @param onComplete A callback to execute when the animation is complete.
     */

    @Override
    public void distributeCardsWithAnimation(List<Player> players, List<CardView> cardViews, Runnable onComplete) {
        // Supprimer toutes les CardView non désirées
        rootPane.getChildren().removeIf(node -> node instanceof CardView);

        // Ajouter les nouvelles CardView
        rootPane.getChildren().addAll(cardViews);

        final int[] index = { 0 };
        Random random = new Random();
        List<CardAnimationTask> tasks = new ArrayList<>();

        // Pour chaque joueur, créer une tâche d'animation pour chaque carte.
        for (Player player : players) {
            double targetX = getPlayerXPosition(players.indexOf(player), players.size());
            double targetY = getPlayerYPosition(players.indexOf(player), players.size());

            for (int j = 0; j < player.getCartes().size(); j++) {
                CardView cardView = cardViews.get(index[0]++);
                // Ajout d'un décalage aléatoire
                double cardOffsetX = random.nextInt(75, 150);
                double cardOffsetY = random.nextInt(75, 150);

                int startX;
                int startY;

                switch (players.size()) {
                    case 2 -> {
                        startX = 525;
                        startY = 580;
                    }
                    default -> {
                        startX = 525;
                        startY = 410;
                    }
                }
                final int cardIndex = j;

                // Réinitialiser la CardView
                cardView.setLayoutX(startX);
                cardView.setLayoutY(startY);
                cardView.setRotate(0);
                cardView.setTranslateX(0);
                cardView.setTranslateY(0);
                cardView.setVisible(true);

                // Ajoute la tâche qui, une fois terminée, appellera onFinished.
                tasks.add(onFinished -> {
                    animateCard(cardView, startX, startY, targetX + cardOffsetX, targetY + cardOffsetY, cardIndex,
                            onFinished);
                });
            }
        }

        // Exécute les tâches d'animation une par une de manière séquentielle.
        animateTasksSequentially(tasks, onComplete);
        cardsContainer.getChildren().clear();
    }

    /**
     * Exécute récursivement les tâches d'animation de carte de manière
     * séquentielle.
     */
    private void animateTasksSequentially(List<CardAnimationTask> tasks, Runnable onComplete) {
        if (tasks.isEmpty()) {
            onComplete.run();
            return;
        }
        CardAnimationTask task = tasks.remove(0);
        task.run(() -> animateTasksSequentially(tasks, onComplete));
    }

    /**
     * Gets the X position for a player based on their index and the number of
     * players.
     *
     * @param playerIndex     The index of the player.
     * @param numberOfPlayers The total number of players.
     * @return The X position for the player.
     */
    private double getPlayerXPosition(int playerIndex, int numberOfPlayers) {
        // Dynamically adjust the X position based on the number of players (max 8)
        switch (numberOfPlayers) {
            case 2 -> {
                return 650;
            }
            case 3 -> {
                return 650;
            }
            case 4 -> {
                return switch (playerIndex) {
                    case 0 ->
                        650;
                    case 1 ->
                        650;
                    case 2 ->
                        540;
                    default ->
                        775;
                };
            }
            case 5 -> {
                return switch (playerIndex) {
                    case 0 ->
                        775;
                    case 1 ->
                        540;
                    case 2 ->
                        540;
                    case 3 ->
                        775;
                    default ->
                        650;
                };
            }
            case 6 -> {
                return switch (playerIndex) {
                    case 0 ->
                        650;
                    case 1 ->
                        540;
                    case 2 ->
                        775;
                    case 3 ->
                        420;
                    case 4 ->
                        650;
                    default ->
                        880;
                };
            }
            case 7 -> {
                return switch (playerIndex) {
                    case 0 ->
                        420;
                    case 1 ->
                        420;
                    case 2 ->
                        650;
                    case 3 ->
                        650;
                    case 4 ->
                        880;
                    case 5 ->
                        880;
                    default ->
                        650;
                };
            }
            case 8 -> {
                return switch (playerIndex) {
                    case 0 ->
                        290;
                    case 1 ->
                        520;
                    case 2 ->
                        760;
                    case 3 ->
                        1000;
                    case 4 ->
                        650;
                    case 5 ->
                        420;
                    case 6 ->
                        650;
                    default ->
                        880;
                };
            }
            default -> {
                return 300;
            }
        }
    }

    /**
     * Gets the Y position for a player based on their index and the number of
     * players.
     *
     * @param playerIndex     The index of the player.
     * @param numberOfPlayers The total number of players.
     * @return The Y position for the player.
     */
    private double getPlayerYPosition(int playerIndex, int numberOfPlayers) {
        // Dynamically adjust the Y position based on the number of players (max 8)
        switch (numberOfPlayers) {
            case 2 -> {
                return playerIndex == 0 ? 440 : 120;
            }
            case 3 -> {
                return playerIndex == 0 ? 590 : (playerIndex == 1 ? 320 : 35);
            }
            case 4 -> {
                switch (playerIndex) {
                    case 0 -> {
                        return 590;
                    }
                    case 1 -> {
                        return 320;
                    }
                    case 2 -> {
                        return 35;
                    }
                    default -> {
                        return 35;
                    }
                }
            }
            case 5 -> {
                return switch (playerIndex) {
                    case 0 ->
                        590;
                    case 1 ->
                        590;
                    case 2 ->
                        35;
                    case 3 ->
                        35;
                    default ->
                        320;
                };
            }
            case 6 -> {
                return switch (playerIndex) {
                    case 0 ->
                        320;
                    case 1 ->
                        590;
                    case 2 ->
                        590;
                    case 3 ->
                        35;
                    case 4 ->
                        35;
                    default ->
                        35;
                };
            }
            case 7 -> {
                return switch (playerIndex) {
                    case 0 ->
                        35;
                    case 1 ->
                        590;
                    case 2 ->
                        35;
                    case 3 ->
                        590;
                    case 4 ->
                        35;
                    case 5 ->
                        590;
                    default ->
                        320;
                };
            }
            case 8 -> {
                return switch (playerIndex) {
                    case 0 ->
                        95;
                    case 1 ->
                        95;
                    case 2 ->
                        95;
                    case 3 ->
                        95;
                    case 4 ->
                        375;
                    case 5 ->
                        620;
                    case 6 ->
                        620;
                    default ->
                        620;
                };
            }
            default -> {
                return 400;
            }
        }
    }

    /**
     * Fades in gameplay elements with an animation.
     *
     * @param node       The node to fade in.
     * @param onFinished A callback to execute when the animation is complete.
     */
    @Override
    public void fadeInGameplayElements(Node node, Runnable onFinished) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(Interpolator.EASE_IN);
        fadeTransition.setOnFinished(event -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fadeTransition.play();
    }

    /**
     * Sets up the board views for all players.
     *
     * @param players The list of players.
     */
    @Override
    public void setupBoardViews(List<Player> players) {
        cardsContainer.getChildren().clear();
        VBox mainContainer = new VBox(20);
        mainContainer.setOpacity(0);

        for (Player player : players) {

            BoardView boardView = new BoardView(createPlayerCardViews(player));
            VBox playerBoard = new VBox(new Text(player.getName()), boardView);
            mainContainer.getChildren().add(playerBoard);
        }

        cardsContainer.getChildren().add(mainContainer);
    }

    /**
     * Creates a list of card views for the specified player.
     *
     * @param player The player to create card views for.
     * @return A list of card views.
     */
    private List<CardView> createPlayerCardViews(Player player) {
        List<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < player.getCartes().size(); i++) {
            cardViews.add(new CardView(player.getCartes().get(i), i, player.getId()));
        }
        return cardViews;
    }

    /**
     * Animates a card from one position to another.
     *
     * @param cardView   The card view to animate.
     * @param startX     The starting X position.
     * @param startY     The starting Y position.
     * @param targetX    The target X position.
     * @param targetY    The target Y position.
     * @param cardIndex  The index of the card.
     * @param onFinished A callback to execute when the animation is complete.
     */
    private void animateCard(CardView cardView, double startX, double startY, double targetX, double targetY,
            int cardIndex, Runnable onFinished) {
        cardView.setLayoutX(startX);
        cardView.setLayoutY(startY);
        cardView.setRotate(0);

        // Translation de la carte avec une interpolation plus fluide
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.2), cardView);
        translateTransition.setFromX(0);
        translateTransition.setFromY(0);
        translateTransition.setToX(targetX - startX);
        translateTransition.setToY(targetY - startY);
        translateTransition.setInterpolator(Interpolator.EASE_OUT);

        // Rotation de la carte avec un angle aléatoire
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(0.2), cardView);
        Random random = new Random();
        double randomAngle = 360 * random.nextDouble(); // Rotation aléatoire entre 0 et 360 degrés
        rotateTransition.setFromAngle(0);
        rotateTransition.setToAngle(randomAngle);
        rotateTransition.setInterpolator(Interpolator.EASE_OUT);

        // Combinaison des animations
        ParallelTransition parallelTransition = new ParallelTransition(translateTransition, rotateTransition);
        parallelTransition.setOnFinished(event -> {
            cardView.setLayoutX(targetX);
            cardView.setLayoutY(targetY);
            cardView.setTranslateX(0);
            cardView.setTranslateY(0);
            if (onFinished != null) {
                onFinished.run();
            }
        });

        parallelTransition.play();
    }

    /**
     * Displays the first game view with the specified players, current player,
     * remaining cards, and top discard card.
     *
     * @param players           The list of players.
     * @param currentPlayerName The name of the current player.
     * @param remainingCards    The number of remaining cards.
     * @param topDiscardCard    The top discard card.
     */
    @Override
    public void firstShowPlaying(List<Player> players, String currentPlayerName, int remainingCards,
            Card topDiscardCard) {
        clearPreviousCards();
        cardsContainer.getChildren().clear();
        VBox centerPlayerContainer = createPlayerBoard(getPlayerByName(players, currentPlayerName), true);
        HBox topPlayersContainer = createSidePlayersContainer(players, currentPlayerName, true);
        HBox bottomPlayersContainer = createSidePlayersContainer(players, currentPlayerName, false);
        HBox centerArea = createCenterArea(remainingCards, topDiscardCard, centerPlayerContainer);
        VBox mainContainer = createMainContainer(topPlayersContainer, centerArea, bottomPlayersContainer);
        cardsContainer.getChildren().add(mainContainer);
        stage.show();
    }

    /**
     * Gets a card view by its index.
     *
     * @param index The index of the card view.
     * @return The card view with the specified index, or null if not found.
     */
    @Override
    public CardView getCardViewByIndex(int index) {

        List<CardView> cardViews = getAllCardViews();
        for (CardView cardView : cardViews) {
            if (cardView.getIndex() == index) {
                return cardView;
            }
        }
        return null;
    }

    /**
     * Finds a card view by its associated card.
     *
     * @param allCardViews The list of all card views.
     * @param card         The card to find the view for.
     * @return The card view associated with the specified card, or null if not
     *         found.
     */
    @Override
    public CardView findCardViewByCard(List<CardView> allCardViews, Card card) {
        for (CardView cardView : allCardViews) {
            if (cardView.getValue() == card) {
                return cardView;
            }
        }
        return null;
    }

    /**
     * Gets the root pane for the game view.
     *
     * @return The root pane.
     */
    @Override
    public Pane getRootPane() {
        return rootPane;
    }

}
