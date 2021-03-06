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
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.md2k.mcerebrum.core.constant.MCEREBRUM;

/**
 * Observes the application content provider for changes.
 */
public class AppCPObserver extends ContentObserver {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = AppCPObserver.class.getSimpleName();

    /** Android context. */
    private Context context;


    /**
     * Constructor
     *
     * @param context Android context
     * @param handler Message handler
     */
    public AppCPObserver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    /**
     * Wrapper method for <code>onChange</code>.
     *
     * @param selfChange Whether the change is self imposed or not.
     */
    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    /**
     * Sends a broadcast message when the <code>AppCPObserver</code> is changed.
     *
     * @param selfChange Whether the change is self imposed or not.
     * @param uri
     */
    @Override
    public void onChange(boolean selfChange, Uri uri) {
        Intent intent = new Intent(MCEREBRUM.APP_ACCESS.APPCP_CHANGED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.d(TAG, "appcp_changed");
    }
}