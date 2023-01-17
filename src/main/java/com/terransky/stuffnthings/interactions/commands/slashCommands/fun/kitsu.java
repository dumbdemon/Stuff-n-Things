package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.CommandCategory;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Mastermind;
import com.terransky.stuffnthings.utilities.command.Metadata;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Date;

@SuppressWarnings("SpellCheckingInspection")
public class kitsu {

    private static final FastDateFormat FORMAT = Metadata.getFastDateFormat();

    private static Metadata getStandard(String name) throws ParseException {
        return new Metadata()
            .setCommandName(name)
            .setMastermind(Mastermind.DEVELOPER)
            .setCategory(CommandCategory.FUN)
            .setCreatedDate(FORMAT.parse("17-1-2023_12:43"))
            .setNsfw(true);
    }

    public static class anime implements ICommandSlash {

        @Override
        public String getName() {
            return "anime";
        }

        @Override
        public boolean isWorking() {
            return Config.isTestingMode();
        }

        @Override
        public Metadata getMetadata() throws ParseException {
            return getStandard(getName())
                .setLastUpdated(new Date())
                .setDescripstions("Search for an anime using Kitsu.io");
        }

        @Override
        public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        }
    }

    public static class manga implements ICommandSlash {
        @Override
        public String getName() {
            return "manga";
        }

        @Override
        public boolean isWorking() {
            return Config.isTestingMode();
        }

        @Override
        public Metadata getMetadata() throws ParseException {
            return getStandard(getName())
                .setLastUpdated(new Date())
                .setDescripstions("Search for a manga using Kitsu.io");
        }

        @Override
        public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        }
    }
}
