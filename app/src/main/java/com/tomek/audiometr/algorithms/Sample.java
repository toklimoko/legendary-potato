package com.tomek.audiometr.algorithms;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by tokli on 28.08.2018.
 */

public class Sample {

    private double frequency = 0.0;
    private String channel = "";
    private int numberOfFrequencies;
    private ArrayList<Integer> chosenFrequencies;

    private ArrayList<Sample> samplesList = new ArrayList<>();
    private ArrayList<Double> frequenciesList = new ArrayList<>();
    private ArrayList<String> channelsList = new ArrayList<>();

    public Sample(int numberOfFrequencies, ArrayList<Integer> chosenFrequencies) {
        this.numberOfFrequencies = numberOfFrequencies;
        this.chosenFrequencies = chosenFrequencies;

        Log.e("test", "Sample: Sample(numberOfFrequencies,chosenFrequencies) // values: numberOfFrequencies = " + numberOfFrequencies
                + "; chosenFrequencies = " + chosenFrequencies);
    }

    private Sample(double frequency, String channel) {
        this.frequency = frequency;
        this.channel = channel;

        Log.e("test", "Sample: Sample(frequency,channel) // values: frequency = " + frequency
                + "; channel = " + channel);
    }

    public ArrayList<Sample> makeSamplesList(){
        for (int i = 0; i < chosenFrequencies.size() * 2; i++) {
            getNewSample();
        }
        return samplesList;
    }


    private void getNewSample() {
        Log.e("test", "Sample: getNewSample() --before");

        ArrayList<String> channels = new ArrayList<>();
        channels.addAll(Arrays.asList("Left", "Right"));

        boolean done = false;

        while (!done) {

            Random randomGenerator = new Random();

            int newFrequency = randomFrequency(randomGenerator, numberOfFrequencies, chosenFrequencies);
            String newChannel = randomChannel(randomGenerator, channels);

            Log.e("test", "Sample: getNewSample() --while loop // msg: start loop with randomly picked frequency and channel");


            for (int i = 0; i < 2; i++) {

                Sample temporarySample = new Sample(newFrequency, newChannel);

                if (!checkIfExists(temporarySample, samplesList)) {
                    addToLists(temporarySample);
                    done = true;
                    break;
                } else {
                    if (newChannel.equals("Left")) {
                        newChannel = "Right";
                    } else if (newChannel.equals("Right")) {
                        newChannel = "Left";
                    }
                }
            }

            Log.e("test", "Sample: getNewSample() --while loop // msg: temporary sample taken from constructor // values: frequency = " + newFrequency + " channel = " + newChannel);

        }

    }



    private int randomFrequency(Random randomGenerator, int numberOfFrequencies, ArrayList<Integer> chosenFrequencies) {
        Log.e("test", "Sample: randomFrequency() --before");

        int indexF = randomGenerator.nextInt(numberOfFrequencies);
        int newFrequency = chosenFrequencies.get(indexF);

        Log.e("test", "Sample: randomFrequency() --after");

        return newFrequency;
    }

    private String randomChannel(Random randomGenerator, ArrayList<String> channels) {
        Log.e("test", "Sample: randomChannel() --before");

        int indexC = randomGenerator.nextInt(2);
        String newChannel = channels.get(indexC);

        Log.e("test", "Sample: randomChannel() --after");

        return newChannel;
    }


    private boolean checkIfExists(Sample sample, ArrayList<Sample> samplesList) {
        Log.e("test", "Sample: checkIfExists() --before");

        int m = 0;

        for (int i = 0; i < samplesList.size(); i++) {

            if (samplesList.get(i).toString().equals(sample.toString())) {
                m++;
            }
        }

        if (m > 0) {
            Log.e("test", "Sample: checkIfExists() --after // msg: sample already exists");
            return true;
        } else {
            Log.e("test", "Sample: checkIfExists() --after // msg: sample doesn't exist");
            return false;
        }

    }

    private void addToLists(Sample temporarySample) {
        Log.e("test", "Sample: addToLists() --before");

        samplesList.add(temporarySample);
        frequenciesList.add(temporarySample.getFrequency());
        channelsList.add(temporarySample.getChannel());

        Log.e("test", "Sample: addToLists() --after");
    }

    private double getFrequency() {
        return frequency;
    }

    private String getChannel() {
        return channel;
    }

    public ArrayList<Double> getFrequenciesList() {
        return frequenciesList;
    }

    public ArrayList<String> getChannelsList() {
        return channelsList;
    }

    @Override
    public String toString() {
        return "\n" + frequency + "\t" + channel;
    }
}