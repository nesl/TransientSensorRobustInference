package org.md2k.mcerebrum.commons.plot;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.md2k.mcerebrum.commons.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for constructing and updating a realtime line chart.
 */
public abstract class RealtimeLineChartActivity extends DemoBase implements
        OnChartValueSelectedListener {

    private LineChart mChart;

    /**
     * Returns the line chart.
     * @return The line chart.
     */
    public LineChart getmChart() {
        return mChart;
    }

    /**
     * Draws the chart and handles touch input and screen orientation.
     *
     * <p>
     *     Steps involved in drawing the chart are:
     *     <uL>
     *         <li>Enable description text</li>
     *         <li>Enable touch gestures</li>
     *         <li>Enable scaling and dragging</li>
     *         <li>Setting pinch zoom</li>
     *         <li>Set the background color</li>
     *         <li>Add empty data</li>
     *         <li>Get and modify the legend</li>
     *     </uL>
     * </p>
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_realtime_linechart);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);
        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.resetAxisMaximum();
        leftAxis.resetAxisMinimum();
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    /**
     * Creates the options menu.
     * @param menu Menu to create.
     * @return Always returns true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * Runs when a menu item is selected on the options menu.
     * @param item Selected menu item.
     * @return Always returns true.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }


    /**
     * Adds a data point to the chart, notifies the chart, and updates the view of the chart.
     * @param value Array of values to add.
     * @param legend Array of value descriptors.
     * @param noPoints Used to limit the number of visible entries.
     */
    public void addEntry(float[] value, String[] legend, int noPoints) {

        LineData data = mChart.getData();

        if (data != null) {
            List<ILineDataSet> dataSets = data.getDataSets();
            if(dataSets == null || dataSets.size()<value.length){
                dataSets = new ArrayList<>();
                for(int i = 0; i < value.length; i++){
                    LineDataSet set = createSet(i, legend[i]);
                    dataSets.add(set);
                    data.addDataSet(set);
                }
            }
            for(int i = 0; i < value.length; i++){
                data.addEntry(new Entry(dataSets.get(i).getEntryCount(),value[i]), i);
            }
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(noPoints);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    /**
     * Array of integer values denoting colors.
     */
    int[] colors = new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    /**
     * Creates the data set for the line chart.
     * @param i Integer value corresponding to a color in the color array.
     * @param l Data set label.
     * @return The <code>LineDataSet</code>.
     */
    private LineDataSet createSet(int i, String l) {
        LineDataSet set = new LineDataSet(null, l);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(colors[i % colors.length]);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setDrawValues(false);
        return set;
    }

    /**
     * Called when a value has been selected inside the chart.
     *
     * @param e The selected Entry.
     * @param h The corresponding highlight object that contains information
     *          about the highlighted position
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    /**
     * Logs "Nothing selected"
     */
    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    /**
     * Calls it's superclass to resume the activity
     */
    @Override
    protected void onPause() {
        super.onPause();
    }
}