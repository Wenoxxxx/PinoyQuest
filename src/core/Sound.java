package src.core;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sound {

    Clip clip;

    // Use file paths (project-relative) because assets are not packaged on the
    // classpath
    private final String[] soundFiles = new String[10];

    public Sound() {
        // Map indexes to actual files found in repo under src/assets/music
        soundFiles[0] = "src/assets/music/1st song.wav";
        soundFiles[1] = "src/assets/music/2nd song.wav";
        soundFiles[2] = "src/assets/music/start.wav";
        soundFiles[3] = "src/assets/music/3rd.wav";
        // fallback / duplicate mapping
        soundFiles[4] = soundFiles[0];
    }

    public void setFile(int i) {
        try {
            String path = (i >= 0 && i < soundFiles.length) ? soundFiles[i] : null;
            if (path == null) {
                System.err.println("[Sound] No file mapped for index: " + i);
                return;
            }

            java.io.File f = new java.io.File(path);
            if (!f.exists()) {
                System.err.println("[Sound] File not found: " + f.getAbsolutePath());
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            clip = AudioSystem.getClip();
            clip.open(ais);
            System.out.println("[Sound] Loaded sound: " + f.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("[Sound] Failed to load/prepare sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void play() {
        if (clip == null) {
            System.err.println("[Sound] play() called but clip is null");
            return;
        }
        clip.start();
    }

    public void loop() {
        if (clip == null) {
            System.err.println("[Sound] loop() called but clip is null");
            return;
        }
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        if (clip == null)
            return;
        clip.stop();
    }
}
