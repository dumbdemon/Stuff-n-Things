package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.IInteractionType;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenuEntity extends IInteraction {

    /**
     * The main handler for select menus.
     *
     * @param event {@link EntitySelectInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown.
     */
    void execute(@NotNull EntitySelectInteractionEvent event, @NotNull EventBlob blob) throws Exception;

    @Override
    default IInteractionType getInteractionType() {
        return IInteractionType.SELECTION_ENTITY;
    }
}
