package org.App.view;

import java.io.File;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Gère la musique de fond du jeu.
 * Permet de jouer, arrêter, mettre en pause et ajuster le volume de la musique.
 */
public class MusicManager {

    private MediaPlayer mediaPlayer;
    private  static MusicManager INSTANCE;

    /**
     * Initialise le MusicManager avec un fichier audio.
     *
     * @param musicFilePath Le chemin du fichier audio (par exemple, "music.mp3").
     */
    public MusicManager(String musicFilePath) {

        INSTANCE = this;
        Media media = new Media(new File(musicFilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Répète la musique en boucle
    }

    /**
     * Joue la musique de fond.
     */
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public static MusicManager getINSTANCE() {
        return INSTANCE;
    }

    /**
     * Arrête la musique de fond.
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Met en pause la musique de fond.
     */
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Définit le volume de la musique.
     *
     * @param volume Le volume (entre 0.0 et 1.0).
     */
    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    /**
     * Retourne le volume actuel de la musique.
     *
     * @return Le volume actuel (entre 0.0 et 1.0).
     */
    public double getVolume() {
        return mediaPlayer != null ? mediaPlayer.getVolume() : 0.0;
    }
}