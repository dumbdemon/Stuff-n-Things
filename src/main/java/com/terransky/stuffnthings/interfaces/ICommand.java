package com.terransky.stuffnthings.interfaces;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.text.ParseException;

public interface ICommand extends IBaseDiscordElement {

    CommandData getCommandData() throws ParseException;

    //Whether the command works and whether it should be pushed to the public version.
    default boolean isWorking() {
        return true;
    }
}
