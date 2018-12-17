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

package org.md2k.mcerebrum.core.constant;

/**
 * Constants used across the mCerebrum platform.
 */
public class MCEREBRUM {
    /**
     * Constants relating to application access.
     */
    public static class APP_ACCESS {
        /** Stop DataKit operation. <p><code>"DATAKIT_STOP"</code>.</p> */
        public static String OP_DATAKIT_STOP = "DATAKIT_STOP";

        /** Operation. <p><code>"OP"</code>.</p> */
        public static String OP = "OP";

        /** Application content provider changed. <p><code>"appcp_changed"</code>.</p> */
        public static String APPCP_CHANGED = "appcp_changed";
    }

    /**
     * Constants used for configuration.
     */
    public static class CONFIG {
        /** Update type: never <p><code>"NEVER"</code>.</p> */
        public static final String UPDATE_TYPE_NEVER = "NEVER";

        /** Update type: notify <p><code>"NOTIFY"</code>.</p> */
        public static final String UPDATE_TYPE_NOTIFY = "NOTIFY";

        /** Update type: automatic <p><code>"AUTO"</code>.</p> */
        public static final String UPDATE_TYPE_AUTOMATIC = "AUTO";

        /** Update type: manual <p><code>"MANUAL"</code>.</p> */
        public static final String UPDATE_TYPE_MANUAL = "MANUAL";

        /** Update type: freebie <p><code>"FREEBIE"</code>.</p> */
        public static final String TYPE_FREEBIE = "FREEBIE";

        /** Server type <p><code>"SERVER"</code>.</p> */
        public static final String TYPE_SERVER = "SERVER";

        /** Configured type <p><code>""</code>.</p> */
        public static final String TYPE_CONFIGURED = "CONFIGURED";

        /**
         * Download type enumeration.
         *
         * <p>
         *     Availble download types:
         *     <ul>
         *         <li>Github</li>
         *         <li>URL</li>
         *         <li>JSON</li>
         *         <li>Server</li>
         *         <li>Unkown</li>
         *     </ul>
         * </p>
         */
        public enum TYPE_DOWNLOAD {
            GITHUB, URL, JSON, SERVER, UNKNOWN
        }

        /**
         * Determines what the <code>TYPE_DOWNLOAD</code> constant value should be based on the given
         * download link.
         *
         * @param downloadLink Download link to type.
         * @return The download type
         */
        public static TYPE_DOWNLOAD getDownloadType(String downloadLink){
            if(downloadLink == null)
                return TYPE_DOWNLOAD.UNKNOWN;
            if(downloadLink.toLowerCase().endsWith(".json"))
                return CONFIG.TYPE_DOWNLOAD.JSON; // Is config strictly needed here?
            if(downloadLink.toLowerCase().endsWith(".zip"))
                return TYPE_DOWNLOAD.URL;
            if(downloadLink.split("/").length == 2)
                return TYPE_DOWNLOAD.GITHUB;
            return TYPE_DOWNLOAD.SERVER;
        }
    }

    /**
     * Constants used application information.
     */
    public static class APP {
        /** Use app as: required <p><code>"REQUIRED"</code>.</p> */
        public static final String USE_AS_REQUIRED = "REQUIRED";

        /** Use app as: optional <p><code>"OPTIONAL"</code>.</p> */
        public static final String USE_AS_OPTIONAL = "OPTIONAL";

        /** Use app as: not in use <p><code>"NOT_IN_USE"</code>.</p> */
        public static final String USE_AS_NOT_IN_USE = "NOT_IN_USE";

        /** Application type: Study <p><code>"STUDY"</code>.</p> */
        public static final String TYPE_STUDY = "STUDY";

        /** Application type: mCerebrum <p><code>"MCEREBRUM"</code>.</p> */
        public static final String TYPE_MCEREBRUM = "MCEREBRUM";

        /** Application type: DataKit <p><code>"DATAKIT"</code>.</p> */
        public static final String TYPE_DATAKIT = "DATAKIT";

        /** Update type: never <p><code>"NEVER"</code>.</p> */
        public static final String UPDATE_TYPE_NEVER = "NEVER";

        /** Update type: notify <p><code>"NOTIFY"</code>.</p> */
        public static final String UPDATE_TYPE_NOTIFY = "NOTIFY";

        /** Update type: automatic <p><code>"AUTO"</code>.</p> */
        public static final String UPDATE_TYPE_AUTOMATIC = "AUTO";

        /** Update type: manual <p><code>"MANUAL"</code>.</p> */
        public static final String UPDATE_TYPE_MANUAL = "MANUAL";

        /**
         * Download type enumeration.
         *
         * <p>
         *     Availble download types:
         *     <ul>
         *         <li>Google Playstore</li>
         *         <li>URL</li>
         *         <li>JSON</li>
         *         <li>Unkown</li>
         *     </ul>
         * </p>
         */
        public enum TYPE_DOWNLOAD {
            PLAYSTORE, URL, JSON, UNKNOWN
        }
    }
}
