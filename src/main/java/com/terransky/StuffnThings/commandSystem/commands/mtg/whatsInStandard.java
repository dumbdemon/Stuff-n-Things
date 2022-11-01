package com.terransky.StuffnThings.commandSystem.commands.mtg;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.exceptions.DiscordAPIException;
import com.terransky.StuffnThings.jacksonMapper.whatsInStandard.Ban;
import com.terransky.StuffnThings.jacksonMapper.whatsInStandard.Set;
import com.terransky.StuffnThings.jacksonMapper.whatsInStandard.WhatsInStandardData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
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
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Get Magic: the Gathering's set list for the standard format.")
            .addSubcommands(
                new SubcommandData("all", "Get all info about the standard format."),
                new SubcommandData("sets", "Get the standard sets only."),
                new SubcommandData("bans", "Get the ban list only.")
                    .addOption(OptionType.BOOLEAN, "include-reason", "Include reason for ban.")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        event.deferReply().queue();
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("What's in standard?")
            .setColor(Commons.defaultEmbedColor);
        String subCommand = event.getSubcommandName();
        if (subCommand == null) throw new DiscordAPIException("No subcommand received");
        String version = "6";
        ObjectMapper om = new ObjectMapper();

        URL wis = new URL("https://whatsinstandard.com/api/v%s/standard.json".formatted(version));
        WhatsInStandardData wisData = om.readValue(wis, WhatsInStandardData.class);
        if (wisData.isDeprecated()) {
            event.getHook().sendMessageEmbeds(
                eb.setDescription("Version %s has been deprecated. Please contact <@%s> to update it.".formatted(version, Commons.config.get("OWNER_ID")))
                    .build()
            ).queue();
            return;
        }

        switch (subCommand) {
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