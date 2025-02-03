package org.App.view;

import java.util.ArrayList;
import java.util.List;

import org.App.model.Card;
import org.App.model.Player;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameView {

    private final Stage stage;
    private final VBox cardsContainer;

    public GameView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Skyjo");
        stage.setFullScreen(true);

        this.cardsContainer = new VBox(20);
        this.cardsContainer.setAlignment(Pos.CENTER);

        // Create Menu Bar
        MenuBar menuBar = createMenuBar();

        StackPane root = new StackPane(cardsContainer);
        StackPane.setAlignment(cardsContainer, Pos.CENTER);

        // Add menu bar to the scene
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(root);

        if (stage.getScene() == null) {
            stage.setScene(new Scene(borderPane));
        }
    }

    // Create a simple MenuBar with options
    private MenuBar createMenuBar() {
        Menu menu = new Menu("Game");
        MenuItem startNewGame = new MenuItem("Start New Game");
        MenuItem exitGame = new MenuItem("Exit");

        startNewGame.setOnAction(event -> {
            // Add logic to start a new game
            System.out.println("Starting new game...");
        });

        exitGame.setOnAction(event -> {
            stage.close();  // Close the application
        });

        menu.getItems().addAll(startNewGame, exitGame);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        return menuBar;
    }
    
    public void showPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard) {
        cardsContainer.getChildren().clear();
        

        int indexCurrentPlayer = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(currentPlayerName)) {
                indexCurrentPlayer = i;
                break;
            }
        }

        VBox centerPlayerContainer = createPlayerBoard(players.get(indexCurrentPlayer), true);
        HBox topPlayersContainer = new HBox(40);
        HBox bottomPlayersContainer = new HBox(40);
        topPlayersContainer.setAlignment(Pos.CENTER);
        bottomPlayersContainer.setAlignment(Pos.CENTER);
        
        List<VBox> sidePlayers = new ArrayList<>();
        for (int i = 1; i < players.size(); i++) {
            int playerIndex = (indexCurrentPlayer + i) % players.size();
            sidePlayers.add(createPlayerBoard(players.get(playerIndex), false));
        }
        
        for (int i = 0; i < sidePlayers.size(); i++) {
            if (i % 2 == 0) {
                topPlayersContainer.getChildren().add(sidePlayers.get(i));
            } else {
                bottomPlayersContainer.getChildren().add(sidePlayers.get(i));
            }
        }
        
        PickView pickView = new PickView(remainingCards);
        DiscardView discardView = new DiscardView(topDiscardCard);
        HBox commonPiles = new HBox(40, pickView, discardView);
        commonPiles.setAlignment(Pos.CENTER);
        
        VBox mainContainer = new VBox(20, topPlayersContainer, centerPlayerContainer, bottomPlayersContainer, commonPiles);
        mainContainer.setAlignment(Pos.CENTER);

        // Shadow effect for containers
        DropShadow shadow = new DropShadow(10, 5, 5, Color.GRAY);
        mainContainer.setEffect(shadow);

        DropShadow shadow2 = new DropShadow(10, 5, 5, Color.GRAY);
        centerPlayerContainer.setEffect(shadow2);
        
        cardsContainer.getChildren().add(mainContainer);
        stage.show();
    }

    private VBox createPlayerBoard(Player player, boolean isCurrent) {
        Text playerNameText = new Text(player.getName());
        playerNameText.setStyle(isCurrent ? "-fx-font-weight: bold; -fx-font-size: 20px;" : "-fx-font-size: 16px;");
        
        List<CardView> cardViews = new ArrayList<>();
        for (int i = 0; i < player.getCartes().size(); i++) {
            cardViews.add(new CardView(player.getCartes().get(i), i));
        }
        
        BoardView boardView = new BoardView(cardViews);
        boardView.setAlignment(Pos.CENTER);
        VBox playerContainer = new VBox(5, playerNameText, boardView);
        playerContainer.setAlignment(Pos.CENTER);
        
        return playerContainer;
    }

    public void showMessageBox(String message) {
        cardsContainer.getChildren().clear();
        Text messageText = new Text(message);
        cardsContainer.getChildren().add(messageText);
        stage.show();
    }

    public void showRanking(java.util.Map<Player, Integer> ranking) {
        cardsContainer.getChildren().clear();
        VBox rankingContainer = new VBox(10);
        rankingContainer.setAlignment(Pos.CENTER);

        Text rankingTitle = new Text("Classement des joueurs:");
        rankingContainer.getChildren().add(rankingTitle);

        ranking.forEach((player, score) -> {
            Text playerScore = new Text(player.getName() + ": " + score);
            rankingContainer.getChildren().add(playerScore);
        });

        cardsContainer.getChildren().add(rankingContainer);
        stage.show();
    }
}
