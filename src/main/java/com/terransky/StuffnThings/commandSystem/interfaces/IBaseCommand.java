package com.terransky.StuffnThings.commandSystem.interfaces;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface IBaseCommand {

    String getName();

    CommandData getCommandData();

    //Whether the command works and whether it should be pushed to the public version.
    default boolean workingCommand() {
        return true;
    }
}
