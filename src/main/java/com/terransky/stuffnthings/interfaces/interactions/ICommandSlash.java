package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ICommandSlash extends ICommand {

    default String getNameReadable() {
        return WordUtils.capitalize(this.getName().replaceAll("-", " "));
    }

    /**
     * Builds a {@link CommandData} object based on {@link Metadata}.
     *
     * @return A built {@link CommandData} object.
     */
    @Override
    default CommandData getCommandData() {
        return getMetadata().getConstructedCommandData();
    }

    /**
     * Get the {@link Metadata} object of an {@link ICommandSlash}.
     *
     * @return A {@link Metadata} object.
     */
    Metadata getMetadata();

    /**
     * The main slash command handler.
     *
     * @param event A {@link SlashCommandInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws RuntimeException Any exception thrown that could prevent operation.
     * @throws IOException      Potentially could be thrown during network operations
     */
    void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException;

    @Override
    default Type getInteractionType() {
        return Type.COMMAND_SLASH;
    }
}
