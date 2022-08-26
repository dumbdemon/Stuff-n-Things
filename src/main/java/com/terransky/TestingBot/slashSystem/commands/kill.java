package com.terransky.TestingBot.slashSystem.commands;

import com.terransky.TestingBot.Commons;
import com.terransky.TestingBot.slashSystem.ISlash;
import com.terransky.TestingBot.slashSystem.cmdResources.killStringsRandom;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class kill implements ISlash {
    private final String[] killStringsRandom = new killStringsRandom().strings;
    private final Commons cmn = new Commons();

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Time to un-alive random members!")
                .addSubcommands(
                        new SubcommandData("random", "Try your hand at un-aliving someone!"),
                        new SubcommandData("target", "Target someone for a kill.")
                                .addOption(OptionType.USER, "target", "Your target", true),
                        new SubcommandData("suggest", "Suggest a kill-string. Use \"%s\" to represent targets. Up to four can be in a kill-string.")
                );
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        List<String> victims = new ArrayList<>();
        String subCommand = event.getSubcommandName() != null ? event.getSubcommandName() : "targets";
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(cmn.defaultEmbedColor);
        List<Member> memberList = new ArrayList<>();
        if (event.getGuild() != null) memberList = event.getGuild().getMembers();

        for (Member member : memberList) {
            if (!member.getUser().isBot() || member.getUser().equals(event.getJDA().getSelfUser())) {
                victims.add(member.getAsMention());
            }
        }
        String[] targets = victims.toArray(new String[0]);

        switch (subCommand) {
            case "random" -> {
                String message = String.format(killStringsRandom[(int) (Math.random() * killStringsRandom.length)],
                        targets[(int) (Math.random() * targets.length)],
                        targets[(int) (Math.random() * targets.length)],
                        targets[(int) (Math.random() * targets.length)],
                        targets[(int) (Math.random() * targets.length)]
                );

                eb.setColor(cmn.defaultEmbedColor)
                        .setTitle(Objects.requireNonNull(event.getMember()).getEffectiveName())
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
                        .addActionRows(ActionRow.of(suggestion))
                        .build();

                event.replyModal(modal).queue();
            }

            case "target" -> {
                String target = event.getOption("target", event.getJDA().getSelfUser(), OptionMapping::getAsUser).getAsMention();
                if (target.equals(event.getJDA().getSelfUser().getAsMention())) {
                    target = event.getJDA().getSelfUser().getAsMention() + " (hey wait a second...)";
                }
                eb.setTitle(Objects.requireNonNull(event.getMember()).getEffectiveName())
                        .setDescription("\u2026 tried to kill %s but they couldn't because that's bad manners!\n~~This sub command is not ready yet, come back later.~~".formatted(target))
                        .setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());
                event.replyEmbeds(eb.build()).queue();
            }

            default -> eb.setTitle("How did you get here?")
                    .setDescription("No seriously how did you get here?\nThat's impossible.");
        }
    }
}
