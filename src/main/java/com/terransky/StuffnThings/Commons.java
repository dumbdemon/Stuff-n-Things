package com.terransky.StuffnThings;

import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class Commons {
    public static final Color defaultEmbedColor = new Color(102, 51, 102);
    public static final Color secondaryEmbedColor = new Color(153, 102, 153);
    public static final Dotenv config = Dotenv.configure().load();
    /**
     * Checks if config has been set to testing mode.
     */
    public static final boolean isTestingMode = config.get("TESTING_MODE").equals("true");
    private static final NavigableMap<Float, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000f, "\sThousand");
        suffixes.put(1_000_000f, "\sMillion");
        suffixes.put(1_000_000_000f, "\sBillion");
        suffixes.put(1_000_000_000_000f, "\sTrillion");
        suffixes.put(1_000_000_000_000_000f, "\sQuadrillion");
        suffixes.put(1_000_000_000_000_000_000f, "\sQuintillion");
        suffixes.put(1_000_000_000_000_000_000_000f, "\sSextillion");
        suffixes.put(1_000_000_000_000_000_000_000_000f, "\sSeptillion");
        suffixes.put(1_000_000_000_000_000_000_000_000_000f, "\sOctillion");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000f, "\sNonillion");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000f, "\sDecillion");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000_000f, "\sUndecillion");
    }

    @Contract(pure = true)
    private Commons() {
    }

    /**
     * The Bot's minimum required permissions to run all commands.
     *
     * @return {@code List} of JDA {@code Permission}s.
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
        permissionList.add(Permission.MANAGE_WEBHOOKS);
        permissionList.add(Permission.MESSAGE_HISTORY);

        return permissionList;
    }

    /**
     * Makes extra large numbers look nice~ <br />
     * Code Courtesy of <a href="https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java">assylias's answer</a> on stackoverflow.
     *
     * @return {@code String} that contains a two-point decimal and the scale of value.
     */
    public static String largeNumberFormat(float value) {
        DecimalFormat simpleNum = new DecimalFormat("#.##");

        if (value == Float.MIN_VALUE) return largeNumberFormat(Float.MIN_VALUE + 1);
        if (value < 0) return "-" + largeNumberFormat(-value);
        if (value < 1000) return Float.toString(value);

        Map.Entry<Float, String> e = suffixes.floorEntry(value);
        float divideBy = e.getKey();
        String suffix = e.getValue();

        float truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? simpleNum.format(truncated / 10d) + suffix : simpleNum.format(truncated / 10) + suffix;
    }
}
