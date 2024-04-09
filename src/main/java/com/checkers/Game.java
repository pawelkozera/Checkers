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
    private List<Piece> lightPieces;
    private List<Piece> darkPieces;

    public Game(boolean isPlayerStart, Tile[][] tiles, List<Piece> lightPieces, List<Piece> darkPieces) {

        this.tiles = tiles;
        this.lightPieces = lightPieces;
        this.darkPieces = darkPieces;
        moveValidator=new MoveValidator(tiles);
        if(isPlayerStart)
        {
            isPlayerTurn=true;
            for (Piece piece : lightPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }

            for (Piece piece : darkPieces) {
                piece.setOnMouseClicked(mouseEvent -> handlePieceClick(piece));
            }

            for (Tile[] row : tiles) {
                for (Tile tile : row) {
                    tile.setOnMouseClicked(event -> {
                        if (selectedPiece != null && tile.isAccess()) {
                            int newX = tile.getX();
                            int newY = tile.getY();
                            tiles[selectedPiece.getX()][selectedPiece.getY()].removePiece();
                            tile.setPiece(selectedPiece);
                            selectedPiece.setX(newX);
                            selectedPiece.setY(newY);

                            if((Objects.equals(selectedPiece.getColour(), "Light") && selectedPiece.getY()==HEIGHT_BOARD-1)||(Objects.equals(selectedPiece.getColour(), "Dark") &&selectedPiece.getY()==0))
                            selectedPiece.makeKing();

                            selectedPiece = null;
                            isPlayerTurn = !isPlayerTurn;
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

    private void handlePieceClick(Piece piece) {
        if ((isPlayerTurn && lightPieces.contains(piece)) || (!isPlayerTurn && darkPieces.contains(piece))) {
            selectedPiece = piece;
            markPossibleMoves(moveValidator.getPossibleMoves(piece));
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
