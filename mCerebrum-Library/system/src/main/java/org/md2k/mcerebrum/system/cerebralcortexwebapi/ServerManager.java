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

import org.md2k.mcerebrum.system.cerebralcortexwebapi.interfaces.CerebralCortexWebApi;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.AuthResponse;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.models.MinioObjectStats;
import org.md2k.mcerebrum.system.cerebralcortexwebapi.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods for managing server configuration.
 */
public class ServerManager {
    /**
     * Authenticates the given user on the Cerebral Cortex server.
     *
     * @param serverName Name of the Cerebral Cortex server.
     * @param userName Username to authenticate.
     * @param password Password used to authenticate.
     * @return The authorization response.
     */
    public static AuthResponse authenticate(String serverName, String userName, String password){
        CerebralCortexWebApi ccService = ApiUtils.getCCService(serverName);
        CCWebAPICalls ccWebAPICalls = new CCWebAPICalls(ccService);
        return ccWebAPICalls.authenticateUser(userName, password);
    }

    /**
     * Returns a list of <code>MinioObjectStats</code> that contain configuration information.
     *
     * @param serverName Name of the Cerebral Cortex server.
     * @param userName Name of the user.
     * @param password Password of the user.
     * @return A list of <code>MinioObjectStats</code> that contain configuration information.
     */
    public static List<MinioObjectStats> getConfigFiles(String serverName, String userName, String password){
        CerebralCortexWebApi ccService = ApiUtils.getCCService(serverName);
        CCWebAPICalls ccWebAPICalls = new CCWebAPICalls(ccService);
        AuthResponse authResponse = ccWebAPICalls.authenticateUser(userName, password);
        final List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(authResponse.getAccessToken(), "configuration");
        if(objectList == null || objectList.size() == 0)
            return new ArrayList<>();
        else
            return objectList;
    }

    /**
     * Returns a <code>MinioObjectStats</code> object that contains configuration information.
     *
     * @param serverName Name of the Cerebral Cortex server.
     * @param userName Name of the user.
     * @param password Password of the user.
     * @param fileName File name of the configuration file to return.
     * @return The specified <code>MinioObjectStats</code> object.
     */
    public static MinioObjectStats getConfigFile(String serverName, String userName, String password, String fileName){
        CerebralCortexWebApi ccService = ApiUtils.getCCService(serverName);;
        CCWebAPICalls ccWebAPICalls = new CCWebAPICalls(ccService);
        AuthResponse authResponse = ccWebAPICalls.authenticateUser(userName, password);
        final List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(authResponse.getAccessToken(), "configuration");
        if(objectList == null || objectList.size() == 0)
            return null;
        for(int i = 0; i < objectList.size(); i++){
            if(objectList.get(i).getObjectName().equals(fileName))
                return objectList.get(i);
        }
        return null;
    }

    /**
     * Returns the <code>lastModified</code> field of the given file.
     *
     * @param serverName Name of the Cerebral Cortex server.
     * @param userName Username to authenticate.
     * @param password Password used to authenticate.
     * @param fileName File name of the object to access.
     * @return The <code>lastModified</code> field of the given file.
     */
    public static String getLastModified(String serverName, String userName, String password, String fileName){
        CerebralCortexWebApi ccService = ApiUtils.getCCService(serverName);
        CCWebAPICalls ccWebAPICalls = new CCWebAPICalls(ccService);
        AuthResponse authResponse = ccWebAPICalls.authenticateUser(userName, password);
        if(authResponse == null)
            return null;

        final List<MinioObjectStats> objectList = ccWebAPICalls.getObjectsInBucket(authResponse.getAccessToken(), "configuration");

        if(objectList == null || objectList.size() == 0)
            return null;
        for(int i = 0; i < objectList.size(); i++){
            if(!objectList.get(i).getObjectName().equals(fileName))
                continue;
            return objectList.get(i).getLastModified();
        }
        return null;
    }

    /**
     * Downloads the configuration file from the server.
     *
     * @param serverName Name of the Cerebral Cortex server.
     * @param userName Name of the user.
     * @param password Password of the user.
     * @param fileName Name of the file to download.
     * @return Whether the download was successful or not.
     */
    public static boolean download(String serverName, String userName, String password, String fileName){
        CerebralCortexWebApi ccService = ApiUtils.getCCService(serverName);
        CCWebAPICalls ccWebAPICalls = new CCWebAPICalls(ccService);
        AuthResponse authResponse = ccWebAPICalls.authenticateUser(userName, password);
        if(authResponse == null)
            return false;
        return ccWebAPICalls.downloadMinioObject(authResponse.getAccessToken(), "configuration", fileName, "config.zip");
    }
}
