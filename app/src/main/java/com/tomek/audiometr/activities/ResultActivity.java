package com.tomek.audiometr.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tomek.audiometr.R;
import com.tomek.audiometr.algorithms.Files;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ResultActivity extends Activity {

    private double decibelsLimit;
    private double frequencyLimitMin;
    private double frequencyLimitMax;

    private String path;

    private LinearLayout chartLayout;
    private ImageView imageViewBackground;
    private ImageView imageSaveIcon;

    private GraphicalView chartView;
    private XYSeries seriesR;
    private XYSeries seriesL;
    private XYSeries seriesT;

    private ArrayList<Double> xAxis;
    private ArrayList<Double> yAxis;
    private ArrayList<String> channels;
    private ArrayList<Double> times;

    private Vibrator vibe;

    private Files file;

    private void separateByChannels(){

        if (seriesR != null || seriesL != null) {
            seriesR.clearSeriesValues();
            seriesL.clearSeriesValues();
        }

        seriesR = new XYSeries("UCHO PRAWE");
        seriesL = new XYSeries("UCHO LEWE");

        for (int i = 0; i < xAxis.size(); i++) {
            if (channels.get(i).equals("Right")) {
                seriesR.add(xAxis.get(i), yAxis.get(i));
            } else if (channels.get(i).equals("Left")) {
                seriesL.add(xAxis.get(i), yAxis.get(i));
            }
        }
    }

    private void rejectPoints(){
        if (seriesT != null) {
            seriesT.clearSeriesValues();
        }

        seriesT = new XYSeries("NIEWAŻNY POMIAR");

        double sum = 0.0;
        double average = 0.0;

        for (int i = 0; i < times.size(); i++) {
           sum = sum + times.get(i);
        }
        average = sum/times.size();

        for (int i = 0; i < times.size(); i++) {
            if (times.get(i)<0.20*average || times.get(i)>1.80*average){
                seriesT.add(xAxis.get(i),yAxis.get(i));
            }
        }
    }

    private void drawChart() {

        XYSeriesRenderer rendererR = new XYSeriesRenderer();
        rendererR.setLineWidth(7);
        rendererR.setColor(Color.argb(255, 255, 145, 6));
        rendererR.setPointStyle(PointStyle.CIRCLE);
        rendererR.setPointStrokeWidth(22);

        XYSeriesRenderer rendererL = new XYSeriesRenderer();
        rendererL.setLineWidth(7);
        rendererL.setColor(Color.argb(255, 41, 182, 246));
        rendererL.setPointStyle(PointStyle.X);
        rendererL.setPointStrokeWidth(32);

        XYSeriesRenderer rendererT = new XYSeriesRenderer();
//        rendererT.setLineWidth(5);
        rendererT.setLineWidth(0);
//        rendererT.setColor(Color.argb(255,186  ,6,6)); //RED
        rendererT.setColor(Color.GRAY);
        rendererT.setPointStyle(PointStyle.SQUARE);
        rendererT.setPointStrokeWidth(20);

        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(rendererR);
        mRenderer.addSeriesRenderer(rendererL);
        mRenderer.addSeriesRenderer(rendererT);
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.TRANSPARENT);
        mRenderer.setYAxisMax(0);
        mRenderer.setYAxisMin(decibelsLimit);
        mRenderer.setXAxisMin(frequencyLimitMin);
        mRenderer.setXAxisMax(frequencyLimitMax);
        mRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
//        mRenderer.setMarginsColor(Color.BLACK);
        mRenderer.setShowGrid(true);
        mRenderer.setGridColor(Color.LTGRAY);
        mRenderer.setAxesColor(Color.WHITE);
        mRenderer.setXLabelsColor(Color.WHITE);
//        mRenderer.setYLabelsColor(0, Color.BLACK);
        mRenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mRenderer.setLabelsTextSize(30);
        mRenderer.setLegendTextSize(40);
        mRenderer.setFitLegend(true);
        mRenderer.setShowLegend(true);
        mRenderer.setMargins(new int[]{150, 120, 150, 50}); // {up,left,down,right}
        mRenderer.setLegendHeight(5);
        mRenderer.setXTitle("Częstotliwość [Hz]");
        mRenderer.setYTitle("Wzmocnienie [dB]");

        mRenderer.setAxisTitleTextSize(50);
        mRenderer.setPanEnabled(true, true);
        mRenderer.setZoomEnabled(true, true);
        mRenderer.setZoomLimits(new double[]{frequencyLimitMin, frequencyLimitMax, decibelsLimit, 0});
//        mRenderer.setChartTitleTextSize(100);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(seriesR);
        dataset.addSeries(seriesL);
        dataset.addSeries(seriesT);

        chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);
        chartLayout.addView(chartView);
        chartView.repaint();
    }


    public void saveResult(View view) {
        vibe.vibrate(50);
        Date currentTime = Calendar.getInstance().getTime();
        path = file.saveFile("/AUDIOMETR/", "Result_" + currentTime.toString() + ".csv",xAxis,yAxis,channels,getApplicationContext(), this);

        Toast.makeText(getApplicationContext(), "Zapisano w: " + path,
                Toast.LENGTH_LONG).show();
    }


    public void closeButton(View v) {
        vibe.vibrate(50);
        finish();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        chartLayout = findViewById(R.id.chartLayout);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        xAxis = (ArrayList<Double>) getIntent().getSerializableExtra("xAxis");
        yAxis = (ArrayList<Double>) getIntent().getSerializableExtra("yAxis");
        channels = (ArrayList<String>) getIntent().getSerializableExtra("channels");
        times = (ArrayList<Double>) getIntent().getSerializableExtra("times");
        decibelsLimit = -1 * (double) getIntent().getSerializableExtra("decibelsLimit");
        frequencyLimitMin = (double) getIntent().getSerializableExtra("frequencyLimitMin");
        frequencyLimitMax = (double) getIntent().getSerializableExtra("frequencyLimitMax");

        separateByChannels();
        rejectPoints();
        file = new Files();

        imageViewBackground = findViewById(R.id.iv_resultBackground);
        imageViewBackground.setImageResource(R.drawable.wall4);
        imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageSaveIcon = findViewById(R.id.iv_save);
        imageSaveIcon.setImageResource(R.drawable.save_icon);
        imageSaveIcon.setScaleType(ImageView.ScaleType.FIT_XY);


        Log.e("test", "ResultActivity: onCreate() // values: " + "\n" + "xAxis.toString() = " + xAxis.toString() + "\n"
                + "yAxis.toString() = " + yAxis.toString() + "\n" + "channels.toString() = " + channels.toString() + "\n" + "times.toString() = " + times.toString() + "\n"
                + "decibelsLimit = " + decibelsLimit + "; frequencyLimitMin = " + frequencyLimitMin + "; frequencyLimitMax = " + frequencyLimitMax);

        drawChart();
    }
}
