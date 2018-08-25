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
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


// obsluga przycisków:

    public ImageButton btnKal;
    public ImageButton btnStart;
    public Vibrator vibe;

    public void kal(){
        btnKal = findViewById(R.id.btn_kal);
        btnKal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentKal = new Intent(MainActivity.this,CalibrationActivity.class);
                intentKal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentKal);
                vibe.vibrate(50);
            }
        });

    }

    public void badanie(){
        btnStart = findViewById(R.id.btn_badanie);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentBad = new Intent(MainActivity.this, AudioTestActivity.class);
                intentBad.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentBad);
                vibe.vibrate(50);
            }
        });

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


        setVolumeControlStream(AudioManager.STREAM_MUSIC); //pozwala na kontrolę głośności

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //wibracje

        kal();
        badanie();

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //obsluga paska bocznego
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //gdy wybrano start - otworz aktywnosc 1
        if (id == R.id.nav_start) {

            //nic nie daję, bo nie ma potrzeby w tej aktywności

            //gdy wybrano kalibruj - otworz aktywnosc 2
        } else if (id == R.id.nav_kalibruj) {

            Intent intentKal = new Intent(MainActivity.this,CalibrationActivity.class);
            intentKal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentKal);

            //gdy wybrano jak badac - otworz okno popup
        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(MainActivity.this,PopUpAppInfo.class);
            intentInfo.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentInfo);

            //gdy wybrano zamknij - zamknij bieżącą aktywnosc
        } else if (id == R.id.nav_powrot) {

            finish();
            //System.exit(0); //drugi sposób zamknięcia aktywności
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
