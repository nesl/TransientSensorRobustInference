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
 * This class provides getter and setter methods for Minio object fields and provides annotations
 * for Gson serialization.
 */
public class MinioObjectStats {

    /**
     * Used for optimistic concurrency control. ??
     * <p>
     *     Serialized name: "etag"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("etag")
    @Expose
    private String etag;

    /**
     * <p>
     *     Serialized name: "is_dir"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("is_dir")
    @Expose
    private String isDir;

    /**
     * Content type.
     * <p>
     *     Serialized name: "content_type"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("content_type")
    @Expose
    private String contentType;

    /**
     * Name of the bucket.
     * <p>
     *     Serialized name: "bucket_name"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("bucket_name")
    @Expose
    private String bucketName;

    /**
     * Name of the object.
     * <p>
     *     Serialized name: "object_name"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("object_name")
    @Expose
    private String objectName;

    /**
     * When the object was last modified.
     * <p>
     *     Serialized name: "last_modified"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("last_modified")
    @Expose
    private String lastModified;

    /**
     * Size of the object.
     * <p>
     *     Serialized name: "size"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("size")
    @Expose
    private String size;


    /**
     * Returns the etag.
     * @return etag
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Sets the etag.
     * @param etag
     */
    public void setEtag(String etag) {
        this.etag = etag;
    }

    /**
     * Returns <code>isDir</code>.
     * @return <code>isDir</code>.
     */
    public String getIsDir() {
        return isDir;
    }

    /**
     * Sets a new value for <code>isDir</code>.
     * @param isDir The new value of <code>isDir</code>.
     */
    public void setIsDir(String isDir) {
        this.isDir = isDir;
    }

    /**
     * Returns the content type.
     * @return The content type descriptor.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets a new content type descriptor.
     * @param contentType The new content type descriptor.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns the name of the bucket this object is stored in.
     * @return The bucket name.
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Sets a new bucket name for this object to be stored in.
     * @param bucketName The new bucket name.
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Returns this object's name.
     * @return This object's name.
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets a new name for this object.
     * @param objectName The new name for this object.
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     * Gets the value of <code>lastModified</code>.
     * @return When this object was last modified.
     */
    public String getLastModified() {
        return lastModified;
    }

    /**
     * Sets a new value for <code>lastModified</code>.
     * @param lastModified The new value for <code>lastModified</code>.
     */
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Returns the size of this object.
     * @return This object's size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the size of this object.
     * @param size The new size of this object.
     */
    public void setSize(String size) {
        this.size = size;
    }
}
