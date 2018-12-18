package com.tomek.audiometr.popups;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.tomek.audiometr.R;

/**
 * Created by tokli on 30.01.2018.
 */

public class PopUpAppInfo extends Activity {

    private Vibrator vibe;


    public void closeButton(View v) {
        vibe.vibrate(50);
        finish();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_up_app_info);
        ImageView imageViewBackground = findViewById(R.id.iv_popUpAppInfo);
        imageViewBackground.setImageResource(R.drawable.wall3);
        imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageView imageViewLogo = findViewById(R.id.iv_logo_pop_info);
        imageViewLogo.setImageResource(R.drawable.logo);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

}
