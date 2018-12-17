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
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

// Java imports
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

// DataKitAPI imports
import org.md2k.datakitapi.DataKitAPI;

/**
 * This application demonstrates how to connect to and make API calls against DataKit via DataKitAPI.
 */

/*
  BASICALLY, the plan is to use this class as the main sensing controller.
  When inserting data, you will need to register a listener for each bluetooth device as well
  When querying data, you will need a file for each different type of sensor source, each one defining buildDataSource
  You will have to alter DataSourceType to include these new sources - i.e. pillowMotionSensor, blanketMotionSensor, etc



 */
public class MainActivity extends AppCompatActivity {

    //TODO: Add remove capability from the phone for BLE devices

    // Variables for the user view
    private TextView startButton;
    private TextView currentCountView;
    private ListView mListView;
    private ListView classListView;
    ArrayAdapter<String> mAdapter;

    ArrayAdapter<String> mClassifierAdapter;
    private ArrayList<String> mClassifiers = new ArrayList<String>();

    private LinearLayoutManager mRecentLayoutManager;
    private ArrayList<String> mDeviceItems = new ArrayList<String>();

    private final String TAG = "DBG-MainActivity: ";

    Context mCTX = this;

    phoneSensorManager mPhoneSensorManager = null;

    DataPackager mDataPackager = new DataPackager();


    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    String appName = "";

    DatabaseReference mDatabase = null;

    /**
     * Upon creation, the buttons, <code>SensorManager</code>, <code>Sensor</code>, and current datetime
     * are initialized. An instance of DataKit is also retrieved/created.
     * @param savedInstanceState Previous state of the application if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(mCTX);

        // Initializes button variables
        startButton = findViewById(R.id.beginButton);
        mListView = findViewById(R.id.listview);
        classListView = findViewById(R.id.classList);
        currentCountView = findViewById(R.id.count_text);

        //Check to see if this app can get location access
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect beacons.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        requestExternalWriteAccess();

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiver, new IntentFilter("DEVICE_FOUND"));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiverRemove, new IntentFilter("DEVICE_REMOVE"));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiverCount, new IntentFilter("UPDATE_COUNT"));
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(broadcastReceiverClassifier, new IntentFilter("CLASSIFY"));

        for(int i = 1; i < 8; ++i) {
            mClassifiers.add("Classifier " + i + ":");
        }

        mAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mDeviceItems);

        mClassifierAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                mClassifiers);

        mListView.setAdapter(mAdapter);
        classListView.setAdapter(mClassifierAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                //String toRemove = value.substring(value.indexOf(" "));
                Log.d(TAG, "CLICKED " + value);
                Intent serviceIntent = new Intent(mCTX, BTEntryManager.class);
                serviceIntent.putExtra("commandBT", value);
                startService(serviceIntent);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        stopServices();

    }



    public void requestExternalWriteAccess() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
    }


    public void startServices(View view) {
        Log.d(TAG, "Starting Services");

        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        DataKitAPI datakitapi = null;
        appName = MainActivity.this.getPackageName();

        mPhoneSensorManager = new phoneSensorManager(mSensorManager, datakitapi.getInstance(this), mDataPackager);
        mPhoneSensorManager.buildApplication(appName);
        mPhoneSensorManager.start();


        //Intent intent = new Intent(this, phoneSensorManager.class);
        //startService(intent);
        mDeviceItems.clear();
        mAdapter.notifyDataSetChanged();
        checkBT();  //Start Bluetooth manager

    }

    public void stopServices() {

        mPhoneSensorManager.query();
        mPhoneSensorManager.stopUpdates();
        //mPhoneSensorManager.disconnectDataKit(); //TODO: BE CAREFUL NOT TO DISCONNECT DATAKIT UNTIL VERY END
        mPhoneSensorManager.interrupt();
    }

    public void checkBT() {
        Log.d(TAG, "Starting Bluetooth Entry Manager");
        Intent intent = new Intent(this, BTEntryManager.class);
        intent.putExtra("appname", appName);
        startService(intent);
    }

    public void stopTracking(View view) {

        stopServices();

        Log.d(TAG, "STOPPING Bluetooth Entry Manager");
        Intent intent = new Intent(this, BTEntryManager.class);

        stopService(intent);

        mDeviceItems.clear();
        mDeviceItems.add("Stopped Tracking.");
        mAdapter.notifyDataSetChanged();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String devPurpose = intent.getStringExtra("device");  //get the type of message from MyGcmListenerService 1 - lock or 0 -Unlock

            Log.d(TAG, "FOUND: " + devPurpose);

            mDeviceItems.add("Added " + devPurpose);
            mAdapter.notifyDataSetChanged();

        }
    };

    private BroadcastReceiver broadcastReceiverRemove = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String devPurpose = intent.getStringExtra("device");  //get the type of message from MyGcmListenerService 1 - lock or 0 -Unlock

            Log.d(TAG, "REMOVE : " + devPurpose);

            mDeviceItems.remove("Added " + devPurpose);
            mAdapter.notifyDataSetChanged();

        }
    };

    private BroadcastReceiver broadcastReceiverCount = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long currentCount = intent.getLongExtra("count", -1);
            Log.d(TAG, "CURRENT COUNT: " + currentCount);
            currentCountView.setText("Count: " + currentCount);
        }
    };

    private BroadcastReceiver broadcastReceiverClassifier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String output = intent.getStringExtra("classification");
            int classifier_num = intent.getIntExtra("classifier_num", 0);
            if(classifier_num > 0) {
                Log.d(TAG, "Received Classification: " + classifier_num + " - " + output);

                String editedItem = "Classifier " + classifier_num + ": " + output;
                mClassifiers.set(classifier_num-1, editedItem);
                mClassifierAdapter.notifyDataSetChanged();


                mDatabase.child("c_results").child(Long.toString(System.currentTimeMillis())).setValue(editedItem);
            }


            //currentCountView.setText("Count: " + currentCount);
        }
    };

    public void setGroundTruth(View view) {
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        Log.d(TAG, "Clicked " + buttonText);

        Intent serviceIntent = new Intent(mCTX, BTEntryManager.class);
        serviceIntent.putExtra("setGT", buttonText);
        startService(serviceIntent);

    }

    @Override  //Android now needs Coarse access location to do BT scans - Idk why but it wont work otherwise
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permissions", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }
                    });
                    builder.show();
                }
                return;
            }
        }
    }


}