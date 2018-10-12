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

import com.tomek.audiometr.dialogs.Dialog;
import com.tomek.audiometr.helpers.Drawer;
import com.tomek.audiometr.helpers.Preferences;
import com.tomek.audiometr.helpers.VolumeController;
import com.tomek.audiometr.popups.PopUpAppInfo;
import com.tomek.audiometr.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton btnCalibration;
    private ImageButton btnAudioTest;
    private Vibrator vibe;
    private AudioManager audioManager;
    private VolumeController volumeController;

    private Preferences preferences;

    private void initCalibrationButton() {
        btnCalibration = findViewById(R.id.btn_calibration);
        btnCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calibrationButtonAction();
            }
        });
    }

    private void calibrationButtonAction() {
        Intent intentCal = new Intent(MainActivity.this, CalibrationActivity.class);
        intentCal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentCal);
        vibe.vibrate(50);
    }

    private void initChoiceButton() {
        btnAudioTest = findViewById(R.id.btn_audioTest);
        btnAudioTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceButtonAction();
            }
        });
    }

    private void choiceButtonAction() {

        Intent intent;

        // CALIBRATION CHECK - WORKING
//        boolean calibrated = preferences.loadCalibrated(getApplicationContext());
//
//        Log.e("boolean calibrated", String.valueOf(calibrated));
//
//
//        if (calibrated) {
//            intent = new Intent(this, ChoiceActivity.class);
//        } else {
//            intent = new Intent(this, Dialog.class);
//        }

        intent = new Intent(this, ChoiceActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        vibe.vibrate(50);
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            int i =0;
            i = 1000;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        audioManager.requestAudioFocus(onAudioFocusChangeListener, 1, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        volumeController = new VolumeController(audioManager);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            volumeController.setMin();
            finish();
        }

        initCalibrationButton();
        initChoiceButton();

        preferences = new Preferences();
        preferences.savePreference("calibrated", "false", getApplicationContext());
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
        int id = item.getItemId();

        Drawer drawer = new Drawer();
        Intent intent = drawer.action(id, getApplicationContext(),volumeController);
        startActivity(intent);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}
