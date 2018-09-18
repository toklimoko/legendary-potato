package com.tomek.audiometr;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFocusRequest;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ImageButton btnCalibration;
    public ImageButton btnAudioTest;
    public Vibrator vibe;
    private AudioManager audioManager;

    public void initCalibrationButton() {
        btnCalibration = findViewById(R.id.btn_calibration);
        btnCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calibrationButtonAction();
            }
        });
    }

    public void calibrationButtonAction() {
        Intent intentCal = new Intent(MainActivity.this, CalibrationActivity.class);
        intentCal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentCal);
        vibe.vibrate(50);
    }

    public void initChoiceButton() {
        btnAudioTest = findViewById(R.id.btn_audioTest);
        btnAudioTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choiceButtonAction();
            }
        });
    }

    public void choiceButtonAction() {
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

    public void setMinVolume() {

        audioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                (int) (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 0.3),
                0);

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

        if (getIntent().getBooleanExtra("EXIT", false)) {
            setMinVolume();
            finish();
        }

        initCalibrationButton();
        initChoiceButton();

        savePreference("calibrated", "false");
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

    AudioManager.OnAudioFocusChangeListener asd = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            int i =0;
            i = 1000;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(asd, 1, AudioManager.AUDIOFOCUS_GAIN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.abandonAudioFocus(asd);
    }
}
