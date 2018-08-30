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
    private int numberOfSamples;
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


    public Sample(int numberOfSamples, ArrayList<Integer> chosenFrequencies) {
        this.numberOfSamples = numberOfSamples;
        this.chosenFrequencies = chosenFrequencies;

        Log.e("test", "sample - konstruktor nc");

    }

    public Sample(double frequency, String channel) {
        this.frequency = frequency;
        this.channel = channel;

        Log.e("test", "sample - konstruktor fc");

    }


    public ArrayList<String> getNewSample() {

        channels = new ArrayList<String>();
        channels.addAll(Arrays.asList("Left", "Right"));

        done = false;
        k = false;

        if (samplesList.size() + 1 > numberOfSamples * 2) {


            Log.e("test", "samplesList.size() =  " + (samplesList.size() + 1) + " numberOfSamples = " + (numberOfSamples * 2));

            return null;
        }


        while (!done) {

            randomGenerator = new Random();

            randomFrequency();
            randomChannel();
            Log.e("test", "while start");

            temporarySample = new Sample(newFrequency, newChannel);

            Log.e("test", "temporary sample taken from constructor - RANDOM: f = " + newFrequency + " ch = " + newChannel);


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

                Log.e("test", "if - exists, else > k, random Frequency - po");

//                Log.e("test", "done = true ");

            }
        }
        Log.e("test", "sampleData prepared to be sent - SENDING:  f = " + newFrequency + " ch = " + newChannel);

        return sampleData;
    }

    public ArrayList<String> addToLists() {

        samplesList.add(temporarySample);
        sampleData.add(String.valueOf(newFrequency));
        sampleData.add(newChannel);

        return sampleData;
    }

    public int randomFrequency() {

        indexF = randomGenerator.nextInt(numberOfSamples);
        newFrequency = chosenFrequencies.get(indexF); //losuj częstotliwość

        Log.e("test", "random Frequency in Sample ");


        return newFrequency;
    }

    public String randomChannel() {

        indexC = randomGenerator.nextInt(2);
        newChannel = channels.get(indexC); //losuj częstotliwość

        Log.e("test", "Random channel in Sample ");


        return newChannel;
    }


    public boolean checkIfExists(Sample sample) {

        Log.e("test", "samplesList size w checkIfExists = " + samplesList.size());
        Log.e("test", "samplesList ma sampla? " + samplesList.contains(sample));
        Log.e("test", "samplesList ma temporarySampla? " + samplesList.contains(temporarySample));

//        if (samplesList.contains(sample)) {
//            Log.e("test", "exists");
//
//            return true;
//        }
//
//        Log.e("test", "doesnt exist");
//
//        return false;
        int k = 0;

        for (int i = 0; i < samplesList.size(); i++){


            Log.e("test", "wyswietlanie kolejnych obiektow z listy: " + samplesList.toString());
            Log.e("test", "obiekt temporary: " + temporarySample.toString());

            if (samplesList.get(i).toString().equals(sample.toString())){
                k++;
            }
        }

        if (k > 0){
            Log.e("test", "exists");
            return true;
        }else{
            Log.e("test", "doesnt exist");
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