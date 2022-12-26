package com.terransky.stuffnthings.interfaces.discordInteractions;

import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

public interface ICommandSlash extends ICommand {

    default String getNameReadable() {
        return WordUtils.capitalize(this.getName().replaceAll("-", " "));
    }

    /**
     * Builds a {@link CommandData} object based on {@link Metadata}.
     *
     * @return A built {@link CommandData} object.
     * @throws ParseException If the pattern used in {@link Metadata#getImplementationDate()} or {@link Metadata#getLastUpdated()}
     *                        in a slash command class is given an invalid date string.
     */
    @Override
    default CommandData getCommandData() throws ParseException {
        Metadata metadata = this.getMetadata();
        SlashCommandData commandData = Commands.slash(this.getName(), metadata.getShortDescription())
            .setNSFW(metadata.isNsfw())
            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(metadata.getDefaultPerms()));

        if (!metadata.getOptions().isEmpty())
            return commandData.addOptions(metadata.getOptions());

        if (!metadata.getSubcommands().isEmpty())
            return commandData.addSubcommands(metadata.getSubcommands());

        if (!metadata.getSubcommandGroups().isEmpty())
            return commandData.addSubcommandGroups(metadata.getSubcommandGroups());

        return commandData;
    }

    /**
     * Get the {@link Metadata} object of an {@link ICommandSlash}.
     *
     * @return A {@link Metadata} object.
     * @throws ParseException If the pattern used in {@link Metadata#getImplementationDate()} or {@link Metadata#getLastUpdated()}
     *                        in a slash command class is given an invalid date string.
     */
    Metadata getMetadata() throws ParseException;

    /**
     * The main slash command handler.
     *
     * @param event A {@link SlashCommandInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception could get thrown.
     */
    void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
