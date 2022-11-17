package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.text.ParseException;

public interface ICommand extends IBaseBotElement {

    /**
     * Get this commands {@link CommandData}.
     *
     * @return A {@link CommandData} object.
     * @throws ParseException If the pattern used in {@code Metadata.getImplementationDate()} or {@code Metadata.getLastUpdated()} in a slash command class
     *                        is given an invalid date string.
     */
    CommandData getCommandData() throws ParseException;

    /**
     * Whether the command works and whether it should be pushed to the public version.
     *
     * @return True unless otherwise.
     */
    default boolean isWorking() {
        return true;
    }
}
