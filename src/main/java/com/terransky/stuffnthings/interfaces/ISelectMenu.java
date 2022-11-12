package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenu extends IBaseDiscordElement {

    void execute(@NotNull SelectMenuInteractionEvent event) throws Exception;
}
