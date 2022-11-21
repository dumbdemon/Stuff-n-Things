package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenu extends IBaseBotElement {

    /**
     * The main handler for select menus.
     *
     * @param event {@link EntitySelectInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown across all ISelectMenus.
     */
    void execute(@NotNull EntitySelectInteractionEvent event, @NotNull EventBlob blob) throws Exception;
}
