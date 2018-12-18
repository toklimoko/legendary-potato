package com.tomek.audiometr.splashes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;

import com.tomek.audiometr.R;
import com.tomek.audiometr.activities.MainActivity;


/**
 * Created by tokli on 22.08.2018.
 */

public class SplashScreen extends Activity {

    private void thread() {
        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(700);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        splashThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ConstraintLayout constraintLayout = findViewById(R.id.cl_splashScreen);
        constraintLayout.setBackgroundColor(getResources().getColor(R.color.SplashSecondColor));

        thread();
    }

}
