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
 * Provides a description for the data collected in the data stream.
 */
public class DataDescriptor {

    /**
     *<p>
     *     Serialized name: "type"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("type")
    @Expose
    private String type;

    /**
     *<p>
     *     Serialized name: "unit"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("unit")
    @Expose
    private String unit;

    /**
     * No arguments constructor for use in serialization
     */
    public DataDescriptor() {}

    /**
     * Constructor
     *
     * @param unit Unit of this <code>DataDescriptor</code>.
     * @param type Type of this <code>DataDescriptor</code>.
     */
    public DataDescriptor(String type, String unit) {
        super();
        this.type = type;
        this.unit = unit;
    }

    /**
     * Returns the type of this <code>DataDescriptor</code>.
     * @return The type of this <code>DataDescriptor</code>.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this <code>DataDescriptor</code>.
     * @param type The type of this <code>DataDescriptor</code>.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the unit of this <code>DataDescriptor</code>.
     * @return The unit of this <code>DataDescriptor</code>.
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit of this <code>DataDescriptor</code>.
     * @param unit The unit of this <code>DataDescriptor</code>.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}