package com.checkers;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GameWindow extends Pane {

    public static final int WIDTH_BOARD = 8;
    public static final int HEIGHT_BOARD = 8;
    private GridPane Tiles = new GridPane();
    private StackPane gameBoard = new StackPane();
    private Menu menu=new Menu();
    GameWindow() {
        gameBoard.getChildren().addAll(Tiles);
        for (int y = 0; y < HEIGHT_BOARD; y++)
            for (int x = 0; x < WIDTH_BOARD; x++) {
                Tile tile;
                if ((x + y) % 2 == 0)
                    tile = new Tile(x, y, Color.WHITE);
                else
                    tile = new Tile(x, y, Color.DARKCYAN);
                Tiles.add(tile, x, y);

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 != 0) {
                    piece = new Piece("DarkPiece.png", x, y);
                }

               if (y >= 5 && (x + y) % 2 != 0) {
                    piece = new Piece("LightPiece.png", x, y);
               }

                if (piece != null) {
                   tile.setPiece(piece);
                }
            }

        Color transparentBlack = Color.rgb(0, 0, 0, 0.2);
        gameBoard.setBorder(new Border(new BorderStroke(transparentBlack, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(15))));
        gameBoard.setLayoutX(50);
        gameBoard.setLayoutY(50);
        this.setStyle("-fx-background-color: #F4E7C6;");

        menu.setLayoutX(780);
        menu.setLayoutY(50);
        this.getChildren().add(gameBoard);
        this.getChildren().add(menu);

    }
}
