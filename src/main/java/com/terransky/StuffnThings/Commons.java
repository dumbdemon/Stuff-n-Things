package com.terransky.StuffnThings;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Commons {
    public static final Color defaultEmbedColor = new Color(102, 51, 102);
    public static final Color secondaryEmbedColor = new Color(153, 102, 153);
    public static final Dotenv config = Dotenv.configure().load();
    public static final boolean isTestingMode = config.get("TESTING_MODE").equals("true");
    public static final boolean enableDatabase = config.get("ENABLE_DATABASE").equals("true");

    @Contract(pure = true)
    private Commons() {
    }

    /**
     * The Bot's minimum required permissions to run all commands.
     *
     * @return {@link List} of JDA {@link Permission}s.
     */
    public static @NotNull List<Permission> requiredPerms() {
        List<Permission> permissionList = new ArrayList<>();
        //For funsies
        permissionList.add(Permission.MESSAGE_SEND);
        permissionList.add(Permission.MESSAGE_ADD_REACTION);
        permissionList.add(Permission.MESSAGE_EMBED_LINKS);
        permissionList.add(Permission.MESSAGE_EXT_EMOJI);
        permissionList.add(Permission.MESSAGE_EXT_STICKER);
        permissionList.add(Permission.VIEW_CHANNEL);

        //Moderation

        return permissionList;
    }
}
