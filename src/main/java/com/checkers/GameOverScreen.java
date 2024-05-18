package com.checkers;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.Objects;

public class GameOverScreen extends Pane {

    private Button goBackButton;
    private Button restartButton;
    private ImageView winnerImageView;
    private Text header;
    public GameOverScreen(double resolutionMultiplier)
    {
        if(resolutionMultiplier>1)
            getStylesheets().add(String.valueOf("GameOverScreenMax.css"));
        else if(resolutionMultiplier>0.75)
            getStylesheets().add(String.valueOf("GameOverScreenDefault.css"));
        else
            getStylesheets().add(String.valueOf("GameOverScreenMin.css"));

        this.setLayoutX(190*resolutionMultiplier);
        this.setLayoutY(237*resolutionMultiplier);
        this.setPrefSize(420*resolutionMultiplier,320*resolutionMultiplier);
        this.setDisable(true);
        this.setVisible(false);
        initializeHeader(resolutionMultiplier);
        initializeImageView(resolutionMultiplier);
        initializeButtons(resolutionMultiplier);
    }
    private void initializeHeader(double resolutionMultiplier)
    {
        this.header=new Text("Wygrał gracz:");
        header.getStyleClass().add("header");
        header.setLayoutX(110*resolutionMultiplier);
        header.setLayoutY(50*resolutionMultiplier);
        this.getChildren().add(header);
    }

    private void initializeImageView(double resolutionMultiplier)
    {
        winnerImageView=new ImageView();
        this.getChildren().add(winnerImageView);
        winnerImageView.setFitWidth(120*resolutionMultiplier);
        winnerImageView.setFitHeight(120*resolutionMultiplier);
        winnerImageView.setLayoutX(146*resolutionMultiplier);
        winnerImageView.setLayoutY(90*resolutionMultiplier);
    }
    private void initializeButtons(double resolutionMultiplier)
    {
        goBackButton=new Button("Powrót");
        restartButton=new Button("Zagraj\nponownie");
        goBackButton.getStyleClass().add("button");
        restartButton.getStyleClass().add("button");
        this.getChildren().addAll(goBackButton,restartButton);
        goBackButton.setPrefSize(130*resolutionMultiplier,70*resolutionMultiplier);
        restartButton.setPrefSize(130*resolutionMultiplier,70*resolutionMultiplier);
        restartButton.setLayoutX(240*resolutionMultiplier);
        goBackButton.setLayoutX(50*resolutionMultiplier);
        restartButton.setLayoutY(230*resolutionMultiplier);
        goBackButton.setLayoutY(230*resolutionMultiplier);
    }
    public void setUpScreen(String winner)
    {
        this.setVisible(true);
        this.setDisable(false);
        if(Objects.equals(winner, "dark"))
        {
            this.getStyleClass().clear();
            this.getStyleClass().add("darkWinner");
            Image avatar= new Image("AvatarPlayerDark.png");
            winnerImageView.setImage(avatar);
        }
        else if(Objects.equals(winner, "light"))
        {
            this.getStyleClass().clear();
            this.getStyleClass().add("lightWinner");
            Image avatar= new Image("AvatarPlayerLight.png");
            winnerImageView.setImage(avatar);
        }
        else if(Objects.equals(winner,"draw"))
        {
            this.getStyleClass().add("draw");
            this.header.setText("Remis");
            header.setLayoutX(header.getLayoutX()*1.5);
            Image avatar= new Image("AvatarDraw.png");
            winnerImageView.setImage(avatar);
        }
        gameSound.playGameEndSound();
    }
    public Button getRestartButton() {return restartButton;}
    public Button getGoBackButton() {
        return goBackButton;
    }
}
