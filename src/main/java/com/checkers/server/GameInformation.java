package com.checkers.server;

public class GameInformation {
    private PlayerToken player1;
    private PlayerToken player2;
    private boolean player1IsPlayingWhite;
    private boolean player1IsMoving;

    public GameInformation(PlayerToken player1, PlayerToken player2) {
        this.player1 = player1;
        this.player2 = player2;

        player1IsMoving = player1IsPlayingWhite = Math.random() < 0.5;
    }
}
