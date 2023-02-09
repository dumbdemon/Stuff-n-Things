package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.jokeAPI.Flags;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeAPIError;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class JokesV2 implements ICommandSlash {
    @Override
    public String getName() {
        return "jokes";
    }

    @NotNull
    private static String getEffectiveURL(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, String command) {
        Flags serverFlags = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.JOKE_FLAGS)
            .map(flags -> (Flags) flags)
            .orElse(new Flags());

        String lang = event.getOption("language", "en", OptionMapping::getAsString);
        boolean nsfw = event.getChannel().asTextChannel().isNSFW();
        boolean religious = serverFlags.getReligious() ? !serverFlags.getReligious() : event.getOption("religious", true, OptionMapping::getAsBoolean);
        boolean political = serverFlags.getPolitical() ? !serverFlags.getPolitical() : event.getOption("political", true, OptionMapping::getAsBoolean);
        boolean racist = serverFlags.getRacist() ? !serverFlags.getRacist() : event.getOption("racist", true, OptionMapping::getAsBoolean);
        boolean sexist = serverFlags.getSexist() ? !serverFlags.getSexist() : event.getOption("sexist", true, OptionMapping::getAsBoolean);
        boolean safeMode = serverFlags.getSafeMode() ? serverFlags.getSafeMode() : "safe".equals(command);

        StringBuilder blacklistFlags = new StringBuilder();
        if (!nsfw)
            blacklistFlags.append("nsfw,");
        if (!religious)
            blacklistFlags.append("religious,");
        if (!political)
            blacklistFlags.append("political,");
        if (!racist)
            blacklistFlags.append("racist,");
        if (!sexist)
            blacklistFlags.append("sexist,");
        if (!nsfw)
            blacklistFlags.append("explicit,");
        String blacklist = blacklistFlags.isEmpty() ?
            "" : "?blacklistFlags=" + blacklistFlags.substring(0, blacklistFlags.length() - 1) + "&";
        return "https://v2.jokeapi.dev/joke/Any" + (safeMode ? "?safe-mode&" : "") + blacklist + "?lang=" + lang;
    }

    @Override
    public Metadata getMetadata() {
        OptionData language = new OptionData(OptionType.STRING, "language", "Get output language. Defaults to English.")
            .addChoices(
                new Command.Choice("Czech", "cs"),
                new Command.Choice("German", "de"),
                new Command.Choice("English", "en"),
                new Command.Choice("Spanish", "es"),
                new Command.Choice("French", "fr"),
                new Command.Choice("Portuguese", "pt")
            );
        return new Metadata(getName(), "Get a random joke", """
            Get a random joke!
            Admins can use `/config jokes` to limit the specifiers.
            """, Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate("2023-02-06T18:34Z"),
            Metadata.parseDate("2023-02-06T18:34Z")
        )
            .addSubcommandGroups(
                new SubcommandGroupData("get", "Get a random joke.")
                    .addSubcommands(
                        new SubcommandData("any", "Get a random joke. No specifier.")
                            .addOptions(language),
                        new SubcommandData("limited", "Get a random jokes is specific categories.")
                            .addOptions(
                                new OptionData(OptionType.BOOLEAN, "religious", "Add religious jokes."),
                                new OptionData(OptionType.BOOLEAN, "political", "Add political jokes."),
                                new OptionData(OptionType.BOOLEAN, "racist", "Add racist jokes."),
                                new OptionData(OptionType.BOOLEAN, "sexist", "Add sexist jokes."),
                                language
                            ),
                        new SubcommandData("safe", "Get a safe random joke")
                    ),
                new SubcommandGroupData("submit", "Submit a joke to https://sv443.net/jokeapi/v2/.")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String[] command = event.getFullCommandName().split(" ");

        if ("submit".equals(command[1])) {
            event.replyEmbeds(
                new EmbedBuilder()
                    .build()
            ).queue();
            return;
        }

        String URL = getEffectiveURL(event, blob, command[2]);
        URL jokeApi = new URL(URL);
        if (Config.isTestingMode()) LoggerFactory.getLogger(JokesV2.class).debug(URL);
        ObjectMapper mapper = new ObjectMapper();
        JokeAPIError theJoke = mapper.readValue(jokeApi, JokeAPIError.class);
        EmbedBuilder comedian = new EmbedBuilder()
            .setTitle(getNameReadable())
            .setColor(EmbedColors.getDefault())
            .setFooter(blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl());

        if (theJoke.getError()) {
            StringBuilder message = new StringBuilder(theJoke.getMessage());
            for (String msg : theJoke.getCausedBy()) {
                message.append("\n• ").append(msg);
            }

            if (theJoke.getCausedBy().isEmpty())
                message.append("\n• Unknown");

            event.replyEmbeds(
                new EmbedBuilder(comedian)
                    .setColor(EmbedColors.getError())
                    .setDescription(message)
                    .build()
            ).queue();
            return;
        }

        if ("single".equals(theJoke.getType())) {
            comedian.setDescription(theJoke.getJoke());
        } else {
            comedian.setDescription(theJoke.getSetup())
                .appendDescription(String.format("%n%n||%s||", theJoke.getDelivery()));
        }

        comedian.addField("Category", theJoke.getCategory(), true)
            .addField("ID", String.valueOf(theJoke.getId()), true);

        event.replyEmbeds(comedian.build()).queue();
    }
}
