package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.jokeAPI.Flags;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeAPI;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitForm;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitResponse;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.JokeSubmitHandler;
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
import java.time.Instant;
import java.util.List;

public class JokesV2 implements ICommandSlash {
    @NotNull
    private static String getEffectiveURL(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, String command) {
        Flags serverFlags = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.JOKE_FLAGS, new Flags(), PropertyMapping::getAsFlags);

        String lang = event.getOption("language", "en", OptionMapping::getAsString);
        String category = event.getOption("category", "Any", OptionMapping::getAsString);
        boolean nsfw = event.getChannel().asTextChannel().isNSFW();
        boolean religious = !serverFlags.getReligious() && event.getOption("religious", true, OptionMapping::getAsBoolean);
        boolean political = !serverFlags.getPolitical() && event.getOption("political", true, OptionMapping::getAsBoolean);
        boolean racist = !serverFlags.getRacist() && event.getOption("racist", true, OptionMapping::getAsBoolean);
        boolean sexist = !serverFlags.getSexist() && event.getOption("sexist", true, OptionMapping::getAsBoolean);
        boolean safeMode = !serverFlags.getSafeMode() && "safe".equals(command);

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
        return "https://v2.jokeapi.dev/joke/" + category + (safeMode ? "?safe-mode&" : "") + blacklist + "lang=" + lang;
    }

    private static void submitJoke(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        String lang = event.getOption("language", "en", OptionMapping::getAsString);
        String category = event.getOption("category", "Misc", OptionMapping::getAsString);
        String joke = event.getOption("joke", OptionMapping::getAsString);
        String setup = event.getOption("setup", OptionMapping::getAsString);
        String delivery = event.getOption("delivery", OptionMapping::getAsString);
        boolean nsfw = event.getOption("nsfw", false, OptionMapping::getAsBoolean);
        boolean religious = event.getOption("religious", false, OptionMapping::getAsBoolean);
        boolean political = event.getOption("political", false, OptionMapping::getAsBoolean);
        boolean racist = event.getOption("racist", false, OptionMapping::getAsBoolean);
        boolean sexist = event.getOption("sexist", false, OptionMapping::getAsBoolean);
        boolean explicit = event.getOption("explicit", false, OptionMapping::getAsBoolean);

        JokeSubmitHandler handler = new JokeSubmitHandler();
        Flags newFlags = new Flags().withNsfw(nsfw)
            .withReligoius(religious)
            .withPolitical(political)
            .withRacist(racist)
            .withSexist(sexist)
            .withExplicit(explicit);

        JokeSubmitForm submitForm = new JokeSubmitForm(3, category, event.getSubcommandName(), joke, setup, delivery, newFlags, lang);

        JokeSubmitResponse response = handler.submitJoke(submitForm);
        EmbedBuilder reply = blob.getStandardEmbed("Joke Submission")
            .setDescription(response.getMessage());

        if (response.getError()) {
            reply.setColor(EmbedColors.getError());
            if (response.getAdditionalInfo() != null)
                reply.addField("Additional Info (From API)", response.getAdditionalInfo(), false);
        } else {
            Flags returnedFlags = response.getSubmission().getFlags();

            if ("single".equals(event.getSubcommandName()))
                reply.addField("Joke", response.getSubmission().getJoke(), false);
            else reply.addField("Setup", response.getSubmission().getSetup(), false)
                .addField("Delivery", response.getSubmission().getDelivery(), false);

            String yes = "**Yes**";
            String no = "***No***";
            reply.setColor(EmbedColors.getDefault())
                .addField("NSFW?", returnedFlags.getNsfw() ? yes : no, false)
                .addField("Religious?", returnedFlags.getReligious() ? yes : no, false)
                .addField("Political?", returnedFlags.getPolitical() ? yes : no, false)
                .addField("Racist?", returnedFlags.getRacist() ? yes : no, false)
                .addField("Sexist?", returnedFlags.getSexist() ? yes : no, false)
                .addField("Explicit?", returnedFlags.getExplicit() ? yes : no, false);
        }

        event.replyEmbeds(
            reply.setTimestamp(Instant.ofEpochMilli(response.getTimestamp()))
                .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public String getName() {
        return "jokes";
    }

    // Currently, the API has disabled Joke Submissions. The code wil remain for since I've already written it.
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

        List<OptionData> jokeOptions = List.of(
            new OptionData(OptionType.STRING, "category", "What kind of joke do you want? Defaults to any.")
                .addChoices(
                    new Command.Choice("Misc", "Misc"),
                    new Command.Choice("Programming", "Programming"),
                    new Command.Choice("Dark", "Dark"),
                    new Command.Choice("Pun", "Pun"),
                    new Command.Choice("Spooky", "Spooky"),
                    new Command.Choice("Christmas", "Christmas")
                ),
            new OptionData(OptionType.BOOLEAN, "religious", "Add religious jokes."),
            new OptionData(OptionType.BOOLEAN, "political", "Add political jokes."),
            new OptionData(OptionType.BOOLEAN, "racist", "Add racist jokes."),
            new OptionData(OptionType.BOOLEAN, "sexist", "Add sexist jokes."),
            language
        );

        return new Metadata(getName(), "Get a random joke", """
            Get a random joke!
            Admins can use `/config jokes` to limit the specifiers.
            """, Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate("2023-02-06T18:34Z"),
            Metadata.parseDate("2023-03-02T09:31Z")
        )
            .addSubcommandGroups(
                new SubcommandGroupData("get", "Get a random joke.")
                    .addSubcommands(
                        new SubcommandData("any", "Get a random joke. No specifier.")
                            .addOptions(language),
                        new SubcommandData("limited", "Get a random jokes is specific categories.")
                            .addOptions(jokeOptions),
                        new SubcommandData("safe", "Get a safe random joke")
                    )
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String[] command = event.getFullCommandName().split(" ");

        if ("submit".equals(command[1])) {
            submitJoke(event, blob);
            return;
        }

        String URL = getEffectiveURL(event, blob, command[2]);
        URL jokeApi = new URL(URL);
        if (Config.isTestingMode()) LoggerFactory.getLogger(JokesV2.class).debug(URL);
        ObjectMapper mapper = new ObjectMapper();
        JokeAPI theJoke = mapper.readValue(jokeApi, JokeAPI.class);
        EmbedBuilder comedian = blob.getStandardEmbed(getNameReadable());

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
