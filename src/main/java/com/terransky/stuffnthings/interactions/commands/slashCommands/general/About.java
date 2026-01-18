package com.terransky.stuffnthings.interactions.commands.slashCommands.general;

import com.terransky.stuffnthings.Managers;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ButtonInteraction;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class About extends SlashCommandInteraction {

    public About() {
        super("about", "Info about the bot, or it's commands.", Mastermind.DEVELOPER, CommandCategory.GENERAL,
            parseDate(2026, 1, 11, 0, 28),
            parseDate(2026, 1, 18, 13, 2)
        );
        addSubcommands(
            new SubcommandData("bot", "Info about the bot."),
            new SubcommandData("command", "Info about a command.")
                .addOptions(
                    new OptionData(OptionType.STRING, "category", "Filter commands by category", false)
                        .addChoices(CommandCategory.getCategoriesAsChoices())
                )
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No Subcommand given");

        if (subcommand.equals("command")) {
            Optional<String> commandCategory = Optional.ofNullable(event.getOption("category", OptionMapping::getAsString));
            String identifier = "all";
            List<SlashCommandMetadata> commandMetadataList;
            if (commandCategory.isPresent()) {
                CommandCategory category = CommandCategory.getCategoryByName(commandCategory.get()).orElse(CommandCategory.GENERAL);

                commandMetadataList = (new Managers.SlashCommands()).getCommandMetadata().stream()
                    .filter(command -> command.getCategory() == category)
                    .sorted()
                    .toList();
                identifier = commandCategory.get();
            } else commandMetadataList = (new Managers.SlashCommands()).getCommandMetadata().stream().sorted().toList();
            SlashCommandMetadata commandMetadata = commandMetadataList.get(0);
            List<ContainerChildComponent> children = new ArrayList<>(getContainerChildComponents(commandMetadata));
            children.add(
                ActionRow.of(
                    new AboutCommand().getButton("Prev", true),
                    new AboutCommand().getButton(ButtonStyle.SECONDARY,
                        String.format("Command (%s/%s)", commandMetadataList.indexOf(commandMetadata) + 1, commandMetadataList.size()),
                        true
                    ),
                    new AboutCommand().getButton(1, identifier, "Next")
                )
            );
            event.replyComponents(StandardResponse.getResponseContainer(String.format("About - %s", commandMetadata.getName()), children)).queue();
        } else {
            //Populate more data.
            event.replyComponents(StandardResponse.getResponseContainer(event.getJDA().getSelfUser().getEffectiveName(), "I am a bot.")).queue();
        }

    }

    @NotNull
    private static List<ContainerChildComponent> getContainerChildComponents(@NotNull SlashCommandMetadata commandMetadata) {
        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of(commandMetadata.getDescription()));
        children.add(TextDisplay.ofFormat("### NSFW?%n%s", commandMetadata.isNSFW() ? "Yes" : "No"));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("### Mastermind%n%s", commandMetadata.getMastermind().getWho()));
        children.add(TextDisplay.ofFormat("### Category%n%s", commandMetadata.getCategory().getName()));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("### Created on %s", Timestamp.getDateAsTimestamp(commandMetadata.getCreatedAt())));
        children.add(TextDisplay.ofFormat("### Last Updated on %s", Timestamp.getDateAsTimestamp(commandMetadata.getUpdatedAt())));
        return children;
    }

    public static class AboutCommand extends ButtonInteraction {
        public AboutCommand() {
            super("about-command", Pattern.compile("about-command-[0-9a-z]+-pg[0-9]+$"));
        }

        @Override
        public String getButtonId(int page, String identifier) {
            return getName() + "-" + identifier + "-pg" + page;
        }

        @Override
        public void execute(@NotNull ButtonInteractionEvent event, EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
            int reference = Integer.parseInt(event.getComponentId().split("-pg")[1]);
            String identifier = event.getComponentId().split("-")[2];
            Optional<CommandCategory> categoryOptional = CommandCategory.getCategoryByName(identifier);
            List<SlashCommandMetadata> metadataList = categoryOptional.map(category -> (new Managers.SlashCommands()).getCommandMetadata().stream()
                .filter(metadata -> metadata.getCategory() == category)
                .sorted()
                .toList())
                .orElseGet(() -> (new Managers.SlashCommands()).getCommandMetadata().stream().sorted().toList());

            SlashCommandMetadata commandMetadata = metadataList.get(reference);

            int nextPage = reference + 1;
            int prevPage = reference - 1;

            List<ContainerChildComponent> children = new ArrayList<>(getContainerChildComponents(commandMetadata));
            children.add(
                ActionRow.of(
                    getButton(prevPage, identifier, "Prev", prevPage < 0),
                    getButton(ButtonStyle.SECONDARY, String.format("Command (%s/%s)", reference + 1, metadataList.size()), true),
                    getButton(nextPage, identifier, "Next", nextPage == metadataList.size())
                )
            );

            event.editComponents(StandardResponse.getResponseContainer(String.format("About - %s", commandMetadata.getName()), children)).queue();
        }

    }
}
