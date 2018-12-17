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

package org.md2k.mcerebrum.core.internet.github.service;

import org.md2k.mcerebrum.core.internet.github.model.ReleaseInfo;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Interface for interacting with GitHub to get release information.
 */
interface GitHubAPI {
    String SERVICE_ENDPOINT = "https://api.github.com/";

    /**
     * Fetches the <code>ReleaseInfo</code> of the latest release of the given repository.
     *
     * @param owner Repository owner.
     * @param repo Repository to fetch from.
     * @return The latest release of the repository.
     */
    @GET("repos/{owner}/{repo}/releases/latest")
    Observable<ReleaseInfo> getReleaseLatest(@Path("owner") String owner, @Path("repo") String repo);

    /**
     * Fetches a list of releases for the given repository.
     *
     * @param owner Repository owner.
     * @param repo Repository to fetch from.
     * @return An array of <code>ReleaseInfo</code> objects.
     */
    @GET("repos/{owner}/{repo}/releases")
    Observable<ReleaseInfo[]> getReleases(@Path("owner") String owner, @Path("repo") String repo);
}
