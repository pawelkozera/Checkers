package com.checkers;


import javafx.scene.media.AudioClip;

public class gameSound {
    private static final AudioClip moveSound = new AudioClip(gameSound.class.getResource("/move_sound.mp3").toString());
    private static final AudioClip promoteSound = new AudioClip(gameSound.class.getResource("/promote_sound.mp3").toString());
    private static final AudioClip gameEnd = new AudioClip(gameSound.class.getResource("/game_end.mp3").toString());
    private static final AudioClip gameStart = new AudioClip(gameSound.class.getResource("/game_start.mp3").toString());

    static {
        moveSound.setVolume(0);
        moveSound.play();
        moveSound.stop();
        moveSound.setVolume(1);

        promoteSound.setVolume(0);
        promoteSound.play();
        promoteSound.stop();
        promoteSound.setVolume(1);

        gameEnd.setVolume(0);
        gameEnd.play();
        gameEnd.stop();
        gameEnd.setVolume(1);

        gameStart.setVolume(0);
        gameStart.play();
        gameStart.stop();
        gameStart.setVolume(1);
    }

    public static void playMoveSound() {moveSound.play();}
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
