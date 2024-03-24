package com.checkers;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class Menu extends Pane {
    Button playSingle = new Button("Zagraj z komputerem");
    Button playMulti= new Button("Zagraj online");
    Button playDark = new Button("Ciemne");
    Button playLight= new Button("Jasne");
   public  Menu(BorderPane gameBoard, double resolutionMultiplier) {
        if(resolutionMultiplier>1)
            getStylesheets().add(String.valueOf("menuStyleMax.css"));
        else if(resolutionMultiplier>0.75)
        getStylesheets().add(String.valueOf("menuStyleDefault.css"));
        else
            getStylesheets().add(String.valueOf("menuStyleMin.css"));
        playSingle.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);
        playMulti.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);
        playSingle.setLayoutX(50*resolutionMultiplier);
        playSingle.setLayoutY(120*resolutionMultiplier);
        playMulti.setLayoutX(50*resolutionMultiplier);
        playMulti.setLayoutY(250*resolutionMultiplier);
        playSingle.getStyleClass().add("darkCyanButton");
        playMulti.getStyleClass().add("darkCyanButton");

        playDark.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);
        playLight.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);
        playLight.setLayoutX(50*resolutionMultiplier);
        playLight.setLayoutY(120*resolutionMultiplier);
        playDark.setLayoutX(50*resolutionMultiplier);
        playDark.setLayoutY(250*resolutionMultiplier);
        playLight.getStyleClass().add("lightButton");
        playDark.getStyleClass().add("darkButton");

        this.setPrefSize(300*resolutionMultiplier,620*resolutionMultiplier);
        this.getChildren().addAll(playSingle,playMulti);
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);-fx-background-radius: 10;-fx-border-color: #183037;-fx-border-radius: 10;");

        playSingle.setOnMouseClicked(event->{
            OnPlaySingleClick();
        });
       playDark.setOnMouseClicked(event->{
           OnColorPlayClick();
           gameBoard.setRotate(180);
       });
       playLight.setOnMouseClicked(event->{
           OnColorPlayClick();
       });
    }
    public void OnPlaySingleClick() {
    playSingle.setDisable(true);
    playSingle.setVisible(false);
    playMulti.setDisable(true);
    playMulti.setVisible(false);
    this.getChildren().addAll(playDark,playLight);
    }
    public void OnColorPlayClick() {
        playDark.setDisable(true);
        playDark.setVisible(false);
        playLight.setDisable(true);
        playLight.setVisible(false);
    }
}
