package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public interface ICommand extends IInteraction {

    /**
     * Get this commands {@link CommandData}.
     *
     * @return A {@link CommandData} object.
     * @throws ParseException If the pattern used in {@link com.terransky.stuffnthings.utilities.command.Metadata#getCreatedDate()}
     *                        or {@link com.terransky.stuffnthings.utilities.command.Metadata#getLastUpdated()} in a slash command class
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

    /**
     * If this command can be used in all servers.
     *
     * @return True unless otherwise.
     */
    default boolean isGlobal() {
        return true;
    }

    /**
     * Whether this command can only be run by the Server's Owner or by anyone.
     *
     * @return False unless otherwise.
     */
    default boolean isOwnerOnly() {
        return false;
    }

    /**
     * If this is a reserved developer command.
     *
     * @return False unless otherwise.
     */
    default boolean isDeveloperCommand() {
        return false;
    }

    /**
     * If guild command, which guild(s) can it be used in.
     *
     * @return {@link List} of Guild IDs.
     */
    default List<Long> getServerRestrictions() {
        return new ArrayList<>();
    }
}
