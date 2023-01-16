package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.command.Metadata;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;

@SuppressWarnings("SpellCheckingInspection")
public class kitsu {

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
            return Metadata.getEmptyMetadata();
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
            return Metadata.getEmptyMetadata();
        }

        @Override
        public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        }
    }
}
