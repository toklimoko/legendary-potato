package com.tomek.audiometr.helpers;

import android.content.Context;
import android.content.Intent;

import com.tomek.audiometr.R;
import com.tomek.audiometr.activities.CalibrationActivity;
import com.tomek.audiometr.activities.ChoiceActivity;
import com.tomek.audiometr.activities.MainActivity;
import com.tomek.audiometr.popups.PopUpAppInfo;

/**
 * Created by tokli on 19.09.2018.
 */

public class Drawer {

    public Drawer() {
    }

    public Intent action(int id, Context context, VolumeController volumeController){

        Intent intent = null;

        if (id == R.id.nav_start) {

            intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (id == R.id.nav_calibration) {

            intent = new Intent(context, CalibrationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (id == R.id.nav_audioTest) {
            intent = new Intent(context, ChoiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (id == R.id.nav_info) {

            intent = new Intent(context, PopUpAppInfo.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        } else if (id == R.id.nav_exit) {
            volumeController.setMin();
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
        }
    return intent;
    }
}
