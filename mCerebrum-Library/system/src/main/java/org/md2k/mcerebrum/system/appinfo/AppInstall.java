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

package org.md2k.mcerebrum.system.appinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;

import org.md2k.mcerebrum.commons.storage.Storage;
import org.md2k.mcerebrum.commons.storage.StorageType;
import org.md2k.mcerebrum.core.access.appinfo.AppBasicInfo;
import org.md2k.mcerebrum.core.access.appinfo.AppCP;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;
import org.md2k.mcerebrum.core.internet.download.DownloadFile;
import org.md2k.mcerebrum.core.internet.download.DownloadInfo;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Provides methods for managing the installation of applications.
 */
public class AppInstall {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = AppInstall.class.getSimpleName();

    /** Code to verify request replies. <p>Default is 2000.</p>. */
    private static final int REQUEST_CODE = 2000;

    /**
     * Determines what type of app to download.
     *
     * <p>
     *     Supported types are:
     *     <ul>
     *         <li><code>"market://"</code> which is downloaded from the Google Playstore.</li>
     *         <li><code>".json"</code> which is installed through the <code>AppFromJson</code> class.</li>
     *         <li><code>".apk"</code> which is downloaded and sideloaded.</li>
     *     </ul>
     *     If those cases fail, an unkown install type is returned.
     * </p>
     *
     * @param context Android context.
     * @param packageName Name of package to download.
     * @return The installation type of the application to download.
     */
    private static MCEREBRUM.APP.TYPE_DOWNLOAD getDownloadType(Context context, String packageName) {
        String d = AppCP.getDownloadLink(context, packageName);
        if (d == null) return MCEREBRUM.APP.TYPE_DOWNLOAD.UNKNOWN;
        if (d.toLowerCase().startsWith("market://"))
            return MCEREBRUM.APP.TYPE_DOWNLOAD.PLAYSTORE;
        if (d.toLowerCase().endsWith(".json"))
            return MCEREBRUM.APP.TYPE_DOWNLOAD.JSON;
        if (d.toLowerCase().endsWith(".apk"))
            return MCEREBRUM.APP.TYPE_DOWNLOAD.URL;
        return MCEREBRUM.APP.TYPE_DOWNLOAD.UNKNOWN;
    }

    /**
     * Sets the values of <code>isInstalled</code> and <code>currentVersion</code> if applicable.
     *
     * @param context Android context.
     * @param packageName Name of package
     */
    public static void set(Context context, String packageName) {
        boolean isInstalled = AppInfo.isPackageInstalled(context, packageName);
        String currentVersion = null;
        if (isInstalled) {
            currentVersion = AppInfo.getVersionName(context, packageName);
        }
        AppCP.setInstalled(context, packageName, isInstalled, currentVersion);
    }

    /**
     * Checks for an app update depending on the update settings.
     *
     * <p>
     *     If the app is set to never update, is not installed, or update information is unavailable
     *     then this method returns false.
     * </p>
     *
     * @param context Android context.
     * @param packageName Name of package
     * @return Whether the app has an update or not.
     */
    public static boolean hasUpdate(Context context, String packageName) {
        //never update app
        String update = AppCP.getUpdate(context, packageName);
        if (update == null || update.equalsIgnoreCase(MCEREBRUM.APP.UPDATE_TYPE_NEVER))
            return false;

        //App not installed
        boolean installed = AppCP.getInstalled(context, packageName);
        if (!installed)
            return false;

        // No update information
        if (AppCP.getLatestVersion(context, packageName) == null)
            return false;
        if(AppCP.getCurrentVersion(context, packageName) == null)
            return false;

        //Compare update and current
        if (AppCP.getCurrentVersion(context, packageName).equalsIgnoreCase(AppCP.getLatestVersion(context, packageName)))
            return false;
        return true;
    }

    /**
     * Checks for an app update.
     *
     * <p>
     *     If the app is set to never update, is not installed, or update information is unavailable
     *     then this method returns <code>Observable.just(false)</code>.
     * </p>
     *
     * @param context Android context.
     * @param packageName Name of package
     * @return A boolean observer.
     */
    public static Observable<Boolean> checkUpdate(final Context context, final String packageName) {
        String update = AppCP.getUpdate(context, packageName);

        if (update == null || update.equalsIgnoreCase(MCEREBRUM.APP.UPDATE_TYPE_NEVER))
            return Observable.just(false);

        String expectedVersion = AppCP.getExpectedVersion(context, packageName);

        if (expectedVersion != null) {
            AppCP.setLatestVersion(context, packageName, expectedVersion);
            return Observable.just(hasUpdate(context, packageName));
        }

        if (!getDownloadType(context, packageName).equals(MCEREBRUM.APP.TYPE_DOWNLOAD.JSON))
            return Observable.just(false);

        return new AppFromJson().getVersion(context, AppCP.getDownloadLink(context, packageName))
                                .map(new Func1<VersionInfo, Boolean>() {
            /**
             * Logs the version information of the app and calls <code>hasUpdate</code>.
             *
             * @param versionInfo Version information for the app.
             * @return Whether the app has an update or not.
             */
            @Override
            public Boolean call(VersionInfo versionInfo) {
                Log.d(TAG, packageName + " versionInfo = " + versionInfo);
                if(versionInfo == null)
                    return false;
                Log.d(TAG, packageName + " versionInfo = " + versionInfo.versionName);
                AppCP.setLatestVersion(context, packageName, versionInfo.versionName);
                return hasUpdate(context, packageName);
            }
        });
    }

    /**
     * Checks for multiple app updates and merges the resulting <code>Observables</code>.
     *
     * @param context Android context.
     * @return A merged boolean observer.
     */
    public static Observable<Boolean> checkUpdate(Context context){
        ArrayList<String> p = AppBasicInfo.get(context);
        ArrayList<Observable<Boolean>> o = new ArrayList<>();
        for(int i = 0; i < p.size(); i++){
            o.add(checkUpdate(context, p.get(i)));
        }
        return Observable.merge(o);
    }


    /**
     * Fetches the download information and installs the given package.
     *
     * @param activity Current activity.
     * @param packageName Name of the package to install.
     * @return A <code>DownloadInfo</code> observer.
     */
    public static Observable<DownloadInfo> install(final Activity activity, String packageName) {
        final String dirName = Storage.getRootDirectory(activity, StorageType.SDCARD_INTERNAL) + "/mCerebrum/temp";
        final String fileName = "temp.apk";
        Observable<VersionInfo> observable = null;

        switch (getDownloadType(activity, packageName)) {
            case PLAYSTORE:
                Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(AppCP.getDownloadLink(activity, packageName)));
                goToMarket.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivityForResult(goToMarket, REQUEST_CODE);
                return Observable.just(new DownloadInfo(0, 0, true));

            case JSON:
                observable = new AppFromJson().getVersion(activity, AppCP.getDownloadLink(activity, packageName));
                break;

            case URL:
                VersionInfo versionInfo = new VersionInfo();
                versionInfo.downloadURL = AppCP.getDownloadLink(activity, packageName);
                observable = Observable.just(versionInfo);
                break;
        }
        if (observable != null) {
            return observable.flatMap(new Func1<VersionInfo, Observable<DownloadInfo>>() {
                /**
                 * Downloads the needed install files.
                 *
                 * @param versionInfo Version information for the app
                 * @return A <code>DownloadInfo</code> observer.
                 */
                @Override
                public Observable<DownloadInfo> call(VersionInfo versionInfo) {
                    if (versionInfo == null) {
                        return Observable.just(null);
                    }
                    return new DownloadFile().download(versionInfo.downloadURL, dirName, fileName)
                            .subscribeOn(Schedulers.computation());
                }
            }).map(new Func1<DownloadInfo, DownloadInfo>() {
                /**
                 * Gets new <code>DownloadInfo</code> if needed and then calls
                 * <code>AppUtils.installApp()</code> to install the app.
                 *
                 * @param downloadInfo Download information
                 * @return
                 */
                @Override
                public DownloadInfo call(DownloadInfo downloadInfo) {
                    if(downloadInfo == null)
                        return new DownloadInfo(0,0,true);
                    if (downloadInfo.isCompleted()) {
                        AppUtils.installApp(activity, dirName + "/" + fileName,
                                "org.md2k.mcerebrum.provider_file", REQUEST_CODE);
                    }
                    return downloadInfo;
                }
            });
        } else return Observable.error(new Throwable("File can't be downloaded"));
    }


    /**
     * Calls on <code>AppUtils.uninstallApp()</code> to uninstall the named app.
     *
     * @param activity Current activity.
     * @param packageName Name of the package.
     * @param requestCode Request verification code. Should be 2000.
     */
    public static void uninstall(Activity activity, String packageName, int requestCode) {
        AppUtils.uninstallApp(activity, packageName, requestCode);
    }

    /**
     * Checks whether the required apps are installed.
     *
     * @param context Android context.
     * @return Whether the required apps are installed or not.
     */
    public static boolean isRequiredAppInstalled(Context context) {
        ArrayList<String> packageNames = AppBasicInfo.get(context);
        for(int i = 0; i < packageNames.size(); i++) {
            String useAs = AppCP.getUseAs(context, packageNames.get(i));
            if (useAs == null || !useAs.equalsIgnoreCase(MCEREBRUM.APP.USE_AS_REQUIRED))
                continue;
            if (!AppCP.getInstalled(context, packageNames.get(i)))
                return false;
        }
        return true;
    }

    /**
     * Gets the number of installed apps, apps with updates, and total apps.
     *
     * @param context Android context.
     * @return An integer array with the total number of apps that are required, number of apps with
     * updates and number of apps that are not installed.
     */
    public static int[] getInstallStatus(Context context) {
        int result[] = new int[3];
        result[0] = 0;
        result[1] = 0;
        result[2] = 0;
        ArrayList<String> packageNames = AppBasicInfo.get(context);

        for (int i = 0; i < packageNames.size(); i++) {
            if (!AppCP.getInstalled(context, packageNames.get(i)))
                result[2]++;
            else if (AppInstall.hasUpdate(context, packageNames.get(i)))
                result[1]++;
            else result[0]++;
        }
        return result;
    }

    /**
     * Returns the current version of the package.
     *
     * @param context Android context.
     * @param packageName Name of package.
     * @return The current version of the package.
     */
    public static String getCurrentVersion(Context context, String packageName) {
        String versionName = AppCP.getCurrentVersion(context, packageName);
        if(versionName == null)
            versionName = "not installed";
        return versionName;
    }

    /**
     * Returns the latest version name of the package.
     *
     * @param context Android context.
     * @param packageName Name of package
     * @return The last version. If the package is already the latest version, it returns <code>"up-to-date"</code>.
     */
    public static String getLatestVersion(Context context, String packageName) {
        String lastVersionName = AppCP.getLatestVersion(context, packageName);
        if(lastVersionName == null)
            lastVersionName = "up-to-date";
        return lastVersionName;
    }

    /**
     * Returns whether the passed package is installed or not.
     *
     * @param context Android context.
     * @param packageName Name of package
     * @return Whether the named package is installed or not.
     */
    public static boolean getInstalled(Context context, String packageName) {
        return AppCP.getInstalled(context, packageName);
    }

    /**
     * Checks for Study apps and DataKit installations.
     *
     * @param context Android context.
     * @return Whether Study or DataKit have installed applications.
     */
    public static boolean isCoreInstalled(Context context) {
        ArrayList<String> apps = AppBasicInfo.getStudy(context);
        if(apps == null || apps.size() == 0)
            return false;
        if(!AppInstall.getInstalled(context, apps.get(0)))
            return false;
        String app = AppBasicInfo.getDataKit(context);
        if(app == null)
            return false;
        if(!AppInstall.getInstalled(context, app))
            return false;
        return true;
    }

    /**
     * Returns a list of apps tat are required, but not installed.
     *
     * @param context Android context.
     * @return A list of apps that are required, but not installed.
     */
    public static ArrayList<String> getRequiredAppNotInstalled(Context context) {
        ArrayList<String> packageNames = AppBasicInfo.get(context);
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < packageNames.size(); i++) {
            String useAs = AppCP.getUseAs(context, packageNames.get(i));
            if (useAs == null || !useAs.equalsIgnoreCase(MCEREBRUM.APP.USE_AS_REQUIRED))
                continue;
            if(AppCP.getInstalled(context, packageNames.get(i)))
                continue;
            list.add(packageNames.get(i));
        }
        return list;
    }

    /**
     * Sets the values of <code>isInstalled</code> and <code>currentVersion</code> for multiple apps.
     *
     * @param context Android context.
     */
    public static void set(Context context) {
        ArrayList<String> apps = AppBasicInfo.get(context);
        for(int i = 0; i < apps.size(); i++)
            set(context, apps.get(i));
    }

    /**
     * Counts the number of apps that have an update available.
     *
     * @param context Android context.
     * @return The number of apps that have an update available.
     */
    public static int hasUpdate(Context context) {
        int count = 0;
        ArrayList<String> apps = AppBasicInfo.get(context);
        for(int i = 0; i < apps.size(); i++)
            if(hasUpdate(context, apps.get(i)))
                count++;
        return count;
    }
}
