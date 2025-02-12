package org.App.view.utils;

import java.io.File;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Gère la musique de fond du jeu.
 * Permet de jouer, arrêter, mettre en pause et ajuster le volume de la musique.
 * 
 * <p>
 * La classe utilise la classe {@link MediaPlayer} de JavaFX pour lire des
 * fichiers audio.
 * </p>
 * 
 * @see MediaPlayer
 * @see Media
 * 
 * @version 1.0
 * @author Mathéo Piget
 */

public class MusicManager {

    private final MediaPlayer mediaPlayer;
    private static final double FADE_DURATION = 1.0;

    public MusicManager(String musicFilePath) {
        Media media = new Media(new File(musicFilePath).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    /**
     * Joue la musique de fond avec un fondu.
     */
    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0.0);
            mediaPlayer.play();
            fadeIn();
        }
    }

    /**
     * Arrête la musique de fond avec un fondu.
     */
    public void stop() {
        if (mediaPlayer != null) {
            fadeOut(() -> mediaPlayer.stop());
        }
    }

    /**
     * Met en pause la musique de fond avec un fondu.
     */
    public void pause() {
        if (mediaPlayer != null) {
            fadeOut(() -> mediaPlayer.pause());
        }
    }

    /**
     * Effectue un fondu en entrée (augmentation progressive du volume).
     */
    private void fadeIn() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(FADE_DURATION),
                        new KeyValue(mediaPlayer.volumeProperty(), 1.0)));
        timeline.play();
    }

    /**
     * Effectue un fondu en sortie (diminution progressive du volume).
     * 
     * @param onFinished Une action à exécuter une fois le fondu terminé.
     */
    private void fadeOut(Runnable onFinished) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(FADE_DURATION),
                        new KeyValue(mediaPlayer.volumeProperty(), 0.0)));
        timeline.setOnFinished(event -> onFinished.run());
        timeline.play();
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

    /**
     * Indique si la musique est en cours de lecture.
     *
     * @return {@code true} si la musique est en cours de lecture, {@code false}
     *         sinon.
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
}