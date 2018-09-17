package com.tomek.audiometr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by tokli on 30.01.2018.
 */

public class PopUpCalibration extends Activity {

    private ImageView imageViewBackground;
    private ImageView imageViewIcon;
    private Vibrator vibe;

    public void closeButton(View v) {
        vibe.vibrate(50);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_up_calibration_info);
        imageViewBackground = findViewById(R.id.iv_popUpAppInfo);
        imageViewBackground.setImageResource(R.drawable.wall3);
        imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageViewIcon = findViewById(R.id.iv_logo_pop_info);
        imageViewIcon.setImageResource(R.drawable.calibration_icon);
        imageViewIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }


}
