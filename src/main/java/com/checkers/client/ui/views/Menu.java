package com.checkers.client.ui.views;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class Menu extends Pane {
    Button playSingle;
    Button playMulti;
    Button playOffline;
    Button playDark = new Button("Ciemne");
    Button playLight= new Button("Jasne");
    Button easyButton;
    Button normalButton;
    Button hardButton;
    Text infoText = new Text("");
    private boolean isAiGame = false;
    private boolean isOfflineMode = false;

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

        Image imageEasy = new Image("easy.png");
        ImageView imageViewEasy = new ImageView(imageEasy);
        imageViewEasy.setFitWidth(145*resolutionMultiplier);
        imageViewEasy.setFitHeight(35*resolutionMultiplier);

        easyButton=new Button("",imageViewEasy);
        easyButton.setPrefHeight(110*resolutionMultiplier);
        easyButton.setPrefWidth(200*resolutionMultiplier);
        easyButton.getStyleClass().add("greenButton");
        easyButton.setLayoutX(50*resolutionMultiplier);
        easyButton.setLayoutY(120*resolutionMultiplier);
        easyButton.setDisable(true);
        easyButton.setVisible(false);

        Image imageMedium = new Image("medium.png");
        ImageView imageViewMedium = new ImageView(imageMedium);
        imageViewMedium.setFitWidth(140*resolutionMultiplier);
        imageViewMedium.setFitHeight(35*resolutionMultiplier);

        normalButton=new Button("",imageViewMedium);
        normalButton.setPrefHeight(110*resolutionMultiplier);
        normalButton.setPrefWidth(200*resolutionMultiplier);
        normalButton.getStyleClass().add("lightButton");
        normalButton.setLayoutX(50*resolutionMultiplier);
        normalButton.setLayoutY(250*resolutionMultiplier);
        normalButton.setDisable(true);
        normalButton.setVisible(false);

        Image imageHard = new Image("hard.png");
        ImageView imageViewHard = new ImageView(imageHard);
        imageViewHard.setFitWidth(145*resolutionMultiplier);
        imageViewHard.setFitHeight(35*resolutionMultiplier);

        hardButton=new Button("",imageViewHard);
        hardButton.setPrefHeight(110*resolutionMultiplier);
        hardButton.setPrefWidth(200*resolutionMultiplier);
        hardButton.getStyleClass().add("darkButton");
        hardButton.setLayoutX(50*resolutionMultiplier);
        hardButton.setLayoutY(380*resolutionMultiplier);
        hardButton.setDisable(true);
        hardButton.setVisible(false);

        infoText.getStyleClass().add("textInfo");

        hardButton.setOnMouseEntered(mouseEvent -> {
            infoText.setLayoutY(550*resolutionMultiplier);
            infoText.setLayoutX(40*resolutionMultiplier);
            infoText.setText("Tryb dla zaawansowanych\ngraczy");
        });
        hardButton.setOnMouseExited(mouseEvent -> {
            infoText.setText("");
        });

        normalButton.setOnMouseEntered(mouseEvent -> {
            infoText.setLayoutY(550*resolutionMultiplier);
            infoText.setLayoutX(30*resolutionMultiplier);
            infoText.setText("Tryb dla graczy preferujących\nwyważoną grę");
        });
        normalButton.setOnMouseExited(mouseEvent -> {
            infoText.setText("");
        });

        easyButton.setOnMouseEntered(mouseEvent -> {
            infoText.setLayoutY(550*resolutionMultiplier);
            infoText.setLayoutX(45*resolutionMultiplier);
            infoText.setText("Tryb dla początkujących\ngraczy");
        });
        easyButton.setOnMouseExited(mouseEvent -> {
            infoText.setText("");
        });

        playMulti= new Button("Zagraj online", imageViewGlobe);
        playMulti.setLayoutX(50*resolutionMultiplier);
        playMulti.setLayoutY(250*resolutionMultiplier);
        playMulti.getStyleClass().add("whiteButton");
        playMulti.setPrefSize(200*resolutionMultiplier,100*resolutionMultiplier);

        Image imageComputer = new Image("computer.png");
        ImageView imageViewComputer = new ImageView(imageComputer);
        imageViewComputer.setFitWidth(50*resolutionMultiplier);
        imageViewComputer.setFitHeight(50*resolutionMultiplier);
        playSingle = new Button("Zagraj z SI",imageViewComputer);
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
        this.getChildren().addAll(playSingle,playMulti,playOffline,playDark,playLight,easyButton,normalButton,hardButton);
        this.getStyleClass().add("mainPane");

        playOffline.setOnMouseClicked(event->{
            isOfflineMode = true;
            isAiGame = false;
            OnPlaySingleClick();
        });

        playSingle.setOnMouseClicked(event-> {
            isOfflineMode = false;
            isAiGame = true;
            OnPlayAIClick();
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

    public void OnPlayAIClick() {
        playSingle.setDisable(true);
        playSingle.setVisible(false);
        playMulti.setDisable(true);
        playMulti.setVisible(false);
        playOffline.setVisible(false);
        playOffline.setDisable(true);
        easyButton.setVisible(true);
        easyButton.setDisable(false);
        normalButton.setVisible(true);
        normalButton.setDisable(false);
        hardButton.setVisible(true);
        hardButton.setDisable(false);
        this.getChildren().add(infoText);
    }
    public void OnColorPlayClick() {
        playDark.setDisable(true);
        playDark.setVisible(false);
        playLight.setDisable(true);
        playLight.setVisible(false);
        this.setVisible(false);
        this.setDisable(true);
    }

    public void OnDifficultyClick() {
        easyButton.setVisible(false);
        easyButton.setDisable(true);
        normalButton.setVisible(false);
        normalButton.setDisable(true);
        hardButton.setVisible(false);
        hardButton.setDisable(true);
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
        easyButton.setVisible(false);
        easyButton.setDisable(true);
        normalButton.setVisible(false);
        normalButton.setDisable(true);
        hardButton.setVisible(false);
        hardButton.setDisable(true);
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
    public Button getEasyButton(){return easyButton;}
    public Button getNormalButton(){return normalButton;}
    public Button getHardButton(){return hardButton;}
    public boolean isAiGame() {
        return isAiGame;
    }

    public boolean isOfflineMode() {
        return isOfflineMode;
    }
}
