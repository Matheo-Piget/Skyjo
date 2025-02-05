package org.App.view;

import java.util.ArrayList;
import java.util.List;

import org.App.controller.GameController;
import org.App.model.Card;
import org.App.model.Player;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameView {

    private final Stage stage;
    private final VBox cardsContainer;
    private final Scene scene;

    public GameView(Stage stage) {
        this.stage = stage;
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
        borderPane.setCenter(cardsContainer);
        borderPane.setStyle("-fx-background-color: linear-gradient(to bottom, #0F2027, #203A43, #2C5364);");
    
        this.scene = new Scene(borderPane, 1400, 900); // Augmenter la taille de la scène
    
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
        MenuItem startNewGame = new MenuItem("Start New Game");
        MenuItem exitGame = new MenuItem("Exit");

        startNewGame.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");
        exitGame.setStyle("-fx-font-size: 14px; -fx-text-fill: black;");

        startNewGame.setOnAction(event -> GameController.getInstance().startGame());
        exitGame.setOnAction(event -> stage.close());

        gameMenu.getItems().addAll(startNewGame, exitGame);
        gameMenu.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
    
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(gameMenu);
        
        return menuBar;
    }
    
    public void showPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard) {
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

        centerPlayerContainer.setMinSize(150, 200);
        centerPlayerContainer.setMaxSize(150, 200);
        topPlayersContainer.setMinSize(150, 200);
        topPlayersContainer.setMaxSize(150, 200);
        bottomPlayersContainer.setMinSize(150, 200);
        bottomPlayersContainer.setMaxSize(150, 200);
    
        // Ajout de la pioche et de la défausse avec bordure pour tester la visibilité
        PickView pickView = new PickView(remainingCards);
        DiscardView discardView = new DiscardView(topDiscardCard);
    
        pickView.setStyle("-fx-border-color: red; -fx-border-width: 3px;"); // Ajout d'une bordure rouge
        discardView.setStyle("-fx-border-color: blue; -fx-border-width: 3px;"); // Ajout d'une bordure bleue
    
        pickView.setPrefSize(150, 100);
        discardView.setPrefSize(150, 100);
    
        HBox commonPiles = new HBox(20, pickView, discardView);
        commonPiles.setAlignment(Pos.CENTER);
        commonPiles.setStyle("-fx-border-color: green; -fx-border-width: 3px;"); // Bordure verte pour voir si le conteneur est affiché
        commonPiles.setPrefSize(150,200);
        commonPiles.setMaxSize(150,200);
    
        VBox mainContainer = new VBox(20, topPlayersContainer, centerPlayerContainer, bottomPlayersContainer, commonPiles);
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
}
