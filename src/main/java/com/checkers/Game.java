package com.checkers;

import javafx.geometry.Point2D;

import java.util.List;
import java.util.Objects;

import static com.checkers.GameWindow.HEIGHT_BOARD;
import static com.checkers.GameWindow.WIDTH_BOARD;


public class Game {
    private MoveValidator moveValidator;
    private boolean isPlayerStart;
    private Piece selectedPiece;
    private boolean isPlayerTurn;
    private Tile[][] tiles;

    public Game(boolean isPlayerStart, Tile[][] tiles, List<Piece> lightPieces, List<Piece> darkPieces) {
        if(isPlayerStart)
        {
            this.tiles=tiles;
            moveValidator=new MoveValidator(tiles);
            isPlayerTurn=true;
            for (Piece piece:lightPieces) {
                piece.setOnMouseClicked(mouseEvent -> {
                    System.out.println("Piece: "+piece.getX()+" "+piece.getY());
                    if(isPlayerTurn)
                    {
                        selectedPiece = piece;
                        markPossibleMoves(moveValidator.getPossibleMoves(piece));
                    }
                });
            }
            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    tile.setOnMouseClicked(event -> {
                        if (isPlayerTurn && selectedPiece != null && tile.isAccess()) {
                            int newX = tile.getX();
                            int newY = tile.getY();
                            tiles[selectedPiece.getX()][selectedPiece.getY()].removePiece();
                            tile.setPiece(selectedPiece);
                            selectedPiece.setX(newX);
                            selectedPiece.setY(newY);
                            selectedPiece = null;
                           //isPlayerTurn = !isPlayerTurn;
                            for (Tile[] rowToClear: tiles) {
                                for (Tile tileClear : rowToClear) {
                                    tileClear.removeAccess();
                                }
                            }
                        }
                    });
                }
            }
        }
    }
    private void markPossibleMoves(List<Point2D> possibleMoves) {
        for (int y = 0; y < HEIGHT_BOARD; y++) {
            for (int x = 0; x < WIDTH_BOARD; x++) {
                Tile tile = this.tiles[x][y];
                if (possibleMoves.contains(new Point2D(x, y))) {
                    tile.setAccess();
                } else {
                    tile.removeAccess();
                }
            }
        }
    }
}
