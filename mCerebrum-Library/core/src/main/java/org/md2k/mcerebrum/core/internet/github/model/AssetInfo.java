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

package org.md2k.mcerebrum.core.internet.github.model;

/**
 * <code>AssetInfo</code> object
 */
public class AssetInfo {
    private String browser_download_url;
    private String name;
    private String label;
    private String content_type;
    private long size;
    private int download_count;
    private String created_at;
    private String updated_at;

    /**
     * Returns the browser download URL.
     * @return The browser download URL.
     */
    public String getBrowser_download_url() {
        return browser_download_url;
    }

    /**
     * Returns the name.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the label.
     * @return The label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the content type.
     * @return The content type.
     */
    public String getContent_type() {
        return content_type;
    }

    /**
     * Returns the size of the asset.
     * @return The size of the asset.
     */
    public long getSize() {
        return size;
    }

    /**
     * Returns the download count.
     * @return The download count.
     */
    public int getDownload_count() {
        return download_count;
    }

    /**
     * Returns <code>created_at</code>.
     * @return The <code>created_at</code> field.
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * Returns <code>updated_at</code>.
     * @return The <code>updated_at</code> field.
     */
    public String getUpdated_at() {
        return updated_at;
    }
}
