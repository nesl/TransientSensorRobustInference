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

package org.md2k.mcerebrum.system.cerebralcortexwebapi.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Provides getters and setters for <code>MinioBucket</code> objects.
 */
public class MinioBucket {

    /**
     * Name of the bucket.
     * <p>
     *     Serialized name: "bucket-name"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("bucket-name")
    @Expose
    private Object bucketName;

    /**
     * When the bucket was last modified.
     * <p>
     *     Serialized name: "last_modified"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("last_modified")
    @Expose
    private Object lastModified;

    /**
     * Returns the name of the bucket.
     * @return The name of the bucket.
     */
    public Object getBucketName() {
        return bucketName;
    }

    /**
     * Sets the name of the bucket.
     * @param bucketName
     */
    public void setBucketName(Object bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Returns the <code>lastModified</code> object.
     * @return The <code>lastModified</code> object.
     */
    public Object getLastModified() {
        return lastModified;
    }

    /**
     * Sets the <code>lastModified</code> field.
     * @param lastModified The new value for <code>lastModified</code>.
     */
    public void setLastModified(Object lastModified) {
        this.lastModified = lastModified;
    }
}