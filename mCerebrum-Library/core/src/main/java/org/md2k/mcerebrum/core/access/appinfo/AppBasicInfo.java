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

package org.md2k.mcerebrum.core.access.appinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.md2k.mcerebrum.core.R;
import org.md2k.mcerebrum.core.constant.MCEREBRUM;

import java.util.ArrayList;


/**
 * Provides methods for retrieving and setting basic application information with <code>AppCP</code>.
 */
public class AppBasicInfo {

    /**
     * Returns the package name of mCerebrum app.
     * @param context Android context
     * @return The package name of mCerebrum app.
     */
    public static String getMCerebrum(Context context){
        ArrayList<String> packageNames = AppBasicInfo.get(context);
        for(int i = 0; i < packageNames.size(); i++) {
            String type = AppCP.getType(context, packageNames.get(i));
            if(type == null)
                return null;
            if(type.equalsIgnoreCase(MCEREBRUM.APP.TYPE_MCEREBRUM))
                return packageNames.get(i);
        }
        return null;
    }

    /**
     * Returns the package name of DataKit app.
     * @param context Android context.
     * @return The package name of DataKit app.
     */
    public static String getDataKit(Context context){
        ArrayList<String> packageNames = AppBasicInfo.get(context);
        for(int i = 0; i < packageNames.size(); i++) {
            String type = AppCP.getType(context, packageNames.get(i));
            if(type == null)
                return null;
            if(type.equalsIgnoreCase(MCEREBRUM.APP.TYPE_DATAKIT))
                return packageNames.get(i);
        }
        return null;
    }

    /**
     * Returns the package name of Study app.
     * @param context Android context.
     * @return The package name of Study app.
     */
    public static ArrayList<String> getStudy(Context context){
        ArrayList<String> packageNames = AppBasicInfo.get(context);
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < packageNames.size(); i++) {
            String type = AppCP.getType(context, packageNames.get(i));
            if(type == null)
                return null;
            if(type.equalsIgnoreCase(MCEREBRUM.APP.TYPE_STUDY))
                list.add(packageNames.get(i));
        }
        return list;
    }

    /**
     * Returns the given app's icon.
     * @param context Android context.
     * @param packageName Name of the package.
     * @param path File path of the package.
     * @return The given app's icon.
     */
    public static Drawable getIcon(Context context, String packageName, String path) {
        try {
            Drawable icon = context.getPackageManager().getApplicationIcon(packageName);
            if (icon != null)
                return icon;
        } catch (Exception ignored) {}
        try {
            if (AppCP.getIcon(context, packageName) != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path + AppCP.getIcon(context, packageName));
                if (bitmap != null)
                    return new BitmapDrawable(context.getResources(), bitmap);
            }
        } catch (Exception ignored) {}
        return context.getResources().getDrawable(R.drawable.ic_question);
    }

    /**
     * Returns an arraylist of package names.
     * @param context Android context.
     * @return An arraylist of package names.
     */
    public static ArrayList<String> get(Context context){
        ArrayList<String> packageNames = AppCP.read(context);
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < packageNames.size(); i++) {
            String useAs = AppCP.getUseAs(context, packageNames.get(i));
            if(useAs == null)
                continue;
            if(useAs.equalsIgnoreCase(MCEREBRUM.APP.USE_AS_NOT_IN_USE))
                continue;
            list.add(packageNames.get(i));
        }
        return list;
    }

    /**
     * Returns the title of the given package.
     * @param context Android context.
     * @param packageName Name of the package.
     * @return The title of the given package.
     */
    public static String getTitle(Context context, String packageName) {
        return AppCP.getTitle(context, packageName);
    }

    /**
     * Returns the summary of the given package.
     * @param context Android context.
     * @param packageName Name of the package.
     * @return The summary of the given package.
     */
    public static String getSummary(Context context, String packageName) {
        return AppCP.getSummary(context, packageName);
    }

    /**
     * Returns the description of the given package.
     * @param context Android context.
     * @param packageName Name of the package.
     * @return The description of the given package.
     */
    public static String getDescription(Context context, String packageName) {
        return AppCP.getDescription(context, packageName);
    }

    /**
     * Returns whether the package is required, optional, or not in use.
     * @param context Android context.
     * @param packageName Name of the package.
     * @return Whether the package is required, optional, or not in use.
     */
    public static String getUseAs(Context context, String packageName) {
        return AppCP.getUseAs(context, packageName);
    }

    /**
     * Returns the package type.
     * @param context Android context.
     * @param packageName Name of the package.
     * @return The package type.
     */
    public static String getType(Context context, String packageName) {
        return AppCP.getType(context, packageName);
    }

    /**
     * Sets the parameterized fields to the passed values.
     * @param context Android context.
     * @param package_name Name of the package.
     * @param type Application type.
     * @param title Application title.
     * @param summary Application summary.
     * @param description Application description.
     * @param use_as Either required, optional, or not in use.
     * @param download_link Link to download the application from.
     * @param update Update information for the application.
     * @param expected_version Expected version information
     * @param icon Application icon.
     * @param useInStudy Whether the application is being used in the study.
     */
    public static void set(Context context, String package_name, String type, String title,
                           String summary, String description, String use_as, String download_link,
                           String update, String expected_version, String icon, boolean useInStudy) {
        AppCP.set(context, package_name, type, title, summary, description, use_as, download_link,
                update, expected_version, icon, useInStudy);
    }
}
