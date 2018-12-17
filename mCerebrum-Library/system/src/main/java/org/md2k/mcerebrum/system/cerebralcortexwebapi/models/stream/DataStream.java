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

import java.util.HashMap;
import java.util.List;

/**
 * A <code>DataStream</code> object represents a data source and it's corresponding metadata and samples.
 *
 * <p>
 *     <code>DataStreams</code> contain fields for <code>type</code>, <code>name</code>, and <code>ExecutionContext</code>.
 *     They also hold lists for <code>Annotations</code>, <code>DataDescriptors</code>, and <code>DataPoints</code>.
 * </p>
 */
public class DataStream {

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
     *     Serialized name: "identifier"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("identifier")
    @Expose
    private String identifier;

    /**
     *<p>
     *     Serialized name: "owner"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("owner")
    @Expose
    private String owner;

    /**
     *<p>
     *     Serialized name: "name"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("name")
    @Expose
    private String name;

    /**
     *<p>
     *     Serialized name: "data_descriptor"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("data_descriptor")
    @Expose
    private List<HashMap<String, String>> dataDescriptor = null;

    /**
     *<p>
     *     Serialized name: "execution_context"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("execution_context")
    @Expose
    private ExecutionContext executionContext;

    /**
     *<p>
     *     Serialized name: "annotations"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("annotations")
    @Expose
    private List<Annotation> annotations = null;

    /**
     *<p>
     *     Serialized name: "data"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("data")
    @Expose
    private List<DataPoints> dataPoints;

    /**
     * No argument constructor for use in serialization
     */
    public DataStream() {}

    /**
     * Constructor without <code>DataPoints</code> and <code>type</code>.
     *
     * @param dataDescriptor List of descriptions for the data.
     * @param name Name of the <code>DataStream</code>.
     * @param owner Owner of the <code>DataStream</code>.
     * @param annotations Annotations of the <code>DataStream</code>.
     * @param executionContext Execution context of the <code>DataStream</code>.
     * @param identifier <code>DataStream</code> identifier.
     */
    public DataStream(String type, String identifier, String owner, String name,
                      List<HashMap<String, String>> dataDescriptor, ExecutionContext executionContext,
                      List<Annotation> annotations) {
        super();
        this.type = type;
        this.identifier = identifier;
        this.owner = owner;
        this.name = name;
        this.dataDescriptor = dataDescriptor;
        this.executionContext = executionContext;
        this.annotations = annotations;
    }

    /**
     * Constructor with <code>DataPoints</code> and <code>type</code>.
     *
     * @param dataDescriptor List of descriptions for the data.
     * @param name Name of the <code>DataStream</code>.
     * @param owner Owner of the <code>DataStream</code>.
     * @param annotations Annotations of the <code>DataStream</code>.
     * @param executionContext Execution context of the <code>DataStream</code>.
     * @param identifier <code>DataStream</code> identifier.
     * @param dataPoints <code>DataPoints</code> in the <code>DataStream</code>.
     * @param type Type of data in the <code>DataStream</code>.
     */
    public DataStream(String type, String identifier, String owner, String name,
                      List<HashMap<String, String>> dataDescriptor, ExecutionContext executionContext,
                      List<Annotation> annotations, List<DataPoints> dataPoints) {
        super();
        this.type = type;
        this.identifier = identifier;
        this.owner = owner;
        this.name = name;
        this.dataDescriptor = dataDescriptor;
        this.executionContext = executionContext;
        this.annotations = annotations;
        this.dataPoints = dataPoints;
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

    /**
     * Returns the owner of the <code>DataStream</code>.
     * @return The owner of the <code>DataStream</code>.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the owner of the <code>DataStream</code>.
     * @param owner The owner of the <code>DataStream</code>.
     */
    public void setOwner(String owner) {
        this.owner = owner;
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
     * Returns the list of data descriptors in the <code>DataStream</code>.
     * @return The list of data descriptors in the <code>DataStream</code>.
     */
    public List<HashMap<String, String>> getDataDescriptor() {
        return dataDescriptor;
    }

    /**
     * Sets the list of data descriptors in the <code>DataStream</code>.
     * @param dataDescriptor The list of data descriptors in the <code>DataStream</code>.
     */
    public void setDataDescriptor(List<HashMap<String, String>> dataDescriptor) {
        this.dataDescriptor = dataDescriptor;
    }

    /**
     * Returns the execution context of the <code>DataStream</code>.
     * @return The execution context of the <code>DataStream</code>.
     */
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * Sets the execution context of the <code>DataStream</code>.
     * @param executionContext The execution context of the <code>DataStream</code>.
     */
    public void setExecutionContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    /**
     * Returns the list of annotations for the <code>DataStream</code>.
     * @return The list of annotations for the <code>DataStream</code>.
     */
    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * Sets the list of annotations for the <code>DataStream</code>.
     * @param annotations The list of annotations for the <code>DataStream</code>.
     */
    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }
}