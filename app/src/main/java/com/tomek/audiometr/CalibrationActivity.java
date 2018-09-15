package com.tomek.audiometr;

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

    private MediaRecorder mRecorder;
    private AudioManager audioManager;

    private double decibels;

    private ArrayList<Double> list;

    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    private static final double referenceAmp = 1.0;

    public double total = 0.0;
    public double average = 0.0;

    final Handler mHandler = new Handler();

    final Runnable updater = new Runnable() {

        public void run() {
            if (play != null) {
//                updateTv();
                addPoint();
            }
        }
    };

    final Runnable getValue = new Runnable() {

        public void run() {
            averageDecibels();
            toastEnd.show();
            savePreference("maxDecibels", String.valueOf(average));
        }
    };

    private void savePreference(String key, String value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void playAsync() {
        if (play != null) {
            return;
        }

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                list.clear();
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

            recordThread = new Thread() {
                public void run() {

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

    public void onResume() {
        super.onResume();
        startRecorder();
    }

    public void onPause() {
        super.onPause();
        if (recordThread != null) {
            stopRecorder();
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
    }

    public void startRecorder() {
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
            list = new ArrayList<>();
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0; //delete
        }

    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

//    public void updateTv() {
//        mStatusView.setText(Double.toString((soundDb())) + " dB");
//    }

    public void addPoint() {
        decibels = soundDb();
        if (decibels != Double.NEGATIVE_INFINITY) {
            list.add(decibels);
            Log.e("test", "soundDb = " + decibels);
        }
        Log.e("test", "List = " + list);

    }

    public double averageDecibels() {
        total = 0.0;

        for (int i = 0; i < list.size(); i++) {
            total = total + list.get(i);
        }

        Log.e("test", "list.size() = " + list.size());

        average = total / list.size();

        Log.e("test", "Average = " + average);
        return average;
    }

    public double soundDb() {
        decibels = 20 * Math.log10(getAmplitudeEMA() / referenceAmp);
        return decibels;
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public void setMaxVolume() {

        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

    }

    public void calibrateButtonAction() {
        setMaxVolume();
        playAsync();
        recordAsync();
        toast.show();
    }

    public void initCalibrateButton() {
        btnCalibration = findViewById(R.id.btn_calibration);
        btnCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calibrateButtonAction();
                vibe.vibrate(50);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Context context = getApplicationContext();
        CharSequence text = "Trwa kalibracja...";
        CharSequence textEnd = "Zakończono kalibrację";
        int duration = Toast.LENGTH_SHORT;

        toast = Toast.makeText(context, text, duration);
        toastEnd = Toast.makeText(context, textEnd, duration);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (ActivityCompat.checkSelfPermission(CalibrationActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CalibrationActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        initCalibrateButton();
        initHelpButton();
    }

    public void initHelpButton() {
        buttonHelp = findViewById(R.id.btn_help_cal);
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpButtonAction();
            }
        });
    }

    public void helpButtonAction() {
        vibe.vibrate(50);
        Intent intentInfo = new Intent(this, PopUpCalibration.class);
        startActivity(intentInfo);
    }

    @Override
    public void onBackPressed() {
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

        if (id == R.id.nav_start) {

            Intent intentLauncher = new Intent(this, MainActivity.class);
            intentLauncher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLauncher);

        } else if (id == R.id.nav_calibration) {

            Intent intentKal = new Intent(this, CalibrationActivity.class);
            intentKal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentKal);

        } else if (id == R.id.nav_audioTest) {
            Intent intentTest = new Intent(this, ChoiceActivity.class);
            intentTest.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentTest);

        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(this, PopUpAppInfo.class);
            intentInfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentInfo);

        } else if (id == R.id.nav_exit) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
