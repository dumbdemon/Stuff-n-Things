package com.terransky.stuffnthings.commandSystem.commands.mtg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.dataSources.whatsInStandard.Ban;
import com.terransky.stuffnthings.dataSources.whatsInStandard.Set;
import com.terransky.stuffnthings.dataSources.whatsInStandard.WhatsInStandardData;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class whatsInStandard implements ISlashCommand {
    private @NotNull String getSets(@NotNull List<Set> wisData) {
        StringBuilder theSets = new StringBuilder();
        for (Set mtgSet : wisData) {
            if (mtgSet.getCode() != null && mtgSet.getEnterDate().getExact().compareTo(new Date()) <= 0) {
                theSets.append("```%s (%s) || Leaves: %s```"
                    .formatted(
                        mtgSet.getName(),
                        mtgSet.getCode(),
                        mtgSet.getExitDate().getRough()
                    )
                );
            }
        }
        return theSets.toString();
    }

    private @NotNull String getBans(@NotNull List<Ban> wisData, boolean withReason) {
        StringBuilder theBans = new StringBuilder();
        for (Ban mtgBans : wisData) {
            if (withReason) {
                theBans.append("```%s (%s)\n Reason :: %s```".formatted(
                        mtgBans.getCardName(),
                        mtgBans.getSetCode(),
                        mtgBans.getReason()
                    )
                );
            } else {
                theBans.append("```%s (%s)```".formatted(
                        mtgBans.getCardName(),
                        mtgBans.getSetCode()
                    )
                );
            }
        }
        return theBans.toString();
    }

    @Override
    public String getName() {
        return "mtg-standard";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Commons.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Get Magic: the Gathering's set list for the standard format.", """
            *M:tG Command*
            Prints out the sets and ban list information for Magic: the Gathering's standard formant created by Wizards of the Coast.
            Information the bot uses is provided by [WhatsInStandard.com](https://whatsinstandard.com).
            """,
            Mastermind.DEVELOPER,
            SlashModule.MTG,
            format.parse("27-10-2022_12:46"),
            format.parse("23-11-2022_15:00")
        );

        metadata.addSubcommands(
            new SubcommandData("all", "Get all info about the standard format."),
            new SubcommandData("sets", "Get the standard sets only."),
            new SubcommandData("bans", "Get the ban list only.")
                .addOption(OptionType.BOOLEAN, "include-reason", "Include reason for ban.")
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("What's in standard?")
            .setColor(Commons.getDefaultEmbedColor());
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand received");
        String version = "6";
        ObjectMapper om = new ObjectMapper();

        URL wis = new URL("https://whatsinstandard.com/api/v%s/standard.json".formatted(version));
        WhatsInStandardData wisData = om.readValue(wis, WhatsInStandardData.class);
        if (wisData.isDeprecated()) {
            event.getHook().sendMessageEmbeds(
                eb.setDescription("Version %s has been deprecated. Please contact <@%s> to update it.".formatted(version, Commons.getConfig().get("OWNER_ID")))
                    .build()
            ).queue();
            return;
        }

        switch (subcommand) {
            case "all" ->
                eb.setDescription("**Sets**:\n%s\n**Current Bans**:\n%s".formatted(this.getSets(wisData.getSets()), this.getBans(wisData.getBans(), false)));
            case "sets" -> eb.setDescription("**Sets**:\n%s".formatted(this.getSets(wisData.getSets())));
            case "bans" -> {
                boolean withReason = event.getOption("include-reason", false, OptionMapping::getAsBoolean);
                eb.setDescription("**Current Bans**:\n%s".formatted(this.getBans(wisData.getBans(), withReason)));
            }
        }

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
