package org.App.view.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class SoundManager {
    private final MediaPlayer mediaPlayer;

    public SoundManager(String soundFilePath) {
        Media media = new Media(new File(soundFilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    }

    /**
     * Joue le son.
     */
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Arrête le son s'il est déjà en cours
            mediaPlayer.play();
        }
    }

    /**
     * Arrête le son.
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}