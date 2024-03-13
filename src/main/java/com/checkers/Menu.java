package com.checkers;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Menu extends Pane {
    Button playSingle = new Button("Zagraj z komputerem");
    Button playMulti= new Button("Zagraj online");
   public  Menu() {
       getStylesheets().add(String.valueOf("menuStyle.css"));
        playSingle.setPrefSize(200,100);
        playMulti.setPrefSize(200,100);
        playSingle.setLayoutX(50);
        playSingle.setLayoutY(120);
       playMulti.setLayoutX(50);
       playMulti.setLayoutY(250);
        playSingle.getStyleClass().add("darkCyanButton");
        playMulti.getStyleClass().add("darkCyanButton");
        this.setPrefSize(300,620);
        this.getChildren().addAll(playSingle,playMulti);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);-fx-background-radius: 10;");
    }
}
