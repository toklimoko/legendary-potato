package com.tomek.audiometr.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tomek.audiometr.algorithms.LoudnessData;
import com.tomek.audiometr.algorithms.Record;
import com.tomek.audiometr.helpers.Drawer;
import com.tomek.audiometr.helpers.Preferences;
import com.tomek.audiometr.helpers.VolumeController;
import com.tomek.audiometr.algorithms.Play;
import com.tomek.audiometr.popups.PopUpAppInfo;
import com.tomek.audiometr.popups.PopUpCalibration;
import com.tomek.audiometr.R;

import java.util.ArrayList;

public class CalibrationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton btnCalibration;
    private ImageButton buttonHelp;
    private Toast toast;
    private Toast toastEnd;
    private Vibrator vibe;

    private Thread playThread;
    private Thread recordThread;

    private Play play;
    private Record record;
    private Preferences preferences;

    private MediaRecorder mRecorder;
    private AudioManager audioManager;
    private VolumeController volumeController;

    private ArrayList<Double> list;

    private static final double referenceAmp = 1.0;
    public double average = 0;

    private LoudnessData loudnessData;

    private final Handler mHandler = new Handler();

    private final Runnable updater = new Runnable() {

        public void run() {
            if (play != null) {
                list = loudnessData.add(mRecorder,referenceAmp,list);
            }
        }
    };

    private final Runnable getValue = new Runnable() {

        public void run() {
            average = loudnessData.average(list);
            toastEnd.show();
            preferences.savePreference("maxDecibels", String.valueOf(average), getApplicationContext());
            preferences.savePreference("calibrated", "true", getApplicationContext());
        }
    };


    private void playAsync() {
        if (play != null) {
            return;
        }

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (list != null) {
                    list.clear();
                }
                play = new Play(1000, 1, 4, "Both");
                play.playSound();
                play = null;
                mHandler.post(getValue);
            }
        });

        playThread.start();
    }

    private void recordAsync() {

        if (recordThread == null) {

            list = new ArrayList<>();
            recordThread = new Thread() {
                public void run() {

                    mRecorder = record.start(mRecorder,audioManager);

                    while (recordThread != null) {
                        if (play != null) {
                            try {
                                Thread.sleep(200);
                                Log.i("test", "Tock");
                            } catch (InterruptedException e) {
                            }
                            mHandler.post(updater);
                        }
                    }
                }
            };
            recordThread.start();
            Log.d("test", "start recordThread()");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recordThread != null) {
            mRecorder = record.stop(mRecorder);
        }
        if (play != null) {
            play.release();
        }
        try {
            playThread.interrupt();
            recordThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        volumeController.setMin();

        recordThread = null;
        play = null;
    }


    private void initCalibrateButton() {
        btnCalibration = findViewById(R.id.btn_calibration);
        btnCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calibrateButtonAction();
                vibe.vibrate(50);
            }
        });
    }

    private void calibrateButtonAction() {
        volumeController.setMax();
        playAsync();
        recordAsync();
        toast.show();
    }

    private void initHelpButton() {
        buttonHelp = findViewById(R.id.btn_help_cal);
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpButtonAction();
            }
        });
    }

    private void helpButtonAction() {
        vibe.vibrate(50);
        Intent intentInfo = new Intent(this, PopUpCalibration.class);
        startActivity(intentInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Context context = getApplicationContext();
        CharSequence text = "Trwa kalibracja...";
        CharSequence textEnd = "Zakończono kalibrację";
        int duration = Toast.LENGTH_SHORT;

        toast = Toast.makeText(context, text, duration);
        toastEnd = Toast.makeText(context, textEnd, duration);

//        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (ActivityCompat.checkSelfPermission(CalibrationActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CalibrationActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        volumeController = new VolumeController(audioManager);


        initCalibrateButton();
        initHelpButton();

        loudnessData = new LoudnessData();
        record = new Record();
        preferences = new Preferences();
    }


    @Override
    public void onBackPressed() {
        volumeController.setMin();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Drawer drawer = new Drawer();
        Intent intent = drawer.action(id, getApplicationContext(),volumeController);
        startActivity(intent);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
