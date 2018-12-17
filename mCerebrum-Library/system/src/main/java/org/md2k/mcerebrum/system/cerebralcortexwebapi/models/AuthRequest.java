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
 * Provides getter and setter methods for an <code>AuthRequest</code> object.
 */
public class AuthRequest {

    /**
     * Username to use for authentication.
     * <p>
     *     Serialized name: "username".
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("username")
    @Expose
    private String username;

    /**
     * Password to use for authentication.
     * <p>
     *     Serialized name: "password".
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("password")
    @Expose
    private String password;

    /**
     * Constructor
     *
     * @param userName Username to use for authentication.
     * @param userPassword Password to use for authentication.
     */
    public AuthRequest(String userName, String userPassword) {
        this.username = userName;
        this.password = userPassword;
    }

    /**
     * Returns the username.
     * @return The username.
     */
    public Object getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's password.
     * @return The user's password.
     */
    public Object getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

}