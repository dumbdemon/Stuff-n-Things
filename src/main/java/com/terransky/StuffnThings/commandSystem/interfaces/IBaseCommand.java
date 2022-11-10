package com.terransky.StuffnThings.commandSystem.interfaces;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;

public interface IBaseCommand extends Comparable<IBaseCommand> {

    String getName();

    CommandData getCommandData();

    //Whether the command works and whether it should be pushed to the public version.
    default boolean isWorkingCommand() {
        return true;
    }

    @Override
    default int compareTo(@NotNull IBaseCommand iBaseCommand) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), iBaseCommand.getName());
    }
}
