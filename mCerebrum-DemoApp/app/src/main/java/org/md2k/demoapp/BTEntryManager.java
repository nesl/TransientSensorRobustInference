package org.md2k.demoapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.demoapp.classifiers.tfclassifier_1;
import org.md2k.demoapp.classifiers.tfclassifier_1_s;
import org.md2k.demoapp.classifiers.tfclassifier_2_m;
import org.md2k.demoapp.classifiers.tfclassifier_s1m;
import org.md2k.demoapp.classifiers.tfclassifier_s1s2;
import org.md2k.demoapp.classifiers.tfclassifier_s1s2m;
import org.md2k.demoapp.classifiers.tfclassifier_s2m;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BTEntryManager extends Service {

    private final boolean DEBUG = true;
    private final String TAG = "DBG-BTEntryMngr";


    private BluetoothAdapter mBtAdapter = null;
    private boolean hasBTCapability = false;
    private int mNumDevs = 0;
    private boolean mScanning = false;
    private BluetoothManager mBluetoothManager = null;

    private List<String> allowedDevices;
    private List<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>();
    private int maxDeviceConnections = 2;

    private long scanStartTime = 0;
    private boolean startedScanning = false;
    private Context mContext = null;

    private List<SensorTag> stThreads = new ArrayList<SensorTag>();
    private List<MotionSense> msThreads = new ArrayList<>();
    private List<androidWear> awThreads = new ArrayList<>();

    DataPackager mDataPackager = new DataPackager();

    private String UUIDStr = "71b37966-2466-45b7-ae2c-f42851fcac8e";  //UUID for communicating with android wear

    private boolean doneConnecting = false;
    private tfclassifier_1 classifier1;
    private tfclassifier_1_s classifier2;
    private tfclassifier_2_m classifier3;
    private tfclassifier_s1s2 classifier4;
    private tfclassifier_s1m classifier5;
    private tfclassifier_s2m classifier6;
    private tfclassifier_s1s2m classifier7;

    String appName = "";

    public Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Log.d(TAG, String.format("Handler.handleMessage(): msg=%s", msg));
            String data = (String) msg.obj;
            //Log.d(TAG, "Recieved Data: " + data);
            String[] splitData1 = data.split("\\*");
            String devPurpose = splitData1[1];
            //Log.d(TAG, "Received Purpose: " + splitData1[1]);
            String[] splitData2 = splitData1[0].split(":");
            for(String dataPoint : splitData2) {
                //Log.d(TAG, "Other: " + dataPoint);
                //float[] results = classifier.pushToList(dataPoint);
                //TODO: Bring this back if you want to send it to the classifier
                //sendToClassifier(dataPoint, devPurpose);
                //Log.d(TAG, "Prediction results: " + results[0] + "," + results[1] + "," + results[2]);
            }
            //float x =

            //
        }

    };

    /*
            Classifier 1: Just the pillow sensor
            Classifier 2: Just Blanket Sensor
            Classifier 3: Just Motionsense Sensor
            Classifier 4: Pillow and Blanket Sensors
            Classifier 5: Pillow and MotionSense Sensors
            Classifier 6: Blanket and MotionSense Sensors
            Classifier 6: Pillow, Blanket, and MotionSense Sensors
     */
    //This is called whenever a thread sends a data message
    /*private void sendToClassifier(String dPoint, String devPurpose) {
        Log.d(TAG, "RECEIVED: " + devPurpose);
        float[] results = new float[3];
        if(devPurpose.equals(getString(R.string.bodySensor_L))) {
            results = classifier3.pushToList(dPoint);
            printPrediction(results, devPurpose, 3);
            results = classifier5.pushToList(dPoint, getString(R.string.bodySensor_L));
            printPrediction(results, devPurpose, 5);
            results = classifier6.pushToList(dPoint, getString(R.string.bodySensor_L));
            printPrediction(results, devPurpose, 6);
            results = classifier7.pushToList(dPoint, getString(R.string.bodySensor_L));
            printPrediction(results, devPurpose, 7);
        }
        else if(devPurpose.equals(getString(R.string.pillowSensor))) {
            results = classifier1.pushToList(dPoint);
            printPrediction(results, devPurpose, 1);
            results = classifier4.pushToList(dPoint, getString(R.string.pillowSensor));
            printPrediction(results, devPurpose, 4);
            results = classifier5.pushToList(dPoint, getString(R.string.pillowSensor));
            printPrediction(results, devPurpose, 5);
            results = classifier7.pushToList(dPoint, getString(R.string.pillowSensor));
            printPrediction(results, devPurpose, 7);
        }
        else if(devPurpose.equals(getString(R.string.blanketSensor))) {
            results = classifier2.pushToList(dPoint);
            printPrediction(results, devPurpose, 2);
            results = classifier4.pushToList(dPoint, getString(R.string.blanketSensor));
            printPrediction(results, devPurpose, 4);
            results = classifier6.pushToList(dPoint, getString(R.string.blanketSensor));
            printPrediction(results, devPurpose, 6);
            results = classifier7.pushToList(dPoint, getString(R.string.blanketSensor));
            printPrediction(results, devPurpose, 7);
        }

    }*/

    private void printPrediction(float[] results, String devPurpose, int classifierNum) {
        if(results[0] == 0.0 && results[1] == 0.0 && results[2] == 0.0) {
            return;
        }
        String output = devPurpose + " : " + results[0] + "," + results[1] + "," + results[2];
        Intent in = new Intent();
        in.putExtra("classification", output);
        in.putExtra("classifier_num", classifierNum);
        in.setAction("CLASSIFY");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }


    @Override
    public void onCreate() {
        //super.onCreate(b);
        Log.d(TAG, "Creating service!");
        hasBTCapability = checkDeviceBTCapability();
        boolean isBTOn = checkDeviceBT();

        if(DEBUG) {
            if(!hasBTCapability) {
                Log.d(TAG, "ERROR! Device does not have BT Capability");
                return;
            }
            if(!isBTOn) {
                Log.d(TAG, "ERROR! Device BT not on!");
                return;
            }
        }

        //Add the BT devices that should be connected to the phone
        addAllowedDevices();


        //Initialize Bluetooth manager
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                 Log.d(TAG, "ERROR! Unable to initialize BluetoothManager.");
            }
        }

        //Initialize classifier
        classifier1 = new tfclassifier_1(getApplicationContext());
        classifier2 = new tfclassifier_1_s(getApplicationContext());
        classifier3 = new tfclassifier_2_m(getApplicationContext());
        classifier4 = new tfclassifier_s1s2(getApplicationContext());
        classifier5 = new tfclassifier_s1m(getApplicationContext());
        classifier6 = new tfclassifier_s2m(getApplicationContext());
        classifier7 = new tfclassifier_s1s2m(getApplicationContext());
        //beginScan();

    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {

        Log.d(TAG, "Started");
        if(intent != null) {
            appName = intent.getStringExtra("appname");
            String itemCommand  = intent.getStringExtra("commandBT");
            String newGT = intent.getStringExtra("setGT");
            if(appName != null) {
                Log.d(TAG, "SCANNING!");
                beginScan();
            }
            else if(itemCommand != null) {
                Log.d(TAG, "COMMAND ITEM " + itemCommand);
            }
            else if(newGT != null) {
                Log.d(TAG, "New Ground Truth: " + newGT);
                mDataPackager.setGT(newGT);
            }
        }


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "DESTROYING SERVICE");
        ArrayList<String> usedSensorPurposes = new ArrayList<String>();

        //Stop threads
        for(SensorTag st : stThreads) {
            Log.d(TAG, "Stopping Sensortag Threads!");
            usedSensorPurposes.add(st.getPurpose());
            //st.query(5);
            st.stopUpdates();
            //st.disconnectDataKit(); //Also queries for data, and ends the connection
            st.interrupt();
        }
        for(MotionSense ms : msThreads) {
            Log.d(TAG, "Stopping MotionSense Threads!");
            usedSensorPurposes.add(ms.getPurpose());
            //ms.query(5);
            ms.stopUpdates();
            //ms.disconnectDataKit();
            ms.interrupt();
        }

        stopScan();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //Checks if this device has BT capability
    private boolean checkDeviceBTCapability() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isCapableOfBT = false;
        if (mBtAdapter == null) {

        }
        else {
            isCapableOfBT = true;
        }
        return isCapableOfBT;
    }

    private boolean checkDeviceBT() {
        if (!mBtAdapter.isEnabled()) {
            return false;
        }
        return true;
    }


    //User clicks button to start scanning for bluetooth devices
    public void beginScan() {
        if (mScanning) {
            stopScan();
        } else {
            startScan();
        }
    }

    private void startScan() {
        Log.d(TAG, "Starting scan!");
        // Start device discovery
        if (hasBTCapability) {
            mNumDevs = 0;
            Log.d(TAG, "startScan");
            scanLeDevice(true);


        } else {
            Log.d(TAG, "startScan failed, BLE not supported on this device");
        }

    }

    private void stopScan() {
        Log.d(TAG, "StopScan");
        mScanning = false;
        scanLeDevice(false);

    }


    private boolean scanLeDevice(boolean enable) {
        if (enable) {
            mBtAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
            //if(mBtAdapter.getBluetoothLeScanner() == null) {
            //    Log.d(TAG, "You know this error");
            //}
            mScanning = true;

            Log.d(TAG, "Starting threads for motionsense!");

            //Intent awService = new Intent(this, androidWear.class);
            //startService(awService);


        } else {

            mScanning = false;
            mBtAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);

            //Intent awService = new Intent(this, androidWear.class);
            //stopService(awService);

        }
        return mScanning;
    }

    //Adds the BT devices that should be connected to the phone
    private void addAllowedDevices() {
        allowedDevices = new ArrayList<String>();
        allowedDevices.add("54:6C:0E:53:1B:4D");  //This is a sensortag
        allowedDevices.add("54:6C:0E:80:3C:83");  //This is another sensortag
        allowedDevices.add("F7:AD:6A:31:28:48");  //This is a Motionsense HRV
        //allowedDevices.add("F5:C2:CE:D7:44:FC");  //This is another MotionSense HRV
        allowedDevices.add("2C:56:DC:FA:AC:21"); //this is the Zenwatch 2
    }


    //Gets the purpose of this sensor
    //There's a better way to do this, I'm just lazy
    private String getDevicePurpose(String addr) {
        String purpose = "";
        if(addr.equals("54:6C:0E:53:1B:4D")) {  //Sensortag #10
            purpose = getString(R.string.pillowSensor);
        }
        else if(addr.equals("54:6C:0E:80:3C:83")) {  //Sensortag #13
            purpose = getString(R.string.blanketSensor);
        }
        else if(addr.equals("F7:AD:6A:31:28:48")) {  //MotionSense HRV
            purpose = getString(R.string.bodySensor);
        }
        /*else if(addr.equals("F5:C2:CE:D7:44:FC")) {  //MotionSense HRV
            purpose = getString(R.string.bodySensor_R);
        }*/
        else if(addr.equals("2C:56:DC:FA:AC:21")) { //Zenwatch 2
            purpose = "AndroidWearSensor";
        }
        return purpose;
    }

    private void sendInfoToActivity(String devicePurpose) {
        Intent in = new Intent();
        in.putExtra("device",devicePurpose);
        in.setAction("DEVICE_FOUND");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }
    private void removeDeviceInfo(String devicePurpose) {
        Intent in = new Intent();
        in.putExtra("device",devicePurpose);
        in.setAction("DEVICE_REMOVE");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
    }
    private void updateCount() {
        Intent in = new Intent();
        in.putExtra("count", mDataPackager.getCurrentCount());
        in.setAction("UPDATE_COUNT");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);

    }

    //Check if this is a sensortag
    private boolean isSensorTag(String name) {

        boolean is_sensor_tag = false;
        if ((name.equals("SensorTag2")) ||(name.equals("CC2650 SensorTag"))) {
            is_sensor_tag = true;
        }
        return is_sensor_tag;
    }

    //Check if this is a motionsense HRV
    private boolean isMotionSenseHRV(String name) {

        boolean is_mshrv = false;
        if (name.equals("MotionSenseHRV")) {
            is_mshrv = true;
        }
        return is_mshrv;
    }

    //This function sends connection requests to all the relevant discovered devices (i.e. sensorTags, wearables)
    private void connectDevices() {

        if(doneConnecting) {return;}

        Log.d(TAG, "Attempting to Connect to Devices");
        List<String> deviceNames = new ArrayList<String>();
        List<String> deviceAddrs = new ArrayList<String>();
        List<String> usedDeviceAddrs = new ArrayList<String>();
        for(BluetoothDevice dev : discoveredDevices) {

            //Make sure that this device has not already been activated before
            if(!usedDeviceAddrs.contains(dev.getAddress()) || usedDeviceAddrs.isEmpty()) {

                //Log.d(TAG, "Discovered device: " + dev.getName() + " " + dev.getAddress());

                deviceNames.add(dev.getName());
                deviceAddrs.add(dev.getAddress());
                usedDeviceAddrs.add(dev.getAddress());

                connectToDevice(dev);

            }
        }

        doneConnecting = true;
    }

    private void connectToDevice(BluetoothDevice dev) {
        String devicePurpose = getDevicePurpose(dev.getAddress());
        //Log.d(TAG, " Starting new threads!");

        DataKitAPI datakitapi = null;
        if(isSensorTag(dev.getName())) {
            Log.d(TAG, "Starting threads for Sensortags!");
            SensorTag stThread = new SensorTag(getApplicationContext(), mBluetoothManager, dev.getAddress(), dev.getName(), devicePurpose,
                    datakitapi.getInstance(this), mDataPackager, _handler);
            stThread.buildApplication(appName);
            stThread.start();
            stThreads.add(stThread);
            sendInfoToActivity(devicePurpose);
        }

        else if(isMotionSenseHRV(dev.getName())) {
            Log.d(TAG, "Connecting - Starting threads for motionsense!");
            MotionSense msThread = new MotionSense(getApplicationContext(), mBluetoothManager, dev.getAddress(), dev.getName(), devicePurpose,
                    datakitapi.getInstance(this), mDataPackager, _handler);
            msThread.buildApplication(appName);
            msThread.start();  //TODO: Be sure to add this back in when you are done separately testing
            msThreads.add(msThread);
            sendInfoToActivity(devicePurpose);
        }
    }

    private void removeDiscoveredDevice(String addr) {
        BluetoothDevice toRemove = null;
        for(BluetoothDevice dev : discoveredDevices) {
            if(dev.getAddress() == addr) {
                toRemove = dev;
            }
        }
        Log.d(TAG, "Removing discovered device " + getDevicePurpose(addr));
        discoveredDevices.remove(toRemove);
        removeDeviceInfo(getDevicePurpose(addr));
    }

    private void removeDisconnectedDevices() {
        Log.d(TAG, "Removing disconnected Devices");
        //Check each thread, and see if it should be stopped (means that it is disconnected)
        for(SensorTag st : stThreads) {
            if(!st.isConnected()) {
                st.stopUpdates();
                st.interrupt();
                removeDiscoveredDevice(st.getAddress());
            }
        }
        for(MotionSense ms : msThreads) {  //TODO: Still have to add the remove functionality for wristwatch

            if(!ms.isBluetoothConnected()) {
                ms.stopUpdates();
                //ms.disconnectDataKit();
                ms.interrupt();
                removeDiscoveredDevice(ms.getAddress());
            }
        }


    }

    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            if(!startedScanning) {
                scanStartTime = System.currentTimeMillis();
                startedScanning = true;
            }
            if(startedScanning) {
                long timeElapsed = System.currentTimeMillis() - scanStartTime;
                if(timeElapsed > 10000) {  //If we have waited more than ten seconds, stop scanning
                    Log.d(TAG, " Checking for removing disconnected devices");
                    removeDisconnectedDevices();
                    startedScanning = false;
                    //Log.d(TAG, "Current Count: " + mDataPackager.getCurrentCount());
                    updateCount();
                    //stopScan();  //Stop Bluetooth scans
                    //connectDevices();
                }
            }


            final BluetoothDevice device = result.getDevice();
            final int rssi = result.getRssi();
            String deviceName = device.getName();
            String deviceAddr = device.getAddress();

            //Log.d(TAG, "Device Name: " + deviceName + " Device Addr: " + deviceAddr);

            //Only add a device when it is allowed, and it hasn't already been discovered
            if(allowedDevices.contains(deviceAddr) && !discoveredDevices.contains(device)) {
                Log.d(TAG, "Found new device, adding to list " + getDevicePurpose(deviceAddr));
                discoveredDevices.add(device);
                connectToDevice(device);
                //stopScan();  //TODO: REMOVE THIS AFTER YOU ARE DONE TESTING A SINGLE DEVICE
                //connectDevices();
                /*if(discoveredDevices.size() >= maxDeviceConnections) {
                    stopScan();
                    connectDevices();
                }*/
            }

        }
    };




}
