package com.tomek.audiometr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by tokli on 28.08.2018.
 */

public class Sample {

    private double frequency = 0.0;
    private String channel = "";
    private int numberOfSamples;
    private ArrayList<Integer> chosenFrequencies;
    private ArrayList<String> channels = new ArrayList<String>();

    public ArrayList<Sample> samplesList = new ArrayList<>();
    public ArrayList<String> newSample = new ArrayList<>();

    private Random randomGenerator;
    private Sample temporarySample;

    private int indexF = 0;
    private int indexC = 0;
    private int newFrequency = 0;
    private String newChannel = "";
    private boolean k = false;
    private boolean done = false;


    public Sample(int numberOfSamples, ArrayList<Integer> chosenFrequencies) {
        this.numberOfSamples = numberOfSamples;
        this.chosenFrequencies = chosenFrequencies;
    }

    public Sample(double frequency, String channel) {
        this.frequency = frequency;
        this.channel = channel;
    }

    public ArrayList<String> getNewSample() {

        channels = new ArrayList<String>();
        channels.addAll(Arrays.asList("Left", "Right"));

        done = false;
        k = false;

//        if (samplesList.size() >= numberOfSamples) {
//            return null;
//        }

        randomGenerator = new Random();
        randomFrequency();
        randomChannel();

        while (!done) {
            temporarySample = new Sample(newFrequency, newChannel);

            if (checkIfExists(temporarySample)) {
                if (!k) {
                    if (newChannel.equals("Left")) {
                        newChannel = "Right";
                    } else if (newChannel.equals("Right")) {
                        newChannel = "Left";
                    }
                    k = true;
                } else {
                    randomFrequency();
                }
            } else {
                done = true;
            }
        }

        samplesList.add(temporarySample);
        newSample.add(String.valueOf(newFrequency));
        newSample.add(newChannel);

        return newSample;
    }

    public int randomFrequency() {

        indexF = randomGenerator.nextInt(numberOfSamples);
        newFrequency = chosenFrequencies.get(indexF); //losuj częstotliwość

        return newFrequency;
    }

    public String randomChannel() {

        indexC = randomGenerator.nextInt(2);
        newChannel = channels.get(indexC); //losuj częstotliwość

        return newChannel;
    }


    public boolean checkIfExists(Sample sample) {

        if (samplesList.contains(sample)) {
            return true;
        }

        return false;
    }
}
