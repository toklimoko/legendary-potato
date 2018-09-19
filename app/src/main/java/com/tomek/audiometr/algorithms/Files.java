package com.tomek.audiometr.algorithms;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by tokli on 19.09.2018.
 */

public class Files {

    public Files() {
    }

    public String saveFile(String folder, String fileName, ArrayList<Double> xAxis, ArrayList<Double> yAxis, ArrayList<String> channels, Context context) {


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        String string = new String();
        string = "Frequency [Hz]" + "\t" + "Decibels [dB]" + "\t" + "Ear" + "\n";
        java.io.File root = android.os.Environment.getExternalStorageDirectory();
        java.io.File dir = new java.io.File(root.getAbsolutePath() + folder);
        dir.mkdirs();
        java.io.File file = new java.io.File(dir, fileName);

        String path = file.getAbsolutePath();
        Log.i("test", "FILE LOCATION: " + path);


        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);


            for (int i = 0; i < xAxis.size(); i++) {
                string = string + xAxis.get(i).toString() + "\t" + yAxis.get(i).toString() + "\t" + channels.get(i).toString() + "\n";
            }

            pw.print(string);

            pw.flush();
            pw.close();
            f.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("My", "Files not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");


        } catch (IOException e) {
            e.printStackTrace();
        }
    return path;
    }
}
