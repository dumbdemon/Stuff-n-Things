package com.terransky.StuffnThings.commandSystem.Metadata;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public record Metadata(String commandName,
                       String longDescription,
                       Mastermind mastermind,
                       Permission... minPerms) implements Comparable<Metadata> {

    @Override
    public String longDescription() {
        if (longDescription.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
            return longDescription.substring(0, MessageEmbed.DESCRIPTION_MAX_LENGTH);
        }
        return longDescription;
    }

    @Override
    public int compareTo(@NotNull Metadata metadata) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.commandName, metadata.commandName);
    }
}
