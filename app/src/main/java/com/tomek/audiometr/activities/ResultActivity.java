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
import android.widget.TextView;
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

    private LinearLayout chartLayout;
    private TextView textResultValue;


    private XYSeries seriesR;
    private XYSeries seriesL;
    private XYSeries seriesT;

    private ArrayList<Double> xAxis;
    private ArrayList<Double> yAxis;
    private ArrayList<String> channels;
    private ArrayList<Double> times;
    private ArrayList<String> selectedTimes;

    private Vibrator vibe;

    private Files file;

    private void separateByChannels() {

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

    private void rejectPoints() {
        if (seriesT != null) {
            seriesT.clearSeriesValues();
        }

        seriesT = new XYSeries("NIEWAŻNY POMIAR");

        double sum = 0.0;
        double average = 0.0;

        for (int i = 0; i < times.size(); i++) {
            sum = sum + times.get(i);
        }
        average = sum / times.size();

        for (int i = 0; i < times.size(); i++) {
            if (times.get(i) < 0.03 * average || times.get(i) > 1.97 * average) {
                seriesT.add(xAxis.get(i), yAxis.get(i));
                selectedTimes.add("Incorrect");
            } else {
                selectedTimes.add("Correct");
            }
        }
    }

    private void calculateResult() {

        ArrayList<Double> freqToCalculate = new ArrayList<>();
        ArrayList<Double> ampToCalculate = new ArrayList<>();
        ArrayList<String> chanToCalculate = new ArrayList<>();

        //search for frequencies
        for (int i = 0; i < xAxis.size(); i++) {
            double tempFreq = xAxis.get(i);
            double tempAmp = yAxis.get(i);
            String tempChan = channels.get(i);

            if (tempFreq == 500 || tempFreq == 1000 || tempFreq == 2000 || tempFreq == 4000) {
                freqToCalculate.add(tempFreq);
                ampToCalculate.add(tempAmp);
                chanToCalculate.add(tempChan);
            }
        }

        ArrayList<Double> pairedFreq = new ArrayList<>();
        ArrayList<Double> pairedAmp = new ArrayList<>();
        ArrayList<String> pairedChan = new ArrayList<>();

        //search for pairs of frequencies
        for (int i = 0; i < freqToCalculate.size(); i++) {
            double tempFreq = freqToCalculate.get(i);

            for (int j = 0; j < freqToCalculate.size(); j++) {
                if (tempFreq == freqToCalculate.get(j)) {
                    double tempAmp = ampToCalculate.get(j);
                    String tempChan = chanToCalculate.get(j);

                    pairedFreq.add(tempFreq);
                    pairedAmp.add(tempAmp);
                    pairedChan.add(tempChan);
                }
            }
        }

        ArrayList<Double> comparedFreq = new ArrayList<>();
        ArrayList<Double> comparedAmp = new ArrayList<>();
        ArrayList<String> comparedChan = new ArrayList<>();

        //compare
        for (int i = 0; i < pairedFreq.size() - 1; i = i + 2) {
            double tempFreq = pairedFreq.get(i);
            double tempAmp = pairedAmp.get(i);
            String tempChan = pairedChan.get(i);

            double tempFreq2 = pairedFreq.get(i + 1);
            double tempAmp2 = pairedAmp.get(i + 1);
            String tempChan2 = pairedChan.get(i + 1);

            if (tempAmp < tempAmp2) {
                comparedFreq.add(tempFreq);
                comparedAmp.add(tempAmp);
                comparedChan.add(tempChan);
            } else {
                comparedFreq.add(tempFreq2);
                comparedAmp.add(tempAmp2);
                comparedChan.add(tempChan2);
            }

        }

        Log.e("test", "pairedFreqlist = " + pairedFreq.toString() + "\n pairedAmpList = " + pairedAmp.toString() + "\n pairedChanList = " + pairedChan.toString());
        Log.e("test", "comparedFreqlist = " + comparedFreq.toString() + "\n comparedAmpList = " + comparedAmp.toString() + "\n comparedChanList = " + comparedChan.toString());

        //count average

        double sum = 0;
        for (int i = 0; i < comparedAmp.size(); i++) {
            sum = sum + comparedAmp.get(i);
        }
        double average = sum / comparedAmp.size();

        setTextResult(Math.round(average));
    }


    private void setTextResult(double resultValue) {
        String string = resultValue + " dB";
        textResultValue.setText(string);
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

        GraphicalView chartView = ChartFactory.getLineChartView(this, dataset, mRenderer);
        chartLayout.addView(chartView);
        chartView.repaint();
    }


    public void saveResult(View view) {
        vibe.vibrate(50);
        Date currentTime = Calendar.getInstance().getTime();
        String path = file.saveFile("/AUDIOMETR/", "Result_" + currentTime.toString() + ".csv", xAxis, yAxis, channels, times, selectedTimes, getApplicationContext(), this);

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

        selectedTimes = new ArrayList<>();

        textResultValue = findViewById(R.id.txtResult);


        separateByChannels();
        rejectPoints();
        calculateResult();
        file = new Files();

        ImageView imageViewBackground = findViewById(R.id.iv_resultBackground);
        imageViewBackground.setImageResource(R.drawable.wall4);
        imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageView imageSaveIcon = findViewById(R.id.iv_save);
        imageSaveIcon.setImageResource(R.drawable.save_icon);
        imageSaveIcon.setScaleType(ImageView.ScaleType.FIT_XY);


        Log.e("test", "ResultActivity: onCreate() // values: " + "\n" + "xAxis.toString() = " + xAxis.toString() + "\n"
                + "yAxis.toString() = " + yAxis.toString() + "\n" + "channels.toString() = " + channels.toString() + "\n" + "times.toString() = " + times.toString() + "\n"
                + "decibelsLimit = " + decibelsLimit + "; frequencyLimitMin = " + frequencyLimitMin + "; frequencyLimitMax = " + frequencyLimitMax);

        drawChart();
    }
}
