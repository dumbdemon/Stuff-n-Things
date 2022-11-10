package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.ExtraDetails;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.Mastermind;
import com.terransky.StuffnThings.commandSystem.commands.cmdResources.killStrings;
import com.terransky.StuffnThings.exceptions.DiscordAPIException;
import com.terransky.StuffnThings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class kill implements ISlashCommand {
    private final String[] randomStrings = killStrings.random;
    private final String[] targetStrings = killStrings.target;

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public ExtraDetails getExtraDetails() {
        return new ExtraDetails(this.getName(), """
            Take a chance and try to kill a random member in your server! Or just *that guy* cause they've been annoying you recently.
            """, Mastermind.USER);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Time to un-alive random members!")
            .addSubcommands(
                new SubcommandData("random", "Try your hand at un-aliving someone!"),
                new SubcommandData("target", "Target someone for a kill.")
                    .addOption(OptionType.USER, "target", "Your target", true),
                new SubcommandData("suggest", "Suggest a kill-string. Use \"%s\" to represent targets. Up to four can be in a kill-string.")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        Random random = new Random();
        List<String> victims = new ArrayList<>();
        String subCommand = event.getSubcommandName();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.DEFAULT_EMBED_COLOR);
        List<Member> memberList = new ArrayList<>();
        if (event.getGuild() != null)
            memberList = event.getGuild().getMembers().stream().filter(it -> !it.getUser().isBot() || it.getUser().equals(event.getJDA().getSelfUser())).toList();

        for (Member member : memberList) {
            victims.add(member.getAsMention());
        }

        if (subCommand == null) throw new DiscordAPIException("No subcommand was given.");
        String killer = "";
        if (event.getMember() != null) killer = event.getMember().getEffectiveName();

        switch (subCommand) {
            case "random" -> {
                String message = randomStrings[random.nextInt(randomStrings.length)].formatted(
                    victims.get(random.nextInt(victims.size())),
                    victims.get(random.nextInt(victims.size())),
                    victims.get(random.nextInt(victims.size())),
                    victims.get(random.nextInt(victims.size()))
                );

                eb.setColor(Commons.DEFAULT_EMBED_COLOR)
                    .setTitle(killer)
                    .setDescription("\u2026 " + message)
                    .setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

                event.replyEmbeds(eb.build()).queue();
            }

            case "suggest" -> {
                TextInput suggestion = TextInput.create("kill-suggestion", "Suggestion", TextInputStyle.PARAGRAPH)
                    .setRequired(true)
                    .setRequiredRange(10, MessageEmbed.DESCRIPTION_MAX_LENGTH / 4)
                    .setPlaceholder("Use \"%s\" to represent up to four targets! There could be more, but I don't wanna!")
                    .build();

                Modal modal = Modal.create("kill-suggest", "Suggest Kill-String")
                    .addActionRow(suggestion)
                    .build();

                event.replyModal(modal).queue();
            }

            case "target" -> {
                String target = event.getOption("target", event.getJDA().getSelfUser(), OptionMapping::getAsUser).getAsMention();
                if (target.equals(event.getJDA().getSelfUser().getAsMention())) {
                    target += " (hey wait a second...)";
                }
                eb.setTitle(killer)
                    .setDescription("\u2026 %s".formatted(targetStrings[random.nextInt(targetStrings.length)]).formatted(target))
                    .setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());
                event.replyEmbeds(eb.build()).queue();
            }
        }
    }
}