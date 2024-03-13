package com.checkers;

import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends StackPane {

    private Rectangle area;
    public int x;
    public int y;
    Color color;
    public void setPiece(Piece piece) {
        getChildren().add(piece);
    }
    public Tile(int x,int y,Color color){
        area=new Rectangle(80,80);
        this.x=x;
        this.y=y;
        this.color=color;
        area.setFill(color);
        getChildren().add(area);
    }
}
