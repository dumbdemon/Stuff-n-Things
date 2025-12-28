package com.terransky.stuffnthings.utilities.managers;

import com.terransky.stuffnthings.interfaces.interactions.CommandInteraction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public class CommandInteractionManager<T extends CommandInteraction<?>> extends InteractionManager<T> {

    public List<CommandData> getCommandData() {
        return interactions.stream()
            .filter(CommandInteraction::isWorking)
            .filter(interaction -> !interaction.isGuildPrivate())
            .map(CommandInteraction::getCommandData)
            .toList();
    }

    public List<CommandData> getCommandData(Guild guild) {
        return interactions.stream()
            .filter(CommandInteraction::isWorking)
            .filter(CommandInteraction::isGuildPrivate)
            .filter(interaction -> interaction.getRestrictedServers().contains(guild.getIdLong()))
            .map(CommandInteraction::getCommandData)
            .toList();
    }
}
