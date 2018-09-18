package com.tomek.audiometr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

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
    private double defMaxDecibels = 0.0;
    private double maxDecibels = 40.0;
    private double decibelsInTable = 0.0;
    private int indexOfMaxDecibels = 0;
    private int level = 0;
    private int index = 0;
    private int newFrequency;
    private boolean stop = false;
    private boolean endOfTest = false;

    private AudioManager audioManager;

    private ImageButton buttonStart;
    private ImageButton buttonHeard;
    private ImageButton buttonCancel;
    private ImageButton buttonResult;
    private ImageButton buttonHelp;

    private TextView textViewStart;
    private TextView textViewHeard;
    private TextView textViewCancel;
    private TextView textViewResult;
    private TextView textViewAudioTest;

    private TableLayout tableLayout;

    private Vibrator vibe;
    private Thread playThread;
    private Play play;

    private Random randomGenerator;
    private Sample sample;

    private ArrayList<Integer> allFrequencies;
    private ArrayList<Integer> chosenFrequencies;
    private ArrayList<Sample> samplesList;
    private ArrayList<String> newSample;

    private ArrayList<Double> xAxis;
    private ArrayList<Double> yAxis;
    private ArrayList<String> channels;
    
    private Double[][] table;

    private Double[][] writeTable() {

        table = new Double[31][2];

        double value = -150.0;

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (j == 0) {
                    table[i][j] = value;
                    value += 5;
                } else if (j == 1) {
                    table[i][j] = Math.pow(10, (table[i][0]) / 20);
                }
            }
        }
        return table;
    }

    private double loadDecibels() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String scoreDecibels = sharedPreferences.getString("maxDecibels", String.valueOf(defMaxDecibels));
        maxDecibels = Double.parseDouble(scoreDecibels);
        maxDecibels = maxDecibels*(-1);

        return maxDecibels;
    }

    private void findClosestInTable(){

        double myDecibels = maxDecibels;
        double distance = Math.abs(table[0][0] - maxDecibels);
        int idx = 0;
        for (int c = 1; c < table.length; c++) {
            double cdistance = Math.abs(table[c][0] - myDecibels);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        decibelsInTable = table[idx][0];
        indexOfMaxDecibels = idx;

        Log.e("test", "Found closest decibels in table = " + decibelsInTable + "IndexOfMaxDecibels = " + indexOfMaxDecibels);
    }

    private ArrayList<Integer> loadFrequencies(){

        SharedPreferences sharedPreferences = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String scoreFrequencies = sharedPreferences.getString("allFrequencies", "");
        scoreFrequencies = scoreFrequencies.replaceAll("[\\[\\](){} ]","");
        ArrayList<String> frequenciesListString = new ArrayList<>(Arrays.asList(scoreFrequencies.split(",")));
        allFrequencies = new ArrayList<>();
        for(int i = 0; i < frequenciesListString.size(); i++) {
            allFrequencies.add(Integer.parseInt(frequenciesListString.get(i)));
        }
        return allFrequencies;
    }

    private ArrayList<Integer> randomFrequencies() {
        Log.e("test", "AudioTestActivity: randomFrequencies() --before");

        index = 0;
        newFrequency = 0;
        randomGenerator = new Random();
        while (chosenFrequencies.size() < numberOfFrequencies) {
            index = randomGenerator.nextInt(allFrequencies.size());
            newFrequency = allFrequencies.get(index);
            if (!chosenFrequencies.contains(newFrequency)) {
                chosenFrequencies.add(newFrequency);
            }
        }
        frequencyLimitMin = Collections.min(chosenFrequencies);
        frequencyLimitMax = Collections.max(chosenFrequencies);

        Log.e("test", "AudioTestActivity: randomFrequencies() --after // values: chosenFrequencies = " + chosenFrequencies.toString() + "\t"
                + "frequencyLimitMin = " + frequencyLimitMin + "; frequencyLimitMax = " + frequencyLimitMax);

        return chosenFrequencies;
    }

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

            resultButtonAction();
        }
    }

    private double getAmplitude(int level){

        index = indexOfMaxDecibels + level;
        amplitude = table[index][1];
        decibels = table[index][0]-decibelsInTable;

        Log.e("test", "Frequency = " + frequency + "\t amplitude = " + amplitude + "\t decibels = " + decibels + "dB");

        return amplitude;
    }

    private void addPoint() {
        Log.e("test", "AudioTestActivity: addPoint() --before");

        xAxis.add(frequency);
        yAxis.add(decibels);
        channels.add(channel);

        Log.e("test", "AudioTestActivity: addPoint() --after // values: added frequency = " + frequency + "; added decibels = " + decibels + "; added channel = " + channel + "\t"
                + "xAxis.size() = " + xAxis.size() + "; yAxis.size() = " + yAxis.size() + "; channels.size() = " + channels.size());

    }

    private void showResult() {
        Log.e("test", "AudioTestActivity: showResult() --before");

        setMinVolume();

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


    private void setMaxVolume() {

        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);

    }

    private void setMinVolume() {

        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                (int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)*0.3),
                0);

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

        amplitude = 0.0;
        level = 0;
        index = 0;

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // play the loop until the thread is interrupted or condition is met
                while (!Thread.currentThread().isInterrupted()) {
                    amplitude = getAmplitude(level);

                    play = new Play(frequency, amplitude, duration, channel);
                    play.playSound();
                    play = null;

                    level++;

                    Log.e("test", "AudioTestActivity: playButtonAction() --while loop // values: amplitude = " + amplitude +"\t level = " + level);

                    if (index > (table.length-1) || level > 9 ) {
                        addPoint();

                        if (endOfTest) {
                            hardResetValues();
                            Log.e("test", "AudioTestActivity: playButtonAction() --while loop // msg: hardResetValue() called");
                        } else {
                            resetValues();
                            getNewSample();
                            playAsync();

                            Log.e("test", "AudioTestActivity: playButtonAction() --while loop // msg: got new sample, new Thread");
                        }
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
                randomFrequencies();
                playButtonAction();
            }
        });

        Log.e("test", "AudioTestActivity: initPlaySoundButton() --after");
    }

    private void playButtonAction() {
        Log.e("test", "AudioTestActivity: playButtonAction() --before");

        vibe.vibrate(50);
        stop = false;
        endOfTest = false;
        showAudioTestMode();
        onPause();
        setMaxVolume();
        resetValues();
        loadDecibels();
        findClosestInTable();
        getNewSample();
        playAsync();

        Log.e("test", "AudioTestActivity: playButtonAction() --after");
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
        addPoint();
        resetValues();
        getNewSample();
        setMaxVolume();
        playAsync();

        Log.e("test", "AudioTestActivity: stopButtonAction() --after");
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
        showStartMode();
        onPause();
        hardResetValues();

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
        endOfTest = true;
        showResultMode();
        showResult();

        Log.e("test", "AudioTestActivity: resultButtonAction() --after");
    }

    private void initHelpButton() {
        Log.e("test", "AudioTestActivity: initHelpButton() --before");

        buttonHelp = findViewById(R.id.btn_help);
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpButtonAction();
            }
        });

        Log.e("test", "AudioTestActivity: initHelpButton() --after");
    }

    private void helpButtonAction() {
        Log.e("test", "AudioTestActivity: helpButtonAction() --before");

        vibe.vibrate(50);
        Intent intentInfo = new Intent(this, PopUpAudioTest.class);
        startActivity(intentInfo);

        Log.e("test", "AudioTestActivity: helpButtonAction() --after");
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
        try {
            playThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setMinVolume();

        play = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        textViewStart = findViewById(R.id.tv_start);
        textViewHeard = findViewById(R.id.tv_heard);
        textViewCancel = findViewById(R.id.tv_back);
        textViewResult = findViewById(R.id.tv_result);
        textViewAudioTest = findViewById(R.id.textViewAudioTest);
        tableLayout = findViewById(R.id.tl_audioTest);

        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();

        loadFrequencies();

        numberOfFrequencies = allFrequencies.size();

        hardResetValues();

        initPlaySoundButton();
        initStopSoundButton();
        initCancelButton();
        initResultButton();
        initHelpButton();

        showStartMode();

        writeTable();

        Log.e("test", "AudioTestActivity: onCreate() // values: allFrequencies.toString() = " + allFrequencies.toString() + "\n numberOfFrequencies = " + numberOfFrequencies);
    }

    @Override
    public void onBackPressed() {
        setMinVolume();
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
            setMinVolume();
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