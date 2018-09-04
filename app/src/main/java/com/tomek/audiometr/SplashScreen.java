package com.tomek.audiometr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;

import java.lang.reflect.Constructor;


/**
 * Created by tokli on 22.08.2018.
 */

public class SplashScreen extends Activity {

    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        constraintLayout = findViewById(R.id.cl_splashScreen);

        constraintLayout.setBackgroundColor(getResources().getColor(R.color.SplashSecondColor));

        Thread myThread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(700);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }

}
