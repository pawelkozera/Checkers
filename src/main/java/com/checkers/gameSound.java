package com.checkers;


import javafx.scene.media.AudioClip;

public class gameSound {
    private static final AudioClip moveSound = new AudioClip(gameSound.class.getResource("/move_sound.mp3").toString());
    private static final AudioClip promoteSound = new AudioClip(gameSound.class.getResource("/promote_sound.mp3").toString());
    private static final AudioClip gameEnd = new AudioClip(gameSound.class.getResource("/game_end.mp3").toString());
    private static final AudioClip gameStart = new AudioClip(gameSound.class.getResource("/game_start.mp3").toString());
    public static void playMoveSound()
    {
        moveSound.play();
    }
    public static void playPromoteSound()
    {
        promoteSound.play();
    }
    public static void playGameEndSound()
    {
        gameEnd.play();
    }
    public static void playGameStartSound()
    {
        gameStart.play();
    }
}
