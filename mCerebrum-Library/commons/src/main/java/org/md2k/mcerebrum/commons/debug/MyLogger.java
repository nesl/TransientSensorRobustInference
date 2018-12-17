package org.md2k.mcerebrum.commons.debug;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.CsvFormatStrategy;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;

import java.io.File;

public class MyLogger {
    public static void setLogger(final Context context){
        final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file
        String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folder = diskPath + File.separatorChar + "mCerebrum"+ File.separatorChar+"logger";

        HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
        ht.start();
        Handler handler = new MyWriteHandler(ht.getLooper(), folder, context.getPackageName(), MAX_BYTES);

        FormatStrategy formatStrategy = CsvFormatStrategy.newBuilder()
                .tag("MCEREBRUM")
                .logStrategy(new DiskLogStrategy(handler))
                .build();
        Logger.addLogAdapter(new DiskLogAdapter(formatStrategy){
            @Override public boolean isLoggable(int priority, String tag) {
                if(priority<=Log.VERBOSE) return false;

                boolean isDebuggable =  ( 0 != ( context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
                if(isDebuggable) return true;

                try {
                    int versionCode= context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;

                    if(versionCode%100==0)
                        //Release version
                        return false;
                    else if(priority>= Log.WARN) return true;
                    else return false;
                } catch (PackageManager.NameNotFoundException e) {
                    return false;
                }
            }
        });
        Logger.addLogAdapter(new AndroidLogAdapter(){
            @Override public boolean isLoggable(int priority, String tag) {
                boolean isDebuggable =  ( 0 != ( context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE ) );
                if(isDebuggable) return true;

                try {
                    int versionCode= context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;

                    if(versionCode%100==0)
                        //Release version
                        return false;
                    else if(priority>= Log.WARN) return true;
                    else return false;
                } catch (PackageManager.NameNotFoundException e) {
                    return false;
                }
            }
        });
    }
}
