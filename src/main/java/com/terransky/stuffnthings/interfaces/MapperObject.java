package com.terransky.stuffnthings.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("unused")
public interface MapperObject {

    @JsonIgnore
    @BsonIgnore
    default void saveTestJson() throws IOException {
        saveTestJson("test");
    }

    @JsonIgnore
    @BsonIgnore
    default void saveTestJson(String name) throws IOException {
        File testFile = new File(name + ".json");
        BufferedWriter writer = new BufferedWriter(new FileWriter(testFile));
        writer.write(getAsJsonString());
        writer.close();
    }

    @JsonIgnore
    @BsonIgnore
    default String getAsJsonString() throws IOException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
