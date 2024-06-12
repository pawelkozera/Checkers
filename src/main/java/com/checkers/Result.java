package com.checkers;

public class Result {
    private final Move move;
    private final int score;

    public Result(Move move, int score) {
        this.move = move;
        this.score = score;
    }

    public Move getMove() {
        return move;
    }

    public int getScore() {
        return score;
    }
}