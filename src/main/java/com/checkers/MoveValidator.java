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
        int backwardDirection = -forwardDirection;

        if (isValidPosition(piece.getX() + 1, piece.getY() + forwardDirection)&& tiles[piece.getX() + 1][piece.getY()+ forwardDirection].isEmpty()) {
            possibleMoves.add(new Point2D(piece.getX() + 1, piece.getY() + forwardDirection));
        }
        if (isValidPosition(piece.getX() - 1, piece.getY() + forwardDirection)&& tiles[piece.getX() - 1][piece.getY()+ forwardDirection].isEmpty()) {
            possibleMoves.add(new Point2D(piece.getX() - 1, piece.getY() + forwardDirection));
        }

        if(piece.isKing)
        {
            checkKingMoves(piece,forwardDirection,1,possibleMoves);
            checkKingMoves(piece,forwardDirection,-1,possibleMoves);
            checkKingMoves(piece,backwardDirection,1,possibleMoves);
            checkKingMoves(piece,backwardDirection,-1,possibleMoves);
        }

        return possibleMoves;
    }

    private void checkKingMoves(Piece piece,int directionY,int step,List<Point2D> possibleMoves)
    {
        int x= piece.getX();
        int y= piece.getY();
        while(true)
        {
            x=x+step;
            y=y+directionY;

            if (!isValidPosition(x, y))
                break;
            if (!tiles[x][y].isEmpty())
                break;
            else
                possibleMoves.add(new Point2D(x, y));
        }
    }
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < WIDTH_BOARD && y >= 0 && y < HEIGHT_BOARD;
    }
}
