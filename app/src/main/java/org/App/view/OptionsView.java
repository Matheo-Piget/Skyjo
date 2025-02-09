package org.App.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class OptionsView {

    private final Stage stage;
    private final Scene scene;

    public OptionsView(Stage stage) {
        this.stage = stage;

        // Création des éléments de l'interface utilisateur
        VBox optionsContainer = new VBox(20);
        optionsContainer.setPadding(new Insets(30));
        optionsContainer.setAlignment(Pos.CENTER);
        optionsContainer.setStyle("-fx-background-color: #34495e; -fx-padding: 40px; -fx-border-radius: 15px;");

        Label title = new Label("Options");
        title.setFont(new javafx.scene.text.Font("Arial", 32));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(5, Color.BLACK));

        // ComboBox pour sélectionner le thème
        Label themeLabel = new Label("Sélectionner le thème :");
        themeLabel.setTextFill(Color.WHITE);
        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Clair", "Sombre");
        themeComboBox.setValue("Clair");

        // ComboBox pour sélectionner le mode de jeu
        Label modeLabel = new Label("Sélectionner le mode de jeu :");
        modeLabel.setTextFill(Color.WHITE);
        ComboBox<String> modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll("Classique", "Rapide");
        modeComboBox.setValue("Classique");

        // Bouton pour sauvegarder les options
        Button saveButton = createStyledButton("Sauvegarder");
        saveButton.setOnAction(e -> saveOptions(themeComboBox.getValue(), modeComboBox.getValue()));

        // Bouton pour revenir au menu principal
        Button backButton = createStyledButton("Retour");
        backButton.setOnAction(e -> goBackToMenu());

        optionsContainer.getChildren().addAll(title, themeLabel, themeComboBox, modeLabel, modeComboBox, saveButton, backButton);

        this.scene = new Scene(optionsContainer, 700, 500);
        this.scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        stage.show();
    }

    private void saveOptions(String theme, String mode) {
        // Logique pour sauvegarder les options sélectionnées
        System.out.println("Thème sélectionné : " + theme);
        System.out.println("Mode de jeu sélectionné : " + mode);
        // Vous pouvez ajouter ici la logique pour appliquer les options sélectionnées
    }

    private void goBackToMenu() {
        GameMenuView gameMenuView = new GameMenuView(stage);
        stage.setScene(gameMenuView.getScene());
        stage.setFullScreen(true);
        gameMenuView.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button"); // Assurez-vous que cette classe est bien dans votre fichier CSS
        button.setPrefSize(200, 40);
        return button;
    }
}