package com.checkers;

import java.util.ArrayList;
import java.util.List;

public class LongestTakingSequence {
    private final int[][] DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    private int sequenceLength = 0;
    private List<LongestTakingSequenceInformation> longestTakingSequenceInformations = new ArrayList<>();

    public int findLongestSequence(int[][] inputBoard, int color, int x, int y, int sequence) {
        if (color != inputBoard[x][y]) {
            return 0;
        }

        int[][] boardCopy = copyBoard(inputBoard);

        int oppositeColor;
        if (color == 1) {
            oppositeColor = 2;
        }
        else {
            oppositeColor = 1;
        }

        int nearTileX, nearTileY;
        for (int[] direction : DIRECTIONS) {
            nearTileX = x + direction[0];
            nearTileY = y + direction[1];

            boolean nearTileInBounds = nearTileX >= 0 && nearTileY >= 0 && nearTileX <= 7 && nearTileY <= 7;
            if (nearTileInBounds && inputBoard[nearTileX][nearTileY] == oppositeColor) {
                int moveToTileX, moveToTileY;
                moveToTileX = nearTileX + direction[0];
                moveToTileY = nearTileY + direction[1];

                boolean moveToTileInBounds = moveToTileX >= 0 && moveToTileY >= 0 && moveToTileX <= 7 && moveToTileY <= 7;
                if (moveToTileInBounds && inputBoard[moveToTileX][moveToTileY] == 0) {
                    int[][] nextBoard = copyBoard(boardCopy);
                    nextBoard[x][y] = 0;
                    nextBoard[nearTileX][nearTileY] = 0;
                    nextBoard[moveToTileX][moveToTileY] = color;

                    int newSequence = findLongestSequence(nextBoard, color, moveToTileX, moveToTileY, sequence + 1);

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
            }
        }

        return sequence;
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
                {0, 2, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 2, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
        };

        int color = 1;
        LongestTakingSequence longestSequence = new LongestTakingSequence();
        longestSequence.findLongestSequence(board, color, 0, 0, 0);

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



