package com.tomek.audiometr;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
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

    private LinearLayout chartLayout;

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

//        chart = new Chart();

        chartLayout = findViewById(R.id.chartLayout);

        xAxis = (ArrayList<Double>) getIntent().getSerializableExtra("xAxis");
        yAxis = (ArrayList<Double>) getIntent().getSerializableExtra("yAxis");
        channels = (ArrayList<String>) getIntent().getSerializableExtra("channels");

        Log.e("test", "Result Activity");
        Log.e("test", "xAxis = " + xAxis.toString());
        Log.e("test", "yAxis = " + yAxis.toString());
        Log.e("test", "channels = " + channels.toString());

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
        rendererR.setLineWidth(3);
        rendererR.setColor(Color.GREEN);
        rendererR.setPointStyle(PointStyle.CIRCLE);

        XYSeriesRenderer rendererL = new XYSeriesRenderer();
        rendererL.setLineWidth(3);
        rendererL.setColor(Color.BLUE);
        rendererL.setPointStyle(PointStyle.CIRCLE);

        XYMultipleSeriesRenderer mrenderer = new XYMultipleSeriesRenderer();
        mrenderer.addSeriesRenderer(rendererR);
        mrenderer.addSeriesRenderer(rendererL);
//        mrenderer.setYAxisMin(1.5);
        mrenderer.setYAxisMax(0);
        mrenderer.setXAxisMin(0);
        mrenderer.setXAxisMax(18000);
        mrenderer.setMarginsColor(Color.WHITE);
        mrenderer.setShowGrid(true);
        mrenderer.setMarginsColor(Color.WHITE);
        mrenderer.setGridColor(Color.LTGRAY);
        mrenderer.setAxesColor(Color.BLACK);
        mrenderer.setXLabelsColor(Color.BLACK);
        mrenderer.setYLabelsColor(3, Color.BLACK);
        mrenderer.setYLabelsAlign(Paint.Align.CENTER);
        mrenderer.setLabelsTextSize(30);
        mrenderer.setLegendTextSize(40);
        mrenderer.setFitLegend(true);
        mrenderer.setShowLegend(true);

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
