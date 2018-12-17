package org.md2k.demoapp;


import android.net.Uri;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.w3c.dom.Node;

import java.util.Arrays;

public class androidWear extends WearableListenerService {
    private static final String TAG = "DBG-SensorRecvService";

    //private RemoteSensorManager sensorManager;

    @Override
    public void onCreate() {
        super.onCreate();

        //sensorManager = RemoteSensorManager.getInstance(this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged()");

        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem dataItem = dataEvent.getDataItem();
                Uri uri = dataItem.getUri();
                String path = uri.getPath();

                if (path.startsWith("/sensors/")) {
                    unpackSensorData(
                            Integer.parseInt(uri.getLastPathSegment()),
                            DataMapItem.fromDataItem(dataItem).getDataMap()
                    );
                }
            }
        }
    }

    private void unpackSensorData(int sensorType, DataMap dataMap) {
        int accuracy = dataMap.getInt("ACCURACY");
        long timestamp = dataMap.getLong("TIMESTAMP");
        float[] values = dataMap.getFloatArray("VALUES");

        Log.d(TAG, "Received sensor data " + sensorType + " = " + Arrays.toString(values));

        //sensorManager.addSensorData(sensorType, accuracy, timestamp, values);
    }
}
