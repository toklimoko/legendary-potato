package com.tomek.audiometr.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;

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
        Intent intentBad = new Intent(MainActivity.this, ChoiceActivity.class);
        intentBad.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentBad);
        vibe.vibrate(50);
    }

    private void savePreference(String key, String value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        volumeController = new VolumeController(audioManager);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            volumeController.setMin();
            finish();
        }

        initCalibrationButton();
        initChoiceButton();

        savePreference("calibrated", "false");
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

        if (id == R.id.nav_start) {

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
            volumeController.setMin();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}
