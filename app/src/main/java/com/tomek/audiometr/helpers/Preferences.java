package com.tomek.audiometr.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by tokli on 18.09.2018.
 */

public class Preferences {


    private double maxDecibels;
    private double defMaxDecibels = 75;

    public Preferences() {
    }


    public SharedPreferences.Editor savePreference(String key, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

        return editor;
    }

    public SharedPreferences.Editor saveFrequencies(String key, ArrayList<Integer> list, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, String.valueOf(list));
        editor.apply();

        return editor;

    }

    public ArrayList<Integer> loadFrequencies(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String scoreFrequencies = sharedPreferences.getString("allFrequencies", "");
        scoreFrequencies = scoreFrequencies.replaceAll("[\\[\\](){} ]","");
        ArrayList<String> frequenciesListString = new ArrayList<>(Arrays.asList(scoreFrequencies.split(",")));
        ArrayList<Integer> allFrequencies = new ArrayList<>();
        for(int i = 0; i < frequenciesListString.size(); i++) {
            allFrequencies.add(Integer.parseInt(frequenciesListString.get(i)));
        }
        return allFrequencies;
    }


    public double loadDecibels(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String scoreDecibels = sharedPreferences.getString("maxDecibels", String.valueOf(defMaxDecibels));
        maxDecibels = Double.parseDouble(scoreDecibels);
        maxDecibels = maxDecibels*(-1);

        return maxDecibels;
    }

    public boolean loadCalibrated(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String score = sharedPreferences.getString("calibrated", "");

        Log.e("score", score);

        if (score.equals("true")) {
            return true;
        }
        return false;
    }
}
