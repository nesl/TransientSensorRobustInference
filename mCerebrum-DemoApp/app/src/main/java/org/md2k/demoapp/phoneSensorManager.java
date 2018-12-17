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

package org.md2k.demoapp;

// Android imports
import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

// Java imports
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

// DataKitAPI imports
import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.application.Application;
import org.md2k.datakitapi.source.application.ApplicationBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;

/**
 * This application demonstrates how to connect to and make API calls against DataKit via DataKitAPI.
 */

/*
  BASICALLY, the plan is to use this class as the main sensing controller.
  When inserting data, you will need to register a listener for each bluetooth device as well
  When querying data, you will need a file for each different type of sensor source, each one defining buildDataSource
  You will have to alter DataSourceType to include these new sources - i.e. pillowMotionSensor, blanketMotionSensor, etc



 */
public class phoneSensorManager extends Thread implements SensorEventListener {

    //TODO: This class is connected through the main activity, so the datapackager is different than the BT devices

    // Variables for accelerometer data
    private SensorManager mSensorManager;
    private Sensor mSensor = null;
    private long lastSaved;
    private double minSampleTime = 1000; // 1 second
    public static final double GRAVITY = 9.81;

    String dataKitId = "";

    // Variables for DataKit objects
    private DataKitAPI datakitapi;
    private DataSourceClient regDataSourceClient = null;
    private DataSourceClient subDataSourceClient = null;
    private ArrayList<DataType> dataTypeQuery = null;
    private DataTypeDoubleArray dataTypeDoubleArray;
    private DataTypeLong querySize;
    //private Boolean isHF;

    String mDevicePurpose = "PhoneSensor";
    DataPackager mDataPackager;

    long totalInserts = 0;

    long startTime = 0;


    public final String TAG = "DBG-phoneSensorMngr: ";

    public phoneSensorManager(SensorManager smgr, DataKitAPI dkapi, DataPackager dataPackager) {
        mSensorManager = smgr;
        datakitapi = dkapi;
        mDataPackager = dataPackager;
        Log.d(TAG, "Created");
    }

    @Override
    public void run() {
        // Gets sensor service


        // Sets the desired sensor
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastSaved = DateTime.getDateTime();

        //Connect to the datakit
        connectDataKit();


    }

    /**
     * Builds a new application object. This object represents this application and helps
     * identify data within the database.
     * @return Application datatype
     */
    public Application buildApplication(String id) {
        dataKitId = id;
        return new ApplicationBuilder().setId(id).build();
    }

    /**
     * Builds a data source object representing the sensor and application creating the data source.
     * This demo application only uses the accelerometer, but any available sensor, hardware or software
     * based, can be used.
     * @param application Application object representing this application.
     * @return A data source builder
     */
    public DataSourceBuilder buildDataSource(Application application) {
        return new DataSourceBuilder().setType(DataSourceType.ACCELEROMETER).setApplication(application);
    }

    /**
     * Builds a data source object representing the sensor. In this application this is only used for
     * registering the data source. It could be used to find all data sources of the set type independent
     * from the application.
     * @return A data source builder
     */
    public DataSourceBuilder buildDataSource() {
        return new DataSourceBuilder().setType(DataSourceType.ACCELEROMETER);
    }

    /**
     * Switch mechanism for switching between <code>insertData()</code> and <code>insertHFData</code>.
     * This is not necessary in a typical application, but used to demonstrate the difference between
     * these two data insert methods.
     * @param view hfSwitch
     */
    /*public void setHFSwitch(View view) {
        isHF = hfSwitch.isChecked();
    }*/

    /**
     * Controls behavior of connecting and disconnecting from DataKit. DataKit must be connected before
     * any other methods are called. Not doing so will result in <code>DataKitException</code>s which
     * must be handled. <code>DataKitAPI.connect(new OnConnectionListener()</code> registers a callback
     * interface so that this application and DataKit can communicate.
     */
    public void connectDataKit() {
        try {
            if (datakitapi.isConnected()) {
                disconnectDataKit();
            } else
                datakitapi.connect(new OnConnectionListener() {
                    @Override
                    public void onConnected() {
                        Log.d(TAG, "Connected to Datakit");

                        //Register data source with datakit
                        registerDataSource();
                        //Subscribe to updates made in the database
                        subscribeDataSource();
                        //Begin listening for updates
                        beginInsertingData();
                    }
                });
        } catch (DataKitException ignored) {
            Log.d(TAG, ignored.getMessage());
        }
    }

    public void stopUpdates() {
        unsubscribeDataSource();
        unregisterDataSource(false);
    }

    /**
     * Before DataKit is disconnected, all data sources must be unsubscribed and unregistered.
     * Exception checking is not required when calling <code>DataKitAPI.disconnect()</code>.
     */
    public void disconnectDataKit() {
        datakitapi.disconnect();
        regDataSourceClient = null;
        subDataSourceClient = null;
        dataTypeQuery = null;
        Log.d(TAG, " Data Kit disconnected");
    }

    /**
     * Registers the data source with DataKit. DataKit can not receive data from a data source until
     * that data source is registered.  This is called when the datakit is connected (isConnected)
     */
    public void registerDataSource() {
        try {
            if (!(datakitapi.isConnected())) {
                Log.d(TAG, " Datakit not connected");
            }
            else if (regDataSourceClient == null) {
                regDataSourceClient = datakitapi.register(buildDataSource());
                Log.d(TAG, regDataSourceClient.getDataSource().getType() +
                        " registration successful");
            } else {
                unregisterDataSource(false);
            }
        } catch (DataKitException ignored) {
            unregisterDataSource(true);
            Log.d(TAG, ignored.getMessage());
        }
    }

    /**
     * Unregistering the sensor listener stops the data collection. Unsubscibing the data source removes
     * any remaining callbacks. Then unregistering the data source from DataKit can be done. It is possible
     * to unregister a subset of registered data sources, but <code>DataKitAPI.unregister()</code> only
     * takes one <code>DataSourceClient</code> as a parameter so the method would need to be called
     * individually for each data source.
     * @param failed Whether this method was called because data source registration failed or not.
     *               Only used for error message handling.
     */
    public void unregisterDataSource(boolean failed) {
        try {
            unregisterListener();
            unsubscribeDataSource();
            datakitapi.unregister(regDataSourceClient);
            regDataSourceClient = null;
        } catch (DataKitException ignored){
            Log.d(TAG, ignored.getMessage());
        }
        if (failed)
            Log.d(TAG, regDataSourceClient.getDataSource().getType() +
                    " registration failed");
        else
            Log.d(TAG, "Data source unregistered");
    }

    /**
     * Unregisters the sensor listener. To pass <code>this</code> as the sensor listener, the class
     * must implement <code>SensorEventListener</code>.
     */
    public void unregisterListener() {
        mSensorManager.unregisterListener(this, mSensor);
    }

    /**
     * Subscribing a data source registers a callback interface that returns the data received by
     * the database. In this implementation <code>dataSourceClients</code> only has one node because
     * the only sensor that is registered is the accelerometer. In production the resulting arraylist
     * is likely to have many more nodes.
     */
    public void subscribeDataSource (){
        ArrayList<DataSourceClient> dataSourceClients;
        try {
            if (subDataSourceClient == null) {
                dataSourceClients = datakitapi.find(buildDataSource(buildApplication(dataKitId)));
                if(dataSourceClients.size() == 0) {
                    Log.d(TAG, "Data Source Not Registered");
                } else {
                    subDataSourceClient = dataSourceClients.get(0);
                    // gets index 0 because there should only be one in this application
                    datakitapi.subscribe(subDataSourceClient, subscribeListener);;
                    Log.d(TAG, "Data Source Successfully subscribed");
                }
            } else {
                unsubscribeDataSource();
            }
        } catch (DataKitException ignored) {
            subDataSourceClient = null;
            Log.d(TAG, ignored.getMessage());
        }
    }

    /**
     * <code>OnReceiveListener</code> used for subscription. This demo application simply displays the
     * data to an output text view.
     */
    public OnReceiveListener subscribeListener = new OnReceiveListener() {
        @Override
        public void onReceived(DataType dataType) {
            printSample((DataTypeDoubleArray) dataType);
        }
    };

    /**
     * Unsubscribing a data source only unregisters the callback interface. Data is still collected
     * and inserted. Nullifying the associated <code>DataSourceClient</code> variable prevents conflicts
     * if the data source is subscribed again.
     */
    public void unsubscribeDataSource() {
        try {
            datakitapi.unsubscribe(subDataSourceClient);
            subDataSourceClient = null;
            Log.d(TAG, "Successfully Unsubscribed");
        } catch (DataKitException ignored) {
            Log.d(TAG, ignored.getMessage());
        }
    }

    /**
     * In this implementation, pressing the insert button only registers the sensor listener. All data
     * collection occurs in the overridden <code>onSensorChanged()</code> method.
     * <li><code>SENSOR_DELAY_NORMAL</code> is 6 hertz</li>
     *     <li><code>SENSOR_DELAY_UI</code> is 16 hertz</li>
     *     <li><code>SENSOR_DELAY_GAME</code> is 50 hertz</li>
     *     <li><code>SENSOR_DELAY_FASTEST</code> is 100 hertz</li>
     */
    public void beginInsertingData(){
        if(regDataSourceClient != null) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Log.d(TAG, "No insertion, Data Source Not Registered");
        }
    }

    /**
     * To limit the frequency of samples a minimum sample time of 100 milliseconds. The accelerometer
     * is sampled at 10 hertz and is adjusted for gravity before being passed into a new
     * <code>DataTypeDoubleArray</code>. An appropriate <code>DataType</code> for the sensor should
     * be used. For example, motion sensors should use <code>DataTypeDoubleArray</code> because they
     * return an array of double values. The proximity sensor and other environmental sensors should
     * use <code>DataTypeDouble</code>, as they return an array with only one value.
     * @param event Value of the new accelerometer data.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = DateTime.getDateTime();
        if(startTime == 0) {
            startTime = curTime;
        }

        if ((double)(curTime - lastSaved) > minSampleTime) {
            lastSaved = curTime;
            double[] samples = new double[3];
            samples[0] = event.values[0] / GRAVITY; // X axis
            samples[1] = event.values[1] / GRAVITY; // Y axis
            samples[2] = event.values[2] / GRAVITY; // Z axis
            dataTypeDoubleArray = new DataTypeDoubleArray(curTime, samples);
            //if (isHF)
            //    insertHFData(dataTypeDoubleArray);
            //else
            insertData(dataTypeDoubleArray);
            //Log.d(TAG, "Sensor Changed.");

            //SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
           // String timeString = formatter.format(new Date(curTime));
            //String dataString = timeString + "," + samples[0] + "," + samples[1] + "," + samples[2];
            String dataString = System.currentTimeMillis() + "," + samples[0] + "," + samples[1] + "," + samples[2];
            mDataPackager.exportData(mDevicePurpose, dataString);
        }
    }

    /**
     * Data is inserted into the database using the associated <code>DataSourceClient</code>, which
     * provides relevant metadata, and the data sample itself. This demo also displays the data that
     * is inserted, but that is not necessary. The standard insertion method adds rows to a database
     * that is stored in <code>Android/Data/org.md2k.datakit/files/database.db</code> by default.
     * Using <code>insertHighFrequency()</code> is recommended for sensors that produce a lot of data,
     * such as the accelerometer, to help manage the size of the database.
     * @param data Data to insert into the database
     */
    public void insertData(DataTypeDoubleArray data) {
        totalInserts++;
        try {
            datakitapi.insert(regDataSourceClient, data);
            //printSample(data);
        } catch (DataKitException ignored) {
            Log.e("database insert", ignored.getMessage());
            Log.d(TAG, ignored.getMessage());
        }
    }

    /**
     * Data is passed to <code>insertHighFrequency()</code> similarly to <code>insert()</code>. The
     * difference is that the high frequency data is stored in a gzipped csv file that is stored in
     * <code>Android/Data/org.md2k.datakit/files/raw/</code> by default. Recording the data in this
     * way helps reduce the amount of resources DataKit requires.
     * @param data Data to record.
     */
    public void insertHFData(DataTypeDoubleArray data) {
        try {
            datakitapi.insertHighFrequency(regDataSourceClient, data);
            //printSample(data, subOutput);
        } catch (DataKitException ignored) {
            Log.e("hf data insert", ignored.getMessage());
            Log.d(TAG, ignored.getMessage());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int x) {}

    /**
     * Queries the database for data matching the given data source and parameters.
     *
     * This demo application only shows one query method. In this example, an arraylist of
     * <code>DataSourceClient</code>s matching the desired data source is produced using the
     * <code>DataKitAPI.find()</code> method. <code>DataKitAPI.find()</code> takes a
     * <code>DataSourceBuilder</code> object as a parameter. This <code>DataSourceBuilder</code> can
     * be configured for a particular data source or application. The query call is performed by passing
     * a <code>DataSourceClient</code> and an integer representing the "last n samples" that were collected.
     * Using this query method returns the last n rows matching the given data source in the database,
     * where n is the number of samples to return.
     * <p>
     * Other query methods allow queries for a given time window:
     * <p><code>DataKitAPI.query(DataSourceClient dataSourceClient, long starttimestamp, long endtimestamp)</code></p>
     * Or querying via primary key, where lastSyncedKey is the primary key and limit is the number of
     * rows to return:
     * <p><code>queryFromPrimaryKey(DataSourceClient dataSourceClient, long lastSyncedKey, int limit)</code></p>
     * </p>
     * All <code>DataKitAPI.query()</code> methods return an arraylist of <code>DataType</code> objects.
     * <code>DataKitAPI.queryFromPrimaryKey</code> returns an arraylist of <code>RowObject</code>s.
     *
     * <p>
     * Another useful method demonstrated here is <code>DataKitAPI.querySize()</code> which returns
     * the number of rows in the database as a <code>DataTypeLong</code> object.
     * </p>
     */
    public void query(){
        Log.d("QUERY-Phone: ", "Total Inserts: " + totalInserts);
        try {
            long endTime = DateTime.getDateTime();

            ArrayList<DataSourceClient> dataSourceClients = datakitapi.find(buildDataSource(buildApplication(dataKitId)));
            if (dataSourceClients.size() == 0) {
                Log.d(TAG, "Query failed - Data Source not registered");
            } else {
                querySize = datakitapi.querySize();
                dataTypeQuery = datakitapi.query(dataSourceClients.get(0), startTime, endTime);
                if (dataTypeQuery.size() == 0) {
                    Log.d(TAG, "query size zero");
                } else
                    printQuery(dataTypeQuery);

            }
        } catch (DataKitException ignored) {
            Log.e("query", ignored.getMessage());
            dataTypeQuery = null;
            Log.d(TAG, ignored.getMessage());
        }
    }

    /**
     * This is an example of how a query result might be printed.
     * @param query Query result
     */
    public void printQuery (ArrayList<DataType> query) {
        StringBuilder message = new StringBuilder();
        message.append("Query Size is ").append(querySize.getSample()).append("\n");
        message.append("[X axis, Y axis, Z axis]\n");
        for (DataType data : query) {
            if (data instanceof DataTypeDoubleArray) {
                DataTypeDoubleArray dataArray = (DataTypeDoubleArray)data;
                double[] sample = dataArray.getSample();
                String dataString = "[" + sample[0] + ", " + sample[1] + ", " + sample[2] + "]\n";
                message.append(dataString);
            }
        }
        Log.d("QUERY-Phone: ", message.toString());
    }

    /**
     * Prints a data sample. Used for displaying inserted data and data received when a data source
     * is subscribed.
     * @param data Data sample to print.
     */
    public void printSample(DataTypeDoubleArray data) {

        String timeString = DateFormat.format("HH:mm:ss", new Date(data.getDateTime())).toString();

        double[] sample = data.getSample();
        Log.d("OutputDataPhone", timeString + " [" + sample[0] + ", " + sample[1] + ", " + sample[2] + "]");
    }

}
