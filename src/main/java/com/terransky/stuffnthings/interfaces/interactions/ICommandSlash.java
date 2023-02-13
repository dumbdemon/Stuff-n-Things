package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.apache.commons.text.WordUtils;

public interface ICommandSlash extends ICommand<SlashCommandInteractionEvent> {

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

    @Override
    default Type getInteractionType() {
        return Type.COMMAND_SLASH;
    }
}
