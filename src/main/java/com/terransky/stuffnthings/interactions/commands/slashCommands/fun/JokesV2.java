package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.jokeAPI.Flags;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeAPI;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitForm;
import com.terransky.stuffnthings.dataSources.jokeAPI.JokeSubmitResponse;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.JokeSubmitHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
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
import java.util.ArrayList;
import java.util.List;

public class JokesV2 extends SlashCommandInteraction {
    public JokesV2() {
        super("jokes", "Get a random joke",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2023, 2, 6, 18, 34),
            parseDate(2025, 12, 28, 2, 6)
        );
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

        addSubcommandGroups(
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
        return "https://v2.jokeapi.dev/joke/" + category + (safeMode ? "?safe-mode&" : "") + blacklist + "?lang=" + lang;
    }

    private static void submitJoke(@NotNull SlashCommandInteractionEvent event) throws InterruptedException {
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
        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(response.getMessage()));

        if (response.getError()) {
            if (response.getAdditionalInfo() != null)
                children.add(TextDisplay.ofFormat("## Additional Info (From API)\n", response.getAdditionalInfo()));
        } else {
            Flags returnedFlags = response.getSubmission().getFlags();

            if ("single".equals(event.getSubcommandName()))
                children.add(TextDisplay.ofFormat("### Joke\n%s", response.getSubmission().getJoke()));
            else {
                children.add(TextDisplay.ofFormat("### Setup\n%s", response.getSubmission().getSetup()));
                children.add(TextDisplay.ofFormat("### Delivery\n%s", response.getSubmission().getDelivery()));
            }

            String yes = "**Yes**";
            String no = "***No***";
            children.add(TextDisplay.ofFormat("### NSFW?\n%s", returnedFlags.getNsfw() ? yes : no));
            children.add(TextDisplay.ofFormat("### Religious?\n%s", returnedFlags.getReligious() ? yes : no));
            children.add(TextDisplay.ofFormat("### Political?\n%s", returnedFlags.getPolitical() ? yes : no));
            children.add(TextDisplay.ofFormat("### Racist?\n%s", returnedFlags.getRacist() ? yes : no));
            children.add(TextDisplay.ofFormat("### Sexist?\n%s", returnedFlags.getSexist() ? yes : no));
            children.add(TextDisplay.ofFormat("### Explicit?\n%s", returnedFlags.getExplicit() ? yes : no));
        }

        event.replyComponents(
            StandardResponse.getResponseContainer("Joke Submission", children, response.getError() ? BotColors.ERROR : BotColors.DEFAULT)
        ).setEphemeral(true).queue();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String[] command = event.getFullCommandName().split(" ");

        if ("submit".equals(command[1])) {
            try {
                submitJoke(event);
            } catch (InterruptedException e) {
                event.replyComponents(
                    StandardResponse.getResponseContainer(this, Responses.NETWORK_OPERATION)
                ).queue();
            }
            return;
        }

        String URL = getEffectiveURL(event, blob, command[2]);
        URL jokeApi = new URL(URL);
        if (StuffNThings.getConfig().getCore().getTestingMode()) LoggerFactory.getLogger(JokesV2.class).debug(URL);
        ObjectMapper mapper = new ObjectMapper();
        JokeAPI theJoke = mapper.readValue(jokeApi.openStream(), JokeAPI.class);

        if (theJoke.getError()) {
            StringBuilder message = new StringBuilder(theJoke.getMessage());
            for (String msg : theJoke.getCausedBy()) {
                message.append("\n• ").append(msg);
            }

            if (theJoke.getCausedBy().isEmpty())
                message.append("\n• Unknown");

            event.replyComponents(
                StandardResponse.getResponseContainer(this, message.toString(), BotColors.ERROR)
            ).queue();
            return;
        }
        List<ContainerChildComponent> children = new ArrayList<>();

        if ("single".equals(theJoke.getType())) {
            children.add(TextDisplay.of(theJoke.getJoke()));
        } else {
            children.add(TextDisplay.of(theJoke.getSetup()));
            children.add(Separator.createInvisible(Separator.Spacing.SMALL));
            children.add(TextDisplay.ofFormat("||%s||", theJoke.getDelivery()));
        }

        children.add(Separator.createDivider(Separator.Spacing.SMALL));

        children.add(TextDisplay.ofFormat("## Category [#ID]\n%s [#%s]", theJoke.getCategory(), String.valueOf(theJoke.getId())));

        event.replyComponents(StandardResponse.getResponseContainer(this, children)).queue();
    }
}
