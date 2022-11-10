package com.terransky.StuffnThings.commandSystem.ExtraDetails;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public record ExtraDetails(String commandName,
                           String longDescription,
                           Mastermind mastermind,
                           Permission... minPerms) implements Comparable<ExtraDetails> {

    @Override
    public String longDescription() {
        if (longDescription.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
            return longDescription.substring(0, MessageEmbed.DESCRIPTION_MAX_LENGTH);
        }
        return longDescription;
    }

    @Override
    public int compareTo(@NotNull ExtraDetails extraDetails) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.commandName, extraDetails.commandName);
    }
}
