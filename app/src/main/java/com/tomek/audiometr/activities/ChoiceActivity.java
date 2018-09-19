package com.tomek.audiometr.activities;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tomek.audiometr.algorithms.FrequenciesData;
import com.tomek.audiometr.dialogs.Dialog;
import com.tomek.audiometr.helpers.Drawer;
import com.tomek.audiometr.helpers.Preferences;
import com.tomek.audiometr.helpers.VolumeController;
import com.tomek.audiometr.popups.PopUpAppInfo;
import com.tomek.audiometr.popups.PopUpChoice;
import com.tomek.audiometr.R;

import java.util.ArrayList;
import java.util.Arrays;


public class ChoiceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton buttonGo;
    private ImageButton buttonHelp;

    private RadioGroup radioGroup;
    private RadioButton radioButtonBasic;
    private RadioButton radioButtonExtended;

    private VolumeController volumeController;

    private Vibrator vibe;

    private ArrayList<Integer> allFrequencies;

    private FrequenciesData frequenciesData;

    private Preferences preferences;

    private Intent intent;


    public void checkButton(View v) {
        if (radioButtonBasic.isChecked()) {
            allFrequencies = frequenciesData.basicAudioTest();
            Log.e("test", "Basic Audio Test has been chosen");
        } else if (radioButtonExtended.isChecked()) {
            allFrequencies = frequenciesData.extendedAudioTest();
            Log.e("test", "Extended Audio Test has been chosen");
        }
    }

    private void initGoButton() {
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

    private void goButtonAction() {
        Log.e("test", "ChoiceActivity: goButtonAction() --before");

        vibe.vibrate(50);

        boolean calibrated = preferences.loadCalibrated(getApplicationContext());

        Log.e("boolean calibrated", String.valueOf(calibrated));

        preferences.saveFrequencies("allFrequencies", allFrequencies, getApplicationContext());

        if (calibrated) {
            intent = new Intent(this, AudioTestActivity.class);
        } else {
            intent = new Intent(this, Dialog.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


        Log.e("test", "ChoiceActivity: goButtonAction() --after");
    }

    private void initHelpButton() {
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

    private void helpButtonAction() {
        Log.e("test", "ChoiceActivity: helpButtonAction() --before");

        vibe.vibrate(50);
        intent = new Intent(this, PopUpChoice.class);
        startActivity(intent);

        Log.e("test", "ChoiceActivity: helpButtonAction() --after");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
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

        volumeController = new VolumeController((AudioManager) this.getSystemService(Context.AUDIO_SERVICE));

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        radioGroup = findViewById(R.id.radioGroup);
        radioButtonBasic = findViewById(R.id.rb_basicTest);
        radioButtonExtended = findViewById(R.id.rb_extendedTest);

        frequenciesData = new FrequenciesData();
        preferences = new Preferences();

        initGoButton();
        initHelpButton();
        allFrequencies = frequenciesData.basicAudioTest();
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

        Drawer drawer = new Drawer();
        Intent intent = drawer.action(id, getApplicationContext(), volumeController);
        startActivity(intent);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}