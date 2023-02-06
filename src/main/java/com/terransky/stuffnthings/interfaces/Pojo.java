package com.terransky.stuffnthings.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The interface for all Jackson POJOs
 */
public interface Pojo {

    /**
     * Get a file safe name to save
     *
     * @param name A name to encode
     * @return A file safe name.
     */
    static String toSafeFileName(String name) {
        return URLEncoder.encode(name, StandardCharsets.UTF_8);
    }

    /**
     * Get a configured object mapper for saving.
     *
     * @return A configured {@link ObjectMapper}.
     */
    static ObjectMapper getMapperObject() {
        return new ObjectMapper()
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

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
        String[] className = this.getClass().getName().split("\\.");
        saveAsJsonFile(className[className.length - 1]);
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
        saveAsJsonFile(new File("jsons/" + toSafeFileName(name) + ".json"));
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
    default void saveAsJsonFile(@NotNull final File file) throws IOException {
        LoggerFactory.getLogger(Pojo.class).info("Saving to -> \"{}\"", file.getAbsolutePath());
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
        return getMapperObject()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(this);
    }
}
