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

package org.md2k.mcerebrum.commons.ui.data_quality;

import android.os.Handler;

import java.util.ArrayList;

/**
 * Provides methods for managing the user's view of the <code>DataQuality</code>.
 */
public class UserViewDataQuality {
    private static final String TAG = UserViewDataQuality.class.getSimpleName();
    private Handler handler;
    private DataQualityManager dataQualityManager;
    private ResultCallback resultCallback;

    /**
     * Constructor
     * @param dataQualityManager
     */
    public UserViewDataQuality(DataQualityManager dataQualityManager) {
        handler = new Handler();
        this.dataQualityManager = dataQualityManager;
    }

    /**
     * Sets the result callback and posts <code>runnableUpateView</code> messages to the handler.
     * @param resultCallback Callback for results.
     */
    public void set(ResultCallback resultCallback) {
        this.resultCallback = resultCallback;
        handler.post(runnableUpdateView);
    }

    /**
     * Clears the callback thread.
     */
    public void clear() {
        handler.removeCallbacks(runnableUpdateView);
    }

    /**
     * Updates the view if there are changes to <code>dataQualityManager</code>.
     */
    public void updateView(){
        try {
            if (dataQualityManager == null || dataQualityManager.dataQualityInfos == null
                    || dataQualityManager.dataQualityInfos.size() == 0)
                return;
            int[] result = new int[dataQualityManager.dataQualityInfos.size()];
            ArrayList<DataQualityInfo> dataQualityInfos = dataQualityManager.dataQualityInfos;
            for (int i = 0; i < dataQualityInfos.size(); i++) {
                result[i] = dataQualityInfos.get(i).getQuality();
            }
            resultCallback.onResult(result);
        }catch (Exception e){}
    }

    /**
     * Runnable for updating the view.
     */
    private Runnable runnableUpdateView = new Runnable() {
        /**
         * Updates the view and posts a one seconds delayed <code>run()</code> message.
         */
        @Override
        public void run() {
            updateView();
            handler.postDelayed(this, 1000);
        }
    };
}
