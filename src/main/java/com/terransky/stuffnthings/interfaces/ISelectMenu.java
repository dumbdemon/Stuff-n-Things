package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenu extends IBaseBotElement {

    /**
     * The main handler for select menus.
     *
     * @param event {@link SelectMenuInteractionEvent}.
     * @throws Exception Any exception that could get thrown across all ISelectMenus.
     */
    void execute(@NotNull SelectMenuInteractionEvent event) throws Exception;
}
