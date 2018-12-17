package org.md2k.demoapp;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataPackager {

    final String TAG = "DBG-DATAPACKAGER:";

    String mCurrentDateString = "";

    String mCurrentGroundTruth = "NULL";

    long currentNumberOfValues = 0;

    ntpUpdateThread ntpThread = null;

    private long last_time_updated = 0;
    private long update_delay_millis = 10000; //Every 10 seconds we update the NTP time

    public DataPackager() {
        mCurrentDateString = getCurrentDate();
        Log.d(TAG, "Setting current date: " + mCurrentDateString);

        //Get the ntp update thread so we can add the clock offset to each entry
        ntpThread = new ntpUpdateThread();
        last_time_updated = System.currentTimeMillis();
    }

    private String getCurrentDate() {

        String currentDateString = "";
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        currentDateString = formatter.format(new Date(System.currentTimeMillis()));
        return currentDateString;
    }

    public void setGT(String gt) {
        mCurrentGroundTruth = gt;
    }

    public long getCurrentCount() {
        return currentNumberOfValues;
    }


    //THIS EXPORTS ONE LINE OF DATA FROM A DATABASE
    public boolean exportData(String folderName, String message) {

        if(System.currentTimeMillis() > last_time_updated + update_delay_millis) {
            last_time_updated = System.currentTimeMillis();
            ntpThread.getNTPTime();
        }

        currentNumberOfValues += 1;

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d(TAG, "No SD card, can't export Mood Data");
            return false;
        } else {
            //We use the Download directory for saving our .csv file.
            File exportRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File exportDir = new File(exportRoot, "MD2KEXTRAS/" + folderName);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                file = new File(exportDir, mCurrentDateString + ".csv");
                file.createNewFile();
                printWriter = new PrintWriter(new FileWriter(file, true));

                printWriter.println(message + "," + mCurrentGroundTruth + "," + Long.toString(ntpThread.getOffset())); //write the record to the mood textfile
                //Log.d(TAG, "Successfully wrote to " + folderName);
            }

            catch(Exception exc) {
                //if there are any exceptions, return false
                Log.d(TAG, exc.getMessage());
                return false;
            }
            finally {
                if(printWriter != null) printWriter.close();
            }

            return true;
        }
    }



}
