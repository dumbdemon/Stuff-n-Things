package com.terransky.StuffnThings.selectMenuSystem.selectMenus;

import com.terransky.StuffnThings.selectMenuSystem.ISelectMenu;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class testMenu implements ISelectMenu {
    @Override
    public String getMenuID() {
        return "test-menu";
    }

    @Override
    public void menuExecute(@NotNull SelectMenuInteractionEvent event) {
        //
    }
}
