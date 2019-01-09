package org.md2k.motionsense.phone;

/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.mcerebrum.core.data_format.DataFormat;
import org.md2k.motionsense.ActivityMain;
import org.md2k.motionsense.exportRunnable;
import org.md2k.motionsense.exporter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * This class manages the gyroscope sensor on the device.
 *
 */
public class Gyroscope implements SensorEventListener {
    private static final String SENSOR_DELAY_NORMAL = "6";
    private static final String SENSOR_DELAY_UI = "16";
    private static final String SENSOR_DELAY_GAME = "50";
    private static final String SENSOR_DELAY_FASTEST = "100";

    public static final String INTENT_DATA = "INTENT_DATA";


    private static final String TAG = "DBG-GYRO";

    //Timestamp at which the last Gyroscope data was appended to the CSV file
    long lastSaved;

    long filterDataMinTime = 1000;  //Time in between each frequency check (meant mainly for output debugging)

    //This is the Android sensor manager for getting sensor updates
    private SensorManager mSensorManager;

    String frequency = "";

    //Timestamp at which the last frequency check was outputted (meant mainly for output debugging)
    long lastFrequencyOutput;

    Context ctx;

    //Exporter class for exporting data to CSV
    exporter exp;
    //Service for executing the data export thread
    ExecutorService executor;

    //Count of how much data was sampled in this time frame (filterDataMinTime)
    private long dataCount = 0;

    private long entryDelay = 9; //9 milliseconds have to pass between every sample

    //Queue for writing to a file
    List<exportRunnable> writeQueue;

    /**
     * Constructor
     *
     * @param context Android context
     */
    public Gyroscope(Context context, List<exportRunnable> wQueue) {
        //super(context, DataSourceType.GYROSCOPE);
        frequency = SENSOR_DELAY_FASTEST;
        ctx = context;
        writeQueue = wQueue;
    }


    /**
     * Called when there is a new sensor event.
     * @param event event that triggered the method call
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //long curTime = System.currentTimeMillis();

        //Why not divide by 1000000 directly?  http://fixermark.blogspot.com/2014/06/quirkiness-of-android-sensor-library.html
        long curTimeNanos = event.timestamp;
        long curTime = (new Date()).getTime()
                + (curTimeNanos - System.nanoTime()) / 1000000L;

        //If sufficient time has passed since the last append to the CSV file, we can do another
        // This is just for throttling the amount of data sampled - some phones sample at extremely
        //  high frequency (~200Hz) while some are at lower frequency (~100Hz)
        if((curTime - lastSaved) > entryDelay) {
            lastSaved = curTime;



            double[] samples = new double[3];
            samples[DataFormat.Gyroscope.X] = event.values[0];
            samples[DataFormat.Gyroscope.Y] = event.values[1];
            samples[DataFormat.Gyroscope.Z] = event.values[2];
            String message = curTime + "," + samples[0] + "," + samples[1] + "," + samples[2];

            //This is meant for output debugging - just to check how much data is sampled every second
            if ((curTime - lastFrequencyOutput) > filterDataMinTime) {
                lastFrequencyOutput = curTime;
                Log.d(TAG, " Sampling at " + Long.toString(dataCount) + " samples");

                Intent intent = new Intent(INTENT_DATA);
                intent.putExtra("phone-src", "Phone-GYRO");
                intent.putExtra("phone-freq", Long.toString(dataCount));
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent);

                dataCount = 0;
                Log.d(TAG, message);
            }

            //Append the data to a queue for exporting
            exportRunnable dataToQueue = new exportRunnable("Phone-GYRO", message);
            writeQueue.add(dataToQueue);


            dataCount += 1;

        }
    }

    /**
     * Called when the accuracy of this sensor changes.
     *
     * @param sensor sensor object for this sensor
     * @param accuracy Accuracy of the sensor reading
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Unregisters the listener for this sensor
     */
    public void unregister() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    //Register the Sensor
    public void register() {
        //super.register(dataSourceBuilder, newCallBack);
        mSensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        switch (frequency) {
            case SENSOR_DELAY_UI:
                //filterDataMinTime = 1000.0 / (SENSOR_DELAY_UI_DOUBLE + EPSILON_UI);
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
                break;
            case SENSOR_DELAY_GAME:
                //filterDataMinTime = 1000.0 / (SENSOR_DELAY_GAME_DOUBLE + EPSILON_GAME);
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
                break;
            case SENSOR_DELAY_FASTEST:
                //filterDataMinTime = 1000.0 / (SENSOR_DELAY_FASTEST_DOUBLE + EPSILON_FASTEST);
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case SENSOR_DELAY_NORMAL:
                //filterDataMinTime = 1000.0 / (SENSOR_DELAY_NORMAL_DOUBLE + EPSILON_NORMAL);
                mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                break;

        }
        lastFrequencyOutput=System.currentTimeMillis();
        lastSaved = System.currentTimeMillis();
    }
}

