package com.checkers;

import java.util.ArrayList;
import java.util.List;

import static com.checkers.GameWindow.HEIGHT_BOARD;

public class Minimax {
    public static final int BLACK = 2;
    public static final int WHITE = 1;
    public static final int WHITE_KING = 3;
    public static final int BLACK_KING = 4;
    public static final int EMPTY = 0;
    public static final int MAX_DEPTH = 1;
    private static final int[][] DIRECTIONS_PAWN_BLACK = {{-1, -1}, {1, -1}};
    private static final int[][] DIRECTIONS_PAWN_WHITE = {{-1, 1}, {1, 1}};
    private static final int[][] DIRECTIONS = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

    public static void main(String[] args) {
        int[][] board = {
                {0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 2},
        };

        Move bestMove = minimax(board, MAX_DEPTH, true, Integer.MIN_VALUE, Integer.MAX_VALUE, -1, -1, -1, -1);
        if (bestMove != null && bestMove.board != null) {
            System.out.println("Best move value: " + bestMove.value);
            System.out.println("Start position: (" + bestMove.startX + ", " + bestMove.startY + ")");
            System.out.println("End position: (" + bestMove.endX + ", " + bestMove.endY + ")");
            printBoard(bestMove.board);
        } else {
            System.out.println("No valid moves found.");
        }
    }

    public static Move minimax(int[][] board, int depth, boolean maximizingPlayer, int alpha, int beta, int startX, int startY, int endX, int endY) {
        if (depth == 0 || isGameOver(board)) {
            return new Move(evaluateBoard(board), copyBoard(board), startX, startY, endX, endY);
        }

        List<Move> possibleMoves = generatePossibleMoves(board, maximizingPlayer ? BLACK : WHITE);
        if (possibleMoves.isEmpty()) {
            return new Move(evaluateBoard(board), copyBoard(board), startX, startY, endX, endY);
        }

        Move bestMove;
        if (maximizingPlayer) {
            bestMove = new Move(Integer.MIN_VALUE, copyBoard(board), -1, -1, -1, -1);
            for (Move move : possibleMoves) {
                Move evalMove = minimax(move.board, depth - 1, false, alpha, beta, move.startX, move.startY, move.endX, move.endY);
                if (evalMove.value > bestMove.value) {
                    bestMove = new Move(evalMove.value, copyBoard(move.board), move.startX, move.startY, move.endX, move.endY);
                }
                alpha = Math.max(alpha, evalMove.value);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            bestMove = new Move(Integer.MAX_VALUE, copyBoard(board), -1, -1, -1, -1);
            for (Move move : possibleMoves) {
                Move evalMove = minimax(move.board, depth - 1, true, alpha, beta, move.startX, move.startY, move.endX, move.endY);
                if (evalMove.value < bestMove.value) {
                    bestMove = new Move(evalMove.value, copyBoard(move.board), move.startX, move.startY, move.endX, move.endY);
                }
                beta = Math.min(beta, evalMove.value);
                if (beta <= alpha) {
                    break;
                }
            }
        }

        return bestMove;
    }

    public static boolean isGameOver(int[][] board) {
        boolean whitePieceFound = false;
        boolean blackPieceFound = false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == WHITE || board[row][col] == WHITE_KING) {
                    whitePieceFound = true;
                }
                if (board[row][col] == BLACK || board[row][col] == BLACK_KING) {
                    blackPieceFound = true;
                }

                if (whitePieceFound && blackPieceFound) {
                    return false;
                }
            }
        }

        return true;
    }

    public static int evaluateBoard(int[][] board) {
        int score = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == WHITE) {
                    score -= 3;
                } else if (board[i][j] == 3) {
                    score -= 5;
                } else if (board[i][j] == BLACK) {
                    score += 3;
                } else if (board[i][j] == 4) {
                    score += 5;
                }
            }
        }
        return score;
    }

    public static List<Move> generatePossibleMoves(int[][] board, int player) {
        List<Move> possibleMoves = new ArrayList<>();
        LongestTakingSequence longestSequence = new LongestTakingSequence();

        int player_king = player == WHITE ? 3 : 4;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == player) {
                    longestSequence.findLongestSequence(board, player, i, j, 0, false);
                }
                else if (board[i][j] == player_king) {
                    longestSequence.findLongestSequence(board, player_king, i, j, 0, true);
                }
            }
        }

        List<LongestTakingSequenceInformation> sequences = longestSequence.getLongestTakingSequenceInformations();
        if (!sequences.isEmpty()) {
            for (LongestTakingSequenceInformation seqInfo : sequences) {
                Move move = new Move(0, seqInfo.board(), seqInfo.startX(), seqInfo.startY(), seqInfo.x(), seqInfo.y());
                promotePieceToKing(move.board, seqInfo.x(), seqInfo.y());
                possibleMoves.add(move);
            }
        } else {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (board[i][j] == player) {
                        int[][] directions;
                        if (player == WHITE) {
                            directions = DIRECTIONS_PAWN_WHITE;
                        } else {
                            directions = DIRECTIONS_PAWN_BLACK;
                        }

                        for (int[] direction : directions) {
                            int x = i + direction[0];
                            int y = j + direction[1];
                            if (isInBounds(x, y) && board[x][y] == EMPTY) {
                                int[][] move = copyBoard(board);
                                move[i][j] = EMPTY;
                                move[x][y] = player;
                                promotePieceToKing(move, x, y);
                                possibleMoves.add(new Move(0, move, i, j, x, y));
                            }
                        }
                    } else if (board[i][j] == player_king) {
                        for (int[] direction : DIRECTIONS) {
                            int x = i;
                            int y = j;
                            boolean canMove = true;
                            while (canMove) {
                                x += direction[0];
                                y += direction[1];
                                if (isInBounds(x, y) && board[x][y] == EMPTY) {
                                    int[][] move = copyBoard(board);
                                    move[i][j] = EMPTY;
                                    move[x][y] = player_king;
                                    possibleMoves.add(new Move(0, move, i, j, x, y));
                                } else {
                                    canMove = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    private static void promotePieceToKing(int[][] board, int x, int y) {
        boolean isWhiteKing = board[x][y] == 1 && y == HEIGHT_BOARD - 1;
        boolean isBlackKing = board[x][y] == 2 && y == 0;

        if (isWhiteKing || isBlackKing) {
            board[x][y] = isWhiteKing ? 3 : 4;
        }
    }

    private static boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x <= 7 && y <= 7;
    }

    public static int[][] copyBoard(int[][] originalBoard) {
        int[][] copiedBoard = new int[originalBoard.length][];
        for (int i = 0; i < originalBoard.length; i++) {
            copiedBoard[i] = originalBoard[i].clone();
        }
        return copiedBoard;
    }

    public static void printBoard(int[][] board) {
        if (board == null) {
            System.out.println("Board is null");
            return;
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    static class Move {
        int value;
        int[][] board;
        int startX, startY, endX, endY;

        Move(int value, int[][] board, int startX, int startY, int endX, int endY) {
            this.value = value;
            this.board = board;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
    }
}
