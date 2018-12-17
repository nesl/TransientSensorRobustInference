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
 * Holds information about how the data stream is processed.
 * <p>
 *     The module is given a <code>name</code> and <code>description</code>. It also contains information
 *     about the <code>InputStream</code>, <code>InputParameters</code>, <code>Algorithm</code>,
 *     and <code>OutputStream</code>.
 * </p>
 */
public class ProcessingModule {

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
     *     Serialized name: "description"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("description")
    @Expose
    private String description;

    /**
     *<p>
     *     Serialized name: "input_parameters"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("input_parameters")
    @Expose
    private InputParameters inputParameters;

    /**
     *<p>
     *     Serialized name: "input_streams"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("input_streams")
    @Expose
    private List<InputStream> inputStreams = null;

    /**
     *<p>
     *     Serialized name: "output_streams"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("output_streams")
    @Expose
    private List<OutputStream> outputStreams = null;

    /**
     *<p>
     *     Serialized name: "algorithm"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("algorithm")
    @Expose
    private List<Algorithm> algorithm = null;

    /**
     * No argument constructor for use in serialization
     */
    public ProcessingModule() {}

    /**
     * Constructor
     *
     * @param name Name of the <code>DataStream</code>.
     * @param description Description of the <code>DataStream</code>.
     * @param inputParameters Input parameters of the <code>DataStream</code>.
     * @param inputStreams Input streams of the <code>DataStream</code>.
     * @param outputStreams Output streams of the <code>DataStream</code>.
     * @param algorithm Algorithm of the <code>DataStream</code>.
     */
    public ProcessingModule(String name, String description, InputParameters inputParameters, List<InputStream> inputStreams, List<OutputStream> outputStreams, List<Algorithm> algorithm) {
        super();
        this.name = name;
        this.description = description;
        this.inputParameters = inputParameters;
        this.inputStreams = inputStreams;
        this.outputStreams = outputStreams;
        this.algorithm = algorithm;
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
     * Returns the description of the <code>DataStream</code>.
     * @return The description of the <code>DataStream</code>.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the <code>DataStream</code>.
     * @param description The description of the <code>DataStream</code>.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the input parameters of the <code>DataStream</code>.
     * @return The input parameters of the <code>DataStream</code>.
     */
    public InputParameters getInputParameters() {
        return inputParameters;
    }

    /**
     * Sets the input parameters of the <code>DataStream</code>.
     * @param inputParameters The input parameters of the <code>DataStream</code>.
     */
    public void setInputParameters(InputParameters inputParameters) {
        this.inputParameters = inputParameters;
    }

    /**
     * Returns the list of input streams in the <code>DataStream</code>.
     * @return The list of input streams in the <code>DataStream</code>.
     */
    public List<InputStream> getInputStreams() {
        return inputStreams;
    }

    /**
     * Sets the list of input streams in the <code>DataStream</code>.
     * @param inputStreams The list of input streams in the <code>DataStream</code>.
     */
    public void setInputStreams(List<InputStream> inputStreams) {
        this.inputStreams = inputStreams;
    }

    /**
     * Returns the list of output streams in the <code>DataStream</code>.
     * @return The list of output streams in the <code>DataStream</code>.
     */
    public List<OutputStream> getOutputStreams() {
        return outputStreams;
    }

    /**
     * Sets the list of output streams in the <code>DataStream</code>.
     * @param outputStreams The list of output streams in the <code>DataStream</code>.
     */
    public void setOutputStreams(List<OutputStream> outputStreams) {
        this.outputStreams = outputStreams;
    }

    /**
     * Returns the algorithm.
     * @return The algorithm.
     */
    public List<Algorithm> getAlgorithm() {
        return algorithm;
    }

    /**
     * Sets the algorithm
     * @param algorithm The algorithm to set.
     */
    public void setAlgorithm(List<Algorithm> algorithm) {
        this.algorithm = algorithm;
    }

}