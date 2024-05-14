package com.checkers.server;


import static com.checkers.GameWindow.HEIGHT_BOARD;
import static com.checkers.GameWindow.WIDTH_BOARD;

public class GameValidation {
    private final int[][] DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    public boolean endGame(int[][] board) {
        if (!whiteAndBlackHavePiecesOnBoard(board)) {
            return true;
        }

        if (!playersCanMove(board)) {
            return true;
        }

        return false;
    }

    private boolean whiteAndBlackHavePiecesOnBoard(int[][] board) {
        boolean hasWhite = false;
        boolean hasBlack = false;

        for (int[] row : board) {
            for (int piece : row) {
                if (piece == 1) {
                    hasWhite = true;
                }
                else if (piece == 2) {
                    hasBlack = true;
                }

                if (hasWhite && hasBlack) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean playersCanMove(int[][] board) {
        boolean whiteCanMove = false;
        boolean blackCanMove = false;

        for (int i = 0; i < WIDTH_BOARD; i++) {
            for (int j = 0; j < HEIGHT_BOARD; j++) {
                if (board[i][j] == 1 && !whiteCanMove) {
                    whiteCanMove = checkIfPieceCanMove(board, i, j);
                } else if (board[i][j] == 2 && !blackCanMove) {
                    blackCanMove = checkIfPieceCanMove(board, i, j);
                }

                if (whiteCanMove && blackCanMove) {
                    return true;
                }
            }
        }

        return false;
    }


    private boolean checkIfPieceCanMove(int[][] board, int x, int y) {
        if (canMove(board, x, y)) {
            System.out.println(board[x][y]);
            return true;
        }

        if (canTake(board, x, y)) {
            return true;
        }

        return false;
    }

    private boolean canMove(int[][] board, int x, int y) {
        for (int[] direction : DIRECTIONS) {
            int nearTileX = x + direction[0];
            int nearTileY = y + direction[1];

            if (isInBounds(nearTileX, nearTileY) && board[nearTileX][nearTileY] == 0) {
                return true;
            }
        }

        return false;
    }

    private boolean canTake(int[][] board, int x, int y) {
        int pieceColor = board[x][y];
        int oppositeColor = getOppositeColor(pieceColor);

        for (int[] direction : DIRECTIONS) {
            int nearTileX = x + direction[0];
            int nearTileY = y + direction[1];

            int nextToNearTileX = nearTileX + direction[0];
            int nextToNearTileY = nearTileY + direction[1];

            boolean nearTileInBounds = isInBounds(nearTileX, nearTileY);
            boolean nextToNearTileInBounds = isInBounds(nextToNearTileX, nextToNearTileY);

            if (nearTileInBounds && nextToNearTileInBounds) {
                boolean nearTileIsOppositeColor = board[nearTileX][nearTileY] == oppositeColor;
                boolean nextToNearTileIsEmpty = board[nextToNearTileX][nextToNearTileY] == 0;

                if (nearTileIsOppositeColor && nextToNearTileIsEmpty) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x <= 7 && y <= 7;
    }

    private int getOppositeColor(int color) {
        return (color == 1) ? 2 : 1;
    }

    public static void main(String[] args) {
        int[][] board = {
                {1, 0, 0, 0, 0, 0, 0, 0},
                {0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 2, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };

        GameValidation gameValidation = new GameValidation();
        System.out.println(gameValidation.endGame(board));
    }
}
