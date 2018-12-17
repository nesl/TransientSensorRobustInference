package org.md2k.motionsense;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/*
    This class exports data to a CSV file.
    All files are stored under Documents/MD2KHF
    Each device sensor has its own folder (i.e. Phone-ACC)

    This file also uses the ntpUpdateThread to update the NTP times and store them with
      the data values.

 */
public class exporter {

    final String TAG = "DBG-exporter";

    //This is the filename (i.e. 2018-12-16.csv)
    String mCurrentDateString = "";

    //String mCurrentGroundTruth = "NULL";

    //This is the thread for updating the NTP values
    ntpUpdateThread ntpThread = null;

    // This is the timestamp when the NTP was last updated
    private long last_time_updated = 0;
    private long update_delay_millis = 10000; //Every 10 seconds we update the NTP time

    public exporter() {
        mCurrentDateString = getCurrentDate();
        Log.d(TAG, "Setting current date: " + mCurrentDateString);

        //Get the ntp update thread so we can add the clock offset to each entry
        ntpThread = new ntpUpdateThread();
        last_time_updated = System.currentTimeMillis();
    }

    //Get the current date for creating the file
    private String getCurrentDate() {

        String currentDateString = "";
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        currentDateString = formatter.format(new Date(System.currentTimeMillis()));
        return currentDateString;
    }

    /*public void setGT(String gt) {
        mCurrentGroundTruth = gt;
    }

    public long getCurrentCount() {
        return currentNumberOfValues;
    }*/


    //Exports a set of values to a CSV file
    /*
          Params:  String folderName - directory name of the folder (i.e. Phone-ACC)
                   String message - values that we are appending to the CSV file.
                                    Usually the message will be timestamp, x, y, z
     */
    public boolean exportData(String folderName, String message) {

        //Update the NTP time if sufficient time has passed
        if(System.currentTimeMillis() > last_time_updated + update_delay_millis) {
            last_time_updated = System.currentTimeMillis();
            ntpThread.getNTPTime();
        }


        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d(TAG, "No SD card, can't export Mood Data");
            return false;
        } else {
            //We use the Documents directory for saving our .csv file.
            File exportRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File exportDir = new File(exportRoot, "MD2KHF/" + folderName);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                file = new File(exportDir, mCurrentDateString + ".csv");
                file.createNewFile();

                //This is set in append mode - if the file already exists we append this data to it
                printWriter = new PrintWriter(new FileWriter(file, true));

                //Append the data to the file
                printWriter.println(message + "," + Long.toString(ntpThread.getOffset()));
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
