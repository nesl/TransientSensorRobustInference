package org.md2k.demoapp.classifiers;

import android.content.Context;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.List;

public class tfclassifier_2_m {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private final static String TAG = "DBG-tfclass2_m";
    private static final int N_SAMPLES = 30;
    private static final int num_channels = 3;
    private static List<Float> x;
    private static List<Float> y;
    private static List<Float> z;

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/frozen_motionsense.pb";
    //    private static final String MODEL_FILE = "file:///android_asset/frozen_har.pb";
    private static final String INPUT_NODE = "input";
    private static final String[] OUTPUT_NODES = {"y_"};
    private static final String OUTPUT_NODE = "y_";
    private static final long[] INPUT_SIZE = {1, 1, N_SAMPLES, num_channels};  //Window size and number of channels
    private static final int OUTPUT_SIZE = 3;  //Number of output labels

    public tfclassifier_2_m(final Context context) {
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);


        x = new ArrayList<>();
        y = new ArrayList<>();
        z = new ArrayList<>();
    }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    //Incoming string format will be of the form: x,y,z
    public float[] pushToList(String dataEntry) {
        float[] result = new float[OUTPUT_SIZE];
        String[] dataSplit = dataEntry.split(",");

        if(dataSplit.length >=3 ) {
            float x_in = Float.valueOf(dataSplit[0]);
            float y_in = Float.valueOf(dataSplit[1]);
            float z_in = Float.valueOf(dataSplit[2]);
            //Log.d(TAG, "Received split data" + x_in + "," + y_in + "," + z_in);
            x.add(x_in);
            y.add(y_in);
            z.add(z_in);
        }

        //We can make a prediction if we have enough data.
        //We only remove the first x,y,z point so we can predict every time new data comes in,
        //instead of having to predict in batches of N_SAMPLES
        if(x.size() + y.size() + z.size() >= N_SAMPLES*num_channels) {
            Log.d(TAG, "Making prediction: " + x.size() + " vals");
            List<Float> data = new ArrayList<>();
            data.addAll(x);
            data.addAll(y);
            data.addAll(z);
            result = predictProbabilities(toFloatArray(data));

            x.remove(0);
            y.remove(0);
            z.remove(0);

        }

        return result;
    }

    public float[] predictProbabilities(float[] data) {

        //Log.d(TAG, "Dimensions of data: " + data.length);
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        return result;
    }
}

