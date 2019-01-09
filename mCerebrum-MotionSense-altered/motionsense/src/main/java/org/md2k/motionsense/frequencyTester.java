package org.md2k.motionsense;

import android.util.Log;

//This class measures the frequency of a sensor
public class frequencyTester {

    String testname = "";
    long lastTimeRecorded = 0;
    long count = 0;
    long delay = 1000;


    frequencyTester(String testName, long lastTime, long delay) {
        lastTimeRecorded = lastTime;
        this.delay = delay;
        this.testname = testName;
    }

    public void checkTime() {
        long currentTime = System.currentTimeMillis();
        count++;
        if(lastTimeRecorded + delay > currentTime) {
            lastTimeRecorded = currentTime;
            Log.d("abcde", testname + " : " + Long.toString(count) + " every " + Long.toString(delay) + " ms");
            count = 0;
        }
    }
}
