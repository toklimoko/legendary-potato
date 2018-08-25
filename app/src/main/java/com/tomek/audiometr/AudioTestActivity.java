package com.tomek.audiometr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
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
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener { //podwójna implementacja

    private Button btn_generuj;
    private Button btn_Play_bad;
    private Button btn_Stop_bad;
    private Button btn_rysuj;

    private Spinner spinner;

    private double frequency = 0.0;
    private double amplitudeR = 0.0;
    private double amplitudeL = 0.0;
//    private boolean b = false;

    private Toast toast1;
    private Toast toast2;
    private Toast toast3;

    private ArrayList<Double> listaF; //lista czestotliwosci podczas 1 próby
    private ArrayList<Double> listaAR; //lista amplitud podczas 1 próby
    private ArrayList<Double> listaAL; //lista amplitud podczas 1 próby
    public ArrayList<Double> listaX; //lista wartości X do wykresu (częstotliwości dla każdej z prób)
    public ArrayList<Double> listaYR; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)
    public ArrayList<Double> listaYL; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)

    private XYSeries series; //seria danych do wykresu

    private LinearLayout chartLayout;
    private GraphicalView chartView;


    //deklaracja nazw itemów do spinnera
    private static final String[]paths = {"Próba 1", "Próba 2", "Próba 3", "Próba 4", "Próba 5",
            "Próba 6", "Próba 7", "Próba 8", "Próba 9", "Próba 10", "Próba 11", "Próba 12",
            "Próba 13", "Próba 14", "Próba 15", "Próba 16", "Próba 17", "Próba 18", "Próba 19",
            "Próba 20"};


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

        //deklaracja spinnera
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AudioTestActivity.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //tworzę nowe listy x i y do wykresów
        listaX = new ArrayList<>();
        listaYR = new ArrayList<>();
        listaYL = new ArrayList<>();

        buttonGeneruj();
        buttonStop();
        buttonStart();
        buttonRysuj();

        btn_generuj.setVisibility(View.VISIBLE);
        btn_Play_bad.setVisibility(View.INVISIBLE);
        btn_Stop_bad.setVisibility(View.INVISIBLE);
        btn_rysuj.setVisibility(View.INVISIBLE);

        Context context = getApplicationContext();
        CharSequence text = "Załadowano dane. Można zacząć badanie.";
        CharSequence text2 = "Generowanie dźwięku";
        CharSequence text3 = "Zapisano pomiar. Wybierz nowy dźwięk.";
        int duration = Toast.LENGTH_SHORT;

        toast1 = Toast.makeText(context, text, duration);
        toast2 = Toast.makeText(context,text2, duration);
        toast3 = Toast.makeText(context,text3, duration);


        if (series != null)
        series.clearSeriesValues();
    }

    public void buttonGeneruj(){
        btn_generuj= findViewById(R.id.btn_generuj);
        btn_generuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (series != null)
                series.clearSeriesValues();


                listaF = new ArrayList<>();
                listaAR = new ArrayList<>();
                listaAL = new ArrayList<>();

                //dodaj element 0 do list

                amplitudeR = 0;
                amplitudeL = 0;

                    Play play = new Play(frequency, amplitudeR, amplitudeL, 1);

                    listaF.add(frequency);
                    listaAR.add(amplitudeR);
                    listaAL.add(amplitudeL);

                btn_generuj.setVisibility(View.INVISIBLE);
                btn_Play_bad.setVisibility(View.VISIBLE);
                btn_Stop_bad.setVisibility(View.VISIBLE);
                btn_rysuj.setVisibility(View.INVISIBLE);

                    toast1.show();
            }
        });
    }



    public void buttonStart(){
        btn_Play_bad = findViewById(R.id.btn_glosniej);
        btn_Play_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_generuj.setVisibility(View.INVISIBLE);
                btn_Play_bad.setVisibility(View.VISIBLE);
                btn_Stop_bad.setVisibility(View.VISIBLE);
                btn_rysuj.setVisibility(View.INVISIBLE);

                //tyle razy ile wcisnieto przycisk tyle podglosnij i dodaj do list F i A

//                while (b = true){


/////POPRAWIĆ - DODAĆ ZALEŻNOŚĆ, ZWIĘKSZAĆ TYLKO DLA WYBRANEGO KANAŁU
                    amplitudeR += 0.05;
                    amplitudeL += 0.05;

                    Play play = new Play(frequency, amplitudeR, amplitudeL, 1);

                    //toast2.show();

                    listaF.add(frequency);
                    listaAR.add(amplitudeR);
                    listaAL.add(amplitudeL);

//                    break;
//                }
            }
        });
    }



    public void buttonStop(){
        btn_Stop_bad = findViewById(R.id.btn_slysze);
        btn_Stop_bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                b = false;

                //pobierz te wartości częstotliwości i amplitudy, przy których użytkownik wcisnął "słyszę"
                frequency = listaF.get(listaF.size() - 1);
                amplitudeR = listaAR.get(listaAR.size() - 1);
                amplitudeL = listaAL.get(listaAL.size() - 1);

                //dodaj nowe wartości do list wykorzystywanych do tworzenia wykresów
                listaX.add(frequency);
                listaYR.add(amplitudeR);
                listaYL.add(amplitudeL);

                btn_generuj.setVisibility(View.VISIBLE);
                btn_Play_bad.setVisibility(View.INVISIBLE);
                btn_Stop_bad.setVisibility(View.INVISIBLE);
                btn_rysuj.setVisibility(View.VISIBLE);

                toast3.show();
            }
        });
    }


    public void buttonRysuj(){
        btn_rysuj= findViewById(R.id.btn_doWykresu);
        btn_rysuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rysuj();
          }
        });
    }

    public void rysuj(){

        series = new XYSeries("Audiogram");

        //dodaj każdy punkt pomiarowy do serii danych do wykresu
        for (int i = 0; i<listaX.size();i++){
            series.add(listaX.get(i),listaYR.get(i));
            series.add(listaX.get(i),listaYL.get(i)); //duplikacja przy zmianie na nowego playa - do poprawy (XR, XL)

        }

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(3);
        renderer.setColor(Color.BLUE);
        renderer.setPointStyle(PointStyle.CIRCLE);

        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(renderer);
        mrenderer.setYAxisMin(1.5);
        mrenderer.setYAxisMax(0);
        mrenderer.setXAxisMin(0);
        mrenderer.setXAxisMax(18000);
        mrenderer.setMarginsColor(Color.WHITE);
        mrenderer.setShowGrid(true);
        mrenderer.setMarginsColor(Color.WHITE);
        mrenderer.setGridColor(Color.LTGRAY);
        mrenderer.setAxesColor(Color.BLACK);
        mrenderer.setXLabelsColor(Color.BLACK);
        mrenderer.setYLabelsColor(0, Color.BLACK);
        mrenderer.setYLabelsAlign(Paint.Align.CENTER);
        mrenderer.setLabelsTextSize(30);
        mrenderer.setLegendTextSize(30);
        mrenderer.setFitLegend(true);
        mrenderer.setShowLegend(false);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(series);

        chartLayout = findViewById(R.id.llv);
        chartView = ChartFactory.getLineChartView(this,dataset,mrenderer);
        chartLayout.addView(chartView);
        chartView.repaint();

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
            Intent intentInfo = new Intent(AudioTestActivity.this,PopUpAudioTest.class);
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

            Intent intentLauncher = new Intent(AudioTestActivity.this,MainActivity.class);
            startActivity(intentLauncher);

        } else if (id == R.id.nav_kalibruj) {

            Intent intentKal = new Intent(AudioTestActivity.this,CalibrationActivity.class);
            startActivity(intentKal);

        } else if (id == R.id.nav_info) {

            Intent intentInfo = new Intent(AudioTestActivity.this,PopUpAppInfo.class);
            startActivity(intentInfo);

        } else if (id == R.id.nav_powrot) {
            finish();
            //System.exit(0); //drugi sposób zamykania aktywności

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        //załaduj częstotliwości - deklaracja wartości itemów do spinnera

        switch (position) {
            case 0:
                frequency = 1000;
                break;
            case 1:
                frequency = 8000;
                break;
            case 2:
                frequency = 150;
                break;
            case 3:
                frequency = 12000;
                break;
            case 4:
                frequency = 3000;
                break;
            case 5:
                frequency = 1500;
                break;
            case 6:
                frequency = 100;
                break;
            case 7:
                frequency = 4000;
                break;
            case 8:
                frequency = 15000;
                break;
            case 9:
                frequency = 2500;
                break;
            case 10:
                frequency = 500;
                break;
            case 11:
                frequency = 6000;
                break;
            case 12:
                frequency = 17000;
                break;
            case 13:
                frequency = 250;
                break;
            case 14:
                frequency = 14000;
                break;
            case 15:
                frequency = 125;
                break;
            case 16:
                frequency = 16000;
                break;
            case 17:
                frequency = 400;
                break;
            case 18:
                frequency = 700;
                break;
            case 19:
                frequency = 10000;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
