package skyjo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SkyjoGame extends Application {

    // Création de la scène principale
    @Override
    public void start(Stage primaryStage) {
        // Création d'une VBox pour la mise en page
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2d2d2d;");

        // Titre du jeu
        javafx.scene.control.Label title = new javafx.scene.control.Label("Skyjo");
        title.setFont(new Font(36));
        title.setTextFill(Color.WHITE);

        // Bouton pour démarrer une nouvelle partie
        Button startButton = new Button("Démarrer");
        startButton.setFont(new Font(20));
        startButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white;");
        startButton.setOnAction(e -> startNewGame());

        // Ajout du titre et du bouton dans la VBox
        root.getChildren().addAll(title, startButton);

        // Création de la scène
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Skyjo - Jeu");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Méthode pour démarrer une nouvelle partie
    private void startNewGame() {
        // Affichage temporaire d'une nouvelle scène pour commencer le jeu
        System.out.println("Nouvelle partie commencée !");
        // Ici, tu pourrais remplacer par la logique pour générer les cartes et gérer l'interface du jeu.
    }

    public static void main(String[] args) {
        launch(args);
    }
}
