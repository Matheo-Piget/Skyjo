package org.App;

import org.App.controller.GameController;
import org.App.view.GameView;

import javafx.application.Application;
import javafx.stage.Stage;

public final class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        GameView view = new GameView(primaryStage);
        GameController controller = new GameController(view);

        view.afficherAccueil(controller::startGame);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
