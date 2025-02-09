package org.App.view;

import java.util.ArrayList;
import java.util.List;

import org.App.App;
import org.App.model.Card;
import org.App.model.Player;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
        stage.setFullScreen(false);
        stage.setMaximized(true);

        // Apply gradient background to the root pane
        rootPane.getStyleClass().add("root");

        this.cardsContainer = new VBox(20);
        this.cardsContainer.setAlignment(Pos.CENTER);
        this.cardsContainer.getStyleClass().add("vbox");

        // Barre de menu
        MenuBar menuBar = createMenuBar();
        menuBar.getStyleClass().add("menu-bar");


        // Conteneur principal
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new BorderPane(menuBar));
        borderPane.setCenter(rootPane);
        borderPane.setBottom(cardsContainer);

        this.scene = new Scene(borderPane, 1400, 900);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Redimensionner dynamiquement
        cardsContainer.prefHeightProperty().bind(scene.heightProperty().subtract(100));
        cardsContainer.prefWidthProperty().bind(scene.widthProperty().subtract(100));

        scene.setOnMouseClicked(event -> {
            System.out.println("Scene clicked at (" + event.getX() + ", " + event.getY() + ")");
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
        gameMenu.getStyleClass().add("menu");

        MenuItem startNewGame = new MenuItem("Start New Game");
        MenuItem exitGame = new MenuItem("Exit");

        startNewGame.getStyleClass().add("menu-item");
        exitGame.getStyleClass().add("menu-item");

        startNewGame.setOnAction(event -> 
        {
            stage.close();
            App.getINSTANCE().restart();
        });
        exitGame.setOnAction(event -> stage.close());

        gameMenu.getItems().addAll(startNewGame, exitGame);

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
        playerContainer.setPrefSize(200, 300);
        playerContainer.setMaxSize(200, 300);

        return playerContainer;
    }

    public void showRanking(java.util.Map<Player, Integer> ranking) {


        Button showRankingButton = new Button("Show Ranking");
        showRankingButton.setOnAction(event -> {
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
        });

        cardsContainer.getChildren().add(showRankingButton);
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
        final int totalCards = cardViews.size(); // Get total number of cards to distribute
        int[] remainingAnimations = { totalCards }; // Track how many animations are remaining

        for (Player player : players) {
            double targetX = getPlayerXPosition(players.indexOf(player), numberOfPlayers);
            double targetY = getPlayerYPosition(players.indexOf(player), numberOfPlayers);

            for (int j = 0; j < player.getCartes().size(); j++) {
                CardView cardView = cardViews.get(index[0]++);

                double cardOffsetX = (j % 4) * 50; // Adjust X based on card index
                double cardOffsetY = (j / 4) * 70; // Adjust Y based on card index

                animateCard(cardView, startX, startY, targetX + cardOffsetX, targetY + cardOffsetY, j, () -> {
                    remainingAnimations[0]--; // Decrease the remaining animation count
                    if (remainingAnimations[0] == 0) {
                        // Once all animations are completed, execute the callback
                        onComplete.run();
                    }
                });
            }
        }
    }

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
                    case 0 -> 650;
                    case 1 -> 650;
                    case 2 -> 540;
                    default -> 775;
                };
            }
            case 5 -> {
                return switch (playerIndex) {
                    case 0 -> 775;
                    case 1 -> 540;
                    case 2 -> 540;
                    case 3 -> 775;
                    default -> 650;
                };
            }
            case 6 -> {
                return switch (playerIndex) {
                    case 0 -> 650;
                    case 1 -> 540;
                    case 2 -> 775;
                    case 3 -> 420;
                    case 4 -> 650;
                    default -> 880;
                };
            }
            case 7 -> {
                return switch (playerIndex){
                    case 0 -> 420;
                    case 1 -> 420;
                    case 2 -> 650;
                    case 3 -> 650;
                    case 4 -> 880;
                    case 5 -> 880;
                    default -> 650;
                };
            }
            case 8 -> {
                return switch (playerIndex) {
                    case 0 -> 290;
                    case 1 -> 520;
                    case 2 -> 760;
                    case 3 -> 1000;
                    case 4 -> 650;
                    case 5 -> 420;
                    case 6 -> 650;
                    default -> 880;
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
                    case 0 -> 590;
                    case 1 -> 590;
                    case 2 -> 35;
                    case 3 -> 35;
                    default -> 320;
                };
            }
            case 6 -> {
                return switch (playerIndex) {
                    case 0 -> 320;
                    case 1 -> 590;
                    case 2 -> 590;
                    case 3 -> 35;
                    case 4 -> 35;
                    default -> 35;
                };
            }
            case 7 -> {
                return switch (playerIndex) {
                    case 0 -> 35;
                    case 1 -> 590;
                    case 2 -> 35;
                    case 3 -> 590;
                    case 4 -> 35;
                    case 5 -> 590;
                    default -> 320;
                };
            }
            case 8 -> {
                return switch (playerIndex) {
                    case 0 -> 35;
                    case 1 -> 35;
                    case 2 -> 35;
                    case 3 -> 35;
                    case 4 -> 320;
                    case 5 -> 590;
                    case 6 -> 590;
                    default -> 590;
                };
            }
            default -> {
                return 400;
            }
        }
    }

    public void fadeInGameplayElements(Node node, Runnable onFinished) {
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), node);
        fadeTransition.setFromValue(0); // Start fully transparent
        fadeTransition.setToValue(1);   // End fully opaque
        fadeTransition.setInterpolator(Interpolator.EASE_IN);
        fadeTransition.setOnFinished(event -> {
            if (onFinished != null) {
                onFinished.run();
            }
        });
        fadeTransition.play();
    }

    public void setupBoardViews(List<Player> players) {
        cardsContainer.getChildren().clear();
        VBox mainContainer = new VBox(20);
        mainContainer.setOpacity(0); // Start fully transparent

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