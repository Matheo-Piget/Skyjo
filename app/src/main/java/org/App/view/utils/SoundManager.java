package org.App.view.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Manages the playback of sound effects in the application.
 * 
 * <p>
 * This class provides methods to play sound effects from audio files.
 * It uses JavaFX's MediaPlayer class to play sounds in the application.
 * </p>
 * 
 * Use the default constructor to create an instance of SoundManager.
 * 
 * @version 1.0
 * @author Piget Mathéo
 * 
 */
public class SoundManager {
    private static final String SOUND_PATH = "src/main/resources/sounds/";
    private static final String SOUND_EXTENSION = ".mp3";

    // MediaPlayer pour le son de retournement (réutilisé)
    private static final MediaPlayer flipSound = new MediaPlayer(
        new Media(new File(SOUND_PATH + "flip_sound.mp3").toURI().toString())
    );

    // Pool de MediaPlayer pour gérer plusieurs sons simultanément
    private static final List<MediaPlayer> mediaPlayerPool = new ArrayList<>();
    private static final int MAX_MEDIA_PLAYERS = 5; // Limite à 5 sons simultanés

    /**
     * Joue un son à partir du fichier spécifié.
     * 
     * @param soundName Le nom du fichier sonore (sans extension).
     */
    public static void playSound(String soundName) {
        try {
            MediaPlayer mediaPlayer = getAvailableMediaPlayer();
            if (mediaPlayer == null && mediaPlayerPool.size() < MAX_MEDIA_PLAYERS) {
                // Crée un nouveau MediaPlayer si le pool n'est pas plein
                String soundFile = SOUND_PATH + soundName + SOUND_EXTENSION;
                Media sound = new Media(new File(soundFile).toURI().toString());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayerPool.add(mediaPlayer);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop(); // Arrête le son s'il est déjà en cours
                mediaPlayer.play();
            }
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
        }
    }

    /**
     * Joue le son de retournement.
     */
    public static void playFlipSound() {
        flipSound.stop(); // Arrête le son s'il est déjà en cours
        flipSound.play();
    }

    /**
     * Arrête tous les sons en cours.
     */
    public static void stopAllSounds() {
        for (MediaPlayer mediaPlayer : mediaPlayerPool) {
            mediaPlayer.stop();
        }
        flipSound.stop();
    }

    /**
     * Libère les ressources de tous les MediaPlayer.
     */
    public static void dispose() {
        for (MediaPlayer mediaPlayer : mediaPlayerPool) {
            mediaPlayer.dispose();
        }
        mediaPlayerPool.clear();
        flipSound.dispose();
    }

    /**
     * Retourne un MediaPlayer disponible dans le pool.
     * 
     * @return Un MediaPlayer disponible, ou null si aucun n'est disponible.
     */
    private static MediaPlayer getAvailableMediaPlayer() {
        for (MediaPlayer mediaPlayer : mediaPlayerPool) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                return mediaPlayer;
            }
        }
        return null;
    }
}