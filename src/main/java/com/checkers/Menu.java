package com.checkers;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class Menu extends Pane {
    Button playSingle;
    Button playMulti;
    Button playOffline;
    Button playDark = new Button("Ciemne");
    Button playLight= new Button("Jasne");
    public  Menu(double resolutionMultiplier) {
        if(resolutionMultiplier>1)
            getStylesheets().add(String.valueOf("menuStyleMax.css"));
        else if(resolutionMultiplier>0.75)
            getStylesheets().add(String.valueOf("menuStyleDefault.css"));
        else
            getStylesheets().add(String.valueOf("menuStyleMin.css"));

        Image imageGlobe = new Image("globe.png");
        ImageView imageViewGlobe = new ImageView(imageGlobe);
        imageViewGlobe.setFitWidth(50*resolutionMultiplier);
        imageViewGlobe.setFitHeight(50*resolutionMultiplier);

        playMulti= new Button("Zagraj online", imageViewGlobe);
        playMulti.setLayoutX(50*resolutionMultiplier);
        playMulti.setLayoutY(250*resolutionMultiplier);
        playMulti.getStyleClass().add("whiteButton");
        playMulti.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);

        Image imageComputer = new Image("computer.png");
        ImageView imageViewComputer = new ImageView(imageComputer);
        imageViewComputer.setFitWidth(50*resolutionMultiplier);
        imageViewComputer.setFitHeight(50*resolutionMultiplier);
        playSingle = new Button("Zagraj ze SI",imageViewComputer);
        playSingle.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);
        playSingle.setLayoutX(50*resolutionMultiplier);
        playSingle.setLayoutY(380*resolutionMultiplier);
        playSingle.getStyleClass().add("whiteButton");

        Image imageOffline = new Image("offline.png");
        ImageView imageViewOffline= new ImageView(imageOffline);
        imageViewOffline.setFitWidth(50*resolutionMultiplier);
        imageViewOffline.setFitHeight(50*resolutionMultiplier);
        playOffline= new Button("Zagraj offline", imageViewOffline);
        playOffline.setLayoutX(50*resolutionMultiplier);
        playOffline.setLayoutY(120*resolutionMultiplier);
        playOffline.getStyleClass().add("whiteButton");
        playOffline.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);

        playDark.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);
        playLight.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);
        playLight.setLayoutX(50*resolutionMultiplier);
        playLight.setLayoutY(120*resolutionMultiplier);
        playDark.setLayoutX(50*resolutionMultiplier);
        playDark.setLayoutY(250*resolutionMultiplier);
        playLight.getStyleClass().add("lightButton");
        playDark.getStyleClass().add("darkButton");
        playLight.setVisible(false);
        playDark.setDisable(true);
        playLight.setDisable(true);
        playDark.setVisible(false);
        this.setPrefSize(300*resolutionMultiplier,620*resolutionMultiplier);
        this.getChildren().addAll(playSingle,playMulti,playOffline,playDark,playLight);
        this.getStyleClass().add("mainPane");

        playOffline.setOnMouseClicked(event->{
            OnPlaySingleClick();
        });
    }
    public void OnPlaySingleClick() {
        playSingle.setDisable(true);
        playSingle.setVisible(false);
        playMulti.setDisable(true);
        playMulti.setVisible(false);
        playOffline.setVisible(false);
        playOffline.setDisable(true);
        playLight.setVisible(true);
        playDark.setDisable(false);
        playLight.setDisable(false);
        playDark.setVisible(true);
    }
    public void OnColorPlayClick() {
        playDark.setDisable(true);
        playDark.setVisible(false);
        playLight.setDisable(true);
        playLight.setVisible(false);
        this.setVisible(false);
        this.setDisable(true);
    }

    public void onPlayMultiClick() {
        playSingle.setDisable(true);
        playSingle.setVisible(false);
        playMulti.setDisable(true);
        playMulti.setVisible(false);
        playOffline.setVisible(false);
        playOffline.setDisable(true);
        this.setVisible(false);
        this.setDisable(true);
    }

    public void restart() {
        playSingle.setDisable(false);
        playSingle.setVisible(true);
        playMulti.setDisable(false);
        playMulti.setVisible(true);
        playOffline.setVisible(true);
        playOffline.setDisable(false);
        playLight.setVisible(false);
        playDark.setDisable(true);
        playLight.setDisable(true);
        playDark.setVisible(false);
        this.setVisible(true);
        this.setDisable(false);
    }

    public Button getPlayDark() {
        return playDark;
    }
    public Button getPlayLight(){
        return playLight;
    }
    public Button getPlayMulti() {
        return playMulti;
    }
}
