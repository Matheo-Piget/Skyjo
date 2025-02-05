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
        stage.setFullScreen(false);  // Désactive le mode plein écran
        stage.setMaximized(true);    // Maximiser la fenêtre à la place
    
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
        playerContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); -fx-border-radius: 10px; -fx-padding: 10px;");
    
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

    public void distributeCards(List<Player> players, List<CardView> cardViews) {
        double startX = 400; // Starting X position of the deck
        double startY = 300; // Starting Y position of the deck
    
        // Clear the rootPane and add all cards to it
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(cardViews);
    
        // Animate cards to their target positions in the BoardView
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            VBox playerBoard = createPlayerBoard(player, false); // Create the player's board
            BoardView boardView = (BoardView) playerBoard.getChildren().get(1); // Get the BoardView
    
            for (int j = 0; j < player.getCartes().size(); j++) {
                CardView cardView = cardViews.get(i * player.getCartes().size() + j);
    
                // Calculate the target position in the BoardView
                int row = j / boardView.getColumnCount();
                int col = j % boardView.getColumnCount();
                double targetX = boardView.getLayoutX() + col * (cardView.getWidth() + boardView.getHgap());
                double targetY = boardView.getLayoutY() + row * (cardView.getHeight() + boardView.getVgap());
    
                // Animate the card to its target position
                animateCard(cardView, startX, startY, targetX, targetY, j, () -> {
                    // After animation, add the card to the BoardView
                    boardView.add(cardView, col, row);
                });
            }
        }
    
        // Animate the pick and discard cards
        PickView pickView = new PickView(0); // Replace with actual pick view
        DiscardView discardView = new DiscardView(null); // Replace with actual discard view
    
        // Example: Animate a card to the pick view
        CardView pickCard = cardViews.get(0); // Replace with actual pick card
        animateCard(pickCard, startX, startY, pickView.getLayoutX(), pickView.getLayoutY(), 0, () -> {
            pickView.getChildren().add(pickCard);
        });
    
        // Example: Animate a card to the discard view
        CardView discardCard = cardViews.get(1); // Replace with actual discard card
        animateCard(discardCard, startX, startY, discardView.getLayoutX(), discardView.getLayoutY(), 0, () -> {
            discardView.setTopCard(discardCard.getValue());
        });
    }
    
    private void animateCard(CardView cardView, double startX, double startY, double targetX, double targetY, int cardIndex, Runnable onFinished) {
        System.out.println("Animating card from (" + startX + ", " + startY + ") to (" + targetX + ", " + targetY + ")");
        cardView.setLayoutX(startX);
        cardView.setLayoutY(startY);
    
        TranslateTransition transition = new TranslateTransition(Duration.seconds(2), cardView);
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