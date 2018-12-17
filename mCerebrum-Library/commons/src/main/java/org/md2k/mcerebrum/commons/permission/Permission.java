/*
 * Copyright (c) 2018, The University of Memphis, MD2K Center of Excellence
 *
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

package org.md2k.mcerebrum.commons.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.PermissionChecker;
import android.util.Log;

import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

import static android.Manifest.permission.ACCESS_CHECKIN_PROPERTIES;
import static android.Manifest.permission.BATTERY_STATS;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;
import static android.Manifest.permission.READ_LOGS;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

/**
 * Handles permission requests and callbacks.
 */
public class Permission{

    /**
     * Requests for the needed permissions for the given activity.
     * @param activity Activity requesting permissions.
     * @param permissionCallback Callback interface used for requesting permissions.
     */
    public static void requestPermission(Activity activity, final PermissionCallback permissionCallback) {
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            RxPermissions rxPermissions = new RxPermissions(activity);
            rxPermissions.request(info.requestedPermissions).subscribe(new Action1<Boolean>() {
                /**
                 * Calls the <code>permissionCallback</code> interface.
                 * @param isGranted Whether permissions have been granted or not.
                 */
                @Override
                public void call(Boolean isGranted) {
                    permissionCallback.OnResponse(isGranted);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines if the app has the needed permissions.
     * @param context Android context
     * @return Whether the app has the needed permissions.
     */
    public static boolean hasPermission(Context context){

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            for (int i = 0; i < info.requestedPermissions.length; i++) {
                if(info.requestedPermissions[i].equals(READ_LOGS)) continue;
                if(info.requestedPermissions[i].equals(BATTERY_STATS)) continue;
                if(info.requestedPermissions[i].equals(ACCESS_CHECKIN_PROPERTIES)) continue;
                if(info.requestedPermissions[i].equals(PACKAGE_USAGE_STATS)) continue;
                if(info.requestedPermissions[i].equals(SYSTEM_ALERT_WINDOW)) continue;
                if(context.checkCallingOrSelfPermission(info.requestedPermissions[i])!= PermissionChecker.PERMISSION_GRANTED) {
                    Log.d("abc", "no permission = " + info.requestedPermissions[i]);
                    return false;
                }
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }
}
