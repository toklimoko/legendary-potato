package com.tomek.audiometr;

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
    private ArrayList<String> channels = new ArrayList<String>();

    public ArrayList<Sample> samplesList = new ArrayList<>();
    public ArrayList<String> sampleData = new ArrayList<>();

    private Random randomGenerator;
    private Sample temporarySample;

    private int indexF = 0;
    private int indexC = 0;
    private int newFrequency = 0;
    private String newChannel = "";
    private boolean k = false;
    private boolean done = false;
    private int m;


    public Sample(int numberOfFrequencies, ArrayList<Integer> chosenFrequencies) {
        this.numberOfFrequencies = numberOfFrequencies;
        this.chosenFrequencies = chosenFrequencies;

        Log.e("test", "Sample: Sample(numberOfFrequencies,chosenFrequencies) // values: numberOfFrequencies = " + numberOfFrequencies
                + "; chosenFrequencies = " + chosenFrequencies);
    }

    public Sample(double frequency, String channel) {
        this.frequency = frequency;
        this.channel = channel;

        Log.e("test", "Sample: Sample(frequency,channel) // values: frequency = " + frequency
                + "; channel = " + channel);
    }


    public ArrayList<String> getNewSample() {
        Log.e("test", "Sample: getNewSample() --before");

        channels = new ArrayList<>();
        channels.addAll(Arrays.asList("Left", "Right"));

        done = false;
        k = false;

        if (samplesList.size() + 1 > numberOfFrequencies * 2) {

            Log.e("test", "Sample: getNewSample() // msg: all attempts made, reuturn null // values: samplesList.size() =  "
                    + (samplesList.size() + 1) + "; numberOfFrequencies = " + (numberOfFrequencies * 2));

            return null;
        }


        while (!done) {

            randomGenerator = new Random();

            randomFrequency();
            randomChannel();
            Log.e("test", "Sample: getNewSample() --while loop // msg: start loop with randomly picked frequency and channel");

            temporarySample = new Sample(newFrequency, newChannel);

            Log.e("test", "Sample: getNewSample() --while loop // msg: temporary sample taken from constructor // values: frequency = " + newFrequency + " channel = " + newChannel);

            if (!checkIfExists(temporarySample)) {
                addToLists();
                done = true;
            } else {

                if (newChannel.equals("Left")) {
                    newChannel = "Right";
                } else if (newChannel.equals("Right")) {
                    newChannel = "Left";
                }

                temporarySample = new Sample(newFrequency, newChannel);
                if (!checkIfExists(temporarySample)) {
                    addToLists();
                    done = true;
                }

                Log.e("test", "Sample: getNewSample() --while loop // msg: test if channels change helps");
            }
        }
        Log.e("test", "Sample: getNewSample() --while loop --after // msg: sending back // values: frequency = " + newFrequency + " channel = " + newChannel);

        return sampleData;
    }

    public ArrayList<String> addToLists() {
        Log.e("test", "Sample: addToLists() --before");

        samplesList.add(temporarySample);
        sampleData.add(String.valueOf(newFrequency));
        sampleData.add(newChannel);

        Log.e("test", "Sample: addToLists() --after");
        return sampleData;
    }

    public int randomFrequency() {
        Log.e("test", "Sample: randomFrequency() --before");

        indexF = randomGenerator.nextInt(numberOfFrequencies);
        newFrequency = chosenFrequencies.get(indexF); //losuj częstotliwość

        Log.e("test", "Sample: randomFrequency() --after");

        return newFrequency;
    }

    public String randomChannel() {
        Log.e("test", "Sample: randomChannel() --before");

        indexC = randomGenerator.nextInt(2);
        newChannel = channels.get(indexC); //losuj częstotliwość

        Log.e("test", "Sample: randomChannel() --after");

        return newChannel;
    }


    public boolean checkIfExists(Sample sample) {
        Log.e("test", "Sample: checkIfExists() --before");

        m = 0;

        for (int i = 0; i < samplesList.size(); i++){
            Log.e("test", "Sample: checkIfExists() --for loop // values: samplesList.toString() = " + samplesList.toString() + "; temporarySample.toString() = " + temporarySample.toString());

            if (samplesList.get(i).toString().equals(sample.toString())){
                m++;
            }
        }

        if (m > 0){
            Log.e("test", "Sample: checkIfExists() --after // msg: sample already exists");
            return true;
        }else{
            Log.e("test", "Sample: checkIfExists() --after // msg: sample doesn't exist");
            return false;
        }

    }

    public ArrayList<Sample> getSamplesList() {
        return samplesList;
    }

    public void setSamplesList(ArrayList<Sample> samplesList) {
        this.samplesList = samplesList;
    }

    @Override
    public String toString() {
        return frequency + '\'' + channel;
    }
}