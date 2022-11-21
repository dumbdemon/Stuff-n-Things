package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IButton extends IBaseBotElement {

    /**
     * The main handler for buttons.
     *
     * @param event A {@link ButtonInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown across all IButtons.
     */
    void execute(@NotNull ButtonInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
