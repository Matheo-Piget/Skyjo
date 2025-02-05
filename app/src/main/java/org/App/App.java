package org.App;

import org.App.view.GameMenuView;

import javafx.application.Application;
import javafx.stage.Stage;

public final class App extends Application {

    public  static App INSTANCE;

    @Override
    public void start(Stage primaryStage) {

        GameMenuView menuView = new GameMenuView(primaryStage);
        INSTANCE = this;
    }

    
    public static App getINSTANCE() {
        return INSTANCE;
    }

    public void restart
    () {
        Stage primaryStage = new Stage();
        start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
