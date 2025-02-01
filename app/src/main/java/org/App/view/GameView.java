package org.App.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameView {
    private final Stage stage;
    private final Button startButton;
    private final Text title;

    public GameView(Stage stage) {
        this.stage = stage;
        this.startButton = new Button("Démarrer");
        this.title = new Text("Skyjo");
    }

    public void afficherAccueil(Runnable onStart) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2d2d2d;");

        title.setFont(new Font(36));
        title.setStyle("-fx-fill: white;");

        startButton.setFont(new Font(20));
        startButton.setStyle("-fx-background-color: #008CBA; -fx-text-fill: white;");
        startButton.setOnAction(e -> onStart.run());

        root.getChildren().addAll(title, startButton);
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }
}
