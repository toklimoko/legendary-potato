package com.tomek.audiometr;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by tokli on 31.01.2018.
 */

public class Play {

    private double frequency = 0.0;
    private double amplitude = 0.0;
    private int duration = 1;
    private String channel = "Both"; //domyslnie

    private double amplitudeL = 0.0;
    private double amplitudeR = 0.0;

    AudioTrack mAudioTrack;


    public Play(double frequency, double amplitude, int duration, String channel) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.duration = duration * 44100; //44100 - tyle bitów to jedna sekunda
        this.channel = channel;
    }

    // generowanie sgnału sinusoidalnego z wykorzystaniem klasy AudioTrack
    public void playSound() {
        // AudioTrack definition
        int mBufferSize = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_8BIT);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                mBufferSize, AudioTrack.MODE_STREAM);

        // Sine wave
        double[] mSound = new double[duration];
        short[] mBuffer = new short[duration];


        for (int i = 0; i < mSound.length; i++) {
            mSound[i] = Math.sin((2 * Math.PI * i / (44100 / frequency)));
            mBuffer[i] = (short) (mSound[i] * Short.MAX_VALUE);
        }

        // definicja wartości "gain" kanałów
        if (channel.equals("Left")) {
            amplitudeL = amplitude;
        } else if (channel.equals("Right")) {
            amplitudeR = amplitude;
        } else if (channel.equals("Both")){
            amplitudeR = amplitude;
            amplitudeL = amplitude;
        }

        mAudioTrack.setStereoVolume((float) amplitudeL, (float) amplitudeR);


        try {
            mAudioTrack.play();
        } catch(IllegalStateException e){
            e.printStackTrace();
        }

        mAudioTrack.write(mBuffer, 0, mSound.length);

        try {
            mAudioTrack.stop();
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
        mAudioTrack.release();

    }

    void release() {
        if (mAudioTrack != null) {
            try {
                mAudioTrack.stop();
            } catch (IllegalStateException e){
                e.printStackTrace();
            }
            mAudioTrack.release();
        }
    }


}
