package com.terransky.stuffnthings.interactions.commands.slashCommands.mtg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.whatsInStandard.Ban;
import com.terransky.stuffnthings.dataSources.whatsInStandard.MtGSet;
import com.terransky.stuffnthings.dataSources.whatsInStandard.WhatsInStandardData;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class whatsInStandard implements ICommandSlash {
    private @NotNull String getSets(@NotNull List<MtGSet> mtgSets) {
        StringBuilder theSets = new StringBuilder();
        for (MtGSet mtgSet : mtgSets.stream().filter(set ->
            set.getCode() != null &&
                set.getEnterDate().getExact().compareTo(new Date()) <= 0 &&
                (set.getExitDate().getExact() == null || set.getExitDate().getExact().compareTo(new Date()) >= 0)
        ).toList()) {
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

    @NotNull
    private List<String> getSetCodesOfCurrent(@NotNull List<MtGSet> mtgSets) {
        return new ArrayList<>() {{
            for (MtGSet mtgSet : mtgSets.stream().filter(set ->
                set.getCode() != null &&
                    set.getEnterDate().getExact().compareTo(new Date()) <= 0 &&
                    (set.getExitDate().getExact() == null || set.getExitDate().getExact().compareTo(new Date()) >= 0)
            ).toList()) {
                add(mtgSet.getCode());
            }
        }};
    }

    private @NotNull String getBans(@NotNull List<Ban> banList, @NotNull List<MtGSet> mtgSets, boolean withReason) {
        StringBuilder theBans = new StringBuilder();
        List<String> setCodes = getSetCodesOfCurrent(mtgSets);
        for (Ban card : banList.stream().filter(card -> setCodes.stream().anyMatch(set -> set.equals(card.getSetCode()))).toList()) {
            theBans.append("[```%s (%s)%s```](%s)".formatted(
                    card.getCardName(),
                    card.getSetCode(),
                    withReason ? "\n Reason :: " + card.getReason() : "",
                    "https://scryfall.com/search?q=!\"%s\" set:%s"
                        .formatted(card.getCardName(), card.getSetCode())
                        .replaceAll(" ", "%20")
                )
            );
        }
        return theBans.toString();
    }

    @Override
    public String getName() {
        return "whats-in-standard";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "Get Magic: the Gathering's set list for the standard format.", """
            Prints out the sets and ban list information for Magic: the Gathering's standard formant created by Wizards of the Coast.
            Information the bot uses is provided by [WhatsInStandard.com](https://whatsinstandard.com).
            """,
            Mastermind.DEVELOPER,
            SlashModule.MTG,
            format.parse("27-10-2022_12:46"),
            format.parse("21-12-2022_20:05")
        )
            .addSubcommands(
                new SubcommandData("all", "Get all info about the standard format."),
                new SubcommandData("sets", "Get the standard sets only."),
                new SubcommandData("bans", "Get the ban list only.")
                    .addOption(OptionType.BOOLEAN, "include-reason", "Include reason for ban."),
                new SubcommandData("what-is-standard", "What is standard?")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("What's in standard?")
            .setColor(EmbedColors.getDefault());
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand received");
        String version = "6";
        ObjectMapper om = new ObjectMapper();

        URL wis = new URL("https://whatsinstandard.com/api/v%s/standard.json".formatted(version));
        WhatsInStandardData wisData = om.readValue(wis, WhatsInStandardData.class);
        if (wisData.isDeprecated()) {
            event.getHook().sendMessageEmbeds(
                eb.setDescription("Version %s has been deprecated. Please create an issue [here](%s).".formatted(version, Config.getErrorReportingURL()))
                    .setColor(EmbedColors.getError())
                    .build()
            ).queue();
            return;
        }

        switch (subcommand) {
            case "all" ->
                eb.setDescription("**Sets**:\n%s\n**Current Bans**:\n%s".formatted(this.getSets(wisData.getSets()), this.getBans(wisData.getBans(), wisData.getSets(), false)));
            case "sets" -> eb.setDescription("**Sets**:\n%s".formatted(this.getSets(wisData.getSets())));
            case "bans" -> {
                boolean withReason = event.getOption("include-reason", false, OptionMapping::getAsBoolean);
                eb.setDescription("**Current Bans**:\n%s".formatted(this.getBans(wisData.getBans(), wisData.getSets(), withReason)));
            }
            case "what-is-standard" -> eb.setDescription("""
                **What *is* Standard?**
                [Standard](https://magic.wizards.com/en/formats/standard) is a tournament format containing several recent Magic: The Gathering sets. Most sets enter the format when they're released and drop out about twenty-one months later.
                                
                Generally the group contains 5???8 sets; when a ninth would be released, the eldest four are dropped. This is a rule of thumb and exceptions are frequently made. This command will always have current information.
                                
                A **Standard card** is a card from a set currently part of the legal pool. *Different versions of a card count as the same card.*
                               
                A **Standard deck** contains 60+ Standard cards and can optionally have a sideboard of up to 15 additional such cards. Apart from basic lands, the combined main deck and sideboard cannot have more than four copies of any card.
                                
                **Related sets and formats**
                [Brawl](https://magic.wizards.com/en/formats/brawl) is a format based on Standard???all rotations listed here apply to Brawl as well???but Brawl has its own ban list.
                                
                Not all sets enter Standard upon release. For example, Masters sets and [Commander](https://magic.wizards.com/en/formats/commander) sets never enter the format.
                """);
        }

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
