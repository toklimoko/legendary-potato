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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;


public class ChoiceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton buttonGo;
    private ImageButton buttonHelp;

    private RadioGroup radioGroup;
    private RadioButton radioButtonBasic;
    private RadioButton radioButtonExtended;

    private Vibrator vibe;

    private ArrayList<Integer> allFrequencies;

    public void checkButton(View v) {
        if (radioButtonBasic.isChecked()) {
            basicAudioTest();
        } else if (radioButtonExtended.isChecked()) {
            extendedAudioTest();
        }
    }

    public void basicAudioTest(){
        allFrequencies = new ArrayList<>();
        allFrequencies.addAll(Arrays.asList(125, 250, 500, 1000, 2000, 3000, 4000, 6000, 8000));
        Log.e("test", "checkedBasic = " + radioButtonBasic.isChecked());

    }

    public void extendedAudioTest(){
        allFrequencies = new ArrayList<>();
        allFrequencies.addAll(Arrays.asList(100, 125, 150, 200, 300, 400, 500, 700, 1000, 2000, 2500, 3000, 4000, 6000, 8000, 10000, 12000, 14000, 15000, 16000, 17000, 18000));
        Log.e("test", "checkedExtended = " + radioButtonExtended.isChecked());
    }

    public void initGoButton() {
        Log.e("test", "ChoiceActivity: initGoButton() --before");

        buttonGo = findViewById(R.id.btn_go);
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goButtonAction();
            }
        });

        Log.e("test", "ChoiceActivity: initGoButton() --after");
    }

    public void goButtonAction() {
        Log.e("test", "ChoiceActivity: goButtonAction() --before");

        vibe.vibrate(50);
        Intent intentAudioTest = new Intent(this, AudioTestActivity.class);
        intentAudioTest.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentAudioTest.putExtra("allFrequencies", allFrequencies);
        startActivity(intentAudioTest);

        Log.e("test", "ChoiceActivity: goButtonAction() --after");
    }


    public void initHelpButton() {
        Log.e("test", "ChoiceActivity: initHelpButton() --before");

        buttonHelp = findViewById(R.id.btn_help);
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpButtonAction();
            }
        });

        Log.e("test", "ChoiceActivity: initHelpButton() --after");
    }

    public void helpButtonAction() {
        Log.e("test", "ChoiceActivity: helpButtonAction() --before");

        vibe.vibrate(50);
        Intent intentInfo = new Intent(this, PopUpAudioTest.class);
        startActivity(intentInfo);

        Log.e("test", "ChoiceActivity: helpButtonAction() --after");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
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
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonBasic = findViewById(R.id.rb_basicTest);
        radioButtonExtended = findViewById(R.id.rb_extendedTest);

        initGoButton();
        initHelpButton();
        basicAudioTest();
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