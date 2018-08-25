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
 * klssa obslugujaca popUp'a drugiego (dot. celu kalibracji)
 */

public class PopUpCalibration extends Activity {

    private ImageView mImageView;
//    double screenFactor = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_up_calibration_info);

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        int width = dm.widthPixels;
//        int height = dm.heightPixels;
//
//        getWindow().setLayout((int) (width*screenFactor),(int) (height*screenFactor));
//

        mImageView = findViewById(R.id.iv_ksiazka);
        mImageView.setImageResource(R.drawable.manual_icon);


    }

    public void closeButton(View v) {

        finish();

    }
}
