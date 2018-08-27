package com.tomek.audiometr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;

public class AudioTestActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener { //podwójna implementacja


//    private boolean b = false;

    private Toast toast1;
    private Toast toast2;
    private Toast toast3;

    private ArrayList<Double> listaF; //lista czestotliwosci podczas 1 próby
    private ArrayList<Double> listaA; //lista amplitud podczas 1 próby
    public ArrayList<Double> listaX; //lista wartości X do wykresu (częstotliwości dla każdej z prób)
    public ArrayList<Double> listaY; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)

    private XYSeries series; //seria danych do wykresu

    private LinearLayout chartLayout;
    private GraphicalView chartView;


//////////////////////////////

    private Button buttonStart;
    private Button buttonSlysze;

    private Vibrator vibe;
    private Thread playThread;
    private Play play;

    private Thread algorithmThread;
    private boolean stop = false;

    private double frequency = 0.0;
    private double amplitude = 0.0;
    private int duration = 1;
    private String channel = "Both";


    private ArrayList<Integer> frequencyList;
    private int f;


    private void playAsync() {

        if (play != null) {
            return;
        }

        playThread = new Thread(new Runnable() {
            @Override
            public void run() {

                // play the loop until the thread is interrupted or condition is met
                while (!Thread.currentThread().isInterrupted()) {
                    play = new Play(frequency, amplitude, duration, channel); //czestotliwosc = 2000 Hz - wartosc z zakresu najlepszej slyszalnosci ucha
                    play.playSound();
                    play = null;

                    amplitude += 0.005;

                    if (stop || amplitude >= 0.5) {
                        addPoint(frequency,amplitude,channel);
                        break;
                    }
                }
            }
        });

        playThread.start();

    }

    public void initPlaySoundButton() {
        buttonStart = findViewById(R.id.btn_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                amplitude = 0.0;
                playAsync();

//                toast.show();
//                vibe.vibrate(50);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (play != null) {
            play.release();
        }

        if (stop) {
//            newSignalData();
//            addPoint();

            toast1.show();
            stop = false;
        }

        try {
            playThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void initStopSoundButton() {
        buttonSlysze = findViewById(R.id.btn_slysze);
        buttonSlysze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButtonAction();
            }
        });
    }

    public void stopButtonAction() {
        stop = true;
        onPause();
    }

    public void newSignalData() {

        // ZRESETUJ AMPLITUDĘ, POBIERZ NOWY PUNKT Z LISTY WYLOSOWANYCH CZĘSTOTLIWOŚCI (I KANAŁÓW)

        amplitude = 0.0;

        if (f <= frequencyList.size()) {
            frequency = frequencyList.get(f);
            f++;
            playAsync();

        } else {
            //koniec badania

        }


        stop = false;

    }


    public void addPoint(double frequency, double amplitude, String channel) {
        // AKCJA GDY STOP - DODAJ PUNKT DO LIST




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

        //tworzę nowe listy x i y do wykresów
        listaX = new ArrayList<>();
        listaY = new ArrayList<>();


//
//  TOASTY

//
//        Context context = getApplicationContext();
//        CharSequence text = "Załadowano dane. Można zacząć badanie.";
//        CharSequence text2 = "Generowanie dźwięku";
//        CharSequence text3 = "Zapisano pomiar. Wybierz nowy dźwięk.";
//        int duration = Toast.LENGTH_SHORT;
//
//        toast1 = Toast.makeText(context, text, duration);
//        toast2 = Toast.makeText(context,text2, duration);
//        toast3 = Toast.makeText(context,text3, duration);


        if (series != null)
            series.clearSeriesValues();


        ////////////////////////////////////////////

//        algorithm();
        initPlaySoundButton();
        initStopSoundButton();


        frequencyList = new ArrayList<>();
        frequencyList.add(1000);
        frequencyList.add(3000);
        frequencyList.add(5000);

        frequency = frequencyList.get(f);
        f++;


        Context context = getApplicationContext();
        CharSequence text = "Stop wciśnięty";

        int duration = Toast.LENGTH_SHORT;

        toast1 = Toast.makeText(context, text, duration);


    }

//    public void buttonGeneruj(){
//        btn_generuj= findViewById(R.id.btn_generuj);
//        btn_generuj.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (series != null)
//                series.clearSeriesValues();
//
//
//                listaF = new ArrayList<>();
//                listaA = new ArrayList<>();
//
//                //dodaj element 0 do list
//
//                amplitude = 0;
//
//                    Play play = new Play(frequency, amplitude, 1, "Right");
//
//                    listaF.add(frequency);
//                    listaA.add(amplitude);
//
//                btn_generuj.setVisibility(View.INVISIBLE);
//                btn_Play_bad.setVisibility(View.VISIBLE);
//                btn_Stop_bad.setVisibility(View.VISIBLE);
//                btn_rysuj.setVisibility(View.INVISIBLE);
//
//                    toast1.show();
//            }
//        });
//    }
//
//
//
//    public void buttonStart(){
//        btn_Play_bad = findViewById(R.id.btn_glosniej);
//        btn_Play_bad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                btn_generuj.setVisibility(View.INVISIBLE);
//                btn_Play_bad.setVisibility(View.VISIBLE);
//                btn_Stop_bad.setVisibility(View.VISIBLE);
//                btn_rysuj.setVisibility(View.INVISIBLE);
//
//                //tyle razy ile wcisnieto przycisk tyle podglosnij i dodaj do list F i A
//
////                while (b = true){
//
//                    amplitude += 0.05;
//
//                    Play play = new Play(frequency, amplitude, 1, "Right");
//
//                    //toast2.show();
//
//                    listaF.add(frequency);
//                    listaA.add(amplitude);
//
////                    break;
////                }
//            }
//        });
//    }
//
//
//
//
//    public void buttonStop(){
//        btn_Stop_bad = findViewById(R.id.btn_slysze);
//        btn_Stop_bad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                b = false;
//
//                //pobierz te wartości częstotliwości i amplitudy, przy których użytkownik wcisnął "słyszę"
//                frequency = listaF.get(listaF.size() - 1);
//                amplitude = listaA.get(listaA.size() - 1);
//
//                //dodaj nowe wartości do list wykorzystywanych do tworzenia wykresów
//                listaX.add(frequency);
//                listaY.add(amplitude);
//
//                btn_generuj.setVisibility(View.VISIBLE);
//                btn_Play_bad.setVisibility(View.INVISIBLE);
//                btn_Stop_bad.setVisibility(View.INVISIBLE);
//                btn_rysuj.setVisibility(View.VISIBLE);
//
//                toast3.show();
//            }
//        });
//    }
//
//
//    public void buttonRysuj(){
//        btn_rysuj= findViewById(R.id.btn_doWykresu);
//        btn_rysuj.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                rysuj();
//          }
//        });
//    }
//
//    public void rysuj(){
//
//        series = new XYSeries("Audiogram");
//
//        //dodaj każdy punkt pomiarowy do serii danych do wykresu
//        for (int i = 0; i<listaX.size();i++){
//            series.add(listaX.get(i),listaY.get(i));
//
//        }
//
//        XYSeriesRenderer renderer = new XYSeriesRenderer();
//        renderer.setLineWidth(3);
//        renderer.setColor(Color.BLUE);
//        renderer.setPointStyle(PointStyle.CIRCLE);
//
//        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
//        mrenderer.addSeriesRenderer(renderer);
//        mrenderer.setYAxisMin(1.5);
//        mrenderer.setYAxisMax(0);
//        mrenderer.setXAxisMin(0);
//        mrenderer.setXAxisMax(18000);
//        mrenderer.setMarginsColor(Color.WHITE);
//        mrenderer.setShowGrid(true);
//        mrenderer.setMarginsColor(Color.WHITE);
//        mrenderer.setGridColor(Color.LTGRAY);
//        mrenderer.setAxesColor(Color.BLACK);
//        mrenderer.setXLabelsColor(Color.BLACK);
//        mrenderer.setYLabelsColor(0, Color.BLACK);
//        mrenderer.setYLabelsAlign(Paint.Align.CENTER);
//        mrenderer.setLabelsTextSize(30);
//        mrenderer.setLegendTextSize(30);
//        mrenderer.setFitLegend(true);
//        mrenderer.setShowLegend(false);
//
//        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//        dataset.addSeries(series);
//
//        chartLayout = findViewById(R.id.llv);
//        chartView = ChartFactory.getLineChartView(this,dataset,mrenderer);
//        chartLayout.addView(chartView);
//        chartView.repaint();
//
//    }

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
            startActivity(intentLauncher);

        } else if (id == R.id.nav_kalibruj) {

            Intent intentKal = new Intent(AudioTestActivity.this, CalibrationActivity.class);
            startActivity(intentKal);

        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(AudioTestActivity.this, PopUpAppInfo.class);
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
