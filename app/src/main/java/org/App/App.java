package org.App;

import org.App.view.GameMenuView;

import javafx.application.Application;
import javafx.stage.Stage;

public final class App extends Application {
    @Override
    public void start(Stage primaryStage) {

        GameMenuView menuView = new GameMenuView(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
