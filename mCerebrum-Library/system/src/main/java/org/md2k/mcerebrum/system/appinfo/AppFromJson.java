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

import android.content.Context;

import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import rx.Observable;
import rx.Subscriber;

/**
 * Provides methods for checking for updates for apps installed from JSON.
 */
public class AppFromJson {
    /**
     * Gets the version information and creates an observer to check for updates.
     *
     * @param context Android context.
     * @param downloadLink URL to download the application from.
     * @return A <code>VersionInfo</code> observer.
     */
    public Observable<VersionInfo> getVersion(final Context context, final String downloadLink) {
        return Observable.create(new Observable.OnSubscribe<VersionInfo>() {
            /**
             * Creates a new <code>AppUpdaterUtils</code> object with a listener and starts that service.
             * @param subscriber A new <code>VersionInfo</code> subscriber.
             */
            @Override
            public void call(final Subscriber<? super VersionInfo> subscriber) {
                try {
                    AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(context)
                            .setUpdateFrom(UpdateFrom.JSON)
                            .setUpdateJSON(downloadLink)
                            .withListener(new AppUpdaterUtils.UpdateListener() {
                                /**
                                 * Updates the <code>VersionInfo</code> fields, then passes the
                                 * data to the subcriber and signals the completion.
                                 *
                                 * @param update The current update.
                                 * @param isUpdateAvailable Whether a new update is available.
                                 */
                                @Override
                                public void onSuccess(Update update, Boolean isUpdateAvailable) {
                                    VersionInfo versionInfo = new VersionInfo();
                                    versionInfo.versionName = update.getLatestVersion();
                                    versionInfo.versionCode = update.getLatestVersionCode();
                                    versionInfo.downloadURL = update.getUrlToDownload().toString();
                                    versionInfo.releaseInfo = update.getReleaseNotes();
                                    subscriber.onNext(versionInfo);
                                    subscriber.onCompleted();
                                }

                                /**
                                 * Passes a null to the subscriber and then signals completion.
                                 *
                                 * @param error The app update error that occured.
                                 */
                                @Override
                                public void onFailed(AppUpdaterError error) {
                                    subscriber.onNext(null);
                                    subscriber.onCompleted();
                                }
                            });
                    appUpdaterUtils.start();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
}
