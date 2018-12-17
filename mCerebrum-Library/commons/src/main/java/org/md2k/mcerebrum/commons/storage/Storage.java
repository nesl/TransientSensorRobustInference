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

package org.md2k.mcerebrum.commons.storage;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides methods for reading, writing, and measuring storage.
 */
public class Storage {
    /**
     * Private Constructor
     */
    private Storage() {}

    /**
     * Returns the path to the root directory depending on the storage type.
     * @param context Android context.
     * @param storageType Type of storage, either application, external, internal, or external preferred.
     * @return The path to the root directory.
     */
    public static String getRootDirectory(Context context, StorageType storageType) {
        switch (storageType) {
            case SDCARD_APPLICATION:
                return context.getFilesDir().getAbsolutePath();
            case SDCARD_EXTERNAL:
                return getRootDirectorySDCardExternal(context);
            case SDCARD_INTERNAL:
                return getRootDirectorySDCardInternal();
            case SDCARD_EXTERNAL_PREFERRED:
                return getRootDirectoryPreferred(context);
        }
        return null;
    }

    /**
     * Returns the root directory for the external preferred storage type.
     * @param context Android context.
     * @return The path to the root directory.
     */
    private static String getRootDirectoryPreferred(Context context){
        String rootDirectory = getRootDirectorySDCardExternal(context);
        if(rootDirectory == null)
            rootDirectory = getRootDirectorySDCardInternal();
        return rootDirectory;
    }

    /**
     * Returns the root directory for the external storage type.
     * @param context Android context.
     * @return The path to the root directory.
     */
    private static String getRootDirectorySDCardExternal(Context context) {
        String strSDCardPath = System.getenv("SECONDARY_STORAGE");
        File[] externalFilesDirs = context.getExternalFilesDirs(null);
        for (File externalFilesDir : externalFilesDirs) {
            if (externalFilesDir == null)
                continue;
            if (strSDCardPath == null)
                return null;
            if (externalFilesDir.getAbsolutePath().contains(strSDCardPath))
                return externalFilesDir.getAbsolutePath();
        }
        return null;
    }

    /**
     * Returns the root directory for the internal storage type.
     * @return The path to the root directory.
     */
    private static String getRootDirectorySDCardInternal() {
        String directory = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            directory = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return directory;
    }

    /**
     * Determines if the give storage type exists.
     * @param context Android context.
     * @param storageType Type of storage, either application, external, internal, or external preferred.
     * @return Whether the given storage type exists.
     */
    public static boolean isExist(Context context, StorageType storageType) {
        if(storageType == StorageType.ASSET)
            return context.getAssets() != null;
        else {
            return getRootDirectory(context, storageType) != null;
        }
    }

    /**
     * Returns the total about of space the storage has in bytes.
     * @param context Android context.
     * @param storageType Type of storage, either application, external, internal, or external preferred.
     * @return The total about of space the storage has in bytes.
     */
    public static long getSpaceTotal(Context context, StorageType storageType) {
        if(!isExist(context, storageType))
            return -1;
        return new StatFs(getRootDirectory(context, storageType)).getTotalBytes();
    }

    /**
     * Returns the amount of free space the storage has in bytes.
     * @param context Android context.
     * @param storageType Type of storage, either application, external, internal, or external preferred.
     * @return The amount of free space the storage has in bytes.
     */
    public static long getSpaceFree(Context context, StorageType storageType) {
        if(!isExist(context, storageType))
            return -1;
        return new StatFs(getRootDirectory(context, storageType)).getAvailableBytes();
    }

    /**
     * Returns the amount of used space on the storage has in bytes.
     * @param context Android context.
     * @param storageType Type of storage, either application, external, internal, or external preferred.
     * @return The amount of used space on the storage has in bytes.
     */
    public static long getSpaceUsed(Context context, StorageType storageType) {
        if(!isExist(context, storageType))
            return -1;
        return getSpaceTotal(context, storageType)-getSpaceFree(context, storageType);
    }

    /**
     * Copies the files from the source path to the destination path.
     * @param sourcePath File source to copy.
     * @param destinationPath File source to copy to.
     * @return Whether the operation was successful or not.
     */
    public static boolean copy(String sourcePath, String destinationPath) {
        return FileUtils.copyFile(sourcePath, destinationPath);
    }

    /**
     * Copies a file from an asset to the destination path.
     * @param context Android context.
     * @param assetFilePath File path of the asset to copy.
     * @param destinationFilePath File path to copy to.
     * @return Whether the operation was successful or not.
     * @throws IOException
     */
    public static boolean copyFromAsset(Context context, String assetFilePath, String destinationFilePath) throws IOException {
        InputStream in = context.getAssets().open(assetFilePath);
        FileUtils.createOrExistsFile(destinationFilePath);
        FileOutputStream out = new FileOutputStream(destinationFilePath);
        if (in == null)
            return false;
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        return true;
    }

    /**
     * Unzips a zip archive.
     * @param zipFilePath File path to the zip file.
     * @param destDirPath Destination directory.
     * @return Whether the operation was successful or not.
     */
    public static boolean unzip(String zipFilePath, String destDirPath) {
        return ZipUtils.unzipFile(zipFilePath, destDirPath);
    }

    /**
     * Deletes the given directory.
     * @param dirPath Path to the directory to delete.
     * @return Whether the operation was successful or not.
     */
    public static boolean deleteDir(String dirPath) {
        return FileUtils.deleteDir(dirPath);
    }

    /**
     * Deletes the given file.
     * @param filePath Path to the file to delete.
     * @return Whether the operation was successful or not.
     */
    public static boolean deleteFile(String filePath) {
        return FileUtils.deleteFile(filePath);
    }

    /**
     * Reads a json file and returns the data.
     * @param filePath Path to the json file.
     * @param classType Class that defines the object or data contained in the json file.
     * @param <T> Formal generic.
     * @return The data or object from the json file.
     * @throws FileNotFoundException
     */
    public static <T> T readJson(String filePath, Class<T> classType) throws FileNotFoundException {
        T data = null;
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            data = gson.fromJson(reader, classType);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }
        return data;
    }

    /**
     * Reads a json file and returns the data.
     * @param context Android context.
     * @param assetFilePath Path to the json file.
     * @param classType Class that defines the object or data contained in the json file.
     * @param <T>
     * @return The data or object from the json file.
     * @throws FileNotFoundException
     */
    public static <T> T readJsonFromAsset(Context context, String assetFilePath, Class<T> classType) throws FileNotFoundException {
        T data = null;
        BufferedReader reader = null;
        try {
            InputStream in = context.getAssets().open(assetFilePath);
            reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            data = gson.fromJson(reader, classType);
        } catch (IOException e) {
            throw new FileNotFoundException();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ignored) {}
            }
        }
        return data;
    }

    /**
     * Reads a json file and returns the data in an arrayList.
     * @param context Android context.
     * @param assetFilePath Path to the json file.
     * @param classType Class that defines the object or data contained in the json file.
     * @param <T>
     * @return The data or object from the json file in an arrayList.
     * @throws FileNotFoundException
     */
    public static <T> ArrayList<T> readJsonArrayFromAsset(Context context, String assetFilePath,
                                                          Class<T> classType) throws FileNotFoundException {
        ArrayList<T> data = null;
        BufferedReader reader = null;
        try {
            InputStream in = context.getAssets().open(assetFilePath);
            reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            data = gson.fromJson(reader, new ListOfSomething<>(classType));
        } catch (IOException e) {
            throw new FileNotFoundException();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }
        return data;
    }

    /**
     * Reads a json file and returns the data in an arrayList.
     * @param filePath Path to the json file.
     * @param classType Class that defines the object or data contained in the json file.
     * @param <T>
     * @return The data or object from the json file in an arrayList.
     * @throws FileNotFoundException
     */
    public static <T> ArrayList<T> readJsonArrayList(String filePath, Class<T> classType) throws FileNotFoundException {
        ArrayList<T> data = null;
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(in));
            Gson gson = new Gson();
            data = gson.fromJson(reader, new ListOfSomething<>(classType));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
        }
        return data;
    }

    /**
     * Returns a <code>Drawable</code> object from it's file path.
     * @param filePath Path to the file.
     * @return A  <code>Drawable</code> object.
     */
    public static Drawable readDrawable(String filePath) {
        try {
            return Drawable.createFromPath(filePath);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Nested class for creating a list of class types and arguments.
     * @param <X> Formal generic.
     */
    private static class ListOfSomething<X> implements ParameterizedType {

        private Class<?> wrapped;

        /**
         * Constructor
         * @param wrapped Class to list things about.
         */
        ListOfSomething(Class<X> wrapped) {
            this.wrapped = wrapped;
        }

        /**
         * Returns the type arguments.
         * @return An array of type arguments.
         */
        public java.lang.reflect.Type[] getActualTypeArguments() {
            return new java.lang.reflect.Type[]{wrapped};
        }

        /**
         * Returns the raw type.
         * @return List.
         */
        public java.lang.reflect.Type getRawType() {
            return List.class;
        }

        /**
         * Returns the owner type.
         * @return Always returns null.
         */
        public java.lang.reflect.Type getOwnerType() {
            return null;
        }
    }

    /**
     * Writes an object or data to a json file.
     * @param filePath File path to the new json file.
     * @param data Object or data to write.
     * @param <T> Formal generic.
     * @return Whether the operation was successful.
     * @throws IOException
     */
    public static <T> boolean writeJson(String filePath, T data) throws IOException {
        boolean result = true;
        FileWriter writer = null;
        try {
            if (!FileUtils.createOrExistsFile(filePath)) return false;
            Gson gson = new Gson();
            String json = gson.toJson(data);
            writer = new FileWriter(filePath);
            writer.write(json);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                result = false;
            }
        }
        return result;
    }

    /**
     * Writes an arrayList of data to a json file.
     * @param filePath File path to the new json file.
     * @param data ArrayList of data to write.
     * @param <T> Formal generic.
     * @return Whether the operation was successful.
     * @throws IOException
     */
    public static <T> boolean writeJsonArray(String filePath, ArrayList<T> data) throws IOException {
        boolean result = true;
        FileWriter writer = null;
        try {
            if (!FileUtils.createOrExistsFile(filePath)) return false;
            Gson gson = new Gson();
            String json = gson.toJson(data);
            writer = new FileWriter(filePath);
            writer.write(json);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                result = false;
            }
        }
        return result;
    }
}
