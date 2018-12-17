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

import org.md2k.mcerebrum.core.access.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * App ContentProvider Model class.
 * This class is auto-generated by Android ContentProvider Generator.
 * For more information see <a href="https://github.com/BoD/android-contentprovider-generator">GitHub</a>.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface AppInfoModel extends BaseModel {

    long getId();

    @Nullable
    String getPackageName();

    @Nullable
    String getType();

    @Nullable
    String getTitle();

    @Nullable
    String getSummary();

    @Nullable
    String getDescription();

    @Nullable
    Boolean getUseInStudy();

    @Nullable
    String getUseAs();

    @Nullable
    Boolean getInstalled();

    @Nullable
    String getDownloadLink();

    @Nullable
    String getUpdates();

    @Nullable
    String getCurrentVersion();

    @Nullable
    String getLatestVersion();

    @Nullable
    String getExpectedVersion();

    @Nullable
    String getIcon();

    @Nullable
    Boolean getMcerebrumSupported();

    @Nullable
    String getFuncInitialize();

    @Nullable
    Boolean getInitialized();

    @Nullable
    String getFuncUpdateInfo();

    @Nullable
    String getFuncConfigure();

    @Nullable
    Boolean getConfigured();

    @Nullable
    Boolean getConfigureMatch();

    @Nullable
    String getFuncPermission();

    @Nullable
    Boolean getPermissionOk();

    @Nullable
    String getFuncBackground();

    @Nullable
    Boolean getBackgroundRunningTime();

    @Nullable
    Boolean getIsBackgroundRunning();

    @Nullable
    String getFuncReport();

    @Nullable
    String getFuncClear();

    @Nullable
    Boolean getDatakitConnected();
}