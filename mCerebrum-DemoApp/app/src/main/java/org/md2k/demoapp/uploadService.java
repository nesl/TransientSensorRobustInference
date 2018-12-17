package org.md2k.demoapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.google.;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;

import java.io.File;

public class uploadService extends IntentService {

    //private String FirebaseURL = "gs://isee-msu.appspot.com";


    final private String TAG = "DBG-UploadService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public uploadService(String name) {
        super(name);
    }
    public uploadService() {
        super("uploadService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {


        Log.d(TAG, "Recieved Intent To Handle");

    }

    //Upload the data
    public void uploadResults(String[] sensorsUsed, String classifyResults) {

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //database.child("c_results").child();


        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");

    }
}
