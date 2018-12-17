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

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.md2k.mcerebrum.core.access.BuildConfig;
import org.md2k.mcerebrum.core.access.base.BaseContentProvider;
import org.md2k.mcerebrum.core.access.appinfo.AppInfoColumns;
import org.md2k.mcerebrum.core.access.configinfo.ConfigInfoColumns;
import org.md2k.mcerebrum.core.access.serverinfo.ServerInfoColumns;
import org.md2k.mcerebrum.core.access.studyinfo.StudyInfoColumns;
import org.md2k.mcerebrum.core.access.userinfo.UserInfoColumns;

/**
 * Content provider for samples
 */
public class SampleProvider extends BaseContentProvider {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = SampleProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "org.md2k.mcerebrum.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_APP_INFO = 0;
    private static final int URI_TYPE_APP_INFO_ID = 1;

    private static final int URI_TYPE_CONFIG_INFO = 2;
    private static final int URI_TYPE_CONFIG_INFO_ID = 3;

    private static final int URI_TYPE_SERVER_INFO = 4;
    private static final int URI_TYPE_SERVER_INFO_ID = 5;

    private static final int URI_TYPE_STUDY_INFO = 6;
    private static final int URI_TYPE_STUDY_INFO_ID = 7;

    private static final int URI_TYPE_USER_INFO = 8;
    private static final int URI_TYPE_USER_INFO_ID = 9;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, AppInfoColumns.TABLE_NAME, URI_TYPE_APP_INFO);
        URI_MATCHER.addURI(AUTHORITY, AppInfoColumns.TABLE_NAME + "/#", URI_TYPE_APP_INFO_ID);
        URI_MATCHER.addURI(AUTHORITY, ConfigInfoColumns.TABLE_NAME, URI_TYPE_CONFIG_INFO);
        URI_MATCHER.addURI(AUTHORITY, ConfigInfoColumns.TABLE_NAME + "/#", URI_TYPE_CONFIG_INFO_ID);
        URI_MATCHER.addURI(AUTHORITY, ServerInfoColumns.TABLE_NAME, URI_TYPE_SERVER_INFO);
        URI_MATCHER.addURI(AUTHORITY, ServerInfoColumns.TABLE_NAME + "/#", URI_TYPE_SERVER_INFO_ID);
        URI_MATCHER.addURI(AUTHORITY, StudyInfoColumns.TABLE_NAME, URI_TYPE_STUDY_INFO);
        URI_MATCHER.addURI(AUTHORITY, StudyInfoColumns.TABLE_NAME + "/#", URI_TYPE_STUDY_INFO_ID);
        URI_MATCHER.addURI(AUTHORITY, UserInfoColumns.TABLE_NAME, URI_TYPE_USER_INFO);
        URI_MATCHER.addURI(AUTHORITY, UserInfoColumns.TABLE_NAME + "/#", URI_TYPE_USER_INFO_ID);
    }

    /**
     * Creates an instance of <code>SampleProviderSQLiteOpenHelper</code>.
     * @return An <code>SQLiteOpenHelper</code>
     */
    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return SampleProviderSQLiteOpenHelper.getInstance(getContext());
    }

    /**
     * Determines if the app is in debugging mode.
     * @return Whether the app is in debugging mode or not.
     */
    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    /**
     * Returns the type of the given content.
     * @param uri Content to return the type of.
     * @return The type of the passed content.
     */
    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_APP_INFO:
                return TYPE_CURSOR_DIR + AppInfoColumns.TABLE_NAME;
            case URI_TYPE_APP_INFO_ID:
                return TYPE_CURSOR_ITEM + AppInfoColumns.TABLE_NAME;

            case URI_TYPE_CONFIG_INFO:
                return TYPE_CURSOR_DIR + ConfigInfoColumns.TABLE_NAME;
            case URI_TYPE_CONFIG_INFO_ID:
                return TYPE_CURSOR_ITEM + ConfigInfoColumns.TABLE_NAME;

            case URI_TYPE_SERVER_INFO:
                return TYPE_CURSOR_DIR + ServerInfoColumns.TABLE_NAME;
            case URI_TYPE_SERVER_INFO_ID:
                return TYPE_CURSOR_ITEM + ServerInfoColumns.TABLE_NAME;

            case URI_TYPE_STUDY_INFO:
                return TYPE_CURSOR_DIR + StudyInfoColumns.TABLE_NAME;
            case URI_TYPE_STUDY_INFO_ID:
                return TYPE_CURSOR_ITEM + StudyInfoColumns.TABLE_NAME;

            case URI_TYPE_USER_INFO:
                return TYPE_CURSOR_DIR + UserInfoColumns.TABLE_NAME;
            case URI_TYPE_USER_INFO_ID:
                return TYPE_CURSOR_ITEM + UserInfoColumns.TABLE_NAME;
        }
        return null;
    }

    /**
     * Inserts the given content to the database.
     * @param uri Content to insert.
     * @param values Content values to insert.
     * @return A copy of the inserted URI with the rowId appended.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    /**
     * Inserts multiple rows into the database.
     * @param uri Content to insert.
     * @param values Content values to insert.
     * @return The number of rows added.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    /**
     * Updates the selected rows with the new content values.
     * @param uri Content to update.
     * @param values New content values.
     * @param selection Selection to update.
     * @param selectionArgs Arguments for the selection string.
     * @return The number of rows updated.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection +
                " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    /**
     * Deletes the selected rows from the content database.
     * @param uri Content to delete.
     * @param selection Selection to delete.
     * @param selectionArgs Arguments for the selection string.
     * @return The number of rows deleted.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" +
                Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    /**
     * Constructs a query for the database and returns a cursor over the result set.
     * @param uri Content to query for.
     * @param projection Attributes to query.
     * @param selection Selection to query.
     * @param selectionArgs Arguments for the selection string.
     * @param sortOrder Order to sort the results in.
     * @return A cursor over the result set.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" +
                    Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder + " groupBy=" +
                    uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) +
                    " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * Returns an object containing parameter information for the query.
     * @param uri Content to query for.
     * @param projection Attributes to query.
     * @param selection Selection to query.
     * @return A <code>QueryParams</code> object containing the parameters of the query statement as
     * strings.
     */
    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_APP_INFO:
            case URI_TYPE_APP_INFO_ID:
                res.table = AppInfoColumns.TABLE_NAME;
                res.idColumn = AppInfoColumns._ID;
                res.tablesWithJoins = AppInfoColumns.TABLE_NAME;
                res.orderBy = AppInfoColumns.DEFAULT_ORDER;
                break;
            case URI_TYPE_CONFIG_INFO:
            case URI_TYPE_CONFIG_INFO_ID:
                res.table = ConfigInfoColumns.TABLE_NAME;
                res.idColumn = ConfigInfoColumns._ID;
                res.tablesWithJoins = ConfigInfoColumns.TABLE_NAME;
                res.orderBy = ConfigInfoColumns.DEFAULT_ORDER;
                break;
            case URI_TYPE_SERVER_INFO:
            case URI_TYPE_SERVER_INFO_ID:
                res.table = ServerInfoColumns.TABLE_NAME;
                res.idColumn = ServerInfoColumns._ID;
                res.tablesWithJoins = ServerInfoColumns.TABLE_NAME;
                res.orderBy = ServerInfoColumns.DEFAULT_ORDER;
                break;
            case URI_TYPE_STUDY_INFO:
            case URI_TYPE_STUDY_INFO_ID:
                res.table = StudyInfoColumns.TABLE_NAME;
                res.idColumn = StudyInfoColumns._ID;
                res.tablesWithJoins = StudyInfoColumns.TABLE_NAME;
                res.orderBy = StudyInfoColumns.DEFAULT_ORDER;
                break;
            case URI_TYPE_USER_INFO:
            case URI_TYPE_USER_INFO_ID:
                res.table = UserInfoColumns.TABLE_NAME;
                res.idColumn = UserInfoColumns._ID;
                res.tablesWithJoins = UserInfoColumns.TABLE_NAME;
                res.orderBy = UserInfoColumns.DEFAULT_ORDER;
                break;
            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }
        switch (matchedId) {
            case URI_TYPE_APP_INFO_ID:
            case URI_TYPE_CONFIG_INFO_ID:
            case URI_TYPE_SERVER_INFO_ID:
            case URI_TYPE_STUDY_INFO_ID:
            case URI_TYPE_USER_INFO_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
