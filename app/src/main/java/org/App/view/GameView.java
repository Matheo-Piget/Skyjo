package org.App.view;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameView {
    private final Stage stage;
    private final Text nomJoueur;
    private final VBox cardsContainer;

    public GameView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Skyjo");
        stage.setWidth(1920);
        stage.setHeight(1100);
        this.nomJoueur = new Text();
        this.cardsContainer = new VBox(10);
        // Crée la scène avec le root (si ce n'est pas déjà fait)
        if (stage.getScene() == null) {
            stage.setScene(new javafx.scene.Scene(new StackPane(cardsContainer)));
        }
    }

    public void showMessageBox(String message) {
        // Clear the previous content
        cardsContainer.getChildren().clear();
    
        // Create a Text instance with the message
        Text messageText = new Text(message);
    
        // Add the message to the container
        cardsContainer.getChildren().add(messageText);
    
        stage.show();
    }

    public void afficherJeu(String nomJoueurActuel, java.util.List<org.App.model.Card> cartesJoueur, int remainingCards, org.App.model.Card topDiscardCard) {
        // Clear the previous content
        cardsContainer.getChildren().clear();
    
        // Update the player's name
        nomJoueur.setText("Joueur actuel: " + nomJoueurActuel);
    
        // Create CardView instances for each Card
        java.util.List<CardView> cardViews = new java.util.ArrayList<>();
        for (int i = 0; i < cartesJoueur.size(); i++) {
            cardViews.add(new CardView(cartesJoueur.get(i), i));
        }
    
        // Create BoardView with the CardView instances
        BoardView boardView = new BoardView(cardViews);
    
        // Create PickView with the remaining cards
        PickView pickView = new PickView(remainingCards);
    
        // Create DiscardView with the top discard card
        DiscardView discardView = new DiscardView(topDiscardCard);
    
        // Add the player's name, the board, and the pick view to the container
        javafx.scene.layout.HBox gameContainer = new javafx.scene.layout.HBox(20, boardView, pickView, discardView);
        cardsContainer.getChildren().addAll(nomJoueur, gameContainer);
    
        stage.show();
    }
}
