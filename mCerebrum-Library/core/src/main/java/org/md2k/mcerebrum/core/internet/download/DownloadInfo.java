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

package org.md2k.mcerebrum.core.internet.download;

/**
 * Object for holding data about a download including total file size, current file size, and whether
 * the download has been completed or not.
 */
public class DownloadInfo{
    /**
     * Constructor
     *
     * @param totalFileSize Size of the whole file.
     * @param currentFileSize Size of the downloaded portion of the file.
     * @param completed Whether the download has been completed or not.
     */
    public DownloadInfo(long totalFileSize, long currentFileSize, boolean completed){
        this.currentFileSize = currentFileSize;
        this.totalFileSize = totalFileSize;
        this.completed = completed;
        if(currentFileSize == totalFileSize)
            progress = 100;
        else
            progress = (100.0 * currentFileSize) / totalFileSize;
    }

    private double progress;
    private long currentFileSize;
    private long totalFileSize;
    private boolean completed;

    /**
     * Returns the download progress.
     * @return The download progress.
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Sets the download progress.
     * @param progress The download progress.
     */
    public void setProgress(long progress) {
        this.progress = progress;
    }

    /**
     * Returns the current file size.
     * @return The current file size.
     */
    public long getCurrentFileSize() {
        return currentFileSize;
    }

    /**
     * Sets the current file size.
     * @param currentFileSize The current file size.
     */
    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    /**
     * Returns the total file size.
     * @return The total file size.
     */
    public long getTotalFileSize() {
        return totalFileSize;
    }

    /**
     * Returns whether the download has been completed or not.
     * @return Whether the download has been completed or not.
     */
    public boolean isCompleted() {
        return completed;
    }
}