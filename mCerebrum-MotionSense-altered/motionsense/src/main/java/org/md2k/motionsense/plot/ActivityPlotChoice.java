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

package org.md2k.motionsense.plot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.motionsense.R;
import org.md2k.motionsense.permission.Permission;
import org.md2k.motionsense.permission.PermissionCallback;

/**
 * This class handles the plot settings activity creation and menu.
 */
public class ActivityPlotChoice extends AppCompatActivity {

    /**
     * Creates the plot settings activity
     *
     * @param savedInstanceState This activity's previous state, is null if this activity has never
     *                           existed.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permission.requestPermission(this, new PermissionCallback() {

            /**
             * Finishes the activity if permissions are denied and starts the plot activity if they are.
             * @param isGranted Whether permissions are granted or not.
             */
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    Toast.makeText(getApplicationContext(),
                            "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    if(getIntent().getParcelableExtra(DataSource.class.getSimpleName())!=null){
                        Intent intent = new Intent(ActivityPlotChoice.this, ActivityPlot.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(DataSource.class.getSimpleName(),
                                getIntent().getParcelableExtra(DataSource.class.getSimpleName()));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    setContentView(R.layout.activity_plot_choice);
                    getFragmentManager().beginTransaction().replace(R.id.layout_preference_fragment,
                            new PrefsFragmentPlot()).commit();
                }
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Handles menu item selection.
     *
     * @param item Android MenuItem
     * @return true when the menu item selection actions are successful.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
