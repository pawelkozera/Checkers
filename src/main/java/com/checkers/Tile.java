package com.checkers;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Tile extends StackPane {

    Piece piece;
    private Rectangle area;
    private boolean access;
    private Circle point=new Circle(20);
    public int x;
    public int y;
    Color color;
    public Tile(int x,int y,Color color,double resolutionMultiplier){
        area=new Rectangle(80*resolutionMultiplier,80*resolutionMultiplier);
        this.x=x;
        this.y=y;
        this.color=color;
        area.setFill(color);
        getChildren().add(area);
        access=false;
        point.setFill(Color.AQUAMARINE);
        point.setRadius(20*resolutionMultiplier);
    }
    public void setPiece(Piece piece) {
        this.piece=piece;
        getChildren().add(piece);
    }
    public void removePiece() {
        piece=null;
    }
    public Piece getPiece() {
        return piece;
    }
    public boolean isEmpty(){
        if(piece==null)
            return true;
        else
            return false;
    }

    public void setAccess() {
        if(!access) {
            this.access = true;
            getChildren().add(point);
        }
    }
    public boolean isAccess() {
        if(access)
            return true;
        else
            return false;
    }
    public void removeAccess() {
        if(access) {
            getChildren().remove(point);
            access=false;
        }
    }
    public int getX() {
        return x;
    }
    public int  getY() {
        return y;
    }
}
