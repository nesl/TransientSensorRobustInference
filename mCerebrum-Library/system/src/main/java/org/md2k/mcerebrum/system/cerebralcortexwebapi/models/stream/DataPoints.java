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

package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * A data point consists of a sample and a timeframe (starting and ending timestamps).
 */
public class DataPoints {

    /**
     *<p>
     *     Serialized name: "starttime"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("starttime")
    @Expose
    private String starttime;

    /**
     *<p>
     *     Serialized name: "endtime"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("endtime")
    @Expose
    private String endtime;

    /**
     *<p>
     *     Serialized name: "sample"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("sample")
    @Expose
    private String sample;

    /**
     * No arguments constructor for use in serialization
     */
    public DataPoints() {}

    /**
     * Constructor
     *
     * @param endtime Endtime of the data point.
     * @param starttime Starttime of the data point.
     * @param sample Sampling data for the data point.
     */
    public DataPoints(String starttime, String endtime, String sample) {
        super();
        this.starttime = starttime;
        this.endtime = endtime;
        this.sample = sample;
    }

    /**
     * Returns the starttime of the data point.
     * @return The starttime of the data point.
     */
    public String getStarttime() {
        return starttime;
    }

    /**
     * Sets the starttime of the data point.
     * @param starttime The starttime of the data point.
     */
    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    /**
     * Returns the endtime of the data point.
     * @return The endtime of the data point.
     */
    public String getEndtime() {
        return endtime;
    }

    /**
     * Sets the endtime of the data point.
     * @param endtime The endtime of the data point.
     */
    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    /**
     * Returns the sample of the data point.
     * @return The sample of the data point.
     */
    public String getSample() {
        return sample;
    }

    /**
     * Sets the sample of the data point.
     * @param sample The sample of the data point.
     */
    public void setSample(String sample) {
        this.sample = sample;
    }

}