package org.md2k.motionsense.phone;

import android.content.Context;

import org.md2k.motionsense.exporter;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    This class manages all the listeners for sensor events on the phone.
    So far it only uses the accelerometer and gyroscope class (each implements its own onSensorChanged)

    This class is instantiated and called from ServiceMotionSense.
 */
public class sensorSourceManager {

    private Context ctx;
    Accelerometer accSensor;
    Gyroscope gyroSensor;

    ExecutorService executor;

    public sensorSourceManager(Context context, exporter exp) {
        ctx = context;

        executor = Executors.newSingleThreadExecutor();

        accSensor = new Accelerometer(ctx, exp, executor);
        gyroSensor = new Gyroscope(ctx, exp, executor);

    }

    public void registerListeners() {
        accSensor.register();
        gyroSensor.register();
    }

    public void destroy() {
        executor.shutdown();
        accSensor.unregister();
        gyroSensor.unregister();
    }

}
