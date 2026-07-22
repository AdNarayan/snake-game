import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    public static void playEat() {
        playTone(523, 60, 40);
        playTone(659, 50, 35);
    }

    public static void playGameOver() {
        playTone(400, 150);
        playTone(300, 150);
        playTone(200, 300);
    }

    private static void playTone(double freq, int ms) {
        playTone(freq, ms, 80);
    }

    private static void playTone(double freq, int ms, int amplitude) {
        Thread t = new Thread(() -> {
            try {
                float sampleRate = 44100;
                byte[] buf = new byte[(int) (sampleRate * ms / 1000)];
                for (int i = 0; i < buf.length; i++) {
                    double angle = 2.0 * Math.PI * i / (sampleRate / freq);
                    buf[i] = (byte) (Math.sin(angle) * amplitude);
                }
                AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
                sdl.write(buf, 0, buf.length);
                sdl.drain();
                sdl.close();
            } catch (LineUnavailableException e) {
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void playWav(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return;
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
        } catch (Exception e) {
        }
    }
}
