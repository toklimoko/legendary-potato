package com.tomek.audiometr.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tokli on 18.09.2018.
 */

public class Preferences {


    private double maxDecibels;
    private double defMaxDecibels = 0.0;

    public Preferences() {
    }


    public SharedPreferences.Editor savePreference(String key, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

        return editor;
    }


    public double loadDecibels(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        String scoreDecibels = sharedPreferences.getString("maxDecibels", String.valueOf(defMaxDecibels));
        maxDecibels = Double.parseDouble(scoreDecibels);
        maxDecibels = maxDecibels*(-1);

        return maxDecibels;
    }
}
