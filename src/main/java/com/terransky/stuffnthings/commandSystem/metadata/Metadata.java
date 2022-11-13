package com.terransky.stuffnthings.commandSystem.metadata;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public record Metadata(String commandName,
                       String longDescription,
                       Mastermind mastermind,
                       Date implementationDate,
                       Date lastUpdated,
                       Permission... minPerms) implements Comparable<Metadata> {

    @Override
    public String longDescription() {
        int descriptionMaxLength = MessageEmbed.DESCRIPTION_MAX_LENGTH;

        if (longDescription.length() > descriptionMaxLength) {
            return longDescription.substring(0, descriptionMaxLength);
        }

        return longDescription;
    }

    public long getImplementedAsEpochSecond() {
        return implementationDate.toInstant().getEpochSecond();
    }

    public long getLastEditedAsEpochSecond() {
        return lastUpdated.toInstant().getEpochSecond();
    }

    @Override
    public int compareTo(@NotNull Metadata metadata) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.commandName, metadata.commandName);
    }
}
