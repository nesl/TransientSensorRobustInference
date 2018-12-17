package org.md2k.demoapp;

import android.os.SystemClock;
import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class ntpUpdateThread {

    private final String TAG = "DBG-ntpUpdate";

    private long recv_time = 0;
    private long ntp_offset = 0;



    public ntpUpdateThread() {
        getNTPTime();
    }

    //Gets the offset of the NTP clock, and gets a new offset depending on how long the delay is.
    public long getOffset() {

        return ntp_offset;
    }

    public void getNTPTime() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                String host = "time.apple.com";
                NTPUDPClient client = new NTPUDPClient();
                // We want to timeout if a response takes longer than 5 seconds
                client.setDefaultTimeout(2000);
                try {
                    TimeInfo timeInfo = null;
                    final InetAddress inetAddress = InetAddress.getByName(host);
                    long ntp_time = 0;
                    if (inetAddress != null) {
                        timeInfo = client.getTime(inetAddress);
                        if (timeInfo != null) {
                            long responseTicks = SystemClock.elapsedRealtime();
                            //Time on the client when it transmitted the NTP request
                            final long originateTimestamp = timeInfo.getMessage().getOriginateTimeStamp().getTime();
                            //Time on the NTP server when it received the NTP request
                            final long serverRecvTimestamp = timeInfo.getMessage().getReceiveTimeStamp().getTime();
                            //Time on the NTP server when it transmitted the NTP request
                            final long serverTransmitTimestamp = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            //Time on the client when it received the NTP response
                            final long responseTimestamp = timeInfo.getReturnTime();
                            //Log.d(TAG, "Response timestamp: " + Long.toString(responseTimestamp));

                            //Offset calculated from : https://www.perlmonks.org/?node_id=1115975 and https://www.eecis.udel.edu/~mills/time.html
                            final long offset = ((serverRecvTimestamp - originateTimestamp) + (serverTransmitTimestamp - responseTimestamp))/2;
                            Log.d(TAG, "OFFSET: " + Long.toString(offset));
                            ntp_offset = offset;
                            final long roundtrip_delay = (responseTimestamp - originateTimestamp) - (serverTransmitTimestamp - serverTransmitTimestamp);
                            Log.d(TAG, "Roundtrip Delay: " + Long.toString(roundtrip_delay));

                            final long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                            //Log.d(TAG, "Offset Needed: " + Long.toString(timeInfo.getDelay()));
                            //long serverTime = timeInfo.getReturnTime();
                            final Date ntpDate = new Date(serverTime);
                            ntp_time = ntpDate.getTime();
                            recv_time = timeInfo.getReturnTime();
                        }
                    }
                    Log.d(TAG, "UDP NTP Time: " + Long.toString(ntp_time));
                    Log.d(TAG, "DIFFERENCE SYS - UDP: " + Long.toString(recv_time - ntp_time));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.close();
            }
        };

        thread.start();

    }

}

