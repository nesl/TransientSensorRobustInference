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

package org.md2k.motionsense;

import android.util.Log;

import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.motionsense.device.Sensor;

/**
 * Creates a <code>Data</code> object that holds
 */
public class Data {
    private Sensor sensor;
    private DataType dataType;

    /**
     * Constructor
     * @param sensor Sensor providing the data.
     * @param dataType Type of data.
     */
    public Data(Sensor sensor, DataType dataType) {
        this.sensor = sensor;
        this.dataType = dataType;
    }

    /**
     * Returns the sensor.
     * @return The sensor.
     */
    public Sensor getSensor() {
        return sensor;
    }

    /**
     * Returns the <code>DataType</code>.
     * @return The <code>DataType</code>.
     */
    public DataType getDataType() {
        return dataType;
    }

    public void printDoubleArrayData() {
        String timeString = Long.toString(dataType.getDateTime());

        if (dataType instanceof DataTypeDoubleArray) {
            DataTypeDoubleArray dataArray = (DataTypeDoubleArray)dataType;
            double[] sample = dataArray.getSample();
            Log.d("abcde", timeString + " [" + sample[0] + ", " + sample[1] + ", " + sample[2] + "]");
        }
    }

    public String getDoubleArrayData() {
        String timeString = Long.toString(dataType.getDateTime());

        if (dataType instanceof DataTypeDoubleArray) {
            DataTypeDoubleArray dataArray = (DataTypeDoubleArray)dataType;
            double[] sample = dataArray.getSample();
            return timeString + "," + sample[0] + ", " + sample[1] + ", " + sample[2];
        }
        return "";
    }
}
