package com.checkers.client.ui.elements;

import com.checkers.client.mechanics.sound.gameSound;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;


public class Piece extends Group{

    private static final double SIZE = 70.0;
    private final String color;
    public boolean isKing=false;
    private int x;
    private int y;
    private final String path;
    ImageView imageView;
    private final double resolutionMultiplier;
    public Piece(String color,String path,int x,int y, double resolutionMultiplier) {
        Image image = new Image(path);
        this.imageView = new ImageView(image);
        imageView.setFitWidth(SIZE*resolutionMultiplier);
        imageView.setFitHeight(SIZE*resolutionMultiplier);
        this.getChildren().add(imageView);
        this.x=x;
        this.y=y;
        this.color=color;
        this.path=path;
        this.resolutionMultiplier=resolutionMultiplier;
    }

    public boolean isKing() {
        return isKing;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public String getColour() {
        return color;
    }
    public void setX(int x) {
        this.x=x;
    }
    public void setY(int y) {
        this.y=y;
    }
    public void removePieceFromBoard() {
        StackPane stackPane = (StackPane) this.getParent();
        stackPane.getChildren().remove(this);
    }
    public void makeKing()
    {
        if(!isKing) {
            this.isKing = true;
            Image image;
            if (Objects.equals(color, "Dark")) {
                image = new Image("DarkQueen.png");
            } else {
                image = new Image("LightQueen.png");
            }
            imageView.setImage(image);
            gameSound.playPromoteSound();
        }
    }

    public void makePawn() {
        this.isKing = false;
        Image image;
        if(Objects.equals(color, "Dark"))
        {
            image = new Image("DarkPiece.png");
        }
        else {
            image = new Image("LightPiece.png");
        }
        imageView.setImage(image);
    }

    public Piece clone() {
            Piece cloned = new Piece(this.color,this.path,this.x,this.y,this.resolutionMultiplier);
            return cloned;
    }
}