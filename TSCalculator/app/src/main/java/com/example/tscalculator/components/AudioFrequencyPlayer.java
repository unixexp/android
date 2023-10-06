package com.example.tscalculator.components;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioFrequencyPlayer {

    private Thread generatorThread = null;
    private Thread startPlayThread = null;
    private AudioTrack audioTrack = null;
    private int frequency;
    private final int duration = 1; // Seconds
    private final int sampleRate = 8000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final byte generatedSound[] = new byte[numSamples*2];
    private boolean playing = false;
    private boolean generated = false;

    public AudioFrequencyPlayer(int defaultFrequency) {
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, generatedSound.length,
                AudioTrack.MODE_STATIC);
        setFrequency(defaultFrequency);
    }

    public void setFrequency(int f) {
        frequency = f;
        if (!playing) {
            generateSound();
        } else {
            stop();
            play();
        }

    }

    public void play() {
        if (playing)
            return;

        if (startPlayThread != null) {
            while (startPlayThread.isAlive()) {
                try {
                    startPlayThread.join();
                } catch (InterruptedException e) {
                    System.out.println("Start play thread interrupted");
                }
            }
        }

        generateSound();
        playing = true;

        startPlayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!generated) {
                    // Wait till sound will be generated }
                }
                if (audioTrack.getState() == 1) {
                    audioTrack.setLoopPoints(0, audioTrack.getBufferSizeInFrames(),
                            -1);
                    audioTrack.play();
                }
            }
        });
        startPlayThread.start();
    }

    public void stop() {
        if (audioTrack != null && audioTrack.getState() == 1)
            audioTrack.stop();
        playing = false;
    }

    private void generateSound() {
        if (generatorThread != null) {
            generated = true;
            while (generatorThread.isAlive()) {
                try {
                    generatorThread.join();
                } catch (InterruptedException e) {
                    System.out.println("Generate thread interrupted");
                }
            }
            generated = false;
        } else {
            generated = false;
        }

        Thread generatorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!generated) {
                    for (int i = 0; i < numSamples; ++i) {
                        sample[i] = Math.sin(2 * Math.PI * i / ((float) sampleRate / (float) frequency));
                    }

                    // Convert to 16 bit pcm sound array
                    // assumes the sample buffer is normalized.
                    int idx = 0;
                    for (final double dVal : sample) {
                        // Scale to maximum amplitude
                        final short val = (short) ((dVal * 32767));
                        // in 16 bit wav PCM, first byte is the low order byte
                        generatedSound[idx++] = (byte) (val & 0x00ff);
                        generatedSound[idx++] = (byte) ((val & 0xff00) >>> 8);
                    }

                    audioTrack.write(generatedSound, 0, generatedSound.length);

                    generated = true;
                }
            }
        });
        generatorThread.start();
    }

    public int getFrequency() {
        return frequency;
    }

}
