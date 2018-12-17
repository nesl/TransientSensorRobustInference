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
 * Provides name and identifier information for a data stream.
 */
public class Annotation {

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
     * <p>
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
    public Annotation() {
    }

    /**
     * Constructor
     *
     * @param name Name of the annotation.
     * @param identifier Annotation identifier.
     */
    public Annotation(String name, String identifier) {
        super();
        this.name = name;
        this.identifier = identifier;
    }

    /**
     * Returns the annotation name.
     * @return The annotation name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the annotation name.
     * @param name The new annotation name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the annotation identifier.
     * @return The annotation identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the annotation identifier.
     * @param identifier The new annotation identifer.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}