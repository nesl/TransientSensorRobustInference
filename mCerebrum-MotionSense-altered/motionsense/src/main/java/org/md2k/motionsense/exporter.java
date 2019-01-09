package org.md2k.motionsense;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/*
    This class exports data to a CSV file.
    All files are stored under Documents/MD2KHF
    Each device sensor has its own folder (i.e. Phone-ACC)

    This file also uses the ntpUpdateThread to update the NTP times and store them with
      the data values.

 */
public class exporter {

    public class dataBuffer {
        String output = "";
        String filename = "";
        int bufferCount = 0;
        dataBuffer(String filename, String message) {
            this.filename = filename;
            this.output = "\n" + message;
        }

        void addToBuffer(String message) {
            output += "\n" + message;
            bufferCount++;

        }
        void clearBuffer() {
            output = "";
            bufferCount = 0;
        }

    }

    final String TAG = "DBG-exporter";

    //This is the filename (i.e. 2018-12-16.csv)
    String mCurrentDateString = "";

    //String mCurrentGroundTruth = "NULL";

    //This is the thread for updating the NTP values
    ntpUpdateThread ntpThread = null;

    // This is the timestamp when the NTP was last updated
    private long last_time_updated = 0;
    private long update_delay_millis = 10000; //Every 10 seconds we update the NTP time

    //Service Context - used for sending notifications
    private Context ctx;

    // This is the session number - basically, every time the user clicks start, a new session begins
    private int session_number = -1;

    //set up dataBuffer - this is so we can do writing in batches instead of line by line
    private List<dataBuffer> bufferList;

    public exporter(Context ctx) {
        this.ctx = ctx;
        mCurrentDateString = getCurrentDate();
        Log.d(TAG, "Setting current date: " + mCurrentDateString);

        //Get the ntp update thread so we can add the clock offset to each entry
        ntpThread = new ntpUpdateThread(this.ctx);
        last_time_updated = System.currentTimeMillis();

        bufferList = new ArrayList<dataBuffer>();
    }

    //Get the current date for creating the file
    private String getCurrentDate() {

        String currentDateString = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        currentDateString = formatter.format(new Date(System.currentTimeMillis()));
        return currentDateString;
    }

    /*public void setGT(String gt) {
        mCurrentGroundTruth = gt;
    }

    public long getCurrentCount() {
        return currentNumberOfValues;
    }*/


    //Buffer data to be exported later - basically, instead of writing line by line,
    // write a large number of lines at once occasionally
    public void bufferData(String folderName, String message) {

        //Update the NTP time if sufficient time has passed
        if(System.currentTimeMillis() > last_time_updated + update_delay_millis) {
            last_time_updated = System.currentTimeMillis();
            ntpThread.getNTPTime();
        }

        boolean foundData = false;

        //For each databuffer, we check if it matches the current folder
        // If it does, we add the message to its buffer
        for (dataBuffer d : bufferList) {
            if (d.filename.equals(folderName)) {
                foundData = true;
                d.addToBuffer(message + "," + Long.toString(ntpThread.getOffset()));

                //If we have greater than 30 items in the buffer, we export the items
                if(d.bufferCount > 30) {
                    exportData(d.filename, d.output);
                    d.clearBuffer();
                }
            }
        }

        //If this folder is not already in the list of databuffers, we add it.
        if(!foundData) {
            dataBuffer d = new dataBuffer(folderName, message + "," + Long.toString(ntpThread.getOffset()));
            bufferList.add(d);
        }
    }




    //Exports a set of values to a CSV file
    /*
          Params:  String folderName - directory name of the folder (i.e. Phone-ACC)
                   String message - values that we are appending to the CSV file.
                                    Usually the message will be timestamp, x, y, z
     */
    public boolean exportData(String folderName, String message) {


        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.d(TAG, "No SD card, can't export Mood Data");
            return false;
        } else {
            //We use the Documents directory for saving our .csv file.
            File exportRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File exportDir = new File(exportRoot, "MD2KHF/" + folderName + "/" + mCurrentDateString);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file;
            PrintWriter printWriter = null;
            try
            {
                //If we don't have a session number, we find what session we are on.
                //If the current session number is 0 (i.e. we haven't started the sessions for this exporter)
                //  Then we check the directory to see if we already have an active session for today
                //  We use the number of files in the directory to determine what session we are on.
                if(session_number == -1) {
                    if(exportDir.listFiles() != null) {
                        session_number = exportDir.listFiles().length;
                    }
                }
                file = new File(exportDir, Integer.toString(session_number) +  ".csv");
                file.createNewFile();

                //This is set in append mode - if the file already exists we append this data to it
                printWriter = new PrintWriter(new FileWriter(file, true));

                //Append the data to the file
                printWriter.print(message);
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
