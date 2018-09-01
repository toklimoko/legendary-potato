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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.achartengine.GraphicalView;
import org.achartengine.model.XYSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AudioTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener { //podwójna implementacja

    private double frequency = 0.0;
    private double amplitude = 0.05;
    private int duration = 1;
    private String channel = "Both";
    private int numberOfFrequencies = 5;

    private Toast toast1;
    private Toast toast2;

    private Button buttonStart;
    private Button buttonSlysze;
    private Button buttonCancel;

    private Vibrator vibe;
    private Thread playThread;
    private Play play;
    private boolean stop = false;
    private boolean koniecBadania = false;

    private Random randomGenerator;
    private Sample sample;

    private ArrayList<Integer> allFrequencies;
    private ArrayList<Integer> chosenFrequencies;
    private ArrayList<Sample> samplesList;
    private ArrayList<String> newSample;

    private ArrayList<Double> xAxis; //lista wartości X do wykresu (częstotliwości dla każdej z prób)
    private ArrayList<Double> yAxis; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)
    private ArrayList<String> channels; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)

    private void playAsync() {

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

//                    amplitude += 0.005;
                    amplitude += 0.05;

                    Log.d("Amplitude = ", "Amplitude = " + amplitude);
                    if (stop || amplitude >= 0.5) {
                        addPoint(); //add result to a chart
                        Log.e("test", "playAsync - if - przed stopButtonAction");

                        if (koniecBadania) {
                            hardResetValues();
                        } else {
                            stopButtonAction();
                            resetValues();
                        }
                        break;
                    }
                }
            }
        });
        playThread.start();
    }

    public void playButtonAction() {
        Log.e("test", "playButtonAction - przed w metodzie");
        stop = false;
        koniecBadania = false;
        onPause();
        resetValues();
        getNewSample();
        playAsync();
        Log.e("test", "playButtonAction - po ");

    }

    public void initPlaySoundButton() {
        buttonStart = findViewById(R.id.btn_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("test", "initPlaySoundButton - przed");
                randomFrequencies();
                playButtonAction();
                Log.e("test", "initPlaySoundButton - po");
            }
        });
    }


    public void initStopSoundButton() {
        buttonSlysze = findViewById(R.id.btn_slysze);
        buttonSlysze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("test", "initStopSoundButton - przed");
                stop = true;
                stopButtonAction();
                Log.e("test", "initStopSoundButton - przed");
            }
        });
    }

    public void stopButtonAction() {
        stop = true;
        resetValues();
        Log.e("test", "stopButtonAction - przed");
        getNewSample();
        playAsync();
        Log.e("test", "stopButtonAction - po");
    }

    public void initCancelButton() {
        buttonCancel = findViewById(R.id.btn_koniec);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("test", "initCancelButton - przed");
                stop = true;
                cancelButtonAction();
                Log.e("test", "initCancelButton - przed");
            }
        });
    }

    public void cancelButtonAction() {
        onPause();
        Log.e("test", "onPause z cancelButtonAction - po");
        hardResetValues();
    }

    public void hardResetValues() {
        resetValues();
        chosenFrequencies = new ArrayList<>();
//        ////
//        chosenFrequencies = allFrequencies;//// tylko do testowania, usunąć przy losowaniu częstotliwości
//        ////
        samplesList = new ArrayList<>();

    }

    public void resetValues() {
        amplitude = 0.0;
        sample = new Sample(numberOfFrequencies, chosenFrequencies);
    }

    public ArrayList<Integer> randomFrequencies() {

        Log.e("test", "randomFrenquencies - przed");
        int index = 0;
        int newFrequency = 0;
        randomGenerator = new Random();
        while (chosenFrequencies.size() < numberOfFrequencies) {
            index = randomGenerator.nextInt(allFrequencies.size());
            newFrequency = allFrequencies.get(index);
            if (!chosenFrequencies.contains(newFrequency)) {
                chosenFrequencies.add(newFrequency);
            }
        }
        Log.e("test", "randomFrenquencies - po");
        return chosenFrequencies;
    }

    private void getNewSample() {
        Log.e("test", "newSample pobrany - przed newSample");
        if (newSample != null) {
            newSample.clear();
        }
        sample.setSamplesList(samplesList);
        newSample = sample.getNewSample();
        if (newSample != null) {
            frequency = Double.parseDouble(newSample.get(0));
            channel = newSample.get(1);
            Log.e("test", "samplesList size w przed getSamplesList() = " + samplesList.size());
            samplesList = sample.getSamplesList();
            Log.e("test", "samplesList size po getSamplesList() = " + samplesList.size());
            stop = false;
            Log.e("test", "newSample pobrany - po; frequency = " + frequency + " channel = " + channel);
        } else {
            //koniec badania
            koniecBadania = true;
//            toast2.show();
            showResult();


        }
    }

    public void addPoint() {
        // DODAJ PUNKT DO WYKRESU
        Log.e("test", "addPoint");

        Log.e("test", "freq = " + frequency);
        Log.e("test", "amp = " + amplitude);
        Log.e("test", "chan = " + channel);

        xAxis.add(frequency);
        yAxis.add(amplitude);
        channels.add(channel);

        Log.e("test", "AP XF = " + xAxis.size());
        Log.e("test", "AP XA = " + yAxis.size());
        Log.e("test", "AP C = " + channels.size());
    }

    public void showResult() {
//        chart = new Chart(xAxis, yAxis, channels);
//        chart.setxAxis(xAxis);
//        chart.setyAxis(yAxis);
//        chart.setChannels(channels);



        Log.e("test", "SR XF = " + xAxis.toString());
        Log.e("test", "SR XA = " + yAxis.toString());
        Log.e("test", "SR C = " + channels .toString());



        Intent intentResult = new Intent(AudioTestActivity.this, ResultActivity.class);
//        intentResult.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

//        Bundle bundle = new Bundle();


        intentResult.putExtra("xAxis", xAxis);
        intentResult.putExtra("yAxis", yAxis);
        intentResult.putExtra("channels", channels);


        startActivity(intentResult);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (play != null) {
            play.release();
        }
        if (stop) {
            toast1.show();
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
        //obsluga lewego paska
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        allFrequencies = new ArrayList<>();
//        allFrequencies.addAll(Arrays.asList(2000, 2500 // tylko do testowania, usunąć, aktywować poniższe
//        allFrequencies.addAll(Arrays.asList(700, 800, 900, 1000, 1500, 2000, 2500, 2700, 3000, 3200, 3500, 3800, 4000, 6000, 7000, 7300 // tylko do testowania, usunąć, aktywować poniższe
//        ));
        allFrequencies.addAll(Arrays.asList(100, 125, 150, 250, 400, 500, 700, 1000, 1500, 2500, 3000, 4000, 6000, 8000, 10000, 12000, 14000, 15000
        ));

        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();
        channels = new ArrayList<>();

        Context context = getApplicationContext();
        CharSequence text = "Stop wciśnięty";
        CharSequence text2 = "Koniec badania";

        int duration = Toast.LENGTH_SHORT;

        toast1 = Toast.makeText(context, text, duration);
        toast2 = Toast.makeText(context, text2, duration);

        hardResetValues();

        initPlaySoundButton();
        initStopSoundButton();
        initCancelButton();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings3) {

            //"jak wykonać badanie?"
            Intent intentInfo = new Intent(AudioTestActivity.this, PopUpAudioTest.class);
            startActivity(intentInfo);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_start) {
            Intent intentLauncher = new Intent(AudioTestActivity.this, MainActivity.class);
            intentLauncher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLauncher);

        } else if (id == R.id.nav_kalibruj) {
            Intent intentKal = new Intent(AudioTestActivity.this, CalibrationActivity.class);
            intentKal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentKal);

        } else if (id == R.id.nav_info) {
            Intent intentInfo = new Intent(AudioTestActivity.this, PopUpAppInfo.class);
            intentInfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentInfo);

        } else if (id == R.id.nav_powrot) {
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