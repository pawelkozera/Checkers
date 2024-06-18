package com.checkers.client;

import com.checkers.client.ui.views.GameWindow;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class CheckersApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getStylesheets().add(String.valueOf("startStyle.css"));
        Button resolution1Button = new Button("Rozdzielczość 1 (1100x780)");
        resolution1Button.setOnAction(e -> startGame(stage, 1100, 780));
        resolution1Button.getStyleClass().add("whiteButton");
        Button resolution2Button = new Button("Rozdzielczość 2 (800x600)");
        resolution2Button.setOnAction(e -> startGame(stage, 800, 600));
        resolution2Button.getStyleClass().add("whiteButton");
        resolution2Button.setPrefSize(200,20);
        Button resolution3Button = new Button("Rozdzielczość 3 (1400x950)");
        resolution3Button.setOnAction(e -> startGame(stage, 1400, 950));
        resolution3Button.getStyleClass().add("whiteButton");
        root.getChildren().addAll(resolution1Button, resolution2Button, resolution3Button);
        root.getStyleClass().add("startBackground");
        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("Checkers");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void startGame(Stage stage, int width, int height) {
        Scene scene = new Scene(new GameWindow(width,height), width, height);
        stage.setScene(scene);
        stage.setTitle("Checkers");
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch();
    }
}