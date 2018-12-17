package org.md2k.mcerebrum.core.access.configinfo;

// @formatter:off
import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.md2k.mcerebrum.core.access.base.AbstractContentValues;

/**
 * Config ContentProvider ContentValues class.
 * This class is auto-generated by Android ContentProvider Generator.
 * For more information see <a href="https://github.com/BoD/android-contentprovider-generator">GitHub</a>.
 */
@SuppressWarnings({"ConstantConditions", "unused"})
public class ConfigInfoContentValues extends AbstractContentValues<ConfigInfoContentValues> {
    @Override
    protected Uri baseUri() {
        return ConfigInfoColumns.CONTENT_URI;
    }

    public int update(ContentResolver contentResolver, @Nullable ConfigInfoSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public int update(Context context, @Nullable ConfigInfoSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public ConfigInfoContentValues putCid(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.CID, value);
        return this;
    }

    public ConfigInfoContentValues putCidNull() {
        mContentValues.putNull(ConfigInfoColumns.CID);
        return this;
    }

    public ConfigInfoContentValues putType(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.TYPE, value);
        return this;
    }

    public ConfigInfoContentValues putTypeNull() {
        mContentValues.putNull(ConfigInfoColumns.TYPE);
        return this;
    }

    public ConfigInfoContentValues putTitle(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.TITLE, value);
        return this;
    }

    public ConfigInfoContentValues putTitleNull() {
        mContentValues.putNull(ConfigInfoColumns.TITLE);
        return this;
    }

    public ConfigInfoContentValues putSummary(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.SUMMARY, value);
        return this;
    }

    public ConfigInfoContentValues putSummaryNull() {
        mContentValues.putNull(ConfigInfoColumns.SUMMARY);
        return this;
    }

    public ConfigInfoContentValues putDescription(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.DESCRIPTION, value);
        return this;
    }

    public ConfigInfoContentValues putDescriptionNull() {
        mContentValues.putNull(ConfigInfoColumns.DESCRIPTION);
        return this;
    }

    public ConfigInfoContentValues putVersions(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.VERSIONS, value);
        return this;
    }

    public ConfigInfoContentValues putVersionsNull() {
        mContentValues.putNull(ConfigInfoColumns.VERSIONS);
        return this;
    }

    public ConfigInfoContentValues putUpdates(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.UPDATES, value);
        return this;
    }

    public ConfigInfoContentValues putUpdatesNull() {
        mContentValues.putNull(ConfigInfoColumns.UPDATES);
        return this;
    }

    public ConfigInfoContentValues putExpectedVersion(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.EXPECTED_VERSION, value);
        return this;
    }

    public ConfigInfoContentValues putExpectedVersionNull() {
        mContentValues.putNull(ConfigInfoColumns.EXPECTED_VERSION);
        return this;
    }

    public ConfigInfoContentValues putLatestVersion(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.LATEST_VERSION, value);
        return this;
    }

    public ConfigInfoContentValues putLatestVersionNull() {
        mContentValues.putNull(ConfigInfoColumns.LATEST_VERSION);
        return this;
    }

    public ConfigInfoContentValues putDownloadFrom(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.DOWNLOAD_FROM, value);
        return this;
    }

    public ConfigInfoContentValues putDownloadFromNull() {
        mContentValues.putNull(ConfigInfoColumns.DOWNLOAD_FROM);
        return this;
    }

    public ConfigInfoContentValues putDownloadLink(@Nullable String value) {
        mContentValues.put(ConfigInfoColumns.DOWNLOAD_LINK, value);
        return this;
    }

    public ConfigInfoContentValues putDownloadLinkNull() {
        mContentValues.putNull(ConfigInfoColumns.DOWNLOAD_LINK);
        return this;
    }
}