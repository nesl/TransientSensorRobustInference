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

package org.md2k.mcerebrum.commons.ui.privacy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.R;

import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Provides methods for creating a privacy control view for the user.
 */
public class UserViewPrivacyControl {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = UserViewPrivacyControl.class.getSimpleName();

    private Handler handler;
    private PrivacyControlManager privacyControlManager;
    private ViewPrivacy viewPrivacy;

    /**
     * Constructor
     * @param viewPrivacy View settings for privacy.
     */
    public UserViewPrivacyControl(ViewPrivacy viewPrivacy) {
        handler = new Handler();
        this.viewPrivacy = viewPrivacy;
        privacyControlManager = new PrivacyControlManager();
    }

    /**
     * Runnable for creating a privacy control button.
     */
    private Runnable runnablePrivacy = new Runnable() {
        /**
         * Creates a button that either shows the time remaining before data collection continues or
         * shows that data collection is active.
         */
        @Override
        public void run() {
            privacyControlManager.set(viewPrivacy.getContext());
            long remainingTime = privacyControlManager.getRemainingTime();
            if (remainingTime > 0) {
                remainingTime /= 1000;
                int sec = (int) (remainingTime % 60);
                remainingTime /= 60;
                int min = (int) (remainingTime % 60);
                remainingTime /= 60;
                int hour = (int) (remainingTime);
                String show = "Resumed after " + String
                        .format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
                AwesomeTextView t = ((AwesomeTextView) viewPrivacy.findViewById(R.id.textview_privacy_status));
                t.setBootstrapText(new BootstrapText.Builder(viewPrivacy.getContext()).addText(show).build());
                t.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                FancyButton button = (FancyButton) viewPrivacy.findViewById(R.id.btn_pause_resume_data_collection);
                button.setFontIconSize(16);
                button.setIconResource("\uf00d");
                button.setIconColor(ContextCompat.getColor(viewPrivacy.getContext(), R.color.headerOrange));
                button.setBackgroundColor(ContextCompat.getColor(viewPrivacy.getContext(), R.color.colorBackground));
                handler.postDelayed(this, 1000);
            } else {
                FancyButton button = (FancyButton) viewPrivacy.findViewById(R.id.btn_pause_resume_data_collection);
                button.setFontIconSize(16);
                button.setIconColor(Color.WHITE);
                button.setBackgroundColor(ContextCompat.getColor(viewPrivacy.getContext(), R.color.colorBackground));
                button.setIconResource("\uf04c");
                String show = "Data Collection Active";
                AwesomeTextView t = ((AwesomeTextView) viewPrivacy.findViewById(R.id.textview_privacy_status));
                t.setBootstrapText(new BootstrapText.Builder(viewPrivacy.getContext()).addText(show).build());
                t.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                handler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * Prepares a button to resume data collection.
     */
    private void prepareButton() {
        FancyButton button;
        final long lastTime = DateTime.getDateTime();
        button = (FancyButton) viewPrivacy.findViewById(R.id.btn_pause_resume_data_collection);
        button.setOnClickListener(new View.OnClickListener() {
            /**
             * Starts <code>"org.md2k.datakit.ActivityPrivacy"</code>
             * @param v Android view.
             */
            @Override
            public void onClick(View v) {
                if(DateTime.getDateTime() - lastTime < 500)
                    return;
                Intent intent = new Intent();
                intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityPrivacy");
                viewPrivacy.getContext().startActivity(intent);
            }
        });
    }

    /**
     * Removes callbacks, calls <code>prepareButton()</code>, and posts new <code>runnablePrivacy</code>
     * messages to the handler thread.
     */
    public void set() {
        handler.removeCallbacks(runnablePrivacy);
        prepareButton();
        handler.post(runnablePrivacy);
    }

    /**
     * Clears <code>runnablePrivacy</code> messages from the handler.
     */
    public void clear() {
        handler.removeCallbacks(runnablePrivacy);
    }
}
