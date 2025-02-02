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
    
    /**
     * Affiche le plateau de jeu avec tous les boards des joueurs (côte à côte) 
     * et les piles communes (pioche et défausse) centrées.
     * 
     * @param players Liste de tous les joueurs.
     * @param currentPlayerName Nom du joueur actif (pour le mettre en avant).
     * @param remainingCards Nombre de cartes restantes dans la pioche.
     * @param topDiscardCard Carte visible au sommet de la défausse (peut être null).
     */
    public void showPlaying(List<Player> players, String currentPlayerName, int remainingCards, Card topDiscardCard) {
        cardsContainer.getChildren().clear();
        
        // Conteneur horizontal pour disposer côte à côte les boards des joueurs
        HBox playersBoards = new HBox(20);
        playersBoards.setAlignment(Pos.CENTER);

        // Parcours de tous les joueurs pour créer leur board
        for (Player player : players) {
            Text playerNameText = new Text(player.getName());
            // Mise en avant du joueur actif (par exemple en gras)
            if (player.getName().equals(currentPlayerName)) {
                playerNameText.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            } else {
                playerNameText.setStyle("-fx-font-size: 16px;");
            }
            
            // Création de la liste de CardView pour le joueur
            List<CardView> cardViews = new ArrayList<>();
            for (int i = 0; i < player.getCartes().size(); i++) {
                cardViews.add(new CardView(player.getCartes().get(i), i));
            }
            // Création du board du joueur
            BoardView boardView = new BoardView(cardViews);
            
            // Conteneur vertical pour le nom et le board du joueur
            VBox playerContainer = new VBox(5, playerNameText, boardView);
            playerContainer.setAlignment(Pos.CENTER);
            
            playersBoards.getChildren().add(playerContainer);
        }
        
        // Création de la vue pour la pioche et la défausse (communes à tous)
        PickView pickView = new PickView(remainingCards);
        DiscardView discardView = new DiscardView(topDiscardCard);
        HBox commonPiles = new HBox(40, pickView, discardView);
        commonPiles.setAlignment(Pos.CENTER);
        
        // Conteneur principal pour l'ensemble de l'affichage
        VBox mainContainer = new VBox(20, playersBoards, commonPiles);
        mainContainer.setAlignment(Pos.CENTER);
        
        cardsContainer.getChildren().add(mainContainer);
        stage.show();
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
