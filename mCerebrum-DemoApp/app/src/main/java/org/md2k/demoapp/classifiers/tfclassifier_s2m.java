package org.md2k.demoapp.classifiers;

import android.content.Context;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.List;

public class tfclassifier_s2m {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private final static String TAG = "DBG-tfclass_s2m";
    private static final int N_SAMPLES = 30;
    private static final int num_channels = 6;
    private static List<Float> x1;
    private static List<Float> y1;
    private static List<Float> z1;
    private static List<Float> x2;
    private static List<Float> y2;
    private static List<Float> z2;

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/frozen_sensortag2motionsense.pb";
    //    private static final String MODEL_FILE = "file:///android_asset/frozen_har.pb";
    private static final String INPUT_NODE = "input";
    private static final String[] OUTPUT_NODES = {"y_"};
    private static final String OUTPUT_NODE = "y_";
    private static final long[] INPUT_SIZE = {1, 1, N_SAMPLES, num_channels};  //Window size and number of channels
    private static final int OUTPUT_SIZE = 3;  //Number of output labels

    public tfclassifier_s2m(final Context context) {
        inferenceInterface = new TensorFlowInferenceInterface(context.getAssets(), MODEL_FILE);


        x1 = new ArrayList<>();
        y1 = new ArrayList<>();
        z1 = new ArrayList<>();
        x2 = new ArrayList<>();
        y2 = new ArrayList<>();
        z2 = new ArrayList<>();
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
    public float[] pushToList(String dataEntry, String devPurpose) {
        float[] result = new float[OUTPUT_SIZE];
        String[] dataSplit = dataEntry.split(",");

        if(dataSplit.length >=3 ) {

            float x_in = Float.valueOf(dataSplit[0]);
            float y_in = Float.valueOf(dataSplit[1]);
            float z_in = Float.valueOf(dataSplit[2]);
            //Log.d(TAG, "Received split data" + x_in + "," + y_in + "," + z_in);

            if(devPurpose.equals("BlanketSensor")) {
                x1.add(x_in);
                y1.add(y_in);
                z1.add(z_in);

                if(x1.size() > 30) {  //Remove entries if we have too many
                    x1.remove(0);
                    y1.remove(0);
                    z1.remove(0);
                }
            }
            else if(devPurpose.equals("BodySensor")) {
                x2.add(x_in);
                y2.add(y_in);
                z2.add(z_in);

                if(x2.size() > 30) {  //Remove entries if we have too many
                    x2.remove(0);
                    y2.remove(0);
                    z2.remove(0);
                }
            }

        }

        //We can make a prediction if we have enough data.
        //We only remove the first x,y,z point so we can predict every time new data comes in,
        //instead of having to predict in batches of N_SAMPLES
        if(x1.size() + y1.size() + z1.size() +
                x2.size() + y2.size() + z2.size()>= N_SAMPLES*num_channels) {
            List<Float> data = new ArrayList<>();
            data.addAll(x1);
            data.addAll(y1);
            data.addAll(z1);
            data.addAll(x2);
            data.addAll(y2);
            data.addAll(z2);
            result = predictProbabilities(toFloatArray(data));

            x1.remove(0);
            y1.remove(0);
            z1.remove(0);
            x2.remove(0);
            y2.remove(0);
            z2.remove(0);

        }

        return result;
    }

    public float[] predictProbabilities(float[] data) {

        Log.d(TAG, "Dimensions of data: " + data.length);
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE, result);

        return result;
    }
}


