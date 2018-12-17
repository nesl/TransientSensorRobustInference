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

package org.md2k.mcerebrum.system.cerebralcortexwebapi;

import android.util.Log;

import com.google.gson.Gson;

import org.md2k.mcerebrum.system.cerebralcortexwebapi.interfaces.CerebralCortexWebApi;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthRequest;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.CCApiErrorMessage;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioBucket;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioBucketsList;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectsListInBucket;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream.DataStream;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.utils.ApiUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Handles web API calls to Cerebral Cortex.
 */
public class CCWebAPICalls {

    /**
     * Instance of the <code>CerebralCortexWebApi</code>.
     */
    private CerebralCortexWebApi ccService;

    /**
     * Constructor
     *
     * @param ccService Instance of the <code>CerebralCortexWebApi</code>.
     */
    public CCWebAPICalls(CerebralCortexWebApi ccService) {
        this.ccService = ccService;
    }

    /**
     * Authenticates the user.
     *
     * <p>
     *     Example
     *     <code>
     *         CerebralCortexWebApi ccService = ApiUtils.getCCService("https://fourtytwo.md2k.org/");
     *         CCWebAPICalls ccWebAPICalls = new CCWebAPICalls(ccService);
     *         AuthResponse ar = ccWebAPICalls.authenticateUser(username, password);
     *     </code>
     * </p>
     *
     * @param userName Username
     * @param userPassword Password
     * @return An <code>AuthResponse</code>.
     */
    public AuthResponse authenticateUser(String userName, String userPassword) {
        AuthRequest authRequest = new AuthRequest(userName, userPassword);
        Call<AuthResponse> call = ccService.authenticateUser(authRequest);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return (AuthResponse) response.body();
            } else {
                Gson gson = new Gson();
                try {
                    CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(),
                            CCApiErrorMessage.class);
                    Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                } catch (Exception e) {
                    Log.e("CCWebAPI", "Server URL is not like a Cerebral Cortex instance");
                }
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }


    /**
     * Returns a list of Minio buckets.
     *
     * <p>
     *     Example
     *     <code>
     *         List<MinioBucket> buckets = ccWebAPICalls.getMinioBuckets(ar.getAccessToken().toString());
     *     </code>
     * </p>
     *
     * @param accessToken Authenticated access token.
     * @return A list of Minio buckets.
     */
    public List<MinioBucket> getMinioBuckets(String accessToken) {
        Call<MinioBucketsList> call = ccService.bucketsList(accessToken);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return ((MinioBucketsList) response.body()).getMinioBuckets();
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(),
                        CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }

    /**
     * Gets a list of Minio objects in the given bucket.
     *
     * <p>
     *     <code>
     *         List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(ar.getAccessToken()
     *         .toString(), buckets.get(0).getBucketName().toString());
     *     </code>
     * </p>
     *
     * @param accessToken Authenticated access token.
     * @param bucketName Name of the bucket.
     * @return List of Minio objects.
     */
    public List<MinioObjectStats> getObjectsInBucket(String accessToken, String bucketName) {
        Call<MinioObjectsListInBucket> call = ccService.objectsListInBucket(accessToken, bucketName);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return ((MinioObjectsListInBucket) response.body()).getBucketObjects();
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(),
                        CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }

    /**
     * Gets a single Minio object.
     *
     * <p>
     *     Example
     *     <code>
     *         MinioObjectStats object = ccWebAPICalls.getObjectStats(ar.getAccessToken().toString(),
     *                      buckets.get(0).getBucketName().toString(), "203_mcerebrum_syed_new.pdf");
     *     </code>
     *     <code>
     *         MinioObjectStats object = ccWebAPICalls.getObjectStats(ar.getAccessToken().toString(),
     *                      "configuration", "mperf.zip");
     *     </code>
     * </p>
     *
     * @param accessToken Authenticated access token.
     * @param bucketName Name of the bucket.
     * @param objectName Name of the object.
     * @return The given Minio object.
     */
    public MinioObjectStats getObjectStats(String accessToken, String bucketName, String objectName) {
        Call<MinioObjectStats> call = ccService.getMinioObjectStats(accessToken, bucketName, objectName);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return (MinioObjectStats) response.body();
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(),
                        CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return null;
        }
    }

    /**
     * Downloads the given Minio object.
     *
     * <p>
     *     Example
     *     <code>
     *         Boolean result = ccWebAPICalls.downloadMinioObject(ar.getAccessToken().toString(),
     *                          "configuration", "mperf.zip", "mperf.zip");
     *     </code>
     * </p>
     *
     * @param accessToken Authenticated access token.
     * @param bucketName Name of the bucket.
     * @param objectName Name of the object to download.
     * @param outputFileName Name of the file containing the Minio object.
     * @return
     */
    public Boolean downloadMinioObject(String accessToken, String bucketName, String objectName,
                                       String outputFileName) {
        Call<ResponseBody> call = ccService.downloadMinioObject(accessToken, bucketName, objectName);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                return ApiUtils.writeResponseToDisk((ResponseBody) response.body(), outputFileName);
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(),
                        CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return false;
        }
    }


    /**
     * Uploads metadata and puts it with the archive data.
     *
     * <p>
     *     Example
     *     <code>
     *         MetadataBuilder metadataBuilder = new MetadataBuilder();
     *         DataStream dataStreamMetadata = metadataBuilder
     *                  .buildDataStreamMetadata("datastream", "123", "999", "sampleStream", "zip");
     *         Boolean resultUpload = ccWebAPICalls
     *                  .putArchiveDataAndMetadata(ar.getAccessToken().toString(), dataStreamMetadata,
     *                  "/storage/emulated/0/Android/data/org.md2k.datakit/files/raw/raw2/2017092217_2.csv.gz");
     *     </code>
     * </p>
     *
     * @param accessToken Authenticated access token.
     * @param metadata Metadata to upload.
     * @param filePath of the Multipart request.
     * @return Whether the upload was successful or not.
     */
    public Boolean putArchiveDataAndMetadata(String accessToken, DataStream metadata, String filePath) {
        MultipartBody.Part fileMultiBodyPart = ApiUtils.getUploadFileMultipart(filePath);
        Call<ResponseBody> call = ccService.putArchiveDataStreamWithMetadata(accessToken, metadata,
                fileMultiBodyPart);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                Log.d("CCWebAPI", "Successfully uploaded: " + filePath);
                return true;
            } else {
                Gson gson = new Gson();
                CCApiErrorMessage errorBody = gson.fromJson(response.errorBody().charStream(),
                        CCApiErrorMessage.class);
                Log.e("CCWebAPI", "Not successful " + errorBody.getMessage());
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CCWebAPICalls", e.getMessage());
            return false;
        }
    }
}
