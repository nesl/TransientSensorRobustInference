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
 * Parameters for the <code>ProcessingModule</code> input.
 */
public class InputParameters {

    /**
     *<p>
     *     Serialized name: "window_size"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("window_size")
    @Expose
    private Integer windowSize;

    /**
     *<p>
     *     Serialized name: "window_offset"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("window_offset")
    @Expose
    private Integer windowOffset;

    /**
     *<p>
     *     Serialized name: "low_level_threshold"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("low_level_threshold")
    @Expose
    private Double lowLevelThreshold;

    /**
     *<p>
     *     Serialized name: "high_level_threshold"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("high_level_threshold")
    @Expose
    private Double highLevelThreshold;

    /**
     * No argument constructor for use in serialization
     */
    public InputParameters() {}

    /**
     * Constructor
     *
     * @param windowOffset
     * @param highLevelThreshold
     * @param lowLevelThreshold
     * @param windowSize
     */
    public InputParameters(Integer windowSize, Integer windowOffset, Double lowLevelThreshold,
                           Double highLevelThreshold) {
        super();
        this.windowSize = windowSize;
        this.windowOffset = windowOffset;
        this.lowLevelThreshold = lowLevelThreshold;
        this.highLevelThreshold = highLevelThreshold;
    }

    /**
     * Returns the window size.
     * @return The window size.
     */
    public Integer getWindowSize() {
        return windowSize;
    }

    /**
     * Sets the window size.
     * @param windowSize The window size.
     */
    public void setWindowSize(Integer windowSize) {
        this.windowSize = windowSize;
    }

    /**
     * Returns the window offset.
     * @return The window offset.
     */
    public Integer getWindowOffset() {
        return windowOffset;
    }

    /**
     * Sets the window offset.
     * @param windowOffset The window offset.
     */
    public void setWindowOffset(Integer windowOffset) {
        this.windowOffset = windowOffset;
    }

    /**
     * Returns the minimum threshold.
     * @return The minimum threshold.
     */
    public Double getLowLevelThreshold() {
        return lowLevelThreshold;
    }

    /**
     * Sets the minimum threshold.
     * @param lowLevelThreshold The minimum threshold.
     */
    public void setLowLevelThreshold(Double lowLevelThreshold) {
        this.lowLevelThreshold = lowLevelThreshold;
    }

    /**
     * Returns the maximum threshold.
     * @return The maximum threshold.
     */
    public Double getHighLevelThreshold() {
        return highLevelThreshold;
    }

    /**
     * Sets the maximum threshold.
     * @param highLevelThreshold The maximum threshold.
     */
    public void setHighLevelThreshold(Double highLevelThreshold) {
        this.highLevelThreshold = highLevelThreshold;
    }
}