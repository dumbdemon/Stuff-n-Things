package com.terransky.StuffnThings.selectMenuSystem;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenu {
    String getMenuID();

    void menuExecute(@NotNull SelectMenuInteractionEvent event);
}
