package com.tomek.audiometr.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.tomek.audiometr.algorithms.FrequenciesData;
import com.tomek.audiometr.algorithms.LoudnessData;
import com.tomek.audiometr.helpers.Drawer;
import com.tomek.audiometr.helpers.Preferences;
import com.tomek.audiometr.helpers.VolumeController;
import com.tomek.audiometr.algorithms.Play;
import com.tomek.audiometr.popups.PopUpAudioTest;
import com.tomek.audiometr.R;
import com.tomek.audiometr.algorithms.Sample;


import java.util.ArrayList;

public class AudioTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private double frequency = 0.0;
    private double amplitude = 0.0;
    private double decibels = 0.0;
    private int duration = 1;
    private String channel = "Both";
    private int numberOfFrequencies = 0; // equivalent to numberOfFrequencies*2 attempts
    private double frequencyLimitMin = 0;
    private double frequencyLimitMax = 18000;
    private double maxDecibels = -100.0;
    private double maxLevel = 18;
    private double decibelsInTable = 0.0;
    private double indexOfMaxDecibels = 0;
    private int level = 0;
    private int index = 0;
    private boolean stop = false;

    private ImageButton buttonStart;
    private ImageButton buttonHeard;
    private ImageButton buttonCancel;
    private ImageButton buttonResult;

    private TextView textViewStart;
    private TextView textViewHeard;
    private TextView textViewCancel;
    private TextView textViewResult;
    private TextView textViewAudioTest;

    private TableLayout tableLayout;
    private DrawerLayout drawer;

    private Intent intent;

    private Vibrator vibe;
    private Thread playThread;
    private Play play;

    private Sample sample;
    private VolumeController volumeController;

    private ArrayList<Integer> allFrequencies;
    private ArrayList<Integer> chosenFrequencies;
    private ArrayList<Sample> samplesList;
    private ArrayList<String> newSample;

    private ArrayList<Double> maxDecibelsData;

    private ArrayList<Double> xAxis;
    private ArrayList<Double> yAxis;
    private ArrayList<String> channels;

    private Double[][] dataTable;
    private LoudnessData loudnessData;
    private Preferences preferences;
    private FrequenciesData frequenciesData;


    private void getNewSample() {
        Log.e("test", "AudioTestActivity: getNewSample() --before");

        if (newSample != null) {
            newSample.clear();
        }
        sample.setSamplesList(samplesList);
        newSample = sample.getNewSample();
        if (newSample != null) {
            frequency = Double.parseDouble(newSample.get(0));
            channel = newSample.get(1);
            samplesList = sample.getSamplesList();
            stop = false;

            Log.e("test", "AudioTestActivity: getNewSample() --after // values: sampleList.size() = " + samplesList.size() + "; received frequency = " + frequency + "; received channel = " + channel);

        } else {
            Log.e("test", "AudioTestActivity: getNewSample() --after // msg: newSample is null = all attempts made");
            playThread.interrupt();
            resultButtonAction();
        }
    }

    private double getAmplitude(int level) {

        index = (int) indexOfMaxDecibels + level;
        amplitude = dataTable[index][1];
        decibels = dataTable[index][0] - decibelsInTable;

        Log.e("test", "Frequency = " + frequency + "\t amplitude = " + amplitude + "\t decibels = " + decibels + "dB");

        return amplitude;
    }

    private void addPoint() {
        Log.e("test", "AudioTestActivity: add() --before");

        xAxis.add(frequency);
        yAxis.add(decibels);
        channels.add(channel);

        Log.e("test", "AudioTestActivity: add() --after // values: added frequency = " + frequency + "; added decibels = " + decibels + "; added channel = " + channel + "\t"
                + "xAxis.size() = " + xAxis.size() + "; yAxis.size() = " + yAxis.size() + "; channels.size() = " + channels.size());

    }

    private void showResult() {
        Log.e("test", "AudioTestActivity: showResult() --before");

        volumeController.setMin();

        frequencyLimitMin = frequenciesData.xLimits(chosenFrequencies).get(0);
        frequencyLimitMax = frequenciesData.xLimits(chosenFrequencies).get(1);

        Intent intentResult = new Intent(AudioTestActivity.this, ResultActivity.class);
        intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intentResult.putExtra("xAxis", xAxis);
        intentResult.putExtra("yAxis", yAxis);
        intentResult.putExtra("channels", channels);
        intentResult.putExtra("decibelsLimit", decibelsInTable);
        intentResult.putExtra("frequencyLimitMin", frequencyLimitMin);
        intentResult.putExtra("frequencyLimitMax", frequencyLimitMax);

        startActivity(intentResult);

        Log.e("test", "AudioTestActivity: showResult() --after // values: xAxis.toString() = " + xAxis.toString() + "\t"
                + "yAxis.toString() = " + yAxis.toString() + "\t" + "channels.toString() = " + channels.toString());
    }

    private void resetValues() {
        Log.e("test", "AudioTestActivity: resetValues() --before");

        amplitude = 0.0;
        level = 0;
        index = 0;
        sample = new Sample(numberOfFrequencies, chosenFrequencies);

        Log.e("test", "AudioTestActivity: resetValues() --after");
    }

    private void hardResetValues() {
        Log.e("test", "AudioTestActivity: hardResetValues() --before");

        resetValues();
        chosenFrequencies = new ArrayList<>();
        samplesList = new ArrayList<>();
        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();

        Log.e("test", "AudioTestActivity: hardResetValues() --after");
    }


    private void showStartMode() {
        buttonStart.setVisibility(View.VISIBLE);
        textViewStart.setVisibility(View.VISIBLE);
        buttonHeard.setVisibility(View.GONE);
        textViewHeard.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        textViewCancel.setVisibility(View.GONE);
        buttonResult.setVisibility(View.GONE);
        textViewResult.setVisibility(View.GONE);
        tableLayout.setVisibility(View.VISIBLE);
        textViewAudioTest.setText(R.string.tv_audioTest_1);
    }

    private void showAudioTestMode() {
        buttonStart.setVisibility(View.GONE);
        textViewStart.setVisibility(View.GONE);
        buttonHeard.setVisibility(View.VISIBLE);
        textViewHeard.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.VISIBLE);
        textViewCancel.setVisibility(View.VISIBLE);
        buttonResult.setVisibility(View.GONE);
        textViewResult.setVisibility(View.GONE);
        tableLayout.setVisibility(View.GONE);
        textViewAudioTest.setText(R.string.tv_audioTest_2);
    }

    private void showResultMode() {
        buttonStart.setVisibility(View.GONE);
        textViewStart.setVisibility(View.GONE);
        buttonHeard.setVisibility(View.GONE);
        textViewHeard.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.VISIBLE);
        textViewCancel.setVisibility(View.VISIBLE);
        buttonResult.setVisibility(View.VISIBLE);
        textViewResult.setVisibility(View.VISIBLE);
        tableLayout.setVisibility(View.VISIBLE);
        tableLayout.setVisibility(View.GONE);
        textViewAudioTest.setText(R.string.tv_audioTest_3);
    }


    private void playAsync() {
        Log.e("test", "AudioTestActivity: playAsync() --before");

        if (play != null) {
            return;
        }

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {

                amplitude = 0.0;
                level = 0;
                index = 0;

                // play the loop until the thread is interrupted or condition is met
                while (!Thread.currentThread().isInterrupted()) {
                    amplitude = getAmplitude(level);

                    play = new Play(frequency, amplitude, duration, channel);
                    play.playSound();
                    play = null;

                    level++;

                    Log.e("test", "AudioTestActivity: playButtonAction() --while loop // values: amplitude = " + amplitude + "\t level = " + level);

                    if (index > (dataTable.length - 1) || level > maxLevel || amplitude == 1) {
                        playThread.interrupt();
                        addAndPlayNew();
                            Log.e("test","playThread interrupted in if in while loop ");

                        Log.e("test", "AudioTestActivity: playButtonAction() --while loop // msg: got new sample, new Thread");
                        break;
                    }
                }
            }
        });
        playThread.start();

        Log.e("test", "AudioTestActivity: playAsync() --after");
    }


    private void initPlaySoundButton() {
        Log.e("test", "AudioTestActivity: initPlaySoundButton() --before");

        buttonStart = findViewById(R.id.btn_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playButtonAction();
            }
        });

        Log.e("test", "AudioTestActivity: initPlaySoundButton() --after");
    }

    private void playButtonAction() {
        Log.e("test", "AudioTestActivity: playButtonAction() --before");
        chosenFrequencies = frequenciesData.random(numberOfFrequencies, allFrequencies);
        vibe.vibrate(50);
        stop = false;
        showAudioTestMode();
        volumeController.setMax();
        resetValues();
//        maxDecibels = preferences.loadDecibels(getApplicationContext());
        maxDecibelsData = loudnessData.find(maxDecibels);
        decibelsInTable = maxDecibelsData.get(0);
        indexOfMaxDecibels = maxDecibelsData.get(1);
        getNewSample();
        playAsync();

        Log.e("test", "AudioTestActivity: playButtonAction() --after // values: decibelsInTable = " + decibelsInTable + "; indexOfMaxDecibels = " + indexOfMaxDecibels);
    }

    private void initStopSoundButton() {
        Log.e("test", "AudioTestActivity: initStopSoundButton() --before");

        buttonHeard = findViewById(R.id.btn_heard);
        buttonHeard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButtonAction();
            }
        });

        Log.e("test", "AudioTestActivity: initStopSoundButton() --after");
    }

    private void stopButtonAction() {
        Log.e("test", "AudioTestActivity: stopButtonAction() --before");

        vibe.vibrate(50);
        stop = true;
        if (play != null){
            playThread.interrupt();
            play.release();
            play = null;
        }
        addAndPlayNew();
        Log.e("test", "Play thread replayed");


        Log.e("test", "AudioTestActivity: stopButtonAction() --after");
    }

    private void addAndPlayNew(){
        addPoint();
        resetValues();
        getNewSample();
        volumeController.setMax();
        playAsync();
    }

    private void initCancelButton() {
        Log.e("test", "AudioTestActivity: initCancelButton() --before");

        buttonCancel = findViewById(R.id.btn_finish);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButtonAction();
            }
        });

        Log.e("test", "AudioTestActivity: initCancelButton() --after");
    }

    private void cancelButtonAction() {
        Log.e("test", "AudioTestActivity: cancelButtonAction() --before");

        vibe.vibrate(50);
        stop = true;
//        showStartMode();
//        onPause();
//        hardResetValues();

        Intent intent = new Intent(this, ChoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        Log.e("test", "AudioTestActivity: initCancelButton() --after");
    }

    private void initResultButton() {
        Log.e("test", "AudioTestActivity: initResultButton() --before");

        buttonResult = findViewById(R.id.btn_result);
        buttonResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultButtonAction();
            }
        });

        Log.e("test", "AudioTestActivity: initResultButton() --after");
    }

    private void resultButtonAction() {
        Log.e("test", "AudioTestActivity: resultButtonAction() --before");

        vibe.vibrate(50);
        runOnUiThread(new Runnable() { //fixes e "Only the original thread that created a view hierarchy can touch its views."
            @Override
            public void run() {
                showResultMode();
            }
        });
        showResult();

        Log.e("test", "AudioTestActivity: resultButtonAction() --after");
    }

    public void helpButton(View v) {
        vibe.vibrate(50);
        intent = new Intent(this, PopUpAudioTest.class);
        startActivity(intent);
    }

    public void drawerButton(View v) {
        vibe.vibrate(50);
        drawer.openDrawer(Gravity.START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (play != null) {
            play.release();
        }
        if (stop) {
            stop = false;
        }
        if (playThread != null) {
            try {
                playThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        volumeController.setMin();
        play = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
        drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        volumeController = new VolumeController((AudioManager) this.getSystemService(Context.AUDIO_SERVICE));

        textViewStart = findViewById(R.id.tv_start);
        textViewHeard = findViewById(R.id.tv_heard);
        textViewCancel = findViewById(R.id.tv_back);
        textViewResult = findViewById(R.id.tv_result);
        textViewAudioTest = findViewById(R.id.textViewAudioTest);
        tableLayout = findViewById(R.id.tl_audioTest);

        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();

        preferences = new Preferences();

        frequenciesData = new FrequenciesData();

        allFrequencies = preferences.loadFrequencies(getApplicationContext());

        numberOfFrequencies = allFrequencies.size();

        hardResetValues();

        initPlaySoundButton();
        initStopSoundButton();
        initCancelButton();
        initResultButton();

        showStartMode();

        loudnessData = new LoudnessData();
        dataTable = loudnessData.make();

        preferences = new Preferences();

        Log.e("test", "AudioTestActivity: onCreate() // values: allFrequencies.toString() = " + allFrequencies.toString() + "\n numberOfFrequencies = " + numberOfFrequencies);
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

        vibe.vibrate(50);
        int id = item.getItemId();

        Drawer drawer = new Drawer();
        Intent intent = drawer.action(id, getApplicationContext(), volumeController);
        startActivity(intent);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}