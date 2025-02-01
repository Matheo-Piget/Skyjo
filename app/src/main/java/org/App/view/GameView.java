package org.App.view;

import java.util.List;

import org.App.model.Card;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameView {
    private final Stage stage;
    private final Button piocherButton;
    private final Button passerButton;
    private final Text nomJoueur;
    private final VBox cardsContainer;

    public GameView(Stage stage) {
        this.stage = stage;
        this.piocherButton = new Button("Piocher");
        this.passerButton = new Button("Passer le tour");
        this.nomJoueur = new Text();
        this.cardsContainer = new VBox(10);
    }

    public void afficherJeu(String nomJoueurActuel, List<Card> cartesJoueur) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2d2d2d;");

        // Nom du joueur
        nomJoueur.setText("C'est au tour de : " + nomJoueurActuel);
        nomJoueur.setFont(new Font(24));
        nomJoueur.setStyle("-fx-fill: white;");

        // Affichage des cartes du joueur
        cardsContainer.getChildren().clear();
        for (Card carte : cartesJoueur) {
            Text cardText = new Text(carte.valeur().toString());
            cardText.setFont(new Font(18));
            cardText.setStyle("-fx-fill: white;");
            cardsContainer.getChildren().add(cardText);
        }

        // Boutons d'actions
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.getChildren().addAll(piocherButton, passerButton);

        root.getChildren().addAll(nomJoueur, cardsContainer, buttonsBox);

        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public Button getPiocherButton() {
        return piocherButton;
    }

    public Button getPasserButton() {
        return passerButton;
    }
}
