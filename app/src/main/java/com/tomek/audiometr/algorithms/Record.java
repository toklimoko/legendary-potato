package com.tomek.audiometr.algorithms;

import android.media.AudioManager;
import android.media.MediaRecorder;

/**
 * Created by tokli on 18.09.2018.
 */

public class Record {

    public Record() {
    }

    public MediaRecorder start(MediaRecorder mRecorder, AudioManager audioManager) {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
//            mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            if (audioManager.getProperty(AudioManager.PROPERTY_SUPPORT_AUDIO_SOURCE_UNPROCESSED) != null) {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.UNPROCESSED);
            } else {
                mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            }
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " + android.util.Log.getStackTraceString(ioe));

            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }
        }

        return mRecorder;
    }


    public MediaRecorder stop(MediaRecorder mRecorder) {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        return mRecorder;
    }
}
