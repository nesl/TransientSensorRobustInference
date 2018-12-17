package org.md2k.mcerebrum.system.cerebralcortexwebapi.models.stream;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data stream output from the <code>ProcessingModule</code>.
 */
public class OutputStream {

    /**
     *<p>
     *     Serialized name: "name"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("name")
    @Expose
    private String name;

    /**
     *<p>
     *     Serialized name: "identifier"
     *     Exposed to serialization.
     * </p>
     */
    @SerializedName("identifier")
    @Expose
    private String identifier;

    /**
     * No argument constructor for use in serialization
     */
    public OutputStream() {}

    /**
     * Constructor
     *
     * @param name
     * @param identifier
     */
    public OutputStream(String name, String identifier) {
        super();
        this.name = name;
        this.identifier = identifier;
    }

    /**
     * Returns the name of the stream.
     * @return The name of the stream.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the stream.
     * @param name The new name of the stream.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the stream identifier.
     * @return The stream identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the stream identifier.
     * @param identifier The stream identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}