package com.checkers;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

import static com.checkers.GameWindow.HEIGHT_BOARD;
import static com.checkers.GameWindow.WIDTH_BOARD;


public class Piece extends Group {

    private static final double SIZE = 70.0;
    private String color;
    public boolean isKing=false;
    private int x;
    private int y;
    public Piece(String color,String path,int x,int y, double resolutionMultiplier) {
        Image image = new Image(path);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(SIZE*resolutionMultiplier);
        imageView.setFitHeight(SIZE*resolutionMultiplier);
        this.getChildren().add(imageView);
        this.x=x;
        this.y=y;
        this.color=color;
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
}