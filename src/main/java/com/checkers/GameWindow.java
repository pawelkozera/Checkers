package com.checkers;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameWindow extends Pane {

    public static final int WIDTH_BOARD = 8;
    public static final int HEIGHT_BOARD = 8;
    private Game game;
    private final Menu menu;
    private GameInfoScreen gameInfoScreen;
    private GameOverScreen gameOverScreen;
    private GridPane Tiles = new GridPane();
    private Tile [][] tiles = new Tile[WIDTH_BOARD][HEIGHT_BOARD];
    private List <Piece> lightPieces=new ArrayList<>();
    private List <Piece> darkPieces=new ArrayList<>();
    private final BorderPane gameBoard = new BorderPane();
    private final double resolutionMultiplier;

    GameWindow(double width, double height) {
        this.resolutionMultiplier = (double) width / 1100;

        menu=new Menu(resolutionMultiplier);

        gameInfoScreen=new GameInfoScreen(resolutionMultiplier);
        gameOverScreen=new GameOverScreen(resolutionMultiplier);

        gameBoard.setCenter(Tiles);
        createBoard();

        Color color = Color.rgb(0x12, 0x07, 0x00);
        gameBoard.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(27*resolutionMultiplier))));
        gameBoard.setLayoutX(50);
        gameBoard.setLayoutY(50);
        Image backgroundImage = new Image("backgroundTexture.jpg");
        BackgroundSize backgroundSize = new BackgroundSize(width, height, false, false, false, false);
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                backgroundSize);
        this.setBackground(new Background(background));

        menu.setLayoutX(780*resolutionMultiplier);
        menu.setLayoutY(50);
        gameInfoScreen.setLayoutX(780*resolutionMultiplier);
        gameInfoScreen.setLayoutY(50);

        this.getChildren().addAll(gameBoard,menu,gameInfoScreen,gameOverScreen);

        menu.getPlayDark().setOnMouseClicked(event->{
            menu.OnColorPlayClick();
            game=new Game(gameInfoScreen,gameOverScreen,tiles,lightPieces,darkPieces);
            gameSound.playGameStartSound();
        });
        menu.getPlayLight().setOnMouseClicked(event->{
            menu.OnColorPlayClick();
            game=new Game(gameInfoScreen,gameOverScreen,tiles,lightPieces,darkPieces);
            gameSound.playGameStartSound();
        });

        menu.getPlayMulti().setOnMouseClicked(event -> {
            menu.onPlayMultiClick();
            ConnectionInfo connectionInfo = new ConnectionInfo("localhost", 1025);
            game = new Game(gameInfoScreen, gameOverScreen, tiles, lightPieces, darkPieces, connectionInfo, gameBoard);
        });
        gameInfoScreen.getEndGameButton().setOnMouseClicked(event -> {
            restartWindow();
            game.sendEndGameButtonToServer();
        });
        gameOverScreen.getGoBackButton().setOnMouseClicked(event -> {
            restartWindow();
        });
    }

    private Tile getTile(int x, int y) {
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

    private void createBoard()
    {
        for (int y = 0; y < HEIGHT_BOARD; y++)
            for (int x = 0; x < WIDTH_BOARD; x++) {
                Tile tile;
                int reversedY = HEIGHT_BOARD - 1 - y;
                if ((x + y) % 2 != 0)
                    tile = new Tile(x, y, "lightTexture.png", resolutionMultiplier);
                else
                    tile = new Tile(x, y, "darkTexture.png", resolutionMultiplier);

                GridPane.setRowIndex(tile, reversedY);
                GridPane.setColumnIndex(tile, x);
                Tiles.getChildren().add(tile);
                tiles[x][y]=tile;

                Piece piece = null;

                if (y <= 2 && (x + y) % 2 == 0) {
                    piece = new Piece("Light", "LightPiece.png", x, y, resolutionMultiplier);
                }

                if (y >= 5 && (x + y) % 2 == 0) {

                    piece = new Piece("Dark", "DarkPiece.png", x, y, resolutionMultiplier);
                }

                if (piece != null) {
                    getTile(piece.getX(), piece.getY()).setPiece(piece);
                    if (Objects.equals(piece.getColour(), "Dark"))
                        darkPieces.add(piece);
                    else
                        lightPieces.add(piece);
                }
            }

        for (int i = 0; i < WIDTH_BOARD; i++) {
            Label columnLabel = new Label(String.valueOf((char)('A' + i)));
            Label columnLabel2 = new Label(String.valueOf((char)('A' + i)));
            Label rowLabel = new Label(String.valueOf(i+1));
            Label rowLabel2 = new Label(String.valueOf(i+1));

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
            columnLabel.setTranslateY(-55*resolutionMultiplier);
            rowLabel.setTranslateX(-55*resolutionMultiplier);
            columnLabel2.setTranslateY(55*resolutionMultiplier);
            rowLabel2.setTranslateX(55*resolutionMultiplier);
            columnLabel.setRotate(180);
            rowLabel2.setRotate(180);

        }
    }

    private void restartWindow ()
    {
        this.Tiles.getChildren().clear();
        lightPieces.clear();
        darkPieces.clear();
        System.gc();
        gameInfoScreen.setVisible(false);
        gameInfoScreen.setDisable(true);
        gameOverScreen.setVisible(false);
        gameOverScreen.setDisable(true);
        gameInfoScreen.restart();
        menu.restart();
        createBoard();
        gameBoard.setRotate(0);
    }
}