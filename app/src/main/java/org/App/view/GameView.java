package org.App.view;

import java.util.ArrayList;
import java.util.List;

import org.App.App;
import org.App.model.Card;
import org.App.model.Player;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameView {

    private final Stage stage;
    private final VBox cardsContainer;
    private final Scene scene;
    private Pane rootPane;

    public GameView(Stage stage) {
        this.stage = stage;
        rootPane = new Pane();
        stage.setTitle("Skyjo");
        stage.setFullScreen(false); // Désactive le mode plein écran
        stage.setMaximized(true); // Maximiser la fenêtre à la place

        this.cardsContainer = new VBox(20);
        this.cardsContainer.setAlignment(Pos.CENTER);

        // Barre de menu
        MenuBar menuBar = createMenuBar();
        menuBar.setStyle("-fx-background-color: linear-gradient(to right, #1E3C72, #2A5298); -fx-padding: 10px;");

        // Conteneur principal
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);

        borderPane.setCenter(rootPane);
        borderPane.setBottom(cardsContainer);
        borderPane.setStyle("-fx-background-color: linear-gradient(to bottom, #0F2027, #203A43, #2C5364);");

        this.scene = new Scene(borderPane, 1400, 900); // Augmenter la taille de la scène

        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Redimensionner dynamiquement
        cardsContainer.prefHeightProperty().bind(scene.heightProperty().subtract(100));
        cardsContainer.prefWidthProperty().bind(scene.widthProperty().subtract(100));

        rootPane.setOnMouseClicked(event -> {
            double x = event.getX();
            double y = event.getY();
            System.out.println("Clicked at: (" + x + ", " + y + ")");
        });
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        stage.show();
    }

    private MenuBar createMenuBar() {
        Menu gameMenu = new Menu("Game");
        gameMenu.setStyle(
                "-fx-font-size: 16px; -fx-text-fill: black;");
        MenuItem startNewGame = new MenuItem("Start New Game");
        MenuItem exitGame = new MenuItem("Exit");

        startNewGame.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        exitGame.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        startNewGame.setOnAction(event -> App.getINSTANCE().restart());
        exitGame.setOnAction(event -> stage.close());

        gameMenu.getItems().addAll(startNewGame, exitGame);
        gameMenu.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(gameMenu);

        return menuBar;
    }

    public void showPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard) {

        // Clear previous cards
        rootPane.getChildren().clear();

        // Create and add CardView objects to the rootPane
        for (Player player : players) {
            for (Card card : player.getCartes()) {
                CardView cardView = new CardView(card, player.getCartes().indexOf(card));
                rootPane.getChildren().add(cardView);
            }
        }

        cardsContainer.getChildren().clear();

        VBox centerPlayerContainer = createPlayerBoard(getPlayerByName(players, currentPlayerName), true);
        HBox topPlayersContainer = new HBox(20);
        HBox bottomPlayersContainer = new HBox(20);
        topPlayersContainer.setAlignment(Pos.CENTER);
        bottomPlayersContainer.setAlignment(Pos.CENTER);

        List<VBox> sidePlayers = new ArrayList<>();
        for (Player player : players) {
            if (!player.getName().equals(currentPlayerName)) {
                sidePlayers.add(createPlayerBoard(player, false));
            }
        }

        for (int i = 0; i < sidePlayers.size(); i++) {
            if (i % 2 == 0) {
                topPlayersContainer.getChildren().add(sidePlayers.get(i));
            } else {
                bottomPlayersContainer.getChildren().add(sidePlayers.get(i));
            }
        }

        // Création de la pioche et de la défausse
        PickView pickView = new PickView(remainingCards);
        DiscardView discardView = new DiscardView(topDiscardCard);
        pickView.setPrefSize(150, 200);
        discardView.setPrefSize(150, 200);

        // Conteneur pour la zone centrale : pioche - joueur - défausse
        HBox centerArea = new HBox(40, pickView, centerPlayerContainer, discardView);
        centerArea.setAlignment(Pos.CENTER);

        VBox mainContainer = new VBox(20, topPlayersContainer, centerArea, bottomPlayersContainer);
        mainContainer.setAlignment(Pos.CENTER);
        VBox.setVgrow(mainContainer, javafx.scene.layout.Priority.ALWAYS);

        cardsContainer.getChildren().add(mainContainer);
        stage.show();
    }

    private Player getPlayerByName(List<Player> players, String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    private VBox createPlayerBoard(Player player, boolean isCurrent) {
        Text playerNameText = new Text(player.getName());
        playerNameText.setFont(Font.font("Arial", isCurrent ? 22 : 18));
        playerNameText.setFill(isCurrent ? Color.GOLD : Color.WHITE);

        List<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < player.getCartes().size(); i++) {
            cardViews.add(new CardView(player.getCartes().get(i), i));
        }

        BoardView boardView = new BoardView(cardViews);
        boardView.setAlignment(Pos.CENTER);
        VBox playerContainer = new VBox(5, playerNameText, boardView);
        playerContainer.setAlignment(Pos.CENTER);
        playerContainer.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15); -fx-border-radius: 10px; -fx-padding: 10px;");

        playerContainer.setPrefSize(200, 300);
        playerContainer.setMaxSize(200, 300);

        return playerContainer;
    }

    public void showRanking(java.util.Map<Player, Integer> ranking) {
        cardsContainer.getChildren().clear();
        VBox rankingContainer = new VBox(10);
        rankingContainer.setAlignment(Pos.CENTER);

        Text rankingTitle = new Text("Classement des joueurs:");
        rankingTitle.setFont(Font.font("Arial", 24));
        rankingTitle.setFill(Color.LIGHTGRAY);
        rankingContainer.getChildren().add(rankingTitle);

        ranking.forEach((player, score) -> {
            Text playerScore = new Text(player.getName() + ": " + score);
            playerScore.setFont(Font.font("Arial", 18));
            playerScore.setFill(Color.WHITE);
            rankingContainer.getChildren().add(playerScore);
        });

        cardsContainer.getChildren().add(rankingContainer);
        stage.show();
    }

    public void showEndGame() {
        cardsContainer.getChildren().clear();
        Text endGameText = new Text("Fin de la partie !");
        endGameText.setFont(Font.font("Arial", 36));
        endGameText.setFill(Color.WHITE);
        cardsContainer.getChildren().add(endGameText);
        stage.show();
    }

    public void showMessageBox(String message) {
        cardsContainer.getChildren().clear();
        Text messageText = new Text(message);
        messageText.setFont(Font.font("Arial", 24));
        messageText.setFill(Color.WHITE);
        cardsContainer.getChildren().add(messageText);
        stage.show();
    }

    public List<CardView> getAllCardViews() {
        List<CardView> cardViews = new ArrayList<>();
        for (Node node : rootPane.getChildren()) {
            if (node instanceof CardView cardView) {
                cardViews.add(cardView);
            }
        }
        return cardViews;
    }

    public void distributeCardsWithAnimation(List<Player> players, List<CardView> cardViews, Runnable onComplete) {
        double startX = 400;
        double startY = 300;
        int numberOfPlayers = players.size();

        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(cardViews);

        final int[] index = { 0 };
        for (Player player : players) {
            double targetX = getPlayerXPosition(players.indexOf(player), numberOfPlayers);
            double targetY = getPlayerYPosition(players.indexOf(player), numberOfPlayers);

            for (int j = 0; j < player.getCartes().size(); j++) {
                CardView cardView = cardViews.get(index[0]++);
                double cardOffsetX = (j % 5) * 50; // Adjust X based on card index
                double cardOffsetY = (j / 5) * 70; // Adjust Y based on card index

                animateCard(cardView, startX, startY, targetX + cardOffsetX, targetY + cardOffsetY, j, () -> {
                    if (index[0] == cardViews.size()) {
                        onComplete.run(); // Call the next step after animation
                    }
                });
            }
        }
    }

    private double getPlayerXPosition(int playerIndex, int numberOfPlayers) {
        // Dynamically adjust the X position based on the number of players (max 8)
        switch (numberOfPlayers) {
            case 2 -> {
                return playerIndex == 0 ? 300 : 1000;
            }
            case 3 -> {
                return playerIndex == 0 ? 300 : (playerIndex == 1 ? 1000 : 650);
            }
            case 4 -> {
                return playerIndex == 0 ? 300 : (playerIndex == 1 ? 1000 : (playerIndex == 2 ? 650 : 300));
            }
            case 5 -> {
                return switch (playerIndex) {
                    case 0 -> 300;
                    case 1 -> 1000;
                    case 2 -> 650;
                    case 3 -> 100;
                    default -> 1200;
                };
            }
            case 6 -> {
                return switch (playerIndex) {
                    case 0 -> 300;
                    case 1 -> 1000;
                    case 2 -> 650;
                    case 3 -> 100;
                    case 4 -> 1200;
                    default -> 650;
                };
            }
            case 7 -> {
                return switch (playerIndex) {
                    case 0 -> 300;
                    case 1 -> 1000;
                    case 2 -> 650;
                    case 3 -> 100;
                    case 4 -> 1200;
                    case 5 -> 200;
                    default -> 1100;
                };
            }
            case 8 -> {
                return switch (playerIndex) {
                    case 0 -> 300;
                    case 1 -> 1000;
                    case 2 -> 650;
                    case 3 -> 100;
                    case 4 -> 1200;
                    case 5 -> 200;
                    case 6 -> 1100;
                    default -> 1500;
                };
            }
            default -> {
                return 300;
            }
        }
    }

    private double getPlayerYPosition(int playerIndex, int numberOfPlayers) {
        // Dynamically adjust the Y position based on the number of players (max 8)
        switch (numberOfPlayers) {
            case 2 -> {
                return 400;
            }
            case 3 -> {
                return playerIndex == 0 ? 200 : 600;
            }
            case 4 -> {
                return playerIndex == 0 ? 200 : (playerIndex == 1 ? 600 : 400);
            }
            case 5 -> {
                return switch (playerIndex) {
                    case 0 -> 200;
                    case 1 -> 600;
                    case 2 -> 400;
                    case 3 -> 600;
                    default -> 200;
                };
            }
            case 6 -> {
                return switch (playerIndex) {
                    case 0 -> 200;
                    case 1 -> 600;
                    case 2 -> 400;
                    case 3 -> 600;
                    case 4 -> 400;
                    default -> 200;
                };
            }
            case 7 -> {
                return switch (playerIndex) {
                    case 0 -> 200;
                    case 1 -> 600;
                    case 2 -> 400;
                    case 3 -> 600;
                    case 4 -> 400;
                    case 5 -> 600;
                    default -> 200;
                };
            }
            case 8 -> {
                return switch (playerIndex) {
                    case 0 -> 200;
                    case 1 -> 600;
                    case 2 -> 400;
                    case 3 -> 600;
                    case 4 -> 400;
                    case 5 -> 600;
                    case 6 -> 400;
                    default -> 200;
                };
            }
            default -> {
                return 400;
            }
        }
    }

    public void setupBoardViews(List<Player> players) {
        cardsContainer.getChildren().clear();
        VBox mainContainer = new VBox(20);

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
            cardViews.add(new CardView(player.getCartes().get(i), i));
        }
        return cardViews;
    }

    private void animateCard(CardView cardView, double startX, double startY, double targetX, double targetY,
            int cardIndex, Runnable onFinished) {
        System.out
                .println("Animating card from (" + startX + ", " + startY + ") to (" + targetX + ", " + targetY + ")");
        cardView.setLayoutX(startX);
        cardView.setLayoutY(startY);

        TranslateTransition transition = new TranslateTransition(Duration.seconds(4), cardView);
        transition.setToX(targetX - startX);
        transition.setToY(targetY - startY);
        transition.setDelay(Duration.millis(Math.abs(100 * (targetX - startX) / 200) + (cardIndex * 100)));
        transition.setInterpolator(Interpolator.EASE_BOTH);

        transition.setOnFinished(event -> {
            System.out.println("Updating card position to (" + targetX + ", " + targetY + ")");
            cardView.setLayoutX(targetX);
            cardView.setLayoutY(targetY);
            cardView.setTranslateX(0);
            cardView.setTranslateY(0);

            // Execute the onFinished callback
            if (onFinished != null) {
                onFinished.run();
            }
        });

        transition.play();
    }

    public Pane getRootPane() {
        return rootPane;
    }
}