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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.md2k.mcerebrum.commons.R;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Defines a view for <code>DataQuality</code>
 */
public class ViewDataQuality extends LinearLayout {
    /**
     * Constructor
     * <p>
     *     Sets the orientation and gravity of the view.
     * </p>
     * @param context Android context.
     * @param attrs The attributes to build the layout parameters from.
     * @param cDataQualities Array of data quality content.
     */
    public ViewDataQuality(Context context, AttributeSet attrs, final CDataQuality[] cDataQualities) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        switch(cDataQualities.length){
            case 1:
                inflater.inflate(R.layout.view_data_quality_1, this, true);
                break;
            case 2:
                inflater.inflate(R.layout.view_data_quality_2, this, true);
                break;
            case 3:
                inflater.inflate(R.layout.view_data_quality_3, this, true);
                break;
            case 4:
                inflater.inflate(R.layout.view_data_quality_4, this, true);
                break;
            default:
                inflater.inflate(R.layout.view_data_quality_4, this, true);
                break;
       }
       if(cDataQualities.length >= 1) {
           ((TextView) findViewById(R.id.textview_data_quality_title_1)).setText(cDataQualities[0].title);
           FancyButton f = (FancyButton) findViewById(R.id.button_data_quality_1);
           f.setOnClickListener(new OnClickListener() {
               /**
                * Starts the activity for the first index of <code>cDataQualities</code>.
                * @param v Android view.
                */
               @Override
               public void onClick(View v) {
                   startActivity(cDataQualities[0]);
               }
           });
       }
        if(cDataQualities.length >= 2) {
            ((TextView) findViewById(R.id.textview_data_quality_title_2)).setText(cDataQualities[1].title);
            FancyButton f = (FancyButton) findViewById(R.id.button_data_quality_2);
            f.setOnClickListener(new OnClickListener() {
                /**
                 * Starts the activity for the second index of <code>cDataQualities</code>.
                 * @param v Android view.
                 */
                @Override
                public void onClick(View v) {
                    startActivity(cDataQualities[1]);
                }
            });
        }
        if(cDataQualities.length >= 3) {
            ((TextView) findViewById(R.id.textview_data_quality_title_3)).setText(cDataQualities[2].title);
            FancyButton f = (FancyButton) findViewById(R.id.button_data_quality_3);
            f.setOnClickListener(new OnClickListener() {
                /**
                 * Starts the activity for the third index of <code>cDataQualities</code>.
                 * @param v Android view.
                 */
                @Override
                public void onClick(View v) {
                    startActivity(cDataQualities[2]);
                }
            });
        }
        if(cDataQualities.length >= 4) {
            ((TextView) findViewById(R.id.textview_data_quality_title_4)).setText(cDataQualities[3].title);
            FancyButton f = (FancyButton) findViewById(R.id.button_data_quality_4);
            f.setOnClickListener(new OnClickListener() {
                /**
                 * Starts the activity for the fourth index of <code>cDataQualities</code>.
                 * @param v Android view.
                 */
                @Override
                public void onClick(View v) {
                    startActivity(cDataQualities[3]);
                }
            });
        }
    }

    /**
     * Starts the data quality activity.
     * @param cDataQuality Data quality content.
     */
    void startActivity(CDataQuality cDataQuality){
        Intent intent = new Intent(getContext(), ActivityDataQuality.class);
        intent.putExtra("title", cDataQuality.title);
        intent.putExtra("message", cDataQuality.message);
        intent.putExtra("video_link", cDataQuality.video_link);
        intent.putExtra("read", cDataQuality.read);
        intent.putExtra("plot", cDataQuality.plot);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    /**
     * No attribute constructor
     * @param context Android context.
     * @param cDataQualities ArrayList of data quality values.
     */
    public ViewDataQuality(Context context, CDataQuality[] cDataQualities) {
        this(context, null, cDataQualities);
    }

    /**
     * Sets the <code>imageView</code> and <code>textView</code> values for data quality.
     * @param result Data quality results.
     */
    public void setDataQuality(int[] result){
        ImageView imageView;
        TextView textView;
        for(int index = 0; index < result.length; index++) {
            switch (index) {
                case 0:
                    imageView = (ImageView) findViewById(R.id.imageview_data_quality_1);
                    textView = (TextView) findViewById(R.id.textview_data_quality_response_1);
                    break;
                case 1:
                    imageView = (ImageView) findViewById(R.id.imageview_data_quality_2);
                    textView = (TextView) findViewById(R.id.textview_data_quality_response_2);
                    break;
                case 2:
                    imageView = (ImageView) findViewById(R.id.imageview_data_quality_3);
                    textView = (TextView) findViewById(R.id.textview_data_quality_response_3);
                    break;
                case 3:
                    imageView = (ImageView) findViewById(R.id.imageview_data_quality_4);
                    textView = (TextView) findViewById(R.id.textview_data_quality_response_4);
                    break;
                default:
                    imageView = (ImageView) findViewById(R.id.imageview_data_quality_1);
                    textView = (TextView) findViewById(R.id.textview_data_quality_response_1);
                    break;
            }
            imageView.setImageDrawable(getDataQualityImage(result[index]));
            textView.setText(getDataQualityText(result[index]));
        }
    }

    /**
     * Returns the data quality as a string.
     * @param value Quality indicator.
     * @return The data quality as a string.
     */
    String getDataQualityText(int value){
        switch(value){
            case -1:
                return "";
            case DATA_QUALITY.GOOD:
                return "Good";
            case DATA_QUALITY.BAND_OFF:
                return "No Data";
            default:
                return "Poor";
        }
    }

    /**
     * Returns the image for the given quality indicator.
     * @param value Quality indicator.
     * @return The image for the given quality indicator.
     */
    Drawable getDataQualityImage(int value){
        switch(value){
            case -1:
                return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_refresh)
                        .sizeDp(24).color(Color.GRAY);
            case DATA_QUALITY.GOOD:
                return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_check_circle)
                        .sizeDp(24).color(Color.GREEN);
            case DATA_QUALITY.BAND_OFF:
                return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_times_circle)
                        .sizeDp(24).color(Color.RED);
            default:
                return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_exclamation_triangle)
                        .sizeDp(24).color(Color.YELLOW);
        }
    }
}
