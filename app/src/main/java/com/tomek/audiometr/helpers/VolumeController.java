package com.tomek.audiometr.helpers;

import android.media.AudioManager;

/**
 * Created by tokli on 18.09.2018.
 */

public class VolumeController {

    private AudioManager audioManager;

    public VolumeController(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void setMax() {

        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
    }

    public void setMin() {

        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                (int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*0.3),
                0);

    }


}
