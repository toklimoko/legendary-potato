package com.tomek.audiometr;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;


public class ResultActivity extends Activity {

    private double amplitudeLimit;
    private double frequencyLimitMin;
    private double frequencyLimitMax;

    private LinearLayout chartLayout;
    private ImageView imageViewBackground;

    private GraphicalView chartView;
    private XYSeries seriesR; //seria danych do wykresu
    private XYSeries seriesL; //seria danych do wykresu

    private ArrayList<Double> xAxis; //lista wartości X do wykresu (częstotliwości dla każdej z prób)
    private ArrayList<Double> yAxis; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)
    private ArrayList<String> channels; //lista wartości Y do wykresu (amplitudy końcowe dla każdej z prób)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_result);

        chartLayout = findViewById(R.id.chartLayout);

        xAxis = (ArrayList<Double>) getIntent().getSerializableExtra("xAxis");
        yAxis = (ArrayList<Double>) getIntent().getSerializableExtra("yAxis");
        channels = (ArrayList<String>) getIntent().getSerializableExtra("channels");
        amplitudeLimit = (double) getIntent().getSerializableExtra("amplitudeLimit");
        frequencyLimitMin = (double) getIntent().getSerializableExtra("frequencyLimitMin");
        frequencyLimitMax = (double) getIntent().getSerializableExtra("frequencyLimitMax");

        imageViewBackground = findViewById(R.id.iv_resultBackground);
        imageViewBackground.setImageResource(R.drawable.tapeta4);
        imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Log.e("test", "Result Activity");
        Log.e("test", "xAxis = " + xAxis.toString());
        Log.e("test", "yAxis = " + yAxis.toString());
        Log.e("test", "channels = " + channels.toString());
        Log.e("test", "amplitudeLimit = " + amplitudeLimit);
        Log.e("test", "frequencyLimitMin = " + frequencyLimitMin);
        Log.e("test", "frequencyLimitMax = " + frequencyLimitMax);

        drawChart();

    }

    public void drawChart() {

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

        XYSeriesRenderer rendererR = new XYSeriesRenderer();
        rendererR.setLineWidth(5);
        rendererR.setColor(Color.argb(255,255,145,6));
        rendererR.setPointStyle(PointStyle.CIRCLE);

        XYSeriesRenderer rendererL = new XYSeriesRenderer();
        rendererL.setLineWidth(5);
        rendererL.setColor(Color.argb(255,41,182,246));
        rendererL.setPointStyle(PointStyle.CIRCLE);


        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(rendererR);
        mrenderer.addSeriesRenderer(rendererL);
        mrenderer.setApplyBackgroundColor(true);
        mrenderer.setBackgroundColor(Color.TRANSPARENT);
        mrenderer.setYAxisMax(0);
        mrenderer.setYAxisMin(amplitudeLimit);
        mrenderer.setXAxisMin(frequencyLimitMin);
        mrenderer.setXAxisMax(frequencyLimitMax);
        mrenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
//        mrenderer.setMarginsColor(Color.BLACK);
        mrenderer.setShowGrid(true);
        mrenderer.setGridColor(Color.LTGRAY);
        mrenderer.setAxesColor(Color.WHITE);
        mrenderer.setXLabelsColor(Color.WHITE);
//        mrenderer.setYLabelsColor(0, Color.BLACK);
        mrenderer.setYLabelsAlign(Paint.Align.RIGHT);
        mrenderer.setLabelsTextSize(30);
        mrenderer.setLegendTextSize(40);
        mrenderer.setFitLegend(true);
        mrenderer.setShowLegend(true);
        mrenderer.setMargins(new int[]{150,120,150,50}); // {up,left,down,right}
        mrenderer.setLegendHeight(5);
        mrenderer.setXTitle("Częstotliwość");
        mrenderer.setYTitle("Amplituda");

        mrenderer.setAxisTitleTextSize(50);
        mrenderer.setPanEnabled(true, false);
        mrenderer.setZoomEnabled(false, false);
        mrenderer.setChartTitleTextSize(100);

        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        dataset.addSeries(seriesR);
        dataset.addSeries(seriesL);

        chartView = ChartFactory.getLineChartView(this, dataset, mrenderer);

//        chartView = drawChart();
        chartLayout.addView(chartView);

        chartView.repaint();


    }


    public void closeButton(View v) {

        finish();

    }
}
