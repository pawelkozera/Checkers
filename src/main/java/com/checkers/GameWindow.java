package com.checkers;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class GameWindow extends Pane {

    public static final int WIDTH_BOARD = 8;
    public static final int HEIGHT_BOARD = 8;
    private GridPane Tiles = new GridPane();
    private BorderPane gameBoard = new BorderPane();
    private Menu menu;
    List<Point2D> possibleMoves = new ArrayList<>();//tymczasowo

    GameWindow(double width, double height) {
        double resolutionMultiplier = (double) width / 1100;
        possibleMoves.add(new Point2D(2, 3));//tymczasowo
        possibleMoves.add(new Point2D(0, 3));//tymczasowo
        menu=new Menu(gameBoard,resolutionMultiplier);
        gameBoard.setCenter(Tiles);


        for (int y = 0; y < HEIGHT_BOARD; y++)
            for (int x = 0; x < WIDTH_BOARD; x++) {
                Tile tile;
                if ((x + y) % 2 == 0)
                    tile = new Tile(x, y, Color.WHITE,resolutionMultiplier);
                else
                    tile = new Tile(x, y, Color.DARKCYAN,resolutionMultiplier);
                Tiles.add(tile, x, y);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = new Piece("DarkPiece.png", x, y,resolutionMultiplier);
                }

                if (y >= 5 && (x + y) % 2 != 0) {
                    piece = new Piece("LightPiece.png", x, y,resolutionMultiplier);
                }

                if (piece != null) {
                    tile.setPiece(piece);
                    piece.setOnMouseClicked(event->{
                        markPossibleMoves(possibleMoves);
                    });
                }
            }

        for (int i = 0; i < WIDTH_BOARD; i++) {
            Label columnLabel = new Label(String.valueOf((char)('A' + i)));
            Label columnLabel2 = new Label(String.valueOf((char)('A' + i)));
            Label rowLabel = new Label(String.valueOf(HEIGHT_BOARD - i));
            Label rowLabel2 = new Label(String.valueOf(HEIGHT_BOARD - i));

            if(resolutionMultiplier>1) {
                columnLabel.setStyle("-fx-font-size: 18;-fx-text-fill: white;");
                rowLabel.setStyle("-fx-font-size: 18;-fx-text-fill: white;");
                columnLabel2.setStyle("-fx-font-size: 18;-fx-text-fill: white;");
                rowLabel2.setStyle("-fx-font-size: 18;-fx-text-fill: white;");
            }
            else if(resolutionMultiplier>0.75) {
                columnLabel.setStyle("-fx-font-size: 16;-fx-text-fill: white;");
                rowLabel.setStyle("-fx-font-size: 16;-fx-text-fill: white;");
                columnLabel2.setStyle("-fx-font-size: 16;-fx-text-fill: white;");
                rowLabel2.setStyle("-fx-font-size: 16;-fx-text-fill: white;");
            }
            else {
                columnLabel.setStyle("-fx-font-size: 10;-fx-text-fill: white;");
                rowLabel.setStyle("-fx-font-size: 10;-fx-text-fill: white;");
                columnLabel2.setStyle("-fx-font-size: 10;-fx-text-fill: white;");
                rowLabel2.setStyle("-fx-font-size: 10;-fx-text-fill: white;");
            }
            getTile(i,HEIGHT_BOARD-1).getChildren().add(columnLabel);
            getTile(0,i).getChildren().add(rowLabel);
            getTile(i,0).getChildren().add(columnLabel2);
            getTile(WIDTH_BOARD-1,i).getChildren().add(rowLabel2);
            columnLabel.setTranslateY(55*resolutionMultiplier);
            rowLabel.setTranslateX(-55*resolutionMultiplier);
            columnLabel2.setTranslateY(-55*resolutionMultiplier);
            rowLabel2.setTranslateX(55*resolutionMultiplier);
            columnLabel2.setRotate(180);
            rowLabel2.setRotate(180);

        }

        Color color = Color.rgb(0x18, 0x30, 0x37);
        gameBoard.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(27*resolutionMultiplier))));
        gameBoard.setLayoutX(50);
        gameBoard.setLayoutY(50);
        this.setStyle("-fx-background-color: #F4E7C6;");

        menu.setLayoutX(780*resolutionMultiplier);
        menu.setLayoutY(50);
        this.getChildren().add(gameBoard);
        this.getChildren().add(menu);
    }
    private void markPossibleMoves(List<Point2D> possibleMoves) {
        for (int y = 0; y < HEIGHT_BOARD; y++) {
            for (int x = 0; x < WIDTH_BOARD; x++) {
                Tile tile = getTile(x,y);
                if (possibleMoves.contains(new Point2D(x, y))) {
                    tile.setAccess();
                } else {
                    tile.removeAccess();
                }
            }
        }
    }
    Tile getTile(int x, int y) {
        for (Node node : Tiles.getChildren()) {
            if (node instanceof Tile) {
                Tile tile = (Tile) node;
                if (tile.x == x && tile.y == y) {
                    return tile;
                }
            }
        }
        return null;
    }
}