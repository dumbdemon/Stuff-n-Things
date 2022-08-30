package com.terransky.StuffnThings;

import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Commons {
    public final Color defaultEmbedColor = new Color(102, 51, 102);
    public final Color secondaryEmbedColor = new Color(153, 77, 153);

    public int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public final @NotNull List<Permission> requiredPerms() {
        List<Permission> permissionList = new ArrayList<>();
        //For funsies
        permissionList.add(Permission.MESSAGE_SEND);
        permissionList.add(Permission.MESSAGE_ADD_REACTION);
        permissionList.add(Permission.MESSAGE_EMBED_LINKS);
        permissionList.add(Permission.MESSAGE_EXT_EMOJI);
        permissionList.add(Permission.MESSAGE_EXT_STICKER);
        permissionList.add(Permission.VIEW_CHANNEL);

        //Moderation
        permissionList.add(Permission.MANAGE_WEBHOOKS);
        permissionList.add(Permission.MESSAGE_HISTORY);

        return permissionList;
    }
}
