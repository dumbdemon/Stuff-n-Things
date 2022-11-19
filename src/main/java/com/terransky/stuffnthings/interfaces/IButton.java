package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface IButton extends IBaseBotElement {

    /**
     * The main handler for buttons.
     *
     * @param event A {@link ButtonInteractionEvent}.
     * @param guild The Guild the event was called on.
     * @throws Exception Any exception that could get thrown across all IButtons.
     */
    void execute(@NotNull ButtonInteractionEvent event, @NotNull Guild guild) throws Exception;
}
