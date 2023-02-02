package com.terransky.stuffnthings.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The interface for all Jackson POJOs
 */
public interface MapperObject {

    /**
     * Create a json file of this object with the class name as the file name.<br />
     * The file is saved to the <b>jsons</b> folder in the root directory.
     *
     * @throws IOException Is thrown if the file could not be saved or if {@link #getAsJsonString()} failed to parse.
     */
    @JsonIgnore
    @BsonIgnore
    @SuppressWarnings("unused")
    default void saveAsJsonFile() throws IOException {
        saveAsJsonFile(this.getClass().getName());
    }

    /**
     * Create a json file of this object with a custom file name.<br />
     * The file is saved to the <b>jsons</b> folder in the root directory.
     *
     * @param name The name of the file
     * @throws IOException Is thrown if the file could not be saved or if {@link #getAsJsonString()} failed to parse.
     */
    @JsonIgnore
    @BsonIgnore
    default void saveAsJsonFile(String name) throws IOException {
        saveAsJsonFile(new File("jsons/" + name + ".json"));
    }

    /**
     * Create a json file of this object.<br />
     * The file is saved to the <b>jsons</b> folder in the root directory.
     *
     * @param file A {@link File} to save to.
     * @throws IOException Is thrown if the file could not be saved or if {@link #getAsJsonString()} failed to parse.
     */
    @JsonIgnore
    @BsonIgnore
    default void saveAsJsonFile(final File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(getAsJsonString());
        writer.close();
    }

    /**
     * Convert this object into a json string.
     *
     * @return A json string of all non-null values
     * @throws JsonProcessingException Thrown if it failed to parse
     */
    @JsonIgnore
    @BsonIgnore
    default String getAsJsonString() throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
