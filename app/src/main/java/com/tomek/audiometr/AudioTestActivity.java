package com.tomek.audiometr;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class AudioTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private double frequency = 0.0;
    private double amplitude = 0.0;
    private int duration = 1;
    private String channel = "Both";
    private int numberOfFrequencies = 9; // equivalent to numberOfFrequencies*2 attempts
    private double step = 0.01;
    private double amplitudeLimit = 0.1;
    private double frequencyLimitMin = 0;
    private double frequencyLimitMax = 18000;

    private int index;
    private int newFrequency;
    private boolean stop = false;
    private boolean endOfTest = false;

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

    private void playAsync() {
        Log.e("test", "AudioTestActivity: playAsync() --before");

        if (play != null) {
            return;
        }

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // play the loop until the thread is interrupted or condition is met
                while (!Thread.currentThread().isInterrupted()) {
                    play = new Play(frequency, amplitude, duration, channel);
                    play.playSound();
                    play = null;

                    amplitude += step;

                    Log.e("test", "AudioTestActivity: playButtonAction() --while loop // values: amplitude = " + amplitude);

                    if (amplitude >= amplitudeLimit) {
                        addPoint(); //add result to a chart

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

    public void initPlaySoundButton() {
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

    public void playButtonAction() {
        Log.e("test", "AudioTestActivity: playButtonAction() --before");

        vibe.vibrate(50);
        stop = false;
        endOfTest = false;
        showAudioTestMode();
        onPause();
        resetValues();
        getNewSample();
        playAsync();

        Log.e("test", "AudioTestActivity: playButtonAction() --after");
    }

    public void initStopSoundButton() {
        Log.e("test", "AudioTestActivity: initStopSoundButton() --before");

        buttonHeard = findViewById(R.id.btn_heard);
        buttonHeard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = true;
                stopButtonAction();
            }
        });

        Log.e("test", "AudioTestActivity: initStopSoundButton() --after");
    }

    public void stopButtonAction() {
        Log.e("test", "AudioTestActivity: stopButtonAction() --before");

        vibe.vibrate(50);
        stop = true;
        addPoint();
        resetValues();
        getNewSample();
        playAsync();

        Log.e("test", "AudioTestActivity: stopButtonAction() --after");
    }

    public void initCancelButton() {
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

    public void cancelButtonAction() {
        Log.e("test", "AudioTestActivity: cancelButtonAction() --before");

        vibe.vibrate(50);
        stop = true;
        showStartMode();
        onPause();
        hardResetValues();

        Log.e("test", "AudioTestActivity: initCancelButton() --after");
    }

    public void initResultButton() {
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

    public void resultButtonAction() {
        Log.e("test", "AudioTestActivity: resultButtonAction() --before");

        vibe.vibrate(50);
        endOfTest = true;
        showResultMode();
        showResult();

        Log.e("test", "AudioTestActivity: resultButtonAction() --after");
    }

    public void initHelpButton() {
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

    public void helpButtonAction() {
        Log.e("test", "AudioTestActivity: helpButtonAction() --before");

        vibe.vibrate(50);
        Intent intentInfo = new Intent(this, PopUpAudioTest.class);
        startActivity(intentInfo);

        Log.e("test", "AudioTestActivity: helpButtonAction() --after");
    }

    public void resetValues() {
        Log.e("test", "AudioTestActivity: resetValues() --before");

        amplitude = 0.0;
        sample = new Sample(numberOfFrequencies, chosenFrequencies);

        Log.e("test", "AudioTestActivity: resetValues() --after");
    }

    public void hardResetValues() {
        Log.e("test", "AudioTestActivity: hardResetValues() --before");

        resetValues();
        chosenFrequencies = new ArrayList<>();
        samplesList = new ArrayList<>();
        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();

        Log.e("test", "AudioTestActivity: hardResetValues() --after");
    }

    public ArrayList<Integer> randomFrequencies() {
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

    public void addPoint() {
        Log.e("test", "AudioTestActivity: addPoint() --before");

        xAxis.add(frequency);
        yAxis.add(amplitude);
        channels.add(channel);

        Log.e("test", "AudioTestActivity: addPoint() --after // values: added frequency = " + frequency + "; added amplitude = " + amplitude + "; added channel = " + channel + "\t"
                + "xAxis.size() = " + xAxis.size() + "; yAxis.size() = " + yAxis.size() + "; channels.size() = " + channels.size());

    }

    public void showResult() {
        Log.e("test", "AudioTestActivity: showResult() --before");

        Intent intentResult = new Intent(AudioTestActivity.this, ResultActivity.class);
        intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intentResult.putExtra("xAxis", xAxis);
        intentResult.putExtra("yAxis", yAxis);
        intentResult.putExtra("channels", channels);
        intentResult.putExtra("amplitudeLimit", amplitudeLimit);
        intentResult.putExtra("frequencyLimitMin", frequencyLimitMin);
        intentResult.putExtra("frequencyLimitMax", frequencyLimitMax);

        startActivity(intentResult);

        Log.e("test", "AudioTestActivity: showResult() --after // values: xAxis.toString() = " + xAxis.toString() + "\t"
                + "yAxis.toString() = " + yAxis.toString() + "\t" + "channels.toString() = " + channels.toString());

    }

    public void showStartMode() {
        buttonStart.setVisibility(View.VISIBLE);
        textViewStart.setVisibility(View.VISIBLE);
        buttonHeard.setVisibility(View.GONE);
        textViewHeard.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        textViewCancel.setVisibility(View.GONE);
        buttonResult.setVisibility(View.GONE);
        textViewResult.setVisibility(View.GONE);
        textViewAudioTest.setText(R.string.tv_audioTest_1);
    }

    public void showAudioTestMode() {
        buttonStart.setVisibility(View.GONE);
        textViewStart.setVisibility(View.GONE);
        buttonHeard.setVisibility(View.VISIBLE);
        textViewHeard.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.VISIBLE);
        textViewCancel.setVisibility(View.VISIBLE);
        buttonResult.setVisibility(View.GONE);
        textViewResult.setVisibility(View.GONE);
        textViewAudioTest.setText(R.string.tv_audioTest_2);
    }

    public void showResultMode() {
        buttonStart.setVisibility(View.GONE);
        textViewStart.setVisibility(View.GONE);
        buttonHeard.setVisibility(View.GONE);
        textViewHeard.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.VISIBLE);
        textViewCancel.setVisibility(View.VISIBLE);
        buttonResult.setVisibility(View.VISIBLE);
        textViewResult.setVisibility(View.VISIBLE);
        textViewAudioTest.setText(R.string.tv_audioTest_3);
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

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //wibracje

        textViewStart = findViewById(R.id.tv_start);
        textViewHeard = findViewById(R.id.tv_heard);
        textViewCancel = findViewById(R.id.tv_back);
        textViewResult = findViewById(R.id.tv_result);
        textViewAudioTest = findViewById(R.id.textViewAudioTest);

        allFrequencies = new ArrayList<>();
//        allFrequencies.addAll(Arrays.asList(2000, 2500 // tylko do testowania, usunąć, aktywować poniższe
//        allFrequencies.addAll(Arrays.asList(700, 800, 900, 1000, 1500, 2000, 2500, 2700, 3000, 3200, 3500, 3800, 4000, 6000, 7000, 7300 // only for debugging
//        ));
//        allFrequencies.addAll(Arrays.asList(100, 125, 150, 250, 400, 500, 700, 1000, 1500, 2500, 3000, 4000, 6000, 8000, 10000, 12000, 14000, 15000
//        ));
        allFrequencies.addAll(Arrays.asList(125,250, 500, 1000, 2000, 3000, 4000, 6000, 8000));

        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();

        hardResetValues();

        initPlaySoundButton();
        initStopSoundButton();
        initCancelButton();
        initResultButton();
        initHelpButton();

        showStartMode();
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
            Intent intentTest = new Intent(this, AudioTestActivity.class);
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