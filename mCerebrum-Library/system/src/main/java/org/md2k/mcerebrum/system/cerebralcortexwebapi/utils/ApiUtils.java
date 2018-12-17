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

package org.md2k.mcerebrum.system.cerebralcortexwebapi.utils;

import android.os.Environment;
import android.util.Log;

import org.md2k.mcerebrum.system.cerebralcortexwebapi.interfaces.CerebralCortexWebApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Provides utility methods for the Cerebral Cortex Web API.
 */
public class ApiUtils {
    /** Constant used for logging. <p>Uses <code>class.getSimpleName()</code>.</p> */
    private static final String TAG = ApiUtils.class.getSimpleName();

    /**
     * Gets an instance of the Cerebral Cortex service.
     *
     * @param baseUrl URL of the Cerebral Cortex WebAPI.
     * @return A new <code>CerebralCortexWebApi</code> object.
     */
    public static CerebralCortexWebApi getCCService(String baseUrl) {
        return RetrofitClient.getClient(baseUrl).create(CerebralCortexWebApi.class);
    }

    /**
     * Writes the given response to the local disk.
     *
     * @param body Response from the Cerebral Cortex server.
     * @param fileName Name of the file to write to.
     * @return Whether the writing was successful or not.
     */
    public static final Boolean writeResponseToDisk(ResponseBody body, String fileName) {
        try{
            File minioObject = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), fileName);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try{
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(minioObject);

                while (true){
                    int read = inputStream.read(fileReader);
                    if(read == -1){
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("CC Web API", "File Download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            }catch (IOException e){
                return false;
            }finally {
                if(inputStream != null){
                    inputStream.close();
                }
                if(outputStream != null){
                    outputStream.close();
                }
            }
        }catch (IOException e){
            Log.d(TAG,"error...");
            return false;
        }
    }

    /**
     * Creates a file to upload to the server as a multipart request.
     *
     * @param filePath File path of the
     * @return
     */
    public static MultipartBody.Part getUploadFileMultipart(String filePath){
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileMultiBodyPart = MultipartBody.Part
                .createFormData("file", file.getName(), requestFile);
        return fileMultiBodyPart;
    }
}