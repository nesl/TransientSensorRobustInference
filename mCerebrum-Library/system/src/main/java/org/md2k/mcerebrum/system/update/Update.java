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

package org.md2k.mcerebrum.system.update;

import android.content.Context;

import org.md2k.mcerebrum.core.access.serverinfo.ServerCP;
import org.md2k.mcerebrum.system.appinfo.AppInstall;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.ServerManager;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Provides methods for checking for application updates.
 */
public class Update {

    /**
     * Checks for an application update.
     *
     * @param context Android context.
     * @return An <code>Observable</code> that determines if there is an update or not.
     */
    public static Observable<Boolean> checkUpdate(Context context){
        if(ServerCP.getUserName(context) != null)
            return Observable.merge(checkUpdateServer(context), AppInstall.checkUpdate(context))
                .filter(new Func1<Boolean, Boolean>() {
                    /**
                     * @param aBoolean
                     * @return aBoolean
                     */
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                });
        else
            return AppInstall.checkUpdate(context)
                .filter(new Func1<Boolean, Boolean>() {
                    /**
                     * @param aBoolean
                     * @return aBoolean
                     */
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        return aBoolean;
                    }
                });
    }

    /**
     * Checks the update server for the latest app versions.
     *
     * @param context Android context.
     * @return An <code>Observable</code> that determines if there is an update or not.
     */
    public static Observable<Boolean> checkUpdateServer(final Context context) {
        ServerCP.setLatestVersion(context, ServerCP.getCurrentVersion(context));
        return Observable.just(true).subscribeOn(Schedulers.newThread()).map(new Func1<Boolean, Boolean>() {
            /**
             * Checks whether the current app version is the latest version when called.
             *
             * @param aBoolean Need for proper method overriding.
             * @return Whether the current app version is the latest version or not.
             */
            @Override
            public Boolean call(Boolean aBoolean) {

                String latestVersion = ServerManager.getLastModified(ServerCP.getServerAddress(context),
                        ServerCP.getUserName(context),
                        ServerCP.getPasswordHash(context),
                        ServerCP.getFileName(context));

                if(latestVersion == null)
                    return false;
                ServerCP.setLatestVersion(context, latestVersion);
                if(ServerCP.getCurrentVersion(context).equals(latestVersion))
                    return false;
                else
                    return true;
            }
        });
    }

    /**
     * Counts the number of apps that have an update available.
     *
     * @param context Android context.
     * @return The number of apps that have an update available.
     */
    public static int hasUpdate(Context context){
        int count = 0;
        if(ServerCP.hasUpdate(context))
            count++;
        count += AppInstall.hasUpdate(context);
        return count;
    }
}
