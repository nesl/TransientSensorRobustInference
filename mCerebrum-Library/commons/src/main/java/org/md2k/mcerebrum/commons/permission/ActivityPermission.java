package org.md2k.mcerebrum.commons.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Activity for getting permissions
 */
public class ActivityPermission extends Activity {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = ActivityPermission.class.getSimpleName();

    /** Result code constant for overlay permissions. */
    private static final int RESULT_MANAGE_OVERLAY_PERMISSION = 5469;

    /** Result code constant for permission results. */
    private static final int RESULT_PERMISSION = 5470;


    /**
     * Checks that the Android version is M (version 6.0, API level 23) or higher and requests permissions.
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            close(true);
        } else {
                String list[] = PermissionInfo.getList(this);
                if (list != null)
                    requestPermissions(list, RESULT_PERMISSION);
                else
                    close(true);
            }
    }

    /**
     * Creates an intent that is broadcast to notify the system about permissions information.
     * @param result Result value to pass to <code>PermissionInfo</code>.
     */
    void close(boolean result) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(PermissionInfo.INTENT_RESULT, result);
        setResult(Activity.RESULT_OK, resultIntent);
        Intent intent = new Intent(PermissionInfo.INTENT_PERMISSION);
        intent.putExtra(PermissionInfo.INTENT_PERMISSION_RESULT, result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

    /**
     * For Android versions M or higher (version 6.0+, API level 23+), permissions are requested via
     * an overlay.
     * @param requestCode Code sent with the request.
     * @param resultCode Code returned with the result.
     * @param data
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_MANAGE_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this))
                requestPermissions(PermissionInfo.getList(getApplicationContext()), RESULT_PERMISSION);
            else
                close(false);
        }
    }

    /**
     * Determines if all the required permissions have been granted or not.
     * @param requestCode Code sent with the request.
     * @param permissions Array of required permissions.
     * @param grantResults Array of values determining whether permissions were granted or not.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean flag = true;
        switch (requestCode) {
            case RESULT_PERMISSION: {
                if (grantResults.length == 0)
                    flag = false;
                else {
                    for (int grantResult : grantResults) {
                        if (grantResult == PackageManager.PERMISSION_DENIED) {
                            flag = false;
                            break;
                        }
                    }
                }
                close(flag);
            }
        }
    }
}