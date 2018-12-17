package org.md2k.mcerebrum.core.access.serverinfo;

// @formatter:off
import org.md2k.mcerebrum.core.access.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Server ContentProvider Bean class.
 * This class is auto-generated by Android ContentProvider Generator.
 * For more information see <a href="https://github.com/BoD/android-contentprovider-generator">GitHub</a>.
 */
@SuppressWarnings({"WeakerAccess", "unused", "ConstantConditions"})
public class ServerInfoBean implements ServerInfoModel {
    private long mId;
    private String mServerAddress;
    private String mUsername;
    private String mUuid;
    private String mPasswordHash;
    private String mToken;
    private String mFileName;
    private String mCurrentVersion;
    private String mLatestVersion;

    @Override
    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    @Nullable
    @Override
    public String getServerAddress() {
        return mServerAddress;
    }

    public void setServerAddress(@Nullable String serverAddress) {
        mServerAddress = serverAddress;
    }

    @Nullable
    @Override
    public String getUsername() {
        return mUsername;
    }

    public void setUsername(@Nullable String username) {
        mUsername = username;
    }

    @Nullable
    @Override
    public String getUuid() {
        return mUuid;
    }

    public void setUuid(@Nullable String uuid) {
        mUuid = uuid;
    }

    @Nullable
    @Override
    public String getPasswordHash() {
        return mPasswordHash;
    }

    public void setPasswordHash(@Nullable String passwordHash) {
        mPasswordHash = passwordHash;
    }

    @Nullable
    @Override
    public String getToken() {
        return mToken;
    }

    public void setToken(@Nullable String token) {
        mToken = token;
    }

    @Nullable
    @Override
    public String getFileName() {
        return mFileName;
    }

    public void setFileName(@Nullable String fileName) {
        mFileName = fileName;
    }

    @Nullable
    @Override
    public String getCurrentVersion() {
        return mCurrentVersion;
    }

    public void setCurrentVersion(@Nullable String currentVersion) {
        mCurrentVersion = currentVersion;
    }

    @Nullable
    @Override
    public String getLatestVersion() {
        return mLatestVersion;
    }

    public void setLatestVersion(@Nullable String latestVersion) {
        mLatestVersion = latestVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerInfoBean bean = (ServerInfoBean) o;
        return mId == bean.mId;
    }

    @Override
    public int hashCode() {
        return (int) (mId ^ (mId >>> 32));
    }

    @NonNull
    public static ServerInfoBean newInstance(long id, @Nullable String serverAddress, @Nullable String username, @Nullable String uuid, @Nullable String passwordHash, @Nullable String token, @Nullable String fileName, @Nullable String currentVersion, @Nullable String latestVersion) {
        ServerInfoBean res = new ServerInfoBean();
        res.mId = id;
        res.mServerAddress = serverAddress;
        res.mUsername = username;
        res.mUuid = uuid;
        res.mPasswordHash = passwordHash;
        res.mToken = token;
        res.mFileName = fileName;
        res.mCurrentVersion = currentVersion;
        res.mLatestVersion = latestVersion;
        return res;
    }

    @NonNull
    public static ServerInfoBean copy(@NonNull ServerInfoModel from) {
        ServerInfoBean res = new ServerInfoBean();
        res.mId = from.getId();
        res.mServerAddress = from.getServerAddress();
        res.mUsername = from.getUsername();
        res.mUuid = from.getUuid();
        res.mPasswordHash = from.getPasswordHash();
        res.mToken = from.getToken();
        res.mFileName = from.getFileName();
        res.mCurrentVersion = from.getCurrentVersion();
        res.mLatestVersion = from.getLatestVersion();
        return res;
    }

    public static class Builder {
        private ServerInfoBean mRes = new ServerInfoBean();

        public Builder id(long id) {
            mRes.mId = id;
            return this;
        }

        public Builder serverAddress(@Nullable String serverAddress) {
            mRes.mServerAddress = serverAddress;
            return this;
        }

        public Builder username(@Nullable String username) {
            mRes.mUsername = username;
            return this;
        }

        public Builder uuid(@Nullable String uuid) {
            mRes.mUuid = uuid;
            return this;
        }

        public Builder passwordHash(@Nullable String passwordHash) {
            mRes.mPasswordHash = passwordHash;
            return this;
        }

        public Builder token(@Nullable String token) {
            mRes.mToken = token;
            return this;
        }

        public Builder fileName(@Nullable String fileName) {
            mRes.mFileName = fileName;
            return this;
        }

        public Builder currentVersion(@Nullable String currentVersion) {
            mRes.mCurrentVersion = currentVersion;
            return this;
        }

        public Builder latestVersion(@Nullable String latestVersion) {
            mRes.mLatestVersion = latestVersion;
            return this;
        }

        public ServerInfoBean build() {
            return mRes;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
