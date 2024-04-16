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

    public PlayerToken getPlayer1() {
        return player1;
    }

    public PlayerToken getPlayer2() {
        return player2;
    }

    public boolean isPlayer1IsPlayingWhite() {
        return player1IsPlayingWhite;
    }

    public boolean isPlayer1IsMoving() {
        return player1IsMoving;
    }

    public void setPlayer1(PlayerToken player1) {
        this.player1 = player1;
    }

    public void setPlayer2(PlayerToken player2) {
        this.player2 = player2;
    }

    public void setPlayer1IsPlayingWhite(boolean player1IsPlayingWhite) {
        this.player1IsPlayingWhite = player1IsPlayingWhite;
    }

    public void setPlayer1IsMoving(boolean player1IsMoving) {
        this.player1IsMoving = player1IsMoving;
    }
}
