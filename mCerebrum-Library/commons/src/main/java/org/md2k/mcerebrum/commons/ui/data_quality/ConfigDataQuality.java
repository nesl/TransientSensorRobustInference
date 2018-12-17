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

package org.md2k.mcerebrum.commons.ui.data_quality;

import android.os.Parcel;
import android.os.Parcelable;

import org.md2k.datakitapi.source.datasource.DataSource;

/**
 * Data quality configuration object
 */
public class ConfigDataQuality implements Parcelable{
    String title;
    DataSource dataSource;
    String video_link;
    String message;

    /**
     * Constructor
     * @param in Parcel to construct from.
     */
    protected ConfigDataQuality(Parcel in) {
        title = in.readString();
        dataSource = in.readParcelable(DataSource.class.getClassLoader());
        video_link = in.readString();
        message = in.readString();
    }

    /**
     * A <code>ConfigDataQuality</code> Creator
     */
    public static final Creator<ConfigDataQuality> CREATOR = new Creator<ConfigDataQuality>() {
        /**
         * Creates a <code>ConfigDataQuality</code> object from a parcel.
         * @param in Parcel to create the object from.
         * @return A <code>ConfigDataQuality</code> object.
         */
        @Override
        public ConfigDataQuality createFromParcel(Parcel in) {
            return new ConfigDataQuality(in);
        }

        /**
         * Creates a new array of <code>ConfigDataQuality</code> objects.
         * @param size Size of the new array.
         * @return A new array of <code>ConfigDataQuality</code> objects.
         */
        @Override
        public ConfigDataQuality[] newArray(int size) {
            return new ConfigDataQuality[size];
        }
    };

    /**
     * Returns 0 because this <code>Parcelable</code> does not contain a <code>FileDescriptor</code>.
     * @return Always returns 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the calling object to a parcel.
     * @param dest Parcel to write to.
     * @param flags Contextual flags as per <code>Parcelable.writeToParcel()</code>.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeParcelable(dataSource, flags);
        dest.writeString(video_link);
        dest.writeString(message);
    }
}
