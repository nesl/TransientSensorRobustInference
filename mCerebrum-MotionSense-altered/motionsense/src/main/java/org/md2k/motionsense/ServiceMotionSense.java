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

package org.md2k.motionsense;

import android.app.Notification;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;


import com.orhanobut.logger.Logger;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeLong;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.METADATA;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.motionsense.configuration.ConfigurationManager;
import org.md2k.motionsense.datakit.DataKitManager;
import org.md2k.motionsense.device.DeviceManager;
import org.md2k.motionsense.device.Sensor;
import org.md2k.motionsense.device.data_quality.DataQualityManager;
import org.md2k.motionsense.error.ErrorNotify;
import org.md2k.motionsense.permission.Permission;
import org.md2k.motionsense.phone.sensorSourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.BackpressureOverflow;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static org.md2k.motionsense.ActivitySettings.ACTION_LOCATION_CHANGED;

/**
 * Manages the motion sense service.
 */
public class ServiceMotionSense extends Service {
    public static final String INTENT_DATA = "INTENT_DATA";
    private DataKitManager dataKitManager;
    DeviceManager deviceManager;
    Subscription subscription;
    SparseArray<Summary> summary;
    DataQualityManager dataQualityManager;

    //ArrayList<Sensor> sensorsToQuery = new ArrayList<Sensor>();
    //ArrayList<DataSourceClient> clientSources = new ArrayList<DataSourceClient>();
    //long startTime = 0;
    //long endTime = 0;

    /*
        Added Variables:
        - ssm is the manager for all the on board sensors on the phone itself
        - exp is the class for exporting a string to a CSV file
        - executor is for executing new threads (specifically I used it for exporting data)

        To find the code that I added, ctrl+f for ACCELEROMETER
     */
    sensorSourceManager ssm;
    exporter exp;
    List<exportRunnable> writeQueue;  //This is created from a synchronized list
    Thread writeThread;
    frequencyTester ft = new frequencyTester("WristWatch Acc", 0, 1000);
    HashMap<String, Long> deviceInsertedTimestamps = new HashMap<String, Long>();


    //Check if should insert this data - high frequency data from MotionSense HRV sometimes arrives multiple times
    // This function checks if we are recieving data that is old; i.e. the timestamp of the recieved data
    // is older than the most recent timestamp
    // Surprisingly, even though this misses the case where we could miss old data that wasn't recorded already,
    //  we still get around 25Hz of data for each modality
    public boolean isInsertAllowed(String key, long timestamp) {
        boolean insertAllowed = false;

        //If the key is in the current hash map
        if(deviceInsertedTimestamps.containsKey(key)) {
            long lastTimestamp = deviceInsertedTimestamps.get(key);

            //Check if we should insert or not
            if(timestamp > lastTimestamp) {
                insertAllowed = true;
                deviceInsertedTimestamps.put(key, timestamp);
            }

        }
        else {  //Key not found - add to hashmap
            deviceInsertedTimestamps.put(key, timestamp);
        }

        return insertAllowed;

    }


    /**
     * Logs the creation of the service, calls <code>loadListener()</code>, and subscribes an
     * <code>Observable</code> to receive data from the motion sensor.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("Service: onCreate()...");
        summary = new SparseArray<>();

        writeQueue = Collections.synchronizedList(new ArrayList<exportRunnable>());
        exp  = new exporter(this);

        ErrorNotify.removeNotification(ServiceMotionSense.this);
        loadListener();

        ssm = new sensorSourceManager(this, exp, writeQueue);
        ssm.registerListeners();


        //Run a background thread for writing to CSV files
        writeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    writeToFile();
                }
            }
        });
        writeThread.start();

        subscription = Observable.just(true)
                .map(aBoolean -> {
                    Log.e("abc", "permission");
                    boolean res = Permission.hasPermission(ServiceMotionSense.this);
                    if (!res) ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.PERMISSION);
                    return res;
                }).filter(x -> x)
                .map(aBoolean -> {
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    boolean res = mBluetoothAdapter.isEnabled();
                    if (!res)
                        ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.BLUETOOTH_OFF);
                    return res;
                }).filter(x -> x)
                .map(aBoolean -> {
                    LocationManager locationManager = (LocationManager) ServiceMotionSense.this.getSystemService(LOCATION_SERVICE);
                    boolean res = (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                    if (!res) ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.GPS_OFF);
                    return res;
                }).filter(x -> x)
                .map(aBoolean -> {
                    ArrayList<DataSource> dataSources = ConfigurationManager.read(ServiceMotionSense.this);
                    if (dataSources == null || dataSources.size() == 0) {
                        ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.NOT_CONFIGURED);
                        return false;
                    }
                    return true;
                }).filter(x -> x)
                .flatMap(aBoolean -> {
                    dataKitManager = new DataKitManager();
                    return dataKitManager.connect(ServiceMotionSense.this).map(res -> {
                        if (!res)
                            ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.DATAKIT_CONNECTION_ERROR);
                        return res;
                    });
                }).doOnUnsubscribe(() -> {
                    Log.e("abc", "doOnUnsubscribe...datakitmanager");

                    //queryDatabase();

                    if (dataKitManager != null)
                        dataKitManager.disconnect();
                }).filter(x -> x)
                .map(aBoolean -> {
                    ArrayList<DataSource> dataSources = ConfigurationManager.read(ServiceMotionSense.this);
                    deviceManager = new DeviceManager();
                    dataQualityManager = new DataQualityManager();
                    if (dataSources == null || dataSources.size() == 0) return false;
                    for (int i = 0; i < dataSources.size(); i++) {
                        DataSourceClient dataSourceClient = dataKitManager.register(dataSources.get(i));
                        if (dataSourceClient == null) {
                            ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.DATAKIT_REGISTRATION_ERROR);
                            return false;
                        }

                        Sensor sensor = new Sensor(dataSourceClient,
                                dataSources.get(i).getPlatform().getType(),
                                dataSources.get(i).getPlatform().getMetadata().get(METADATA.DEVICE_ID),
                                dataSources.get(i).getMetadata().get("CHARACTERISTIC_NAME"),
                                dataSources.get(i).getType(),
                                dataSources.get(i).getId());
                        /*if(sensor.getDataSourceType().equals("ACCELEROMETER")) {
                            Log.d("abcde", "Added " + sensor.getDataSourceType());
                            sensorsToQuery.add(sensor);
                            clientSources.add(dataSourceClient);
                        }*/
                        //sensor.printAllData();
                        if (dataSources.get(i).getType().equals(DataSourceType.DATA_QUALITY)) {
                            dataQualityManager.addSensor(sensor);
                        } else {
                            deviceManager.add(sensor);
                        }
                    }
                    return true;
                }).filter(x -> x)
                .flatMap(aBoolean -> {
                    return Observable.merge(deviceManager.connect(ServiceMotionSense.this), dataQualityManager.getObservable());
                })
                .doOnUnsubscribe(() -> {
                    Logger.d("Service: doOnUnsubscribe..device manager...disconnecting...");
                    if (deviceManager != null)
                        deviceManager.disconnect();
                })
                .buffer(500, TimeUnit.MILLISECONDS)
                .onBackpressureBuffer(100, new Action0() {
                    @Override
                    public void call() {
                        Logger.e("Device...subscribeConnect()...Data Overflow occurs...after buffer... drop oldest packet");
                    }
                }, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)
                .flatMap(new Func1<List<ArrayList<Data>>, Observable<Data>>() {
                    @Override
                    public Observable<Data> call(List<ArrayList<Data>> arrayLists) {
                        ArrayList<Data> data = new ArrayList<>();
                        for(int i=0;i<arrayLists.size();i++){
                            ArrayList<Data> x = arrayLists.get(i);
                            data.addAll(x);
                        }
                        if(data.size()==0) return null;
                        HashSet<Integer> dsIds = new HashSet<>();
                        for (int i = 0; i < data.size(); i++)
                            dsIds.add(data.get(i).getSensor().getDataSourceClient().getDs_id());

                        for (Integer dsId : dsIds) {
                            ArrayList<Data> dataTemp = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).getSensor().getDataSourceClient().getDs_id() == dsId) {
                                    dataTemp.add(data.get(i));
                                }

                                /*
                                       ADDED CODE:
                                       - Upon receiving data, check if data source type matches what we want (ACC & Gyro)
                                       - If this data is in the correct order - sometimes we get redundant data
                                       - Store the data using another thread so we don't block new incoming data
                                 */

                                if(data.get(i).getSensor().getDataSourceType().equals("ACCELEROMETER")) {
                                    String message = data.get(i).getDoubleArrayData();
                                    String foldername = data.get(i).getSensor().getDeviceId() + "-ACC";
                                    exportRunnable dataToQueue = new exportRunnable(foldername, message);

                                    if(isInsertAllowed(foldername, data.get(i).getDataType().getDateTime())) {
                                        writeQueue.add(dataToQueue);
                                    }


                                }
                                else if(data.get(i).getSensor().getDataSourceType().equals("GYROSCOPE")) {
                                    String message = data.get(i).getDoubleArrayData();
                                    String foldername = data.get(i).getSensor().getDeviceId() + "-GYRO";
                                    exportRunnable dataToQueue = new exportRunnable(foldername, message);

                                    if(isInsertAllowed(foldername, data.get(i).getDataType().getDateTime())) {
                                        writeQueue.add(dataToQueue);
                                    }

                                }
                            }
                            if (dataTemp.size() == 0) continue;
                            DataType[] dataTypes = dataKitManager.insert(dataTemp);
                            for (int i = 0; i < dataTemp.size(); i++) {
                                if (dataTemp.get(i).getSensor().getDataSourceType().equals(DataSourceType.DATA_QUALITY)) {
                                    dataKitManager.setSummary(dataTemp.get(i).getSensor().getDataSourceClient(), dataQualityManager.getSummary(dataTemp.get(i)));
                                } else
                                    dataQualityManager.addData(dataTemp.get(i));
                                Summary s = summary.get(dataTemp.get(i).getSensor().getDataSourceClient().getDs_id());
                                if (s == null) {
                                    s = new Summary();
                                    summary.put(dataTemp.get(i).getSensor().getDataSourceClient().getDs_id(), s);
                                }
                                s.set();
                            }
                            //Log.d("abcde", Integer.toString(dsId) + " : " + );
                            Intent intent = new Intent(INTENT_DATA);
                            intent.putExtra(DataSource.class.getSimpleName(), dataTemp.get(0).getSensor().getDataSourceClient().getDataSource());
                            intent.putExtra(DataType.class.getSimpleName(), dataTypes);
                            intent.putExtra(Summary.class.getSimpleName(), summary.get(dataTemp.get(0).getSensor().getDataSourceClient().getDs_id()));
                            LocalBroadcastManager.getInstance(ServiceMotionSense.this).sendBroadcast(intent);

                        }
//                        dataKitManager.insert(data.getSensor().getDataSourceClient(), data.getDataType());
/*
                        if (data.getSensor().getDataSourceType().equals(DataSourceType.DATA_QUALITY))
                            dataKitManager.setSummary(data.getSensor().getDataSourceClient(), dataQualityManager.getSummary(data));
                        else
                            dataQualityManager.addData(data);

                        Intent intent = new Intent(INTENT_DATA);
                        Summary s = summary.get(data.getSensor().getDataSourceClient().getDs_id());
                        if (s == null) {
                            s = new Summary();
                            summary.put(data.getSensor().getDataSourceClient().getDs_id(), s);
                        }
                        s.set();
                        intent.putExtra(DataSource.class.getSimpleName(), data.getSensor().getDataSourceClient().getDataSource());
                        intent.putExtra(DataType.class.getSimpleName(), data.getDataType());
                        intent.putExtra(Summary.class.getSimpleName(), s);
                        LocalBroadcastManager.getInstance(ServiceMotionSense.this).sendBroadcast(intent);
*/
                        return Observable.just(data.get(0));
                    }
                })
                /*.onBackpressureBuffer(1024, new Action0() {
                    @Override
                    public void call() {
                        Logger.e("Device...subscribeConnect()...Data Overflow occurs..after push...drop oldest packet");
                    }
                }, BackpressureOverflow.ON_OVERFLOW_DROP_OLDEST)*/
/*
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Data>>() {
                    @Override
                    public Observable<? extends Data> call(Throwable throwable) {
                        Logger.e("onresumenext()...throwable="+throwable.getMessage());
                        if(throwable instanceof CompositeException){
                            CompositeException c = (CompositeException) throwable;
                            for(int i=0;i<c.getExceptions().size();i++) {
                                if (!(c.getExceptions().get(i) instanceof MissingBackpressureException)) {
                                    Logger.e("onresumenext()...throwable...e="+c.getExceptions().get(i).getMessage());
                                    return Observable.error(throwable);
                                }
                            }
                            Logger.e("onresumenext()...throwable...all are missingbackpressueexception..continue");
                            return Observable.just(null);
                        }
                        return Observable.error(throwable);
                    }
                })
*/
                .retryWhen(errors -> errors.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                    Logger.e("Service: retryWhen()...error=" + throwable.getMessage()+" "+throwable.toString(), throwable);
                    return Observable.just(null);
                }))
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<Data>() {
                    /**
                     * Logs the completion of the service, unsubscribes the listener, and stops itself.
                     */
                    @Override
                    public void onCompleted() {
                        Logger.d("Service -> onCompleted()");
                        unsubscribe();
                        stopSelf();
                    }

                    /**
                     * Logs the service's error, unsubscribes the listener, and stops itself.
                     */
                    @Override
                    public void onError(Throwable e) {
                        Logger.e("Service onError()... e=" + e.getMessage(), e);
                        unsubscribe();
                        stopSelf();
                    }

                    /**
                     * Inserts the received data into <code>DataKit</code>.
                     * @param data Data received
                     */
                    @Override
                    public void onNext(Data data) {
                        //data.printDoubleArrayData();
                    }
                });
    }

    /**
     * Creates an intent filter and registers it to the receiver.
     */
    void loadListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(ACTION_LOCATION_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    /*void queryDatabase() {
        for(Sensor s : sensorsToQuery) {
            Log.d("abcde", "Querying " + s.getDeviceId() + " : " + s.getDataSourceType());
            try {
                DataTypeLong querySize = dataKitManager.getDatakit().querySize();
                s.getDataSourceClient();
                printQuery(dataKitManager.getDatakit().query(s.getDataSourceClient(), 50));
            } catch (DataKitException e) {
                Log.d("abcde", "ERROR: Datakit not found!");
                e.printStackTrace();
            }
        }
        for(DataSourceClient dsc : clientSources) {
            //Log.d("abcde", "Querying " + s.getDeviceId() + " : " + s.getDataSourceType());

            DataSourceBuilder dsb = new DataSourceBuilder(dsc.getDataSource());
            try {
                ArrayList<DataSourceClient> dataSourceClients = dataKitManager.getDatakit().find(dsb);
                Log.d("abcde", "Found " + dataSourceClients.size() + " matching clients");
                if (dataSourceClients.size() == 0) {
                    Log.d("abcde", "Query failed - Data Source not registered");
                }

                DataTypeLong querySize = dataKitManager.getDatakit().querySize();
                //dsc.getDataSourceClient();
                printQuery(dataKitManager.getDatakit().query(dataSourceClients.get(0), 50));
            } catch (DataKitException e) {
                Log.d("abcde", "ERROR: Datakit not found!");
                e.printStackTrace();
            }
        }
    }*/

    /**
     * Calls unsubscribe, unregisters the receiver, logs the event, and calls super.
     */
    @Override
    public void onDestroy() {
        Logger.d("Service: onDestroy()...");
        //endTime = System.currentTimeMillis();

        ssm.destroy();  //Stop the Phone Sensor Manager
        //Interrupt the write thread and force the rest of the queue to be written to file
        writeThread.interrupt();
        //completeWriting();


        if (ConfigurationManager.isForegroundApp())
            stopForegroundService();
        unsubscribe();
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception ignored) {
        }
        super.onDestroy();
    }

    long lastOutputTime = 0;
    long delay = 1000;
    long writeCount = 0;
    //Write an exportRunnable object to the CSV
    private void writeToFile() {
        if(!writeQueue.isEmpty()) {

            if(System.currentTimeMillis() > lastOutputTime + delay) {

                lastOutputTime = System.currentTimeMillis();
                Log.d("abcde", "Current Queue Size: " + Long.toString(writeQueue.size()));
                Log.d("abcde", "Writes: " + Long.toString(writeCount));

                writeCount = 0;
            }

            //Get the first object in the writeQueue
            exportRunnable toWrite = writeQueue.get(0);
            //Remove the first object from the queue
            writeQueue.remove(0);
            //Buffer the data for writing to a CSV
            exp.bufferData(toWrite.foldername, toWrite.message);

            writeCount++;
        }
    }

    //Write the rest of the writeQueue to file - this is called before this service is destroyed
    private void completeWriting() {
        Log.d("abcde", " Completing Writes to file!");
        synchronized (writeQueue) {
            Iterator i = writeQueue.iterator(); // Must be in synchronized block
            while (i.hasNext()) {
                exportRunnable toWrite = (exportRunnable) i.next();
                exp.exportData(toWrite.foldername, toWrite.message);
            }
            writeQueue.clear();
        }

    }

    /*public void printQuery (ArrayList<DataType> query) {
        Log.d("abcde", "Length of query: " + query.size());
        StringBuilder message = new StringBuilder();
        //message.append("Query Size is ").append(querySize.getSample()).append("\n");
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
    }*/

    /**
     * Unsubscribes the observable.
     */
    void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        subscription = null;
    }

    /**
     * This method has not been implemented yet.
     *
     * @param intent Android intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Creates a new broadcast receiver that receives the bluetooth and location state change intent.
     * Upon receipt it unsubscribes the observable and stops itself.
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action != null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.BLUETOOTH_OFF);
                        unsubscribe();
                        stopSelf();
                        break;
                }
            } else if (action != null && action.equals(ACTION_LOCATION_CHANGED)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    ErrorNotify.handle(ServiceMotionSense.this, ErrorNotify.GPS_OFF);
                    unsubscribe();
                    stopSelf();
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ConfigurationManager.hasDefault() && ConfigurationManager.isForegroundApp())
            startForegroundService();
        return super.onStartCommand(intent, flags, startId);
    }

    private static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";

    private void startForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        // Create notification default intent.
        Intent intent = new Intent();

        // Create notification builder.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setWhen(System.currentTimeMillis());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Wrist app running...");


        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService() {

        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

}

