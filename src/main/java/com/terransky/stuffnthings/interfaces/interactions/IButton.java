package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.IInteraction;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface IButton extends IInteraction {

    /**
     * The main handler for buttons.
     *
     * @param event A {@link ButtonInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws RuntimeException Any exception thrown that could prevent operation.
     * @throws IOException      Potentially could be thrown during network operations
     */
    void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws RuntimeException, IOException;

    @Override
    default Type getInteractionType() {
        return Type.BUTTON;
    }
}
