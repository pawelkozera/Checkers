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

        if (!getPossibleCaptures(piece).isEmpty()) {
            possibleMoves.addAll(getPossibleCaptures(piece));
            return possibleMoves;
        }
        else {
            int forwardDirection = piece.getColour().equals("Light") ? 1 : -1;
            int backwardDirection = -forwardDirection;

            if (isValidPosition(piece.getX() + 1, piece.getY() + forwardDirection) && tiles[piece.getX() + 1][piece.getY() + forwardDirection].isEmpty()) {
                possibleMoves.add(new Point2D(piece.getX() + 1, piece.getY() + forwardDirection));
            }
            if (isValidPosition(piece.getX() - 1, piece.getY() + forwardDirection) && tiles[piece.getX() - 1][piece.getY() + forwardDirection].isEmpty()) {
                possibleMoves.add(new Point2D(piece.getX() - 1, piece.getY() + forwardDirection));
            }

            if (piece.isKing) {
                checkKingMoves(piece, forwardDirection, 1, possibleMoves);
                checkKingMoves(piece, forwardDirection, -1, possibleMoves);
                checkKingMoves(piece, backwardDirection, 1, possibleMoves);
                checkKingMoves(piece, backwardDirection, -1, possibleMoves);
            }

            return possibleMoves;
        }
    }

    public List<Point2D> getPossibleCaptures(Piece piece) {
        List<Point2D> possibleCaptures = new ArrayList<>();

        int forwardDirection = piece.getColour().equals("Light") ? 1 : -1;
        int backwardDirection = -forwardDirection;

        int x = piece.getX();
        int y = piece.getY();
        System.out.println("x:" + x + "y:" + y);

        if (isValidPosition(x + 1, y + forwardDirection) && !tiles[x + 1][y + forwardDirection].isEmpty()) {
            Piece targetPiece = tiles[x + 1][y + forwardDirection].getPiece();
            if (piece.getColour().equals("Light") && targetPiece.getColour().equals("Dark") && isValidPosition(x + 2, y + 2 * forwardDirection) && tiles[x + 2][y + 2 * forwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x + 2, y + 2 * forwardDirection));
            }
            if (piece.getColour().equals("Dark") && targetPiece.getColour().equals("Light") && isValidPosition(x + 2, y + 2 * forwardDirection) && tiles[x + 2][y + 2 * forwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x + 2, y + 2 * forwardDirection));
            }
        }

        if (isValidPosition(x - 1, y + forwardDirection) && !tiles[x - 1][y + forwardDirection].isEmpty()) {
            Piece targetPiece = tiles[x - 1][y + forwardDirection].getPiece();
            if (piece.getColour().equals("Light") & targetPiece.getColour().equals("Dark") && isValidPosition(x - 2, y + 2 * forwardDirection) && tiles[x - 2][y + 2 * forwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x - 2, y + 2 * forwardDirection));
            }
            if (piece.getColour().equals("Dark") && targetPiece.getColour().equals("Light") && isValidPosition(x - 2, y + 2 * forwardDirection) && tiles[x - 2][y + 2 * forwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x - 2, y + 2 * forwardDirection));
            }
        }

        if (isValidPosition(x - 1, y + backwardDirection) && !tiles[x - 1][y + backwardDirection].isEmpty()) {
            Piece targetPiece = tiles[x - 1][y + backwardDirection].getPiece();
            if (piece.getColour().equals("Light") & targetPiece.getColour().equals("Dark") && isValidPosition(x - 2, y + 2 * backwardDirection) && tiles[x - 2][y + 2 * backwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x - 2, y + 2 * backwardDirection));
            }
            if (piece.getColour().equals("Dark") && targetPiece.getColour().equals("Light") && isValidPosition(x - 2, y + 2 * backwardDirection) && tiles[x - 2][y + 2 * backwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x - 2, y + 2 * backwardDirection));
            }
        }

        if (isValidPosition(x + 1, y + backwardDirection) && !tiles[x + 1][y + backwardDirection].isEmpty()) {
            Piece targetPiece = tiles[x + 1][y + backwardDirection].getPiece();
            if (piece.getColour().equals("Light") & targetPiece.getColour().equals("Dark") && isValidPosition(x + 2, y + 2 * backwardDirection) && tiles[x + 2][y + 2 * backwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x + 2, y + 2 * backwardDirection));
            }
            if (piece.getColour().equals("Dark") && targetPiece.getColour().equals("Light") && isValidPosition(x + 2, y + 2 * backwardDirection) && tiles[x + 2][y + 2 * backwardDirection].isEmpty()) {
                possibleCaptures.add(new Point2D(x + 2, y + 2 * backwardDirection));
            }
        }

        if (piece.isKing) {
            checkKingCaptures(piece, 1, forwardDirection, possibleCaptures);
            checkKingCaptures(piece, -1, forwardDirection, possibleCaptures);
            checkKingCaptures(piece, 1, backwardDirection, possibleCaptures);
            checkKingCaptures(piece, -1, backwardDirection, possibleCaptures);
        }

        return possibleCaptures;
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
    private void checkKingCaptures(Piece piece, int directionX, int directionY, List<Point2D> possibleCaptures) {
        int x = piece.getX();
        int y = piece.getY();

        while (true) {
            x += directionX;
            y += directionY;

            if (!isValidPosition(x, y))
                break;

            if (!tiles[x][y].isEmpty()) {
                Piece targetPiece = tiles[x][y].getPiece();
                if (targetPiece != null && !targetPiece.getColour().equals(piece.getColour())) {
                    int nextX = x + directionX;
                    int nextY = y + directionY;
                    if (isValidPosition(nextX, nextY) && tiles[nextX][nextY].isEmpty()) {
                        possibleCaptures.add(new Point2D(nextX, nextY));
                    }
                }
                break;
            }
        }
    }
    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < WIDTH_BOARD && y >= 0 && y < HEIGHT_BOARD;
    }
}
