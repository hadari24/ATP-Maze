package View;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusicPlayer {
    private MediaPlayer mediaPlayer;

    public void playMusic() {
        if (mediaPlayer == null) {
            String path = getClass().getResource("/music/retro-game-arcade-236133.mp3").toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // לופ תמידי
        }
        mediaPlayer.play();
    }

    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
