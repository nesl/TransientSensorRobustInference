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

package org.md2k.mcerebrum.core.access;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import org.md2k.mcerebrum.core.access.base.BaseSQLiteOpenHelperCallbacks;
import org.md2k.mcerebrum.core.access.SampleSQLiteOpenHelperCallbacks;
import org.md2k.mcerebrum.core.access.BuildConfig;
import org.md2k.mcerebrum.core.access.appinfo.AppInfoColumns;
import org.md2k.mcerebrum.core.access.configinfo.ConfigInfoColumns;
import org.md2k.mcerebrum.core.access.serverinfo.ServerInfoColumns;
import org.md2k.mcerebrum.core.access.studyinfo.StudyInfoColumns;
import org.md2k.mcerebrum.core.access.userinfo.UserInfoColumns;

/**
 * Provides methods for creating database table and starting the database.
 */
public class SampleProviderSQLiteOpenHelper extends SQLiteOpenHelper {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = SampleProviderSQLiteOpenHelper.class.getSimpleName();

    /** File name of the database. */
    public static final String DATABASE_FILE_NAME = "sample.db";

    /** Version number for the database. <p>Default is 1.</p> */
    private static final int DATABASE_VERSION = 1;

    /** An instance of this class. */
    private static SampleProviderSQLiteOpenHelper sInstance;

    /** Android context. */
    private final Context mContext;

    /** An instance of <code>BaseSQLiteOpenHelperCallbacks</code>. */
    private final BaseSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    /**
     * SQL command to create a table for application information.
     */
    public static final String SQL_CREATE_TABLE_APP_INFO = "CREATE TABLE IF NOT EXISTS "
            + AppInfoColumns.TABLE_NAME + " ( "
            + AppInfoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + AppInfoColumns.PACKAGE_NAME + " TEXT, "
            + AppInfoColumns.TYPE + " TEXT DEFAULT 'other', "
            + AppInfoColumns.TITLE + " TEXT, "
            + AppInfoColumns.SUMMARY + " TEXT, "
            + AppInfoColumns.DESCRIPTION + " TEXT, "
            + AppInfoColumns.USE_IN_STUDY + " INTEGER DEFAULT 0, "
            + AppInfoColumns.USE_AS + " TEXT DEFAULT 'optional', "
            + AppInfoColumns.INSTALLED + " INTEGER DEFAULT 0, "
            + AppInfoColumns.DOWNLOAD_LINK + " TEXT, "
            + AppInfoColumns.UPDATES + " TEXT DEFAULT 'notify', "
            + AppInfoColumns.CURRENT_VERSION + " TEXT, "
            + AppInfoColumns.LATEST_VERSION + " TEXT, "
            + AppInfoColumns.EXPECTED_VERSION + " TEXT, "
            + AppInfoColumns.ICON + " TEXT, "
            + AppInfoColumns.MCEREBRUM_SUPPORTED + " INTEGER DEFAULT 0, "
            + AppInfoColumns.FUNC_INITIALIZE + " TEXT, "
            + AppInfoColumns.INITIALIZED + " INTEGER DEFAULT 0, "
            + AppInfoColumns.FUNC_UPDATE_INFO + " TEXT, "
            + AppInfoColumns.FUNC_CONFIGURE + " TEXT, "
            + AppInfoColumns.CONFIGURED + " INTEGER DEFAULT 0, "
            + AppInfoColumns.CONFIGURE_MATCH + " INTEGER DEFAULT 0, "
            + AppInfoColumns.FUNC_PERMISSION + " TEXT, "
            + AppInfoColumns.PERMISSION_OK + " INTEGER DEFAULT 0, "
            + AppInfoColumns.FUNC_BACKGROUND + " TEXT, "
            + AppInfoColumns.BACKGROUND_RUNNING_TIME + " INTEGER DEFAULT 0, "
            + AppInfoColumns.IS_BACKGROUND_RUNNING + " INTEGER DEFAULT 0, "
            + AppInfoColumns.FUNC_REPORT + " TEXT, "
            + AppInfoColumns.FUNC_CLEAR + " TEXT, "
            + AppInfoColumns.DATAKIT_CONNECTED + " INTEGER DEFAULT 0 "
            + ", CONSTRAINT unique_name UNIQUE (package_name) ON CONFLICT REPLACE"
            + " );";

    /**
     * SQL command to create a table for configuration information.
     */
    public static final String SQL_CREATE_TABLE_CONFIG_INFO = "CREATE TABLE IF NOT EXISTS "
            + ConfigInfoColumns.TABLE_NAME + " ( "
            + ConfigInfoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ConfigInfoColumns.CID + " TEXT, "
            + ConfigInfoColumns.TYPE + " TEXT, "
            + ConfigInfoColumns.TITLE + " TEXT, "
            + ConfigInfoColumns.SUMMARY + " TEXT, "
            + ConfigInfoColumns.DESCRIPTION + " TEXT, "
            + ConfigInfoColumns.VERSIONS + " TEXT, "
            + ConfigInfoColumns.UPDATES + " TEXT DEFAULT 'manual', "
            + ConfigInfoColumns.EXPECTED_VERSION + " TEXT, "
            + ConfigInfoColumns.LATEST_VERSION + " TEXT, "
            + ConfigInfoColumns.DOWNLOAD_FROM + " TEXT DEFAULT 'url', "
            + ConfigInfoColumns.DOWNLOAD_LINK + " TEXT "
            + ", CONSTRAINT unique_name UNIQUE (cid, type) ON CONFLICT REPLACE"
            + " );";

    /**
     * SQL command to create a table for server information.
     */
    public static final String SQL_CREATE_TABLE_SERVER_INFO = "CREATE TABLE IF NOT EXISTS "
            + ServerInfoColumns.TABLE_NAME + " ( "
            + ServerInfoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ServerInfoColumns.SERVER_ADDRESS + " TEXT, "
            + ServerInfoColumns.USERNAME + " TEXT, "
            + ServerInfoColumns.UUID + " TEXT, "
            + ServerInfoColumns.PASSWORD_HASH + " TEXT, "
            + ServerInfoColumns.TOKEN + " TEXT, "
            + ServerInfoColumns.FILE_NAME + " TEXT, "
            + ServerInfoColumns.CURRENT_VERSION + " TEXT, "
            + ServerInfoColumns.LATEST_VERSION + " TEXT "
            + ", CONSTRAINT unique_name UNIQUE (username) ON CONFLICT REPLACE"
            + " );";

    /**
     * SQL command to create a table for study information.
     */
    public static final String SQL_CREATE_TABLE_STUDY_INFO = "CREATE TABLE IF NOT EXISTS "
            + StudyInfoColumns.TABLE_NAME + " ( "
            + StudyInfoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + StudyInfoColumns.SID + " TEXT, "
            + StudyInfoColumns.TYPE + " TEXT DEFAULT 'FREEBIE', "
            + StudyInfoColumns.TITLE + " TEXT, "
            + StudyInfoColumns.SUMMARY + " TEXT, "
            + StudyInfoColumns.DESCRIPTION + " TEXT, "
            + StudyInfoColumns.VERSION + " TEXT, "
            + StudyInfoColumns.ICON + " TEXT, "
            + StudyInfoColumns.COVER_IMAGE + " TEXT, "
            + StudyInfoColumns.START_AT_BOOT + " INTEGER, "
            + StudyInfoColumns.STARTED + " INTEGER "
            + ", CONSTRAINT unique_name UNIQUE (sid, type) ON CONFLICT REPLACE"
            + " );";

    /**
     * SQL command for creating a table for user information.
     */
    public static final String SQL_CREATE_TABLE_USER_INFO = "CREATE TABLE IF NOT EXISTS "
            + UserInfoColumns.TABLE_NAME + " ( "
            + UserInfoColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + UserInfoColumns.UID + " TEXT, "
            + UserInfoColumns.TYPE + " TEXT, "
            + UserInfoColumns.USERNAME + " TEXT "
            + ", CONSTRAINT unique_name UNIQUE (uid) ON CONFLICT REPLACE"
            + " );";

    /**
     * Returns this instance of <code>SampleProviderSQLiteOpenHelper</code>.
     *
     * <p>
     *     Use the application context, which will ensure that you
     *     don't accidentally leak an Activity's context.
     *     <a href="https://android-developers.googleblog.com/2009/01/avoiding-memory-leaks.html">
     *         Check this Android Dev blog post for more information.</a>
     * </p>
     *
     * @param context Application context.
     * @return This instance of <code>SampleProviderSQLiteOpenHelper</code>.
     */
    public static SampleProviderSQLiteOpenHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Returns a new instance of this class.
     *
     * <p>
     *     Method calls differ here depending on the version of Android. Pre Honeycomb (Version 3.0,
     *     API level 11) versions call the <code>SampleProviderSQLiteOpenHelper</code> constructor
     *     that does not use a <code>DatabaseErrorHandler</code>. Post Honeycomb versions call the
     *     constructor that takes a <code>DatabaseErrorHandler</code> parameter.
     * </p>
     * @param context Android context.
     * @return A new instance of this class.
     */
    private static SampleProviderSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /**
     * Wrapper method for <code>SampleProviderSQLiteOpenHelper()</code>.
     * <p>Pre Honeycomb</p>
     * @param context Android context.
     * @return A new instance of <code>SampleProviderSQLiteOpenHelper</code>.
     */
    private static SampleProviderSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new SampleProviderSQLiteOpenHelper(context);
    }

    /**
     * Pre Honeycomb constructor.
     *
     * @param context Android context
     */
    private SampleProviderSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new SampleSQLiteOpenHelperCallbacks();
    }


    /**
     * Wrapper method for <code>SampleProviderSQLiteOpenHelper()</code>.
     * <p>Post Honeycomb</p>
     * @param context Android context.
     * @return A new instance of <code>SampleProviderSQLiteOpenHelper</code>.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static SampleProviderSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new SampleProviderSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    /**
     * Post Honeycomb constructor
     *
     * @param context Android context
     * @param errorHandler Database error handler
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private SampleProviderSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new SampleSQLiteOpenHelperCallbacks();
    }


    /**
     * Creates SQL tables the given database for application, configuration, server, study, and user information.
     * <p>
     *     If the build configuration is set to debug, this is logged.
     * </p>
     * @param db Database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_APP_INFO);
        db.execSQL(SQL_CREATE_TABLE_CONFIG_INFO);
        db.execSQL(SQL_CREATE_TABLE_SERVER_INFO);
        db.execSQL(SQL_CREATE_TABLE_STUDY_INFO);
        db.execSQL(SQL_CREATE_TABLE_USER_INFO);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    /**
     * Checks if the database is read only when it is opened and registers the callback interface.
     * <p>
     *    If the database is read only, then foreign key constraints are enabled.
     * </p>
     * @param db Database
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    /**
     * Wrapper method for setting foreign key constraints.
     * <p>
     *     If the Android version is pre Jelly Bean (Version 4.1, API level 16) then
     *     <code>setForeignKeyConstraintsEnabledPreJellyBean()</code> is called.
     * </p>
     * @param db Database
     */
    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    /**
     * Enables foreign key constraints.
     * <p>
     *     Pre Jelly Bean
     * </p>
     * @param db Database
     */
    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    /**
     * Enables foreign key constraints.
     * <p>
     *     Post Jelly Bean
     * </p>
     * @param db Database
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Passes version information the callback interface.
     *
     * @param db Database
     * @param oldVersion Old version number
     * @param newVersion New version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
