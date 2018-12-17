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
 * Holds the application's release information
 */
public class ReleaseInfo {
    private String tag_name;
    private String name;
    private String body;
    private boolean prerelease;
    private String created_at;
    private String published_at;
    private AssetInfo[] assets;

    /**
     * Returns the tag name.
     * @return The tag name.
     */
    public String getTag_name() {
        return tag_name;
    }

    /**
     * Returns the release name.
     * @return The release name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the body.
     * @return The body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns whether this is a prerelease or not.
     * @return Whether this is a prerelease or not.
     */
    public boolean isPrerelease() {
        return prerelease;
    }

    /**
     * Returns <code>created_at</code>.
     * @return The <code>created_at</code> field.
     */
    public String getCreated_at() {
        return created_at;
    }

    /**
     * Returns <code>published_at</code>.
     * @return The <code>published_at</code> field.
     */
    public String getPublished_at() {
        return published_at;
    }

    /**
     * Returns an array of <code>AssetInfo</code> objects.
     * @return An array of <code>AssetInfo</code> objects.
     */
    public AssetInfo[] getAssets() {
        return assets;
    }
}
