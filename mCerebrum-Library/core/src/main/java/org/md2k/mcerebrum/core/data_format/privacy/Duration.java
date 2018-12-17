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

package org.md2k.mcerebrum.core.data_format.privacy;

/**
 * Defines the duration of the privacy.
 */
public class Duration {
    String id;
    String title;
    String summary;
    long value;
    long min_value;
    long max_value;

    /**
     * No argument Constructor.
     */
    public Duration(){}

    /**
     * Constructor
     *
     * @param id Identifier.
     * @param title Title
     * @param value Time left in the duration in milliseconds.
     */
    public Duration(String id, String title, long value){
        this.id = id;
        this.title = title;
        this.value = value;
    }

    /**
     * Returns the identifier.
     * @return The identifier.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the title.
     * @return The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the summary.
     * @return The summary.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Returns the value.
     * @return The value.
     */
    public long getValue() {
        return value;
    }

    /**
     * Returns the minimum value.
     * @return The minimum value.
     */
    public long getMin_value() {
        return min_value;
    }

    /**
     * Returns the maximum value.
     * @return The maximum value.
     */
    public long getMax_value() {
        return max_value;
    }
}
