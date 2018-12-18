package com.tomek.audiometr.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.tomek.audiometr.R;
import com.tomek.audiometr.activities.AudioTestActivity;
import com.tomek.audiometr.activities.CalibrationActivity;
import com.tomek.audiometr.activities.ChoiceActivity;

/**
 * Created by tokli on 16.09.2018.
 */

public class Dialog extends Activity {

    private Vibrator vibe;

    public void goCalibrate(View v){
        vibe.vibrate(50);
        Intent intent = new Intent(getApplicationContext(),CalibrationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void skipDialog(View v){
        vibe.vibrate(50);
        Intent intent = new Intent(getApplicationContext(),ChoiceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        ConstraintLayout constraintLayout = findViewById(R.id.cl_dialog);

        constraintLayout.setBackgroundColor(getResources().getColor(R.color.dialog_background_color));

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

}
