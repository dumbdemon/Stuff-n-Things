package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public interface ICommand<T extends GenericCommandInteractionEvent> extends IInteraction<T> {

    /**
     * Get this commands {@link CommandData}.
     *
     * @return A {@link CommandData} object.
     */
    CommandData getCommandData();

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
     * Where a command is disabled due to some issue or another and cannot be used until it is fixed
     *
     * @return False unless otherwise
     */
    default boolean isDisabled() {
        return !getDisabledReason().isEmpty();
    }

    default String getDisabledReason() {
        return "";
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
        return List.of();
    }
}
