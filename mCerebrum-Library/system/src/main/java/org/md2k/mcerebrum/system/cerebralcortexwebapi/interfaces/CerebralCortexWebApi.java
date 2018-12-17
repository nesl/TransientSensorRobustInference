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

package org.md2k.mcerebrum.system.cerebralcortexwebapi.interfaces;

import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioBucketsList;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthRequest;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectsListInBucket;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream.DataStream;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Interface for the HTTP API.
 */
public interface CerebralCortexWebApi {

    /**
     * Gets the access token.
     *
     * @param accessToken Authorization access token
     * @return An <code>AuthResponse</code> call.
     */
    @GET("/api/v1/auth/")
    Call<AuthResponse> getAccessToken(@Header("Authorization") String accessToken);

    /**
     * Authenticates the user.
     *
     * @param authRequest Authorization request.
     * @return An <code>AuthResponse</code> call.
     */
    @POST("/api/v1/auth/")
    Call<AuthResponse> authenticateUser(@Body AuthRequest authRequest);


    /**
     * Gets the list of Minio buckets.
     *
     * @param authorization Authorization header.
     * @return A <code>MinioBucketsList</code> call.
     */
    @GET("/api/v1/object/")
    Call<MinioBucketsList> bucketsList(@Header("Authorization") String authorization);

    /**
     * Gets the list of objects in the given Minio bucket.
     *
     * @param authorization Authorization header.
     * @param bucket Bucket to list.
     * @return A <code>MinioObjectsListInBucket</code> call.
     */
    @GET("/api/v1/object/{bucket}/")
    Call<MinioObjectsListInBucket> objectsListInBucket(@Header("Authorization") String authorization,
                                                       @Path("bucket") String bucket);

    /**
     * Gets a single Minio object from the given bucket.
     *
     * @param authorization Authorization header.
     * @param bucket Bucket the objects are in.
     * @param resource Object to return.
     * @return A <code>MinioObjectStats</code> call.
     */
    @GET("/api/v1/object/stats/{bucket}/{resource}")
    Call<MinioObjectStats> getMinioObjectStats(@Header("Authorization") String authorization,
                                               @Path("bucket") String bucket,
                                               @Path("resource") String resource);

    /**
     * Downloads a Minio object via a <code>ResponseBody</code>.
     *
     * @param authorization Authorization header.
     * @param bucket Bucket the object is in.
     * @param resource The Minio object.
     * @return A <code>ResponseBody</code> call.
     */
    @GET("/api/v1/object/{bucket}/{resource}")
    Call<ResponseBody> downloadMinioObject(@Header("Authorization") String authorization,
                                           @Path("bucket") String bucket,
                                           @Path("resource") String resource);

    /**
     * Puts the archived data Stream with the appropriate metadata.
     *
     * @param authorization Authorization header.
     * @param jsonMetadata Metadata of the data stream.
     * @param file MultipartBody.Part
     * @return A <code>ResponseBody</code> call.
     */
    @Multipart
    @PUT("/api/v1/stream/zip/")
    Call<ResponseBody> putArchiveDataStreamWithMetadata(
            @Header("Authorization") String authorization,
            @Part("metadata") DataStream jsonMetadata,
            @Part MultipartBody.Part file);
}