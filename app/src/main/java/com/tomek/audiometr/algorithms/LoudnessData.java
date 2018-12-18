package com.tomek.audiometr.algorithms;

import android.media.MediaRecorder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by tokli on 18.09.2018.
 */

public class LoudnessData {

    private double decibelsInTable;
    private int indexOfMaxDecibels;
    private double total;
    private double tempAverage;
    private double average;
    private double decibels;
    private static Double[][] table;

//    private static double mEMA = 0.0;
//    static final private double EMA_FILTER = 0.6;

    public LoudnessData() {
    }

    public Double[][] make() {
        table = new Double[31][2];

        double value = -150.0;

        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                if (j == 0) {
                    table[i][j] = value;
                    value += 5;
                } else if (j == 1) {
                    table[i][j] = Math.pow(10, (table[i][0]) / 20);
                }
            }
        }
        return table;
    }

    public ArrayList<Double> find(Double maxDecibels) {
        ArrayList<Double> maxDecibelsData = new ArrayList<>();

        double myDecibels = maxDecibels;
        double distance = Math.abs(table[0][0] - maxDecibels);
        int idx = 0;
        for (int c = 1; c < table.length; c++) {
            double cdistance = Math.abs(table[c][0] - myDecibels);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        decibelsInTable = table[idx][0];
        indexOfMaxDecibels = idx;

        Log.e("test", "Found closest decibels in dataTable = " + decibelsInTable + "IndexOfMaxDecibels = " + indexOfMaxDecibels);

        maxDecibelsData.add(decibelsInTable);
        maxDecibelsData.add((double) indexOfMaxDecibels);

        return maxDecibelsData;
    }


    public double average(ArrayList<Double> list) {
        total = 0.0;

        for (int i = 0; i < list.size(); i++) {
            total = total + list.get(i);
        }

        Log.e("test", "list.size() = " + list.size());

        tempAverage = total / list.size();
        average = tempAverage;

        Log.e("test", "Average = " + average);
        return average;
    }

    private double getDecibels(MediaRecorder mRecorder, Double referenceAmp) {
        decibels = 20 * Math.log10(getAmplitude(mRecorder) / referenceAmp);
        return decibels;
    }


    private double getAmplitude(MediaRecorder mRecorder) {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;
    }

    // Exponential Moving Average Filter:
//    private double getAmplitudeEMA(MediaRecorder mRecorder) {
//        double amp = getAmplitude(mRecorder);
//        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
//        return mEMA;
//    }


    public ArrayList<Double> add(MediaRecorder mRecorder, Double referenceAmp, ArrayList<Double> list) {
        decibels = getDecibels(mRecorder, referenceAmp);
        if (decibels != Double.NEGATIVE_INFINITY) {
            list.add(decibels);
            Log.e("test", "getDecibels = " + decibels);
        }
        Log.e("test", "List = " + list);
        return list;
    }


}
