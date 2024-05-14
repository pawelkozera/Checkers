package com.checkers;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GameInfoScreen extends VBox {

    private Pane playerDarkHalf;
    private Pane playerLightHalf;
    private Pane blockingPane;
    private Text playerDarkName;
    private Text playerLightName;
    private Text playerDarkAdvantage;
    private Text playerLightAdvantage;
    private ImageView avatarPlayerLight;
    private ImageView avatarPlayerDark;
    private GridPane lightPlayerPieceContainer;
    private GridPane darkPlayerPieceContainer;
    private List <ImageView> CapturedLightPieces=new ArrayList<>();
    private List <ImageView>  CapturedDarkPieces=new ArrayList<>();
    public GameInfoScreen(double resolutionMultiplier) {

        this.setVisible(false);
        this.setDisable(true);
        this.setPrefSize(300*resolutionMultiplier,620*resolutionMultiplier);

        if(resolutionMultiplier>1)
            getStylesheets().add(String.valueOf("GameInfoScreenMax.css"));
        else if(resolutionMultiplier>0.75)
            getStylesheets().add(String.valueOf("GameInfoScreenDefault.css"));
        else
            getStylesheets().add(String.valueOf("GameInfoScreenMin.css"));

        initializeHalfPanes(resolutionMultiplier);
        initializeBlockingPane(resolutionMultiplier);
        this.getStyleClass().add("main");

    }

    private void initializeHalfPanes(double resolutionMultiplier)
    {
        playerLightHalf=new Pane();
        playerDarkHalf=new Pane();

        playerLightHalf.setPrefSize(300*resolutionMultiplier,310*resolutionMultiplier);
        playerDarkHalf.setPrefSize(300*resolutionMultiplier,310*resolutionMultiplier);

        playerLightHalf.getStyleClass().add("half");
        playerDarkHalf.getStyleClass().add("half");

        initializeAvatars(resolutionMultiplier);
        initializeNamesText(resolutionMultiplier);
        initializeHBoxContainer(resolutionMultiplier);
        initializeAdvantageText(resolutionMultiplier);
    }

    private void initializeAvatars(double resolutionMultiplier)
    {
        Image avatar1= new Image("AvatarPlayerLight.png");
        Image avatar2= new Image("AvatarPlayerDark.png");
        avatarPlayerLight=new ImageView(avatar1);
        avatarPlayerDark=new ImageView(avatar2);
        avatarPlayerLight.setFitWidth(100*resolutionMultiplier);
        avatarPlayerLight.setFitHeight(100*resolutionMultiplier);
        avatarPlayerLight.setLayoutX(20*resolutionMultiplier);
        avatarPlayerLight.setLayoutY(30*resolutionMultiplier);

        avatarPlayerDark.setFitWidth(100*resolutionMultiplier);
        avatarPlayerDark.setFitHeight(100*resolutionMultiplier);
        avatarPlayerDark.setLayoutX(20*resolutionMultiplier);
        avatarPlayerDark.setLayoutY(30*resolutionMultiplier);
        playerLightHalf.getChildren().add(avatarPlayerLight);
        playerDarkHalf.getChildren().add(avatarPlayerDark);
    }

    private void initializeNamesText(double resolutionMultiplier)
    {
        playerLightName=new Text();
        playerDarkName=new Text();
        playerDarkName.getStyleClass().add("name");
        playerLightName.getStyleClass().add("name");

        playerLightName.setLayoutX(140*resolutionMultiplier);
        playerLightName.setLayoutY(90*resolutionMultiplier);

        playerDarkName.setLayoutX(140*resolutionMultiplier);
        playerDarkName.setLayoutY(90*resolutionMultiplier);

        playerLightHalf.getChildren().add(playerLightName);
        playerDarkHalf.getChildren().add(playerDarkName);
    }
    private void initializeAdvantageText(double resolutionMultiplier)
    {
        playerLightAdvantage=new Text();
        playerDarkAdvantage=new Text();
        playerDarkAdvantage.getStyleClass().add("name");
        playerLightAdvantage.getStyleClass().add("name");

        playerLightAdvantage.setLayoutX(140*resolutionMultiplier);
        playerLightAdvantage.setLayoutY(280*resolutionMultiplier);

        playerDarkAdvantage.setLayoutX(140*resolutionMultiplier);
        playerDarkAdvantage.setLayoutY(280*resolutionMultiplier);

        playerLightHalf.getChildren().add(playerLightAdvantage);
        playerDarkHalf.getChildren().add(playerDarkAdvantage);

    }
    private void initializeHBoxContainer(double resolutionMultiplier)
    {
        Image darkPieceImage= new Image("DarkPiece.png");
        Image lightPieceImage= new Image("LightPiece.png");
        lightPlayerPieceContainer=new GridPane();
        darkPlayerPieceContainer=new GridPane();
        lightPlayerPieceContainer.setHgap(5*resolutionMultiplier);
        darkPlayerPieceContainer.setHgap(5*resolutionMultiplier);
        lightPlayerPieceContainer.setVgap(5*resolutionMultiplier);
        darkPlayerPieceContainer.setVgap(5*resolutionMultiplier);
        lightPlayerPieceContainer.setLayoutX(resolutionMultiplier*15);
        lightPlayerPieceContainer.setLayoutY(resolutionMultiplier*150);
        darkPlayerPieceContainer.setLayoutX(resolutionMultiplier*15);
        darkPlayerPieceContainer.setLayoutY(resolutionMultiplier*150);

        for(int i=0;i<12;i++)
        {
            ImageView imageViewLight=new ImageView(lightPieceImage);
            imageViewLight.setFitHeight(resolutionMultiplier*40);
            imageViewLight.setFitWidth(resolutionMultiplier*40);
            ImageView imageViewDark=new ImageView(darkPieceImage);
            imageViewDark.setFitHeight(resolutionMultiplier*40);
            imageViewDark.setFitWidth(resolutionMultiplier*40);
            imageViewLight.setVisible(false);
            imageViewDark.setVisible(false);
            CapturedLightPieces.add(imageViewLight);
            CapturedDarkPieces.add(imageViewDark);

            int columnIndex = i % 6;
            int rowIndex = i / 6;

            lightPlayerPieceContainer.add(imageViewDark, columnIndex, rowIndex);
            darkPlayerPieceContainer.add(imageViewLight, columnIndex, rowIndex);
        }

        playerLightHalf.getChildren().add(lightPlayerPieceContainer);
        playerDarkHalf.getChildren().add(darkPlayerPieceContainer);
    }
    private void initializeBlockingPane(double resolutionMultiplier)
    {
        blockingPane=new Pane();
        blockingPane.setPrefSize(295*resolutionMultiplier,306*resolutionMultiplier);
        blockingPane.getStyleClass().add("block");
    }
    public void setUpScreen(boolean isLightColor)
    {
        if(isLightColor) {
            this.getChildren().addAll(playerDarkHalf, playerLightHalf);
            playerLightName.setText("Ty");
            playerDarkName.setText("Przeciwnik");
        }
        else {
            this.getChildren().addAll(playerLightHalf, playerDarkHalf);
            playerLightName.setText("Przeciwnik");
            playerDarkName.setText("Ty");
        }
        if(!isLightColor) {
            playerLightHalf.getChildren().add(blockingPane);
        }
        else
            playerDarkHalf.getChildren().add(blockingPane);
    }

    public void refreshGameInfoScreen(int numberOfCapturedLightPieces,int numberOfCapturedDarkPieces, boolean isLightTurn)
    {
        for(int i=0;i<numberOfCapturedLightPieces;i++)
        {
            CapturedLightPieces.get(i).setVisible(true);
        }
        for(int i=0;i<numberOfCapturedDarkPieces;i++)
        {
            CapturedDarkPieces.get(i).setVisible(true);
        }
        setAdvantage(numberOfCapturedLightPieces,numberOfCapturedDarkPieces);
        if(!isLightTurn) {
            playerLightHalf.getChildren().add(blockingPane);
        }
        else
            playerDarkHalf.getChildren().add(blockingPane);
    }
    private void setAdvantage(int numberOfCapturedLightPieces,int numberOfCapturedDarkPieces) {

        playerDarkAdvantage.setText("");
        playerLightAdvantage.setText("");
        if (numberOfCapturedLightPieces > numberOfCapturedDarkPieces) {
            int advantage = numberOfCapturedLightPieces - numberOfCapturedDarkPieces;
            playerDarkAdvantage.setText("+" + advantage);
        } else if (numberOfCapturedLightPieces < numberOfCapturedDarkPieces) {
            int advantage = numberOfCapturedDarkPieces - numberOfCapturedLightPieces;
            playerLightAdvantage.setText("+" + advantage);
        } else {
            playerDarkAdvantage.setText("");
            playerLightAdvantage.setText("");
        }
    }
}
