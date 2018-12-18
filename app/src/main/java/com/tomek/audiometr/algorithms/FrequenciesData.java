package com.tomek.audiometr.algorithms;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by tokli on 19.09.2018.
 */

public class FrequenciesData {

    private ArrayList<Integer> allFrequencies;

    public FrequenciesData() {
    }

    public ArrayList<Integer> basicAudioTest() {
        allFrequencies = new ArrayList<>();
        allFrequencies.addAll(Arrays.asList(125, 250, 500, 1000, 2000, 3000, 4000, 6000, 8000));

        return allFrequencies;
    }

    public ArrayList<Integer> extendedAudioTest() {
        allFrequencies = new ArrayList<>();
        allFrequencies.addAll(Arrays.asList(100, 125, 150, 200, 300, 400, 500, 700, 1000, 2000, 2500, 3000, 4000, 6000, 8000, 10000, 12000, 14000, 15000, 16000, 17000, 18000));

        return allFrequencies;
    }



    public ArrayList<Integer> random(int numberOfFrequencies, ArrayList<Integer> allFrequencies) {
        Log.e("test", "AudioTestActivity: randomFrequencies() --before");

        Random randomGenerator = new Random();
        ArrayList<Integer> chosenFrequencies = new ArrayList<>();

        while (chosenFrequencies.size() < numberOfFrequencies) {
            int index = randomGenerator.nextInt(allFrequencies.size());
            int newFrequency = allFrequencies.get(index);
            if (!chosenFrequencies.contains(newFrequency)) {
                chosenFrequencies.add(newFrequency);
            }
        }

        Log.e("test", "AudioTestActivity: randomFrequencies() --after // values: chosenFrequencies = " + chosenFrequencies.toString());

        return chosenFrequencies;
    }

    public ArrayList<Integer> xLimits (ArrayList<Integer> chosenFrequencies){
        int frequencyLimitMin = Collections.min(chosenFrequencies);
        int frequencyLimitMax = Collections.max(chosenFrequencies);

        ArrayList<Integer> xLimits = new ArrayList<>();
        xLimits.add(frequencyLimitMin);
        xLimits.add(frequencyLimitMax);

        Log.e("test", "frequencyLimitMin = " + frequencyLimitMin + "; frequencyLimitMax = " + frequencyLimitMax);

        return xLimits;

    }

}
