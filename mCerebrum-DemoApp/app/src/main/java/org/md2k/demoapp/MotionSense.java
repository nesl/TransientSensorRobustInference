package org.md2k.demoapp;


import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.application.Application;
import org.md2k.datakitapi.source.application.ApplicationBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class MotionSense extends Thread {

    private final String TAG = "DBG-MotionSense";

    Context mContext = null;

    //BluetoothGatt mBluetoothGatt = null;
    BluetoothAdapter mBtAdapter = null;
    BluetoothManager mBluetoothManager = null;
    //List<String> mDeviceAddrs = new ArrayList<String>();
    //List<String> mDeviceNames = new ArrayList<String>();

    String mDeviceAddr = null;
    String mDeviceName = null;
    String mDevicePurpose = "";

    //List<BluetoothGatt> mBluetoothGatts = new ArrayList<BluetoothGatt>();
    BluetoothGatt mBluetoothGatt = null;

    //This is for timekeeping and ensuring that we only take samples every second
    private long lastSaved = 0;
    private double minSampleTime = 200; // 200ms

    private final String UUID_battery_service = "da39adf0-1d81-48e2-9c68-d0ae4bbd351f";
    private final String UUID_battery_char = "00002a19-0000-1000-8000-00805f9b34fb";
    private final String UUID_led_service = "da395d22-1d81-48e2-9c68-d0ae4bbd351f";
    private final String UUID_led_char = "da39c921-1d81-48e2-9c68-d0ae4bbd351f";

    private BluetoothGattCharacteristic dataC = null;

    DataKitAPI datakitapi = null;
    String dataKitId = "";
    private DataSourceClient regDataSourceClient = null;
    private DataSourceClient subDataSourceClient = null;
    private ArrayList<DataType> dataTypeQuery = null;

    DataPackager mDataPackager;

    Handler mMessageHandler = null;
    String mMessageToSend = "";
    int messageAdds = 0;

    long totalInserts = 0;

    long startTime = 0;

    boolean isConnected = true;


    public MotionSense(Context context, BluetoothManager btManager, String addr, String name, String purpose,
                       DataKitAPI dkapi, DataPackager dataPackager, Handler handler) {

        mBluetoothManager = btManager;
        mContext = context;
        mDeviceAddr = addr;
        mDeviceName = name;
        mDevicePurpose = purpose;
        mDataPackager = dataPackager;
        datakitapi = dkapi;

        Log.d(TAG, "MotionSense Created: " + mDevicePurpose);

        mMessageHandler = handler;
    }


    public String getPurpose() {
        return mDevicePurpose;
    }

    public String getAddress() { return mDeviceAddr;}

    private BluetoothGattService getLEDService(BluetoothGatt gatt) {
        return gatt.getService(UUID.fromString(UUID_battery_service));
    }

    private BluetoothGattService getBatteryService(BluetoothGatt gatt) {
        return gatt.getService(UUID.fromString(UUID_led_service));
    }

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
        return new DataSourceBuilder().setType(DataSourceType.HRV).setApplication(application);
    }

    /**
     * Builds a data source object representing the sensor. In this application this is only used for
     * registering the data source. It could be used to find all data sources of the set type independent
     * from the application.
     * @return A data source builder
     */
    public DataSourceBuilder buildDataSource() {
        return new DataSourceBuilder().setType(DataSourceType.HRV);
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
                    }
                });
        } catch (DataKitException ignored) {
            Log.d(TAG, ignored.getMessage());
        }
    }

    public void stopUpdates() {
        endConnection();

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


    public void enableService(BluetoothGattService s, final BluetoothGatt gatt, String serviceName) {


        Log.d(TAG, "Enabling Motion Sense Service for " + serviceName + ": " + gatt.getDevice().getAddress());

        //Gets all the characteristics for this Motion service
        List<BluetoothGattCharacteristic> characteristics = s.getCharacteristics();


        for (BluetoothGattCharacteristic c : characteristics) {
            //Log.d(TAG, serviceName + " : " + c.getUuid().toString());
            //Log.d(TAG, "Found Characterstic: " + c.getUuid().toString() );
            //if(c.getUuid().toString().equals(UUID_battery_char) || c.getUuid().toString().equals(UUID_led_char) ) {
            if(c.getUuid().toString().equals(UUID_led_char) ) {
                Log.d(TAG, "Found Relevant Characteristic!");
                dataC = c;
            }
        }

        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean notifyenable = true;
                gatt.setCharacteristicNotification(dataC, true);
                printAllInfo("setCharacteristic", dataC);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });

        //We only enable the service if the relevant characteristic is found
        if(dataC != null) {
            worker.start();
        }


    }

    @Override
    public void run() {


        //Register data source with datakit
        registerDataSource();
        //Subscribe to updates made in the database
        subscribeDataSource();

        if(mDeviceAddr.isEmpty() || mDeviceName.isEmpty()) {
            Log.d(TAG, "ERROR, device addresses/names are empty!");
            return;
        }
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        //mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        //for(int i = 0; i < mDeviceAddrs.size(); ++i) {
        //if(!connectBluetooth(mDeviceAddrs.get(i), mDeviceNames.get(i))) {
        if(!connectBluetooth(mDeviceAddr, mDeviceName)) {
            Log.d(TAG, "ERROR! Bluetooth connection unsuccessful: " + mDeviceAddr);
        }
        else {
            Log.d(TAG, "Connections successful!");
        }
        //}
    }


    //Connect to a bluetooth device
    private boolean connectBluetooth(final String addr, final String name) {
        if (mBtAdapter == null || addr == null) {
            Log.d(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        final BluetoothDevice device = mBtAdapter.getRemoteDevice(addr);
        int connectionState = mBluetoothManager.getConnectionState(device,
                BluetoothProfile.GATT);

        if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {

            if (device == null) {
                Log.d(TAG, "Device not found.  Unable to connect.");
                return false;
            }
            // We want to directly connect to the device, so we are setting the
            // autoConnect parameter to false.
            // Here we can start the SensorTag service and let it manage it own connection
            //if(isSensorTag(name)) {
                Log.d(TAG, "Connecting to " + addr);
                //BluetoothGatt currentConnection = device.connectGatt(this, false, mGattCallbacks);
                //mBluetoothGatts.add(currentConnection);
                if(mContext == null) {
                    Log.d(TAG, "ERROR! CONTEXT IS NULL!");
                }
                mBluetoothGatt = device.connectGatt(mContext, false, mGattCallbacks);
                if(mBluetoothGatt == null) {
                    Log.d(TAG, "ERROR! Bluetooth gatt is null!");
                }
            //}


        } else {
            // Log.w(TAG, "Attempt to connect in state: " + connectionState);
            return false;
        }
        return true;
    }


    //Disconnect the BluetoothGatt and close the connection
    private void endConnection() {
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }

    //Query database for information on this sensortag.
    public void query(int size){
        long endTime = DateTime.getDateTime();
        Log.d(TAG, "QUERYING DATA FROM SENSORTAG");
        Log.d("QUERY " + mDevicePurpose + ": ", "Total Inserts: " + totalInserts);
        try {
            ArrayList<DataSourceClient> dataSourceClients = datakitapi.find(buildDataSource(buildApplication(dataKitId)));
            if (dataSourceClients.size() == 0) {
                Log.d(TAG, "Query failed - Data Source not registered");
            } else {
                //querySize = datakitapi.querySize();
                dataTypeQuery = datakitapi.query(dataSourceClients.get(0), startTime, endTime);
                if (dataTypeQuery.size() == 0) {
                    Log.d(TAG, "query size zero");
                } else {
                    Log.d(TAG, "Printing Query!");
                    printQuery(dataTypeQuery);
                }

            }
        } catch (DataKitException ignored) {
            Log.e("query", ignored.getMessage());
            dataTypeQuery = null;
            Log.d(TAG, ignored.getMessage());
        }
    }

    public void printQuery (ArrayList<DataType> query) {
        Log.d(TAG, "PRINTING QUERY");
        StringBuilder message = new StringBuilder();
        //message.append("Query Size is ").append(querySize.getSample()).append("\n");
        message.append("[HRV DATA]\n");
        for (DataType data : query) {
            if (data instanceof DataTypeDoubleArray) {
                DataTypeDoubleArray dataArray = (DataTypeDoubleArray)data;
                double[] sample = dataArray.getSample();
                message.append("[" + sample[0] + ", " + sample[1] + ", " + sample[2] + "]\n");
            }
        }
        Log.d("QUERY " + mDevicePurpose + ": ", message.toString());
    }


    public boolean isBluetoothConnected() {
        return isConnected;
    }

    /**
     * GATT client callbacks
     */
    private BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {

        List <BluetoothGattService> serviceList;

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            Log.d(TAG, " onConnectionChanged " + gatt.getDevice().getAddress());
            if (gatt == null) {
                Log.d(TAG, "mBluetoothGatt not created!");
                return;
            }
            Log.d(TAG, "Connection state changed!");
            BluetoothDevice device = gatt.getDevice();
            String address = device.getAddress();
            // Log.d(TAG, "onConnectionStateChange (" + address + ") " + newState +
            // " status: " + status);

            try {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        //broadcastUpdate(ACTION_GATT_CONNECTED, address, status);
                        Log.d(TAG, "Action GATT connected " + address + " " + status);
                        gatt.discoverServices();  //Discover the device services
                        isConnected = true;


                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        //broadcastUpdate(ACTION_GATT_DISCONNECTED, address, status);
                        Log.d(TAG, "Action GATT disconnected " + address + " " + status);
                        isConnected = false;
                        break;
                    default:
                        Log.d(TAG, "New state not processed: " + newState);
                        break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            BluetoothDevice device = gatt.getDevice();

            Log.d(TAG, "Action GATT Services Discovered " + device.getAddress() + " " + status);

            serviceList = gatt.getServices();

            for (BluetoothGattService s : serviceList) {
                Log.d(TAG, "Received Service: " + s.getUuid().toString());
                enableService(s, gatt, s.getUuid().toString());
            }

            /*final BluetoothGattService ledService = getLEDService(gatt);
            final BluetoothGattService battService = getBatteryService(gatt);


            //final BluetoothGattService movementService = getMotionService(gatt);
            if(ledService != null && battService!= null) {
                Log.d(TAG, "LED and Battery services found!");

                //mBluetoothGatt = gatt;
                Thread worker = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        enableService(ledService, gatt, "LED");
                        //enableService(battService, gatt, "Battery");
                    }

                });
                worker.start();
            }
            else {
                Log.d(TAG, "Service(s) could not be found!");
            }*/

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            long curTime = System.currentTimeMillis();
            if ((double)(curTime - lastSaved) > minSampleTime) {
                lastSaved = curTime;
                //Log.d(TAG, "On characteristic changed " + gatt.getDevice().getAddress());
                byte[] value = characteristic.getValue();
                getLED(value);
                getAccelerometer(value);
                getGyroscope(value);
            }


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            /*if (blocking)unlockBlockingThread(status);
            if (nonBlockQueue.size() > 0) {
                lock.lock();
                for (int ii = 0; ii < nonBlockQueue.size(); ii++) {
                    bleRequest req = nonBlockQueue.get(ii);
                    if (req.characteristic == characteristic) {
                        req.status = bleRequestStatus.done;
                        nonBlockQueue.remove(ii);
                        break;
                    }
                }
                lock.unlock();
            }*/
            //broadcastUpdate(ACTION_DATA_READ, characteristic, status);
            Log.d(TAG, "On characteristic read " + status);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            /*if (blocking)unlockBlockingThread(status);
            if (nonBlockQueue.size() > 0) {
                lock.lock();
                for (int ii = 0; ii < nonBlockQueue.size(); ii++) {
                    bleRequest req = nonBlockQueue.get(ii);
                    if (req.characteristic == characteristic) {
                        req.status = bleRequestStatus.done;
                        nonBlockQueue.remove(ii);
                        break;
                    }
                }
                lock.unlock();
            }*/
            //broadcastUpdate(ACTION_DATA_WRITE, characteristic, status);
            Log.d(TAG, "On character write " + Arrays.toString(characteristic.getValue()) + " " + status);

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            //if (blocking)unlockBlockingThread(status);
            //unlockBlockingThread(status);
            Log.d(TAG, "On Descriptor read " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            //if (blocking)unlockBlockingThread(status);
            // Log.i(TAG, "onDescriptorWrite: " + descriptor.getUuid().toString());
            Log.d(TAG, "On Descriptor write " + status);
        }
    };


    /**
     * Returns the LED data as a double array.
     * @param bytes Data to convert.
     * @return The LED data as a double array.
     */
    double[] getLED(byte[] bytes){
        double[] sample = new double[3];
        sample[0] = ((bytes[12] & 0xff)<<10) | ((bytes[13] & 0xff) <<2) | ((bytes[14] & 0xc0)>>6);
        sample[1] = ((bytes[14] & 0x3f)<<12) | ((bytes[15] & 0xff) <<4) | ((bytes[16] & 0xf0)>>4);
        sample[2] = ((bytes[16] & 0x0f)<<14) | ((bytes[17] & 0xff) <<6) | ((bytes[18] & 0xfc)>>2);

        Log.d("OutputData", "LED: " + sample[0] + "," + sample[1] + "," + sample[2]);
        /*long curTime = DateTime.getDateTime();
        if(startTime == 0) {
            startTime = curTime;
        }

        DataTypeDoubleArray dataTypeDoubleArray = new DataTypeDoubleArray(curTime, sample);
        //if (isHF)
        //    insertHFData(dataTypeDoubleArray);
        //else
        insertData(dataTypeDoubleArray);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        String timeString = formatter.format(new Date(curTime));
        String dataString = timeString + "," + sample[0] + "," + sample[1] + "," + sample[2];
        mDataPackager.exportData(mDevicePurpose, dataString);
        */
        return sample;
    }

    /**
     * Returns the accelerometer data as a double array.
     * @param bytes Data to convert.
     * @return The accelerometer data as a double array.
     */
    double[] getAccelerometer(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertAccelADCtoSI((short)((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
        sample[1] = convertAccelADCtoSI((short)((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
        sample[2] = convertAccelADCtoSI((short)((bytes[4] & 0xff) << 8) | (bytes[5] & 0xff));

        Log.d("OutputData2", "ACC: " + sample[0] + "," + sample[1] + "," + sample[2]);

        long curTime = DateTime.getDateTime();
        if(startTime == 0) {
            startTime = curTime;
        }

        //DataTypeDoubleArray dataTypeDoubleArray = new DataTypeDoubleArray(curTime, sample);
        //if (isHF)
        //    insertHFData(dataTypeDoubleArray);
        //else
        //insertData(dataTypeDoubleArray);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");
        String timeString = formatter.format(new Date(curTime));
        String dataString = System.currentTimeMillis() + "," + sample[0] + "," + sample[1] + "," + sample[2];
        //String dataString = timeString + "," + sample[0] + "," + sample[1] + "," + sample[2];
        //mDataPackager.exportData(mDevicePurpose, dataString);

        /*mMessageToSend += sample[0] + "," + sample[1] + "," + sample[2] + ":";
        messageAdds++;

        if(messageAdds >= 5) {
            mMessageToSend += "*" + mDevicePurpose;
            Message msg = Message.obtain();
            msg.obj = mMessageToSend; // Put the string into Message, into "obj" field.
            msg.setTarget(mMessageHandler); // Set the Handler
            msg.sendToTarget(); //Send the message

            messageAdds = 0;
            mMessageToSend = "";
        }*/

        return sample;
    }

    /**
     * Returns the gyroscope data as a double array.
     * @param bytes Data to convert.
     * @return The gyroscope data as a double array.
     */
    static double[] getGyroscope(byte[] bytes) {
        double[] sample = new double[3];
        sample[0] = convertGyroADCtoSI((short)((bytes[6] & 0xff) << 8) | (bytes[7] & 0xff));
        sample[1] = convertGyroADCtoSI((short)((bytes[8] & 0xff) << 8) | (bytes[9] & 0xff));
        sample[2] = convertGyroADCtoSI((short)((bytes[10] &0xff) << 8) | (bytes[11] & 0xff));

        Log.d("OutputData2", "Gyro: " + sample[0] + "," + sample[1] + "," + sample[2]);

        return sample;
    }

    /**
     * Converts the ADC to SI units.
     * @param x Value to convert.
     * @return The value in SI units.
     */
    private static double convertAccelADCtoSI(double x) {
        return 2.0 * x / 16384;
    }

    /**
     * Converts the ADC to SI units.
     * @param x Value to convert.
     * @return The value in SI units.
     */
    private static double convertGyroADCtoSI(double x) {
        return 500.0 * x / 32768;
    }

    /**
     * Returns the raw data as a double array.
     * @param bytes Data to convert to a double.
     * @return The raw data as a double array.
     */
    static double[] getRaw(byte[] bytes) {
        double[] sample = new double[bytes.length];
        for(int i = 0; i < bytes.length; i++)
            sample[i] = bytes[i];
        return sample;
    }

    /**
     * Prints a data sample. Used for displaying inserted data and data received when a data source
     * is subscribed.
     * @param data Data sample to print.
     */
    public void printSample(DataTypeDoubleArray data) {

        String timeString = DateFormat.format("HH:mm:ss", new Date(data.getDateTime())).toString();

        double[] sample = data.getSample();
        Log.d("OutputData", mDevicePurpose + "::" + timeString + " [" + sample[0] + ", " + sample[1] + ", " + sample[2] + "]");
    }


    public void printAllInfo(String operation, BluetoothGattCharacteristic bgc) {
        String allInfo = TAG;
        Log.d(allInfo, "*********");
        Log.d(allInfo, "Operation: " + operation);
        Log.d(allInfo, "UUID: " + bgc.getUuid().toString());
        Log.d(allInfo, "Value: " + Arrays.toString(bgc.getValue()));
        Log.d(allInfo, "Permissions: " + bgc.getPermissions() + " Properties: " + bgc.getProperties());

        for (BluetoothGattDescriptor bgd : bgc.getDescriptors()) {
            Log.d(allInfo, " - - - - UUID:" + bgd.getUuid());
            Log.d(allInfo, " - - - - Value:" +  Arrays.toString(bgc.getValue()));
            Log.d(allInfo, " - - - - Perms:" +  bgd.getPermissions());


        }
    }
}

