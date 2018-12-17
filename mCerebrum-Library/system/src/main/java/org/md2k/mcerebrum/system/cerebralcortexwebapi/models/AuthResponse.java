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
 * Provides getter and setter methods for an <code>AuthoResponse</code> object.
 */
public class AuthResponse {

    /**
     * UUID representing the user.
     * <p>
     *     Serialized name: "user_uuid".
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("user_uuid")
    @Expose
    private String userUuid;

    /**
     * Authorized access token.
     * <p>
     *     Serialized name: "access_token".
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("access_token")
    @Expose
    private String accessToken;

    /**
     * No args constructor for use in serialization
     */
    public AuthResponse() {}

    /**
     * Constructor
     *
     * @param accessToken Authorized access token.
     * @param userUuid UUID representing the user.
     */
    public AuthResponse(String userUuid, String accessToken) {
        super();
        this.userUuid = userUuid;
        this.accessToken = accessToken;
    }

    /**
     * Returns the UUID representing the user.
     * @return The <code>userUuid</code>.
     */
    public String getUserUuid() {
        return userUuid;
    }

    /**
     * Sets a new <code>userUuid</code>.
     * @param userUuid The new <code>userUuid</code>.
     */
    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    /**
     * Returns the authorized access token.
     * @return The authorized access token.
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets the new authorized access token.
     * @param accessToken The new <code>accessToken</code>.
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}