package com.terransky.stuffnthings.dataSources.debugPacket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.terransky.stuffnthings.interfaces.Pojo;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.FileOperations;
import com.terransky.stuffnthings.utilities.jda.entities.StuffGuild;
import com.terransky.stuffnthings.utilities.jda.entities.StuffUser;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@JsonPropertyOrder({
    "requester",
    "guild",
    "requestedTime"
})
@SuppressWarnings("unused")
public class DebugPacket implements Pojo {

    private StuffUser requester;
    private StuffGuild guild;
    @JsonIgnore
    private OffsetDateTime requestedDateTime;
    private String requestedTime;

    public DebugPacket() {
    }

    public DebugPacket(@NotNull EventBlob blob) {
        this(blob.getGuild(), blob.getMember().getUser());
    }

    public DebugPacket(Guild guild, User user) {
        this.guild = new StuffGuild(Objects.requireNonNull(guild));
        this.requester = new StuffUser(Objects.requireNonNull(user));
        this.requestedDateTime = OffsetDateTime.now(ZoneId.systemDefault());
        this.requestedTime = requestedDateTime.format(DateTimeFormatter.RFC_1123_DATE_TIME);
    }

    public StuffUser getRequester() {
        return requester;
    }

    public void setRequester(StuffUser requester) {
        this.requester = requester;
    }

    public StuffGuild getGuild() {
        return guild;
    }

    public void setGuild(StuffGuild guild) {
        this.guild = guild;
    }

    @JsonIgnore
    public OffsetDateTime getRequestedDateTime() {
        return requestedDateTime;
    }

    @JsonIgnore
    public void setRequestedDateTime(OffsetDateTime requestedDateTime) {
        this.requestedDateTime = requestedDateTime;
    }

    public String getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(String requestedTime) {
        this.requestedTime = requestedTime;
    }

    @Override
    public void saveAsJsonFile() throws IOException {
        saveAsJsonFile(guild.getId() + "_" + requestedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace(":", "_"));
    }

    @Override
    public void saveAsJsonFile(String name) throws IOException {
        String pathName = Pojo.JSON_PATH + "DebugPackets/";
        if (FileOperations.makeDirectory(pathName)) {
            saveAsJsonFile(new File(pathName + Pojo.toSafeFileName(name) + ".json"));
            return;
        }
        LoggerFactory.getLogger(Pojo.class).error("Unable to create directory.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DebugPacket that = (DebugPacket) o;
        return getRequester().equals(that.getRequester()) &&
            getGuild().equals(that.getGuild()) &&
            getRequestedTime().equals(that.getRequestedTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequester(), getGuild(), getRequestedTime());
    }

    @Override
    public String toString() {
        return "DebugPacket{" +
            "requester=" + requester +
            ", guild=" + guild +
            ", requestedTime=" + requestedTime +
            '}';
    }
}
