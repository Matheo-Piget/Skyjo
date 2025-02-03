package org.App;

import java.util.List;

import org.App.controller.GameController;
import org.App.model.HumanPlayer;
import org.App.model.Player;
import org.App.view.GameView;

import javafx.application.Application;
import javafx.stage.Stage;

public final class App extends Application {
    @Override
    public void start(Stage primaryStage) {

        GameView view = new GameView(primaryStage);

        List<Player> players = List.of(
            new HumanPlayer("Joueur 1"),
            new HumanPlayer("Joueur 2")
        );
        GameController controller = new GameController(view, players);

        controller.startGame();  // Démarre le jeu et les interactions
    }

    public static void main(String[] args) {
        launch(args);
    }
}
