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
import com.tomek.audiometr.algorithms.StopWatch;
import com.tomek.audiometr.helpers.Drawer;
import com.tomek.audiometr.helpers.Preferences;
import com.tomek.audiometr.helpers.VolumeController;
import com.tomek.audiometr.algorithms.Play;
import com.tomek.audiometr.popups.PopUpAudioTest;
import com.tomek.audiometr.R;
import com.tomek.audiometr.algorithms.Sample;


import java.util.ArrayList;
import java.util.Objects;

public class AudioTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private double frequency = 0.0;
    //private double amplitude = 0.0;
    private double decibels = 0.0;
    private final int duration = 1500; //miliseconds
    private String channel = "Both";
    private int numberOfFrequencies = 0; // equivalent to numberOfFrequencies*2 attempts
    private double frequencyLimitMin = 0;
    private double frequencyLimitMax = 18000;
    private final double MAXDECIBELS = -100.0;
    private double maxLevel = 18;
    private double decibelsInTable = 0.0;
    private double indexOfMaxDecibels = 0;
    private int level = 0;
    private int index = 0;
    private double time = 0.0;
    private int numberOfSample = 0;
    private int samplesListSize = 0;
    private boolean stop = false;//ta jest do zbadania
    // private boolean saving = false;
    private boolean stopAlgorithm = false;
    private String string = "";

    private ImageButton buttonStart;
    private ImageButton buttonHeard;
    private ImageButton buttonCancel;
    private ImageButton buttonResult;

    private TextView textViewStart;
    private TextView textViewHeard;
    private TextView textViewCancel;
    private TextView textViewResult;
    private TextView textViewAudioTest;
    private TextView textSampleNumber;
    private TextView textProgress;

    private TableLayout tableLayout;
    private DrawerLayout drawer;

    private Intent intent;

    private Vibrator vibe;
   // private Thread playThread;
    private Thread algorithmThread;
    // private Thread savingThread;
    // private Thread timeThread;
    private StopWatch timer;
    private Play play;

    private Sample sample;
    private VolumeController volumeController;

    private ArrayList<Integer> allFrequencies;
    private ArrayList<Integer> chosenFrequencies;
    private ArrayList<Sample> samplesList;

    private ArrayList<Double> maxDecibelsData;

    private ArrayList<Double> xAxis;
    private ArrayList<Double> yAxis;
    private ArrayList<String> channels;
    private ArrayList<Double> times;

    private ArrayList<Double> xAxisFinal;
    private ArrayList<Double> yAxisFinal;
    private ArrayList<String> channelsFinal;
    private ArrayList<Double> timesFinal;


    private Double[][] dataTable;
    private LoudnessData loudnessData;
    private Preferences preferences;
    private FrequenciesData frequenciesData;

    private double getAmplitude(int level) {

        index = (int) indexOfMaxDecibels + level;
        double amplitude = dataTable[index][1];
        decibels = dataTable[index][0] - decibelsInTable;

        Log.e("test", "Channel = " + channel + "\t frequency = " + frequency + "\t amplitude = " + amplitude + "\t decibels = " + decibels + "dB");

        return amplitude;
    }

    private void addPoint() {
        Log.e("test", "AudioTestActivity: add() --before");


        xAxis.add(frequency);
        yAxis.add(decibels);
        channels.add(channel);
        times.add(time);

        Log.e("test", "stop time = " + time);

        Log.e("test", "AudioTestActivity: add() --after // values: added frequency = " + frequency + "; added decibels = " + decibels + "; added channel = " + channel + "\t"
                + "xAxis.size() = " + xAxis.size() + "; yAxis.size() = " + yAxis.size() + "; channels.size() = " + channels.size() + "; times.size() = " + times.size());

    }

    private void showResult() {
        Log.e("test", "AudioTestActivity: showResult() --before");

        volumeController.setMin();

        frequencyLimitMin = frequenciesData.xLimits(chosenFrequencies).get(0);
        frequencyLimitMax = frequenciesData.xLimits(chosenFrequencies).get(1);

        Intent intentResult = new Intent(AudioTestActivity.this, ResultActivity.class);
        intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intentResult.putExtra("xAxis", xAxisFinal);
        intentResult.putExtra("yAxis", yAxisFinal);
        intentResult.putExtra("channels", channelsFinal);
        intentResult.putExtra("times", timesFinal);
        intentResult.putExtra("decibelsLimit", decibelsInTable);
        intentResult.putExtra("frequencyLimitMin", frequencyLimitMin);
        intentResult.putExtra("frequencyLimitMax", frequencyLimitMax);

        startActivity(intentResult);

        Log.e("test", "AudioTestActivity: showResult() --after // values: xAxis.toString() = " + xAxis.toString() + "\n"
                + "yAxis.toString() = " + yAxis.toString() + "\n" + "channels.toString() = " + channels.toString() + "\n" + "times.toString = " + times.toString());
    }

    private void deleteSilence() {

        xAxisFinal = new ArrayList<>();
        yAxisFinal = new ArrayList<>();
        channelsFinal = new ArrayList<>();
        timesFinal = new ArrayList<>();

        for (int i = 0; i < xAxis.size(); i++) {
            Log.e("test", "xAxis.size = " + xAxis.size());

            double tempX = xAxis.get(i);
            double tempY = yAxis.get(i);
            String tempC = channels.get(i);
            double tempT = times.get(i);

            if (tempY != 0.0) {
                xAxisFinal.add(tempX);
                yAxisFinal.add(tempY);
                channelsFinal.add(tempC);
                timesFinal.add(tempT);
            }

        }
    }

    private void resetValues() {
        Log.e("test", "AudioTestActivity: resetValues() --before");

        //amplitude = 0.0;
        level = 0;
        index = 0;


        Log.e("test", "AudioTestActivity: resetValues() --after");
    }

    private void resetLists() {
        Log.e("test", "AudioTestActivity: resetLists() --before");

        resetValues();
        chosenFrequencies = new ArrayList<>();
        samplesList = new ArrayList<>();
        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();
        times = new ArrayList<>();

        Log.e("test", "AudioTestActivity: resetLists() --after");
    }


//    private void timeWatch(){
//
//
//        timeThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                timer = new StopWatch();
//
//            }
//        });
//        timeThread.start();
//    }


   /* private void playAsync() {
        Log.e("test", "AudioTestActivity: playAsync() --before");

        if (play != null) {
            return;
        }

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {

                play = new Play(frequency, amplitude, duration, channel);
                timer.begin();
                play.playSound();
                play = null;
            }
        });
        playThread.start();

        Log.e("test", "AudioTestActivity: playAsync() --after");
    }*/


    private void playAlgorithm() {

        if (algorithmThread != null) {
            return;
        }
        algorithmThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //stop = false;
                for (int i = 0; i < samplesListSize; i++) {

                    numberOfSample = i + 1;
                    setTextSampleNumber(numberOfSample, samplesListSize);

                    frequency = sample.getFrequenciesList().get(i);
                    channel = sample.getChannelsList().get(i);

                    resetValues();

                    stop = false;
                    while (!stop) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }

                        double amplitude = getAmplitude(level);
                        Log.e("test", "AudioTestActivity: playButtonAction() --while loop // values: amplitude = " + amplitude + "\t level = " + level);

                        play = new Play(frequency, amplitude, duration, channel);
                        timer.begin();
                        play.playSound();
                        play = null;

                        if (index > (dataTable.length - 1) || level > maxLevel || amplitude == 1) {
                            Log.e("test", "index = " + index + " dataTable.length-1 = " + (dataTable.length - 1) + " level = " + level + " maxLevel = " + maxLevel + " amplitude = " + amplitude);
                            stop = true;
                            Log.e("test", "AudioTestActivity: playButtonAction() --while loop // msg: got new sample, new Thread");
                            break;
                        }

                        level++;
                    }

                    saveMeasuredSample();

                    if (stopAlgorithm) {
                        break;
                    }
                }

                if (!stopAlgorithm) {
                    resultButtonAction();
                }
            }
        });
        algorithmThread.start();
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
        if (algorithmThread != null) {
            return;
        }
        vibe.vibrate(50);
        showAudioTestMode();
        volumeController.setMax();
        stopAlgorithm = false;
        stop = false;
        timer = new StopWatch();
        playAlgorithm();
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

        stop = true;

        if (play != null) {
            play.release();
            play = null;
        }

        //if(play!=null){

        //}
        //stopAndSave();
        Log.e("test", "AudioTestActivity: stopButtonAction() --after");
    }

    private void saveMeasuredSample() {
        time = timer.getElapsedTimeSecs();
        // saving = true;

        vibe.vibrate(50);

        if (play != null) {
            play.release();
            play = null;
        }

//        if (playThread != null) {
//            playThread.interrupt();
//        }


        addPoint();
        volumeController.setMax();
    }

   /* private void stopAndSave() {

        if (saving) {
            return;
        }
        savingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                time = timer.getElapsedTimeSecs();
                saving = true;

                vibe.vibrate(50);

                if (play != null) {
                    play.release();
                    play = null;
                }
                if (playThread != null) {
                    playThread.interrupt();
                }

                if(stop) {
                    addPoint();
                    volumeController.setMax();
                    stop = false;
                }

                saving = false;
            }
        });
        savingThread.start();
    }*/

    private void initAlgorithm() {

        resetLists();

        chosenFrequencies = frequenciesData.random(numberOfFrequencies, allFrequencies);
        sample = new Sample(numberOfFrequencies, chosenFrequencies);
        //        maxDecibels = preferences.loadDecibels(getApplicationContext());
        maxDecibelsData = loudnessData.find(MAXDECIBELS);
        decibelsInTable = maxDecibelsData.get(0);
        indexOfMaxDecibels = maxDecibelsData.get(1);

        for (int i = 0; i < chosenFrequencies.size() * 2; i++) {
            sample.getNewSample();
        }
        samplesList = sample.getSamplesList();
        samplesListSize = samplesList.size();


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

        textSampleNumber.setVisibility(View.INVISIBLE);
        vibe.vibrate(50);
        stop = true;
        volumeController.setMin();
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
        stopAlgorithm = true;
        runOnUiThread(new Runnable() { //fixes e "Only the original thread that created a view hierarchy can touch its views."
            @Override
            public void run() {
                showResultMode();
            }
        });
        deleteSilence();
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
        stop = true;
        stopAlgorithm = true;

        try {
            algorithmThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }

        algorithmThread = null;
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
        textSampleNumber = findViewById(R.id.txtSampleNumber);
        tableLayout = findViewById(R.id.tl_audioTest);
        textProgress = findViewById(R.id.textProgress);

        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();
        times = new ArrayList<>();

        preferences = new Preferences();

        frequenciesData = new FrequenciesData();

        allFrequencies = preferences.loadFrequencies(getApplicationContext());

        numberOfFrequencies = allFrequencies.size();

        resetLists();

        initPlaySoundButton();
        initStopSoundButton();
        initCancelButton();
        initResultButton();

        showStartMode();

        loudnessData = new LoudnessData();
        dataTable = loudnessData.make();

        preferences = new Preferences();

        initAlgorithm();

        Log.e("test", "AudioTestActivity: onCreate() // values: allFrequencies.toString() = " + allFrequencies.toString() + "\n numberOfFrequencies = " + numberOfFrequencies);
    }

    private void setTextSampleNumber(int numberOfSample, int samplesListSize) {
        string = numberOfSample + " / " + samplesListSize;
        textSampleNumber.setText(string);
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
        textSampleNumber.setVisibility(View.INVISIBLE);
//        if(textSampleNumber != null) {
//            textSampleNumber.clearComposingText();
//        }
        textProgress.setVisibility(View.INVISIBLE);
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
        textSampleNumber.setVisibility(View.VISIBLE);
        textProgress.setVisibility(View.VISIBLE);

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
        tableLayout.setVisibility(View.GONE);
        textViewAudioTest.setText(R.string.tv_audioTest_3);
        textSampleNumber.setVisibility(View.INVISIBLE);
        textProgress.setVisibility(View.INVISIBLE);
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