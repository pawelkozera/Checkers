package com.checkers;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class Piece extends Group {

    private static final double SIZE = 70.0;

    public Piece(String color,int x,int y) {
        Image image = new Image(color);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(SIZE);
        imageView.setFitHeight(SIZE);
        this.getChildren().add(imageView);
    }
}