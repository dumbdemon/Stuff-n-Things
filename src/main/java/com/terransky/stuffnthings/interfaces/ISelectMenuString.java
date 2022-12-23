package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenuString extends IInteraction {

    /**
     * The main handler for select menus.
     *
     * @param event {@link StringSelectInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown.
     */
    void execute(@NotNull StringSelectInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
