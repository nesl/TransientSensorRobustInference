package org.md2k.motionsense;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;


/*
    This class manages all NTP calculations for this app
    It uses apache commons net library to get information from an NTP packet
    This class is called from exporter.java - when we export data and sufficient time passes, we call
      the getNTPTime() function and include that in the CSV entries.
 */
public class ntpUpdateThread {

    private final String TAG = "DBG-ntpUpdate";

    // This is the time at which the NTP packet is received on this device
    private long recv_time = 0;

    //This is the calculated offset of this device compared to the NTP server
    private long ntp_offset = 0;

    //Context that we use for sending notifications
    private Context ctx;


    //We get the NTP offset the first time we call this class
    public ntpUpdateThread(Context ctx) {
        this.ctx = ctx;
        getNTPTime();
    }

    //Gets the offset of the NTP clock, and gets a new offset depending on how long the delay is.
    public long getOffset() {

        return ntp_offset;
    }

    // In another thread, send a request to an NTP server
    //   A few things about this function:
    //     It actually computes 2 NTP offset calculations.
    //     ntp_offset calculates it as shown in https://www.eecis.udel.edu/~mills/time.html
    //     while offset2 calculates it as "time of NTP response received by client" - "time of NTP response transmitted from server"
    //     offset2 is what is calculated in the app "Atomic Clock"
    public void getNTPTime() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                String host = "time.apple.com";
                NTPUDPClient client = new NTPUDPClient();
                // We want to timeout if a response takes longer than 2 seconds
                client.setDefaultTimeout(2000);
                try {
                    TimeInfo timeInfo = null;
                    final InetAddress inetAddress = InetAddress.getByName(host);


                    long ntp_time = 0;  //This is the time at which the server transmits the NTP response packet
                    if (inetAddress != null) {

                        //Time recorded by the client
                        final long systemTimestampOriginate = System.currentTimeMillis();

                        //Send the NTP request
                        timeInfo = client.getTime(inetAddress);
                        if (timeInfo != null) {

                            final long systemTimestampReceived = System.currentTimeMillis();

                            //Time on the client when it transmitted the NTP request
                            final long originateTimestamp = timeInfo.getMessage().getOriginateTimeStamp().getTime();

                            //Time on the NTP server when it received the NTP request
                            final long serverRecvTimestamp = timeInfo.getMessage().getReceiveTimeStamp().getTime();
                            //Time on the NTP server when it transmitted the NTP request
                            final long serverTransmitTimestamp = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            //Time on the client when it received the NTP response
                            final long responseTimestamp = timeInfo.getReturnTime();

                            //Offset calculated from : https://www.perlmonks.org/?node_id=1115975 and https://www.eecis.udel.edu/~mills/time.html
                            final long offset = -((serverRecvTimestamp - originateTimestamp) + (serverTransmitTimestamp - responseTimestamp))/2;
                            Log.d(TAG, "OFFSET: " + Long.toString(offset));
                            ntp_offset = offset;

                            //Calculate the time spent in transit - This is the total time of the packet being sent and received from the client,
                            //  subtracted with the amount of time spent processing on the server
                            final long roundtrip_delay = (responseTimestamp - originateTimestamp) - (serverTransmitTimestamp - serverRecvTimestamp);
                            Log.d(TAG, "Roundtrip Delay: " + Long.toString(roundtrip_delay));

                            final long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            final Date ntpDate = new Date(serverTime);
                            ntp_time = ntpDate.getTime();

                            recv_time = timeInfo.getReturnTime();  //This is the time at which the packet was recieved by the client

                            //Log.d(TAG, "Originate Timestamp - Packet vs SystemTime: " + Long.toString(originateTimestamp) + " - " + Long.toString(systemTimestampOriginate));
                            //Log.d(TAG, "Receieve Timestamp - Packet vs SystemTime: " + Long.toString(recv_time) + " - " + Long.toString(systemTimestampReceived));

                        }
                    }
                    //Log.d(TAG, "UDP NTP Time: " + Long.toString(ntp_time));
                    long offset2 = recv_time - ntp_time;
                    Log.d(TAG, "OFFSET SYS - UDP: " + Long.toString(offset2));

                } catch (IOException e) {  //We get an error if the request times out
                    e.printStackTrace();
                    notifyUser("NTP Timeout! Are you connected to the correct WiFi?");  //Notify the user that something went wrong with the NTP request
                }
                client.close();
            }
        };

        thread.start();

    }

    //Let the user know if something is wrong with the NTP request
    private void notifyUser(String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx);

        //Create the intent that’ll fire when the user taps the notification//

        Intent intent = new Intent(ctx, ActivityMain.class);
        intent.putExtra("notification", 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Alert!");
        mBuilder.setContentText(message);
        mBuilder.setPriority(Notification.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification surveyNotification = mBuilder.build();
        surveyNotification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(299, surveyNotification);

    }


}


