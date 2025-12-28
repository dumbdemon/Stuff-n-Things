package com.terransky.stuffnthings.interactions.commands.slashCommands.mtg;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.whatsInStandard.Ban;
import com.terransky.stuffnthings.dataSources.whatsInStandard.MtGSet;
import com.terransky.stuffnthings.dataSources.whatsInStandard.SetDate;
import com.terransky.stuffnthings.dataSources.whatsInStandard.WhatsInStandardData;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.Pojo;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Timestamp;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public class WhatsInStandard extends SlashCommandInteraction {

    private final Predicate<MtGSet> IS_VALID_SET = (set) -> set.getCode() != null &&
        (set.getEnterDate().getExact() != null && SetDate.toOffsetDateTime(set.getEnterDate().getExact()).isBefore(OffsetDateTime.now())) &&
        (set.getExitDate().getExact() == null || SetDate.toOffsetDateTime(set.getExitDate().getExact()).isAfter(OffsetDateTime.now()));

    public WhatsInStandard() {
        super("whats-in-standard", "Get Magic: the Gathering's set list for the standard format.",
            Mastermind.DEVELOPER, CommandCategory.MTG,
            parseDate(2022, 10, 27, 12, 40),
            parseDate(2025, 12, 27, 2, 49)
        );
        addSubcommands(
            new SubcommandData("all", "Get all info about the standard format."),
            new SubcommandData("sets", "Get the standard sets only."),
            new SubcommandData("bans", "Get the ban list only.")
                .addOption(OptionType.BOOLEAN, "include-reason", "Include reason for ban."),
            new SubcommandData("what-is-standard", "What is standard?")
        );
    }

    private @NotNull String getSets(@NotNull List<MtGSet> mtgSets) {
        StringBuilder theSets = new StringBuilder();
        for (MtGSet mtgSet : mtgSets.stream().filter(IS_VALID_SET).toList()) {
            theSets.append("```%s (%s) || Leaves: %s```"
                .formatted(
                    mtgSet.getName(),
                    mtgSet.getCode(),
                    mtgSet.getExitDate().getRough()
                )
            );
        }
        return theSets.toString();
    }

    private @NotNull String getBans(@NotNull List<Ban> banList, @NotNull List<MtGSet> mtgSets, boolean withReason) {
        StringBuilder theBans = new StringBuilder();
        List<String> setCodes = mtgSets.stream().filter(IS_VALID_SET).map(MtGSet::getCode).toList();
        for (Ban card : banList.stream().filter(card -> setCodes.stream().anyMatch(set -> set.equals(card.getSetCode()))).toList()) {
            theBans.append("""
                    - [**%s [%s]**](%s)%s
                    """.formatted(
                    card.getName(),
                    card.getSetCode(),
                    "https://scryfall.com/search?q=\"%s\" set:%s"
                        .formatted(card.getName(), card.getSetCode())
                        .replaceAll(" ", "%20"),
                    withReason ? "\n    - " + card.getReason() : ""
                )
            );
        }
        return theBans.toString().isEmpty() ? String.format("*No bans as of %s.*", Timestamp.getDateAsTimestamp(OffsetDateTime.now(), Timestamp.LONG_DATE_W_DoW_SHORT_TIME)) :
            theBans.toString();
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException, ExecutionException, InterruptedException {
        event.deferReply().queue();
        String subcommand = event.getSubcommandName();
        String title = "What's in standard?";
        if (subcommand == null) throw new DiscordAPIException("No subcommand received");
        int version = 6;

        InputStream wis = new URL("https://whatsinstandard.com/api/v%s/standard.json".formatted(version)).openStream();
        WhatsInStandardData wisData = Pojo.getMapperObject().readValue(wis, WhatsInStandardData.class);
        if (wisData.getDeprecated()) {
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(title, "\"Version %s has been deprecated. Please create an issue [here](%s).\""
                    .formatted(version, StuffNThings.getConfig().getCore().getReportingUrl()), BotColors.ERROR)
            ).queue();
            return;
        }
        List<ContainerChildComponent> children = new ArrayList<>();

        switch (subcommand) {
            case "all" -> {
                children.add(TextDisplay.ofFormat("### Sets\n%s", this.getSets(wisData.getSets())));
                children.add(TextDisplay.ofFormat("### Current Bans\n%s", this.getBans(wisData.getBans(), wisData.getSets(), false)));
            }
            case "mtGSets" -> children.add(TextDisplay.ofFormat("**Sets**:\n%s", this.getSets(wisData.getSets())));
            case "bans" -> {
                boolean withReason = event.getOption("include-reason", false, OptionMapping::getAsBoolean);
                children.add(TextDisplay.ofFormat("**Current Bans**:\n%s", this.getBans(wisData.getBans(), wisData.getSets(), withReason)));
            }
            case "what-is-standard" -> children.add(TextDisplay.of("""
                **What *is* Standard?**
                [Standard](https://magic.wizards.com/en/formats/standard) is a tournament format containing several recent Magic: The Gathering mtGSets. Most mtGSets enter the format when they're released and drop out about twenty-one months later.
                
                Generally the group contains 5–8 mtGSets; when a ninth would be released, the eldest four are dropped. This is a rule of thumb and exceptions are frequently made. This command will always have current information.
                
                A **Standard card** is a card from a set currently part of the legal pool. *Different versions of a card count as the same card.*
                
                A **Standard deck** contains 60+ Standard cards and can optionally have a sideboard of up to 15 additional such cards. Apart from basic lands, the combined main deck and sideboard cannot have more than four copies of any card.
                
                **Related mtGSets and formats**
                [Brawl](https://magic.wizards.com/en/formats/brawl) is a format based on Standard—all rotations listed here apply to Brawl as well—but Brawl has its own ban list.
                
                Not all mtGSets enter Standard upon release. For example, Masters mtGSets and [Commander](https://magic.wizards.com/en/formats/commander) mtGSets never enter the format.
                """));
        }

        event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(title, children)).queue();
    }
}
