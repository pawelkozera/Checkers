package com.checkers;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

import static com.checkers.GameWindow.HEIGHT_BOARD;
import static com.checkers.GameWindow.WIDTH_BOARD;

public class MoveValidator {
    private Tile[][] tiles;
    public MoveValidator(Tile[][]tiles)
    {
        this.tiles=tiles;
    }
    public List<Point2D> getPossibleMoves(Piece piece) {
        List<Point2D> possibleMoves = new ArrayList<>();
        System.out.println(piece.getColour());
        int forwardDirection = piece.getColour().equals("Light") ? 1 : -1;

        if (isValidPosition(piece.getX() + 1, piece.getY() + forwardDirection)&& tiles[piece.getX() + 1][piece.getY()+ forwardDirection].isEmpty()) {
            possibleMoves.add(new Point2D(piece.getX() + 1, piece.getY() + forwardDirection));
        }
        if (isValidPosition(piece.getX() - 1, piece.getY() + forwardDirection)&& tiles[piece.getX() - 1][piece.getY()+ forwardDirection].isEmpty()) {
            possibleMoves.add(new Point2D(piece.getX() - 1, piece.getY() + forwardDirection));
        }


        if (piece.isKing) {
            if (isValidPosition(piece.getX() + 1, piece.getY() - forwardDirection)) {
                possibleMoves.add(new Point2D(piece.getX() + 1, piece.getY() - forwardDirection));
            }
            if (isValidPosition(piece.getX() - 1, piece.getY() - forwardDirection)) {
                possibleMoves.add(new Point2D(piece.getX() - 1, piece.getY() - forwardDirection));}
        }

        return possibleMoves;
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < WIDTH_BOARD && y >= 0 && y < HEIGHT_BOARD;
    }
}
