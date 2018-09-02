package com.tomek.audiometr;

import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class CalibrationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ImageButton btnKal;
    private Toast toast;
    private Vibrator vibe;

    private Thread playThread;

    private Play play;

    private void playAsync(){
        if(play != null){
            return;
        }

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {
                play = new Play(2000, 0.01, 4, "Both"); //czestotliwosc = 2000 Hz - wartosc z zakresu najlepszej slyszalnosci ucha
                play.playSound();
                play = null;
            }
        });

        playThread.start();
    }


    public void initPlaySoundButton() {
        btnKal = findViewById(R.id.btn_kal);
        btnKal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAsync();
                toast.show();
                vibe.vibrate(50);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setLogo(R.drawable.logo); //dodawanie logo do toolbaru (pasek na gorze)
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mój kod START

        Context context = getApplicationContext();
        CharSequence text = "Powtórz w razie potrzeby";
        int duration = Toast.LENGTH_SHORT;

        toast = Toast.makeText(context, text, duration);

        setVolumeControlStream(AudioManager.STREAM_MUSIC); //pozwala na kontrolę głośności


        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //wibracje

        initPlaySoundButton();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(play != null) {
            play.release();
        }

        try {
            playThread.interrupt();
        }catch (Exception e){
            e.printStackTrace();
        }
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
        getMenuInflater().inflate(R.menu.menu_calibration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings2) {

            //"po co kalibracja?"
            Intent intentKalInfo = new Intent(CalibrationActivity.this,PopUpCalibration.class);
            startActivity(intentKalInfo);

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

            Intent intentLauncher = new Intent(CalibrationActivity.this,MainActivity.class);
            intentLauncher.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentLauncher);

        } else if (id == R.id.nav_kalibruj) {

            Intent intentKal = new Intent(CalibrationActivity.this,CalibrationActivity.class);
            intentKal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentKal);

        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(CalibrationActivity.this,PopUpAppInfo.class);
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
