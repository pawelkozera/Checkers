package com.checkers;

import java.util.ArrayList;
import java.util.List;

public class LongestTakingSequence {
    private final int[][] DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private int sequenceLength = 0;
    private List<LongestTakingSequenceInformation> longestTakingSequenceInformations = new ArrayList<>();

    public void findLongestSequence(int[][] inputBoard, int color, int x, int y, int sequence, boolean forKing) {
        if (forKing) {
            findLongestSequenceForKing(inputBoard, color, x, y, sequence);
        }
        else {
            findLongestSequenceForPawn(inputBoard, color, x, y, sequence);
        }

    }

    public int findLongestSequenceForPawn(int[][] inputBoard, int color, int x, int y, int sequence) {
        if (color != inputBoard[x][y]) {
            return 0;
        }

        int[][] boardCopy = copyBoard(inputBoard);

        for (int[] direction : DIRECTIONS) {
            int nearTileX = x + direction[0];
            int nearTileY = y + direction[1];

            if (isInBounds(nearTileX, nearTileY) && inputBoard[nearTileX][nearTileY] == getOppositeColor(color)) {
                int moveToTileX = nearTileX + direction[0];
                int moveToTileY = nearTileY + direction[1];

                boolean moveToTileInBounds = isInBounds(moveToTileX, moveToTileY);
                if (moveToTileInBounds && inputBoard[moveToTileX][moveToTileY] == 0) {
                    int[][] nextBoard = copyBoard(boardCopy);
                    nextBoard[x][y] = 0;
                    nextBoard[nearTileX][nearTileY] = 0;
                    nextBoard[moveToTileX][moveToTileY] = color;

                    int newSequence = findLongestSequenceForPawn(nextBoard, color, moveToTileX, moveToTileY, sequence + 1);
                    addLongestTakingSequenceInformations(newSequence, nextBoard, moveToTileX, moveToTileY);
                }
            }
        }

        return sequence;
    }

    public int findLongestSequenceForKing(int[][] inputBoard, int color, int x, int y, int sequence) {
        if (color != inputBoard[x][y]) {
            return 0;
        }

        int[][] boardCopy = copyBoard(inputBoard);

        int nearTileX, nearTileY;
        int startX = x;
        int startY = y;

        for (int[] direction : DIRECTIONS) {
            nearTileX = x + direction[0];
            nearTileY = y + direction[1];

            boolean canMove = true;
            while (canMove) {
                boolean nearTileInBounds = isInBounds(nearTileX, nearTileY);
                if (nearTileInBounds && inputBoard[nearTileX][nearTileY] == getOppositeColor(color)) {
                    int moveToTileX = nearTileX + direction[0];
                    int moveToTileY = nearTileY + direction[1];

                    if (isInBounds(moveToTileX, moveToTileY) && inputBoard[moveToTileX][moveToTileY] == 0) {
                        int[][] nextBoard = copyBoard(boardCopy);

                        updateBoard(nextBoard, x, y, nearTileX, nearTileY, moveToTileX, moveToTileY, color);
                        updateBoard(boardCopy, x, y, nearTileX, nearTileY, moveToTileX, moveToTileY, color);

                        x = moveToTileX;
                        y = moveToTileY;

                        nearTileX = x;
                        nearTileY = y;

                        int newSequence = findLongestSequenceForKing(nextBoard, color, moveToTileX, moveToTileY, sequence + 1);
                        addLongestTakingSequenceInformations(newSequence, nextBoard, moveToTileX, moveToTileY);
                    }
                    else {
                        canMove = false;
                        x = startX;
                        y = startY;
                        boardCopy = copyBoard(inputBoard);
                    }
                }
                else if (!nearTileInBounds || inputBoard[nearTileX][nearTileY] != 0) {
                    canMove = false;
                    x = startX;
                    y = startY;
                    boardCopy = copyBoard(inputBoard);
                }

                nearTileX += direction[0];
                nearTileY += direction[1];
            }
        }

        return sequence;
    }

    private void addLongestTakingSequenceInformations(int newSequence, int[][] nextBoard, int moveToTileX, int moveToTileY) {
        if (newSequence > sequenceLength) {
            sequenceLength = newSequence;
            longestTakingSequenceInformations.clear();
            LongestTakingSequenceInformation takingSequenceInformation = new LongestTakingSequenceInformation(nextBoard, newSequence, moveToTileX, moveToTileY);
            longestTakingSequenceInformations.add(takingSequenceInformation);
        }
        else if (newSequence == sequenceLength) {
            LongestTakingSequenceInformation takingSequenceInformation = new LongestTakingSequenceInformation(nextBoard, newSequence, moveToTileX, moveToTileY);
            longestTakingSequenceInformations.add(takingSequenceInformation);
        }
    }

    private void updateBoard(int[][] board, int pawnPosX, int pawnPosY, int nearTileX, int nearTileY, int movePosX, int movePosY, int color) {
        board[pawnPosX][pawnPosY] = 0;
        board[nearTileX][nearTileY] = 0;
        board[movePosX][movePosY] = color;
    }

    private int getOppositeColor(int color) {
        return (color == 1) ? 2 : 1;
    }

    private boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x <= 7 && y <= 7;
    }

    public int[][] copyBoard(int[][] originalBoard) {
        int[][] copiedBoard = new int[originalBoard.length][];
        for (int i = 0; i < originalBoard.length; i++) {
            copiedBoard[i] = originalBoard[i].clone();
        }
        return copiedBoard;
    }

    public List<LongestTakingSequenceInformation> getLongestTakingSequenceInformations() {
        return longestTakingSequenceInformations;
    }
    public int getSequenceLength() {
        return sequenceLength;
    }

    public static void main(String[] args) {
        int[][] board = {
                {1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 2, 0},
                {0, 0, 0, 2, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };

        int color = 1;
        LongestTakingSequence longestSequence = new LongestTakingSequence();
        longestSequence.findLongestSequence(board, color, 0, 0, 0, true);

        System.out.println("Liczba bic dla " + color + ": " + longestSequence.getLongestTakingSequenceInformations().size());

        for (LongestTakingSequenceInformation longestTakingSequence : longestSequence.getLongestTakingSequenceInformations()) {
            System.out.println(longestTakingSequence);
            for (int[] row : longestTakingSequence.board()) {
                for (int cell : row) {
                    System.out.print(cell + " ");
                }
                System.out.println();
            }
        }
    }
}



