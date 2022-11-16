package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenu extends IBaseBotElement {

    /**
     * The main handler for select menus.
     *
     * @param event {@link EntitySelectInteractionEvent}.
     * @throws Exception Any exception that could get thrown across all ISelectMenus.
     */
    void execute(@NotNull EntitySelectInteractionEvent event) throws Exception;
}
