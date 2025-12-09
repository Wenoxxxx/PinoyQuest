package src.core;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class Sound {

    private Clip clip;

    // New folder structure: assets/music/...
    private final String[] soundFiles = new String[10];

    public Sound() {
        soundFiles[0] = "assets/music/1st song.wav";
        soundFiles[1] = "assets/music/2nd song.wav";
        soundFiles[2] = "assets/music/start.wav";
        soundFiles[3] = "assets/music/3rd.wav";

        // fallback
        soundFiles[4] = soundFiles[0];
    }

    public void setFile(int index) {
        try {
            if (index < 0 || index >= soundFiles.length || soundFiles[index] == null) {
                System.err.println("[Sound] No file mapped for index: " + index);
                return;
            }

            File file = new File(soundFiles[index]);

            if (!file.exists()) {
                System.err.println("[Sound] File not found: " + file.getAbsolutePath());
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(ais);

            System.out.println("[Sound] Loaded sound: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("[Sound] Error loading sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip == null) {
            System.err.println("[Sound] Cannot play — clip is null");
            return;
        }
        clip.setFramePosition(0);  // restart sound
        clip.start();
    }

    public void loop() {
        if (clip == null) {
            System.err.println("[Sound] Cannot loop — clip is null");
            return;
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
