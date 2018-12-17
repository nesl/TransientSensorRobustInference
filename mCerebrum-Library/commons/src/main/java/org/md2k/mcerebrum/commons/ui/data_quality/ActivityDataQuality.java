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

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.commons.R;
import org.md2k.mcerebrum.core.access.appinfo.AppCP;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Data quality activity
 */
public class ActivityDataQuality extends AppCompatActivity {
    /** Plot button */
    FancyButton buttonPlot;

    /** Video button */
    FancyButton buttonVideo;

    /** Close activity button */
    FancyButton buttonClose;

    /**
     * This activity is created with buttons for a plot, video link, and closing.
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        // Defines the xml file for the fragment
        setContentView (R.layout.activity_data_quality);
        final String title = getIntent().getStringExtra ("title");
        String message = getIntent().getStringExtra ("message");
        final String video_link = getIntent().getStringExtra ("video_link");
        final DataSource read = getIntent().getParcelableExtra ("read");
        final DataSource plot = getIntent().getParcelableExtra ("plot");
        ((TextView) findViewById (R.id.textview_title)).setText (title);
        ((TextView) findViewById (R.id.textview_content)).setText (message);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled (true);
            getSupportActionBar().setDisplayShowHomeEnabled (true);
        }

        buttonPlot = (FancyButton) findViewById(R.id.button_graph);
        buttonPlot.setOnClickListener(new View.OnClickListener() {
            /**
             * Starts the plot activity when the button is pressed.
             * @param view Android view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                DataKitAPI dataKitAPI = DataKitAPI.getInstance(ActivityDataQuality.this);
                try {
                    ArrayList<DataSourceClient> ds = dataKitAPI.find(new DataSourceBuilder(plot));
                    if(ds.size() == 0){
                        Toasty.error(ActivityDataQuality.this, "Device not registered with datakit",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    b.putParcelable(org.md2k.datakitapi.source.datasource.DataSource.class.getSimpleName(),
                            ds.get(0).getDataSource());
                    intent.putExtra(DataSource.class.getSimpleName(), ds.get(0).getDataSource());
                    intent.putExtras(b);
                    String packageName = ds.get(0).getDataSource().getApplication().getId();
                    intent.setComponent(new ComponentName(packageName,
                            AppCP.getFuncReport(ActivityDataQuality.this, packageName)));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonVideo = (FancyButton) findViewById(R.id.button_video);
        buttonVideo.setOnClickListener(new View.OnClickListener() {
            /**
             * Starts <code>ActivityYoutube</code>.
             * @param view Android view
             */
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ActivityDataQuality.this, ActivityYouTube.class);
                intent.putExtra("video_link", video_link);
                intent.putExtra ("title", title);
                startActivity(intent);
            }
        });

        buttonClose = (FancyButton) findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            /**
             * Closes the activity.
             * @param view Android view
             */
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Manages menu item selection.
     * @param item Selected item.
     * @return Whether the operation was successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
