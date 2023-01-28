package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface ISelectMenuString extends IInteraction {

    /**
     * The main handler for select menus.
     *
     * @param event {@link StringSelectInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws RuntimeException Any exception thrown that could prevent operation.
     * @throws IOException      Potentially could be thrown during network operations
     */
    void execute(@NotNull StringSelectInteractionEvent event, @NotNull EventBlob blob) throws RuntimeException, IOException;

    @Override
    default Type getInteractionType() {
        return Type.SELECTION_STRING;
    }
}
