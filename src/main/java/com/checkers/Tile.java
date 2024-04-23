package com.checkers;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Tile extends StackPane {

    Piece piece;
    private Rectangle area;
    private boolean access;
    private Circle point=new Circle(20);
    private boolean marking;
    private Rectangle markingRectangle;
    private ImagePattern imagePattern;
    public int x;
    public int y;
    String texture;
    public Tile(int x,int y,String texture,double resolutionMultiplier){
        area=new Rectangle(80*resolutionMultiplier,80*resolutionMultiplier);
        markingRectangle=new Rectangle(80*resolutionMultiplier,80*resolutionMultiplier);
        this.x=x;
        this.y=y;
        this.texture=texture;
        Image image = new Image(texture);
        this.imagePattern = new ImagePattern(image);
        area.setFill(imagePattern);
        getChildren().add(area);
        access=false;
        Color transparentGreen = Color.rgb(70, 255, 177, 0.4);
        markingRectangle.setFill(transparentGreen);
        point.setFill(transparentGreen);
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
    public void setMarking() {
        this.marking = true;
        getChildren().add(markingRectangle);
        if(piece!=null)
        {
            getChildren().remove(piece);
            getChildren().add(piece);
        }
    }
    public void removeMarking() {
        if(marking) {
            marking=false;
            getChildren().remove(markingRectangle);
        }
    }
    public int getX() {
        return x;
    }
    public int  getY() {
        return y;
    }
}
