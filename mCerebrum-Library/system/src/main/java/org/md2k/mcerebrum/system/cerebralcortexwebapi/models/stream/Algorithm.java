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

import java.util.List;

/**
 * Provides information about the algorithm applied to the data stream while being processed.
 */
public class Algorithm {

    /**
     *<p>
     *     Serialized name: "method"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("method")
    @Expose
    private String method;

    /**
     * <p>
     *     Serialized name: "description"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("description")
    @Expose
    private String description;

    /**
     * <p>
     *     Serialized name: "authors"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("authors")
    @Expose
    private List<String> authors = null;

    /**
     * <p>
     *     Serialized name: "version"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("version")
    @Expose
    private String version;

    /**
     * <p>
     *     Serialized name: "reference"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("reference")
    @Expose
    private Reference reference;

    /**
     * No argument constructor for use in serialization
     */
    public Algorithm() {}

    /**
     * Constructor
     * @param authors Authors of the algorithm.
     * @param description Description of the algorithm.
     * @param method Method of the algorithm.
     * @param reference Reference of the algorithm.
     * @param version Version of the algorithm.
     */
    public Algorithm(String method, String description, List<String> authors, String version, Reference reference) {
        super();
        this.method = method;
        this.description = description;
        this.authors = authors;
        this.version = version;
        this.reference = reference;
    }

    /**
     * Returns the algorithm's method.
     * @return The algorithm's method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the algorithm's method.
     * @param method The algorithm's method.
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Returns the description's method.
     * @return The description's method.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description's method.
     * @param description The description's method.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the list of authors of the algorithm.
     * @return The list of authors of the algorithm.
     */
    public List<String> getAuthors() {
        return authors;
    }

    /**
     * Sets the list of authors of the algorithm.
     * @param authors The list of authors of the algorithm.
     */
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    /**
     * Returns the version of the algorithm.
     * @return The version of the algorithm.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the algorithm.
     * @param version The version of the algorithm.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the reference of the algorithm.
     * @return The reference of the algorithm.
     */
    public Reference getReference() {
        return reference;
    }

    /**
     * Sets the reference of the algorithm.
     * @param reference The reference of the algorithm.
     */
    public void setReference(Reference reference) {
        this.reference = reference;
    }

}