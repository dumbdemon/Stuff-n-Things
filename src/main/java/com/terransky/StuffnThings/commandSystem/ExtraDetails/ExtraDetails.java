package com.terransky.StuffnThings.commandSystem.ExtraDetails;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

public record ExtraDetails(String commandName,
                           String longDescription,
                           Mastermind mastermind,
                           Permission... minPerms) {

    @Override
    public String longDescription() {
        if (longDescription.length() > MessageEmbed.DESCRIPTION_MAX_LENGTH) {
            return longDescription.substring(0, MessageEmbed.DESCRIPTION_MAX_LENGTH);
        }
        return longDescription;
    }
}
