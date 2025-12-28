package com.terransky.stuffnthings.utilities.jda;

import com.terransky.stuffnthings.StuffNThings;

/**
 * Enums containing emojis uploaded to the Discord Developer console.
 */
public enum BotEmojis {

    YOUTUBE("<:youtube:1275846797994233971>", "<:youtube:1275843911897714708>"),
    UPVOTE("<:upvote:1275846810568491049>", "<:upvote:1275843897888735233>")
    ;

    private final String testingMode;
    private final String stuffNThings;

    BotEmojis(String testingMode, String stuffNThings) {
        this.testingMode = testingMode;
        this.stuffNThings = stuffNThings;
    }

    public String getTestingMode() {
        return testingMode;
    }

    public String getStuffNThings() {
        return stuffNThings;
    }

    public static String getEmoji(BotEmojis botEmojis) {
        return StuffNThings.getConfig().getCore().getTestingMode() ? botEmojis.getTestingMode() : botEmojis.getStuffNThings();
    }
}
