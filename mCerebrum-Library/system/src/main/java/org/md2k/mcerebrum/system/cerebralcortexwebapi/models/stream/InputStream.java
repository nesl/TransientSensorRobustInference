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
 * Data stream input to the <code>ProcessModule</code>.
 */
public class InputStream {

    /**
     * <p>
     *     Serialized name: "name"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("name")
    @Expose
    private String name;

    /**
     *<p>
     *     Serialized name: "identifier"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("identifier")
    @Expose
    private String identifier;

    /**
     * No argument constructor for use in serialization
     */
    public InputStream() {
    }

    /**
     * Constructor
     *
     * @param name Name of the <code>DataStream</code>.
     * @param identifier <code>DataStream</code> identifier.
     */
    public InputStream(String name, String identifier) {
        super();
        this.name = name;
        this.identifier = identifier;
    }

    /**
     * Returns the name of the <code>DataStream</code>.
     * @return The name of the <code>DataStream</code>.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the <code>DataStream</code>.
     * @param name The name of the <code>DataStream</code>.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the <code>DataStream</code> identifier.
     * @return The <code>DataStream</code> identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the <code>DataStream</code> identifier.
     * @param identifier The <code>DataStream</code> identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}