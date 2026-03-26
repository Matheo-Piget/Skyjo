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
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Main game view for the Skyjo game.
 * Layered with StackPane for proper Z-ordering.
 */
public class GameView implements GameViewInterface {

    private final Stage stage;
    private final VBox cardsContainer;
    private Scene scene;
    private final Pane animationOverlay;
    private MusicManager musicManager;

    // Status bar for phase hints
    private final Label statusBar;
    // Info bar
    private final HBox infoBar;
    private final Label pileCountLabel;
    private final Label currentTurnLabel;
    // Toast overlay
    private final StackPane toastLayer;
    // Turn announcement layer
    private final StackPane announcementLayer;

    // Player avatar colors
    private static final String[] AVATAR_COLORS = {
            "#6366f1", "#ec4899", "#f59e0b", "#22c55e",
            "#06b6d4", "#8b5cf6", "#ef4444", "#14b8a6"
    };

    public GameView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Skyjo");
        stage.setFullScreen(false);
        stage.setMaximized(true);

        try {
            musicManager = new MusicManager("/musics/game_music.mp3");
            if (OptionsManager.getVolume() > 0) {
                musicManager.setVolume(OptionsManager.getVolume());
                musicManager.play();
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize music: " + e.getMessage());
            musicManager = null;
        }

        this.cardsContainer = new VBox(0);
        this.cardsContainer.setAlignment(Pos.CENTER);
        this.cardsContainer.getStyleClass().add("vbox");

        // Animation overlay - always on top for Z-ordering fix
        this.animationOverlay = new Pane();
        this.animationOverlay.setMouseTransparent(true);
        this.animationOverlay.setPickOnBounds(false);

        // Status bar
        this.statusBar = new Label("");
        statusBar.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        statusBar.setTextFill(Color.web("#94a3b8"));
        statusBar.setPadding(new Insets(6, 16, 6, 16));
        statusBar.setMaxWidth(Double.MAX_VALUE);
        statusBar.setAlignment(Pos.CENTER);
        statusBar.setStyle("-fx-background-color: rgba(15, 23, 42, 0.6);");

        // Info bar
        this.infoBar = new HBox(16);
        infoBar.getStyleClass().add("info-bar");
        infoBar.setAlignment(Pos.CENTER);

        Label pileItemLabel = new Label("PIOCHE");
        pileItemLabel.getStyleClass().add("info-item");
        this.pileCountLabel = new Label("");
        pileCountLabel.getStyleClass().add("info-value");

        Label infoDot = new Label("\u00b7");
        infoDot.getStyleClass().add("info-dot");

        Label turnItemLabel = new Label("TOUR");
        turnItemLabel.getStyleClass().add("info-item");
        this.currentTurnLabel = new Label("");
        currentTurnLabel.getStyleClass().add("info-value");

        infoBar.getChildren().addAll(pileItemLabel, pileCountLabel, infoDot, turnItemLabel, currentTurnLabel);

        // Toast layer - bottom center
        this.toastLayer = new StackPane();
        toastLayer.setMouseTransparent(true);
        toastLayer.setPickOnBounds(false);
        toastLayer.setAlignment(Pos.BOTTOM_CENTER);
        toastLayer.setPadding(new Insets(0, 0, 40, 0));

        // Announcement layer - center
        this.announcementLayer = new StackPane();
        announcementLayer.setMouseTransparent(true);
        announcementLayer.setPickOnBounds(false);
        announcementLayer.setAlignment(Pos.CENTER);

        try {
            MenuBar menuBar = createMenuBar();
            menuBar.getStyleClass().add("menu-bar");

            VBox topSection = new VBox(menuBar, infoBar, statusBar);

            BorderPane borderPane = new BorderPane();
            borderPane.setTop(topSection);
            borderPane.setCenter(cardsContainer);

            // Subtle background decorations
            Pane bgDecorations = new Pane();
            bgDecorations.setMouseTransparent(true);
            bgDecorations.setPickOnBounds(false);
            createGameBackground(bgDecorations);

            // Layered root: bg -> game content -> announcement -> toast -> animation
            StackPane stackRoot = new StackPane();
            stackRoot.getChildren().addAll(bgDecorations, borderPane, announcementLayer, toastLayer, animationOverlay);

            this.scene = new Scene(stackRoot, 1400, 900);

            String cssPath = OptionsManager.getTheme().equals("Sombre")
                    ? "/themes/game.css"
                    : "/themes/game_light.css";

            try {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            } catch (Exception e) {
                System.err.println("Failed to load CSS: " + cssPath + " - " + e.getMessage());
            }

            cardsContainer.prefHeightProperty().bind(scene.heightProperty().subtract(80));
            cardsContainer.prefWidthProperty().bind(scene.widthProperty());
        } catch (Exception e) {
            System.err.println("Error in GameView initialization: " + e.getMessage());
            e.printStackTrace();
            this.scene = new Scene(new Label("Loading game..."), 800, 600);
        }
    }

    // ==================== Toast notifications ====================

    @Override
    public void showToast(String message) {
        Label toast = new Label(message);
        toast.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        toast.setTextFill(Color.web("#f1f5f9"));
        toast.setStyle(
                "-fx-background-color: rgba(30, 41, 59, 0.92); " +
                "-fx-background-radius: 10; " +
                "-fx-padding: 10 24; " +
                "-fx-border-color: rgba(255,255,255,0.1); " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1;");
        toast.setEffect(new DropShadow(12, 0, 4, Color.color(0, 0, 0, 0.4)));
        toast.setOpacity(0);

        toastLayer.getChildren().add(toast);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), toast);
        fadeIn.setToValue(1);

        // Hold
        FadeTransition hold = new FadeTransition(Duration.millis(1800), toast);
        hold.setFromValue(1);
        hold.setToValue(1);

        // Fade out + slide up
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0);
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(400), toast);
        slideUp.setByY(-20);

        fadeIn.setOnFinished(e -> hold.play());
        hold.setOnFinished(e -> {
            fadeOut.play();
            slideUp.play();
        });
        fadeOut.setOnFinished(e -> toastLayer.getChildren().remove(toast));
        fadeIn.play();
    }

    // ==================== Status bar ====================

    @Override
    public void showStatusMessage(String message) {
        statusBar.setText(message);
    }

    // ==================== Turn announcement ====================

    @Override
    public void showTurnAnnouncement(String playerName) {
        VBox announcement = new VBox(4);
        announcement.setAlignment(Pos.CENTER);
        announcement.setMouseTransparent(true);

        Text turnLabel = new Text("Tour de");
        turnLabel.setFont(Font.font("Segoe UI", 16));
        turnLabel.setFill(Color.web("#94a3b8"));

        Text nameLabel = new Text(playerName);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        nameLabel.setFill(Color.web("#f1f5f9"));
        nameLabel.setEffect(new DropShadow(8, 0, 0, Color.color(0, 0, 0, 0.5)));

        announcement.getChildren().addAll(turnLabel, nameLabel);
        announcement.setOpacity(0);
        announcement.setScaleX(0.8);
        announcement.setScaleY(0.8);

        announcementLayer.getChildren().add(announcement);

        double speed = OptionsManager.getAnimationSpeed();

        // Scale + fade in
        Timeline animIn = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(announcement.opacityProperty(), 0),
                        new KeyValue(announcement.scaleXProperty(), 0.8),
                        new KeyValue(announcement.scaleYProperty(), 0.8)),
                new KeyFrame(Duration.millis(300 * speed),
                        new KeyValue(announcement.opacityProperty(), 1, Interpolator.EASE_OUT),
                        new KeyValue(announcement.scaleXProperty(), 1, Interpolator.EASE_OUT),
                        new KeyValue(announcement.scaleYProperty(), 1, Interpolator.EASE_OUT))
        );

        // Hold then fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400 * speed), announcement);
        fadeOut.setDelay(Duration.millis(800 * speed));
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> announcementLayer.getChildren().remove(announcement));

        animIn.setOnFinished(e -> fadeOut.play());
        animIn.play();
    }

    // ==================== Core methods ====================

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public void show() {
        stage.show();
    }

    private MenuBar createMenuBar() {
        Menu gameMenu = new Menu("Menu");
        gameMenu.getStyleClass().add("menu");

        MenuItem startNewGame = new MenuItem("Nouvelle Partie");
        MenuItem toggleMusic = new MenuItem("Musique On/Off");
        MenuItem increaseVolume = new MenuItem("Volume +");
        MenuItem decreaseVolume = new MenuItem("Volume -");
        MenuItem exitGame = new MenuItem("Quitter");

        startNewGame.setOnAction(event -> {
            SoundManager.dispose();
            if (musicManager != null) musicManager.stop();
            scene.getStylesheets().clear();
            stage.close();
            App.getINSTANCE().restart();
        });

        toggleMusic.setOnAction(event -> {
            if (musicManager != null) {
                if (musicManager.isPlaying()) {
                    musicManager.pause();
                    toggleMusic.setText("Reprendre la Musique");
                } else {
                    musicManager.play();
                    toggleMusic.setText("Musique On/Off");
                }
            }
        });

        increaseVolume.setOnAction(event -> {
            if (musicManager != null)
                musicManager.setVolume(Math.min(1.0, musicManager.getVolume() + 0.1));
        });

        decreaseVolume.setOnAction(event -> {
            if (musicManager != null)
                musicManager.setVolume(Math.max(0.0, musicManager.getVolume() - 0.1));
        });

        exitGame.setOnAction(event -> stage.close());

        gameMenu.getItems().addAll(startNewGame, toggleMusic, increaseVolume, decreaseVolume, exitGame);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(gameMenu);
        return menuBar;
    }

    // ==================== Game display ====================

    @Override
    public void showPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard) {
        clearPreviousCards();
        pileCountLabel.setText(remainingCards + " cartes");
        currentTurnLabel.setText(currentPlayerName);
        cardsContainer.getChildren().clear();

        VBox centerPlayerContainer = createPlayerBoard(getPlayerByName(players, currentPlayerName), true);
        HBox topPlayersContainer = createSidePlayersContainer(players, currentPlayerName, true);
        HBox bottomPlayersContainer = createSidePlayersContainer(players, currentPlayerName, false);

        HBox centerArea = createCenterArea(remainingCards, topDiscardCard, centerPlayerContainer);
        VBox mainContainer = createMainContainer(topPlayersContainer, centerArea, bottomPlayersContainer);

        cardsContainer.getChildren().add(mainContainer);
        stage.show();
    }

    private void clearPreviousCards() {
        animationOverlay.getChildren().removeIf(node -> node instanceof CardView);
    }

    private HBox createSidePlayersContainer(List<Player> players, String currentPlayerName, boolean isTop) {
        HBox container = new HBox(28);
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

    private HBox createCenterArea(int remainingCards, Card topDiscardCard, VBox centerPlayerContainer) {
        PickView pickView = new PickView(remainingCards);
        DiscardView discardView = new DiscardView(topDiscardCard);
        pickView.setPrefSize(100, 140);
        discardView.setPrefSize(100, 140);

        HBox centerArea = new HBox(50, pickView, centerPlayerContainer, discardView);
        centerArea.setAlignment(Pos.CENTER);
        centerArea.getStyleClass().add("table-surface");
        return centerArea;
    }

    private VBox createMainContainer(HBox topPlayersContainer, HBox centerArea, HBox bottomPlayersContainer) {
        VBox mainContainer = new VBox(24, topPlayersContainer, centerArea, bottomPlayersContainer);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(10));
        VBox.setVgrow(mainContainer, Priority.ALWAYS);
        return mainContainer;
    }

    private Player getPlayerByName(List<Player> players, String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    // ==================== Player board with avatar ====================

    private VBox createPlayerBoard(Player player, boolean isCurrent) {
        // Avatar: colored circle with initial
        String avatarColor = AVATAR_COLORS[player.getId() % AVATAR_COLORS.length];
        Circle avatar = new Circle(14);
        avatar.setFill(Color.web(avatarColor));
        avatar.setEffect(new DropShadow(4, 0, 0, Color.web(avatarColor, 0.5)));

        Text initial = new Text(player.getName().substring(0, 1).toUpperCase());
        initial.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        initial.setFill(Color.WHITE);
        StackPane avatarPane = new StackPane(avatar, initial);

        // Name
        Text playerNameText = new Text(player.getName());
        playerNameText.setFont(Font.font("Segoe UI", FontWeight.BOLD, isCurrent ? 18 : 14));
        playerNameText.setFill(isCurrent ? Color.web("#fbbf24") : Color.web("#e2e8f0"));

        HBox nameRow = new HBox(8, avatarPane, playerNameText);
        nameRow.setAlignment(Pos.CENTER);

        // Cards
        List<CardView> cardViews = createCardViewsForPlayer(player);
        BoardView boardView = new BoardView(cardViews);
        boardView.setAlignment(Pos.CENTER);

        // Score
        int totalScore = player.getCumulativeScore();
        Text scoreText = new Text(totalScore + " pts");
        scoreText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        scoreText.setFill(isCurrent ? Color.web("#fbbf24", 0.8) : Color.web("#818cf8"));

        VBox playerContainer = new VBox(6, nameRow, boardView, scoreText);
        playerContainer.setAlignment(Pos.CENTER);
        playerContainer.setPadding(new Insets(8));

        if (isCurrent) {
            playerContainer.getStyleClass().add("player-board-current");
            // Subtle pulse animation on current player
            addPulseAnimation(playerContainer);
        } else {
            playerContainer.getStyleClass().add("player-board");
        }

        return playerContainer;
    }

    /** Adds a subtle breathing/pulse glow to the current player's board. */
    private void addPulseAnimation(VBox board) {
        DropShadow glow = new DropShadow(15, Color.web("#fbbf24", 0.3));
        board.setEffect(glow);

        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(glow.radiusProperty(), 12),
                        new KeyValue(glow.colorProperty(), Color.web("#fbbf24", 0.2))),
                new KeyFrame(Duration.millis(1200),
                        new KeyValue(glow.radiusProperty(), 22, Interpolator.EASE_BOTH),
                        new KeyValue(glow.colorProperty(), Color.web("#fbbf24", 0.45), Interpolator.EASE_BOTH))
        );
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();
    }

    private Text createPlayerNameText(String playerName, boolean isCurrent) {
        Text playerNameText = new Text(playerName);
        playerNameText.setFont(Font.font("Segoe UI", FontWeight.BOLD, isCurrent ? 20 : 16));
        playerNameText.setFill(isCurrent ? Color.web("#fbbf24") : Color.web("#e2e8f0"));
        return playerNameText;
    }

    private List<CardView> createCardViewsForPlayer(Player player) {
        List<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < player.getCartes().size(); i++) {
            cardViews.add(new CardView(player.getCartes().get(i), i, player.getId()));
        }
        return cardViews;
    }

    // ==================== Rankings / Debrief ====================

    @Override
    public void clearAll() {
        animationOverlay.getChildren().clear();
        cardsContainer.getChildren().clear();
    }

    @Override
    public void showFinalRanking(Map<Player, Integer> ranking) {
        cardsContainer.getChildren().clear();

        VBox rankingContainer = new VBox(20);
        rankingContainer.setAlignment(Pos.CENTER);
        rankingContainer.getStyleClass().add("debrief-container");
        rankingContainer.setMaxWidth(520);

        Text rankingTitle = new Text("Classement final");
        rankingTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        rankingTitle.setFill(Color.web("#f1f5f9"));
        rankingContainer.getChildren().add(rankingTitle);

        int rank = 1;
        for (Map.Entry<Player, Integer> entry : ranking.entrySet()) {
            Player player = entry.getKey();
            int score = entry.getValue();

            rankingContainer.getChildren().add(
                    createRankingRow(player, rank, score, player.getCumulativeScore(), true));
            rank++;
        }

        Button restartButton = new Button("Nouvelle partie");
        restartButton.getStyleClass().add("button");
        restartButton.setOnAction(event -> {
            stage.close();
            App.getINSTANCE().restart();
        });

        rankingContainer.getChildren().add(restartButton);

        VBox wrapper = new VBox(rankingContainer);
        wrapper.setAlignment(Pos.CENTER);
        cardsContainer.getChildren().add(wrapper);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), rankingContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        stage.show();
    }

    @Override
    public void showRanking(Map<Player, Integer> ranking) {
        cardsContainer.getChildren().clear();

        VBox rankingContainer = new VBox(18);
        rankingContainer.setAlignment(Pos.CENTER);
        rankingContainer.getStyleClass().add("debrief-container");
        rankingContainer.setMaxWidth(520);

        Text title = new Text("Fin de la manche");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        title.setFill(Color.web("#f1f5f9"));

        Text subtitle = new Text("Scores de cette manche");
        subtitle.setFont(Font.font("Segoe UI", 15));
        subtitle.setFill(Color.web("#94a3b8"));

        rankingContainer.getChildren().addAll(title, subtitle);

        int rank = 1;
        for (Map.Entry<Player, Integer> entry : ranking.entrySet()) {
            Player player = entry.getKey();
            int roundScore = entry.getValue();

            rankingContainer.getChildren().add(
                    createRankingRow(player, rank, roundScore, player.getCumulativeScore(), false));
            rank++;
        }

        Button nextButton = new Button("Manche suivante");
        nextButton.getStyleClass().add("button");
        nextButton.setOnAction(event -> GameController.getInstance().restartRoundWithDelay(0.3));

        VBox buttonBox = new VBox(nextButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(8, 0, 0, 0));
        rankingContainer.getChildren().add(buttonBox);

        VBox wrapper = new VBox(rankingContainer);
        wrapper.setAlignment(Pos.CENTER);
        cardsContainer.getChildren().add(wrapper);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.6), rankingContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        stage.show();
    }

    /** Creates a row for the ranking screen with avatar, name, score. */
    private HBox createRankingRow(Player player, int rank, int displayScore, int totalScore, boolean isFinal) {
        String avatarColor = AVATAR_COLORS[player.getId() % AVATAR_COLORS.length];
        Circle avatar = new Circle(12);
        avatar.setFill(Color.web(avatarColor));

        Text initial = new Text(player.getName().substring(0, 1).toUpperCase());
        initial.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        initial.setFill(Color.WHITE);
        StackPane avatarPane = new StackPane(avatar, initial);

        Text rankText = new Text("#" + rank);
        rankText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        rankText.setFill(rank == 1 ? Color.web("#fbbf24") : Color.web("#64748b"));

        Text nameText = new Text(player.getName());
        nameText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        nameText.setFill(Color.web("#e2e8f0"));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text scoreText = new Text((displayScore >= 0 ? "+" : "") + displayScore);
        scoreText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        scoreText.setFill(displayScore <= 0 ? Color.web("#22c55e") : Color.web("#f1f5f9"));

        Text totalText = new Text(isFinal ? displayScore + " pts" : "Total: " + totalScore);
        totalText.setFont(Font.font("Segoe UI", 13));
        totalText.setFill(Color.web("#818cf8"));

        VBox scoreCol = new VBox(2, scoreText, totalText);
        scoreCol.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(10, rankText, avatarPane, nameText, spacer, scoreCol);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle(rank == 1
                ? "-fx-background-color: rgba(251, 191, 36, 0.1); -fx-background-radius: 10;"
                : "-fx-background-color: rgba(255, 255, 255, 0.03); -fx-background-radius: 10;");

        return row;
    }

    /** Creates subtle floating card-shaped decorations in the game background. */
    private void createGameBackground(Pane layer) {
        Random rng = new Random();
        boolean isDark = OptionsManager.getTheme().equals("Sombre");

        for (int i = 0; i < 8; i++) {
            Rectangle shape = new Rectangle(25 + rng.nextInt(20), 35 + rng.nextInt(25));
            shape.setArcWidth(6);
            shape.setArcHeight(6);
            shape.setFill(isDark
                    ? Color.web("#818cf8", 0.015 + rng.nextDouble() * 0.02)
                    : Color.web("#6366f1", 0.02 + rng.nextDouble() * 0.025));
            shape.setStroke(Color.TRANSPARENT);

            shape.setLayoutX(rng.nextDouble() * 1600);
            shape.setLayoutY(rng.nextDouble() * 1000);
            shape.setRotate(rng.nextDouble() * 360);

            layer.getChildren().add(shape);

            TranslateTransition tt = new TranslateTransition(
                    Duration.seconds(18 + rng.nextDouble() * 20), shape);
            tt.setFromY(0);
            tt.setToY(-20 - rng.nextDouble() * 40);
            tt.setFromX(0);
            tt.setToX(-10 + rng.nextDouble() * 20);
            tt.setAutoReverse(true);
            tt.setCycleCount(TranslateTransition.INDEFINITE);
            tt.setInterpolator(Interpolator.EASE_BOTH);
            tt.play();

            RotateTransition rt = new RotateTransition(
                    Duration.seconds(25 + rng.nextDouble() * 20), shape);
            rt.setByAngle(-8 + rng.nextDouble() * 16);
            rt.setAutoReverse(true);
            rt.setCycleCount(RotateTransition.INDEFINITE);
            rt.setInterpolator(Interpolator.EASE_BOTH);
            rt.play();
        }
    }

    @Override
    public void showEndGame() {
        cardsContainer.getChildren().clear();
        Text endGameText = new Text("Fin de la partie !");
        endGameText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        endGameText.setFill(Color.web("#f1f5f9"));
        cardsContainer.getChildren().add(endGameText);
        stage.show();
    }

    @Override
    public void showMessageBox(String message) {
        // Delegate to toast for a non-intrusive experience
        showToast(message);
    }

    // ==================== Card views helpers ====================

    private void collectCardViews(Node node, List<CardView> cardViews) {
        switch (node) {
            case CardView cardView -> cardViews.add(cardView);
            case Pane pane -> {
                for (Node child : pane.getChildren()) {
                    collectCardViews(child, cardViews);
                }
            }
            default -> {
            }
        }
    }

    @Override
    public List<CardView> getAllCardViews() {
        List<CardView> cardViews = new ArrayList<>();
        collectCardViews(cardsContainer, cardViews);
        return cardViews;
    }

    // ==================== Distribution animation ====================

    @Override
    public void distributeCardsWithAnimation(List<Player> players, List<CardView> cardViews, Runnable onComplete) {
        animationOverlay.getChildren().removeIf(node -> node instanceof CardView);
        animationOverlay.getChildren().addAll(cardViews);

        double speed = OptionsManager.getAnimationSpeed();
        final int[] index = { 0 };
        Random random = new Random();
        List<CardAnimationTask> tasks = new ArrayList<>();

        for (Player player : players) {
            Point2D playerPosition = getPlayerPosition(player.getId(), players.size());
            double targetX = playerPosition.getX();
            double targetY = playerPosition.getY();

            for (int j = 0; j < player.getCartes().size(); j++) {
                CardView cardView = cardViews.get(index[0]++);
                double cardOffsetX = random.nextInt(75, 150);
                double cardOffsetY = random.nextInt(75, 150);

                int startX = 525;
                int startY = players.size() == 2 ? 580 : 410;

                cardView.setLayoutX(startX);
                cardView.setLayoutY(startY);
                cardView.setRotate(0);
                cardView.setTranslateX(0);
                cardView.setTranslateY(0);
                cardView.setVisible(true);

                tasks.add(onFinished -> animateCard(cardView, startX, startY,
                        targetX + cardOffsetX, targetY + cardOffsetY, speed, onFinished));
            }
        }

        animateTasksSequentially(tasks, onComplete);
        cardsContainer.getChildren().clear();
    }

    private void animateTasksSequentially(List<CardAnimationTask> tasks, Runnable onComplete) {
        if (tasks.isEmpty()) {
            onComplete.run();
            return;
        }
        CardAnimationTask task = tasks.remove(0);
        task.run(() -> animateTasksSequentially(tasks, onComplete));
    }

    private Point2D getPlayerPosition(int playerIndex, int numberOfPlayers) {
        double x, y;
        switch (numberOfPlayers) {
            case 2 -> { x = 650; y = playerIndex == 0 ? 390 : 70; }
            case 3 -> { x = 650; y = playerIndex == 0 ? 540 : (playerIndex == 1 ? 270 : -15); }
            case 4 -> {
                x = switch (playerIndex) { case 0, 1 -> 650; case 2 -> 540; default -> 775; };
                y = switch (playerIndex) { case 0 -> 540; case 1 -> 270; case 2 -> 15; default -> -15; };
            }
            case 5 -> {
                x = switch (playerIndex) { case 0 -> 775; case 1, 2 -> 540; case 3 -> 775; default -> 650; };
                y = switch (playerIndex) { case 0, 1 -> 540; case 2, 3 -> -15; default -> 270; };
            }
            case 6 -> {
                x = switch (playerIndex) { case 0 -> 650; case 1 -> 540; case 2 -> 775; case 3 -> 420; case 4 -> 650; default -> 880; };
                y = switch (playerIndex) { case 0 -> 270; case 1, 2 -> 540; default -> -15; };
            }
            case 7 -> {
                x = switch (playerIndex) { case 0, 1 -> 420; case 2, 3 -> 650; case 4, 5 -> 880; default -> 650; };
                y = switch (playerIndex) { case 0, 2, 4 -> -15; case 1, 3, 5 -> 540; default -> 270; };
            }
            case 8 -> {
                x = switch (playerIndex) { case 0 -> 290; case 1 -> 520; case 2 -> 760; case 3 -> 1000; case 4 -> 650; case 5 -> 420; case 6 -> 650; default -> 880; };
                y = switch (playerIndex) { case 0, 1, 2, 3 -> 45; case 4 -> 325; default -> 570; };
            }
            default -> { x = 300; y = 350; }
        }
        return new Point2D(x, y);
    }

    @Override
    public void fadeInGameplayElements(Node node, Runnable onFinished) {
        double speed = OptionsManager.getAnimationSpeed();
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.8 * speed), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(Interpolator.EASE_IN);
        fadeTransition.setOnFinished(event -> {
            if (onFinished != null) onFinished.run();
        });
        fadeTransition.play();
    }

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

    private List<CardView> createPlayerCardViews(Player player) {
        List<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < player.getCartes().size(); i++) {
            cardViews.add(new CardView(player.getCartes().get(i), i, player.getId()));
        }
        return cardViews;
    }

    private void animateCard(CardView cardView, double startX, double startY,
                             double targetX, double targetY, double speed, Runnable onFinished) {
        cardView.setLayoutX(startX);
        cardView.setLayoutY(startY);
        cardView.setRotate(0);

        TranslateTransition tt = new TranslateTransition(Duration.seconds(0.15 * speed), cardView);
        tt.setToX(targetX - startX);
        tt.setToY(targetY - startY);
        tt.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition rt = new RotateTransition(Duration.seconds(0.15 * speed), cardView);
        rt.setToAngle(360 * new Random().nextDouble());
        rt.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition pt = new ParallelTransition(tt, rt);
        pt.setOnFinished(event -> {
            cardView.setLayoutX(targetX);
            cardView.setLayoutY(targetY);
            cardView.setTranslateX(0);
            cardView.setTranslateY(0);
            if (onFinished != null) onFinished.run();
        });
        pt.play();
    }

    @Override
    public void firstShowPlaying(List<Player> players, String currentPlayerName, int remainingCards,
                                 Card topDiscardCard) {
        clearPreviousCards();
        pileCountLabel.setText(remainingCards + " cartes");
        currentTurnLabel.setText(currentPlayerName);
        cardsContainer.getChildren().clear();
        VBox centerPlayerContainer = createPlayerBoard(getPlayerByName(players, currentPlayerName), true);
        HBox topPlayersContainer = createSidePlayersContainer(players, currentPlayerName, true);
        HBox bottomPlayersContainer = createSidePlayersContainer(players, currentPlayerName, false);
        HBox centerArea = createCenterArea(remainingCards, topDiscardCard, centerPlayerContainer);
        VBox mainContainer = createMainContainer(topPlayersContainer, centerArea, bottomPlayersContainer);
        cardsContainer.getChildren().add(mainContainer);
        stage.show();
    }

    @Override
    public CardView getCardViewByIndex(int index) {
        List<CardView> cardViews = getAllCardViews();
        for (CardView cardView : cardViews) {
            if (cardView.getIndex() == index) return cardView;
        }
        return null;
    }

    @Override
    public CardView findCardViewByCard(List<CardView> allCardViews, Card card) {
        for (CardView cardView : allCardViews) {
            if (cardView.getValue() == card) return cardView;
        }
        return null;
    }

    @Override
    public Pane getRootPane() {
        return animationOverlay;
    }
}
