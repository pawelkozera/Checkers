package com.checkers;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Piece extends Group {

    private static final double SIZE = 70.0;
    private int x;
    private int y;
    public Piece(String color,int x,int y, double resolutionMultiplier) {
        Image image = new Image(color);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(SIZE*resolutionMultiplier);
        imageView.setFitHeight(SIZE*resolutionMultiplier);
        this.getChildren().add(imageView);
        this.x=x;
        this.y=y;
    }

}