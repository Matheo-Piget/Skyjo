package org.App.view;

import java.util.ArrayList;
import java.util.List;

import org.App.model.Card;
import org.App.model.Player;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameView {

    private final Stage stage;
    private final VBox cardsContainer;

    public GameView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Skyjo");
        stage.setWidth(1920);
        stage.setHeight(1100);

        this.cardsContainer = new VBox(20);
        this.cardsContainer.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(cardsContainer);
        StackPane.setAlignment(cardsContainer, Pos.CENTER);

        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        }
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
        HBox sidePlayersContainer = new HBox(40);
        sidePlayersContainer.setAlignment(Pos.CENTER);
        
        for (int i = 1; i <= players.size() / 2; i++) {
            int leftIndex = (indexCurrentPlayer - i + players.size()) % players.size();
            int rightIndex = (indexCurrentPlayer + i) % players.size();
            
            VBox leftPlayer = createPlayerBoard(players.get(leftIndex), false);
            VBox rightPlayer = createPlayerBoard(players.get(rightIndex), false);
            
            sidePlayersContainer.getChildren().addAll(leftPlayer, rightPlayer);
        }
        
        PickView pickView = new PickView(remainingCards);
        DiscardView discardView = new DiscardView(topDiscardCard);
        HBox commonPiles = new HBox(40, pickView, discardView);
        commonPiles.setAlignment(Pos.CENTER);
        
        VBox mainContainer = new VBox(20, sidePlayersContainer, centerPlayerContainer, commonPiles);
        mainContainer.setAlignment(Pos.CENTER);
        
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

    public void showRanking(java.util.HashMap<Player, Integer> ranking) {
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
