package com.tomek.audiometr;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by tokli on 30.01.2018.
 *
 * klssa obslugujaca popUp'a trzeciego (dot. informacji o programie)
 */

public class PopUpAppInfo extends Activity {

    private ImageView imageViewBackground;
    private ImageView imageViewLogo;
//    double screenFactor = 1; //0.85

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_up_app_info);
        imageViewBackground = findViewById(R.id.iv_popUpAppInfo);
        imageViewBackground.setImageResource(R.drawable.tapeta2);
        imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageViewLogo = findViewById(R.id.iv_logo_pop_info);
        imageViewLogo.setImageResource(R.drawable.logo);
//        imageViewLogo.setScaleType();

    }

    public void closeButton(View v) {

        finish();

    }
}
