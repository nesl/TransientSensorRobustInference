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

package org.md2k.mcerebrum.commons.ui.day;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.R;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;

import mehdi.sakout.fancybuttons.FancyButton;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Creates a linear layout of days
 */
public class ViewDay extends LinearLayout {
    private static Activity activity;
    private CallbackDay callbackDay;
    private boolean isStartActive = false;
    private boolean isEndActive = false;
    private Subscription subscription;
    PhoneTone phoneTone;
    PhoneDialog phoneDialog;

    /**
     * Constructor
     * @param context Android context.
     * @param attrs The attributes to build the layout parameters from.
     */
    public ViewDay(Context context, AttributeSet attrs) {
        super(activity, attrs);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_day, this, true);
        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(LLParams);
        this.activity = activity;
        this.phoneTone = new PhoneTone(activity);
        this.phoneDialog = new PhoneDialog(activity);
    }

    /**
     * Sets the callback interface for starting/ending the day.
     * @param callbackDay Callback interface.
     */
    public void setCallbackDay(CallbackDay callbackDay) {
        this.callbackDay = callbackDay;
    }

    /**
     * No <code>AttributeSet</code> constructor
     * @param context Android context
     */
    public ViewDay(Activity context) {
        this(context, null);
    }

    /**
     * Creates a button to start the day.
     * @param isActive Whether the button is active or not.
     * @param color Color of the button.
     * @param text Text on the button.
     */
    public void setStartButton(boolean isActive, int color, String text) {
        final FancyButton bs = (FancyButton) findViewById(R.id.button_start);
        bs.setText(text);
        bs.setTextColor(color);
        isStartActive = isActive;
        bs.setOnClickListener(new OnClickListener() {
            /**
             * Prompts user to verify they want to start the day.
             * @param v Button clicked.
             */
            @Override
            public void onClick(View v) {
                if (!isStartActive) return;
                Dialog.simple(activity, "Start Day", "Start the day now?", "Yes", "Cancel", new DialogCallback() {
                    /**
                     * Starts the day if the selected string is "Yes"
                     * @param value String the user selected.
                     */
                    @Override
                    public void onSelected(String value) {
                        if (value.equalsIgnoreCase("Yes")) {
                            callbackDay.onReceive("START");
                        }
                    }
                }).show();
            }
        });

    }

    /**
     * Creates a button to end the day.
     * @param isActive Whether the button is active or not.
     * @param color Color of the button.
     * @param text Text on the button.
     */
    public void setEndButton(boolean isActive, int color, String text) {
        FancyButton bs = (FancyButton) findViewById(R.id.button_end);
        bs.setText(text);
        bs.setTextColor(color);
        isEndActive = isActive;
        bs.setOnClickListener(new OnClickListener() {
            /**
             * Prompts user to verify they want to end the day.
             * @param v Button clicked.
             */
            @Override
            public void onClick(View v) {
                if (!isEndActive) return;
                Dialog.simple(activity, "End Day", "End the day now?", "Yes", "Cancel", new DialogCallback() {
                    /**
                     * Ends the day if the selected string is "Yes"
                     * @param value String the user selected.
                     */
                    @Override
                    public void onSelected(String value) {
                        if (value.equalsIgnoreCase("Yes")) {
                            callbackDay.onReceive("END");
                        }
                    }
                }).show();
            }
        });

    }

    /**
     * Removes the subscription.
     */
    public void removeNotify() {
        Log.d("abc","Day: ViewDay -> removeNotify()");
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
        subscription = null;
    }

    /**
     * Sets up a subscription with the given format and interval.
     * @param format Format to observe
     * @param interval Interval between observations.
     */
    public void setNotify(String format, long interval) {
        Log.d("abc","Day: ViewDay -> setNotify()");

        subscription = Observable.merge(phoneTone.getObservable(format, interval), phoneDialog.getObservable())
                .takeWhile(new Func1<Boolean, Boolean>() {
                    /**
                     * Starts the callback interface if the parameter is true.
                     * @param aBoolean Whether to start the interface or not.
                     * @return The opposite of the passed boolean value.
                     */
                    @Override
                    public Boolean call(Boolean aBoolean) {
                        if (aBoolean) {
                            callbackDay.onReceive("START");
                        }
                        return !aBoolean;
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(Boolean integer) {}
                });
    }
}
