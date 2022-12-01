package com.terransky.stuffnthings.commandSystem.commands.fun;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class kill implements ISlashCommand {

    private final String[] randomStrings = {
        "was in their chem lab with %s trying to kill them with a 20 gauge shotgun!",
        "proctored the duel between Red [%s] and Blue [%s]. Blue won by a landslide!",
        "shot an arrow at %s, but it bounced off and hit %s instead!",
        "convinced %s to give them explosives to kill %s, they blew themselves up instead!",
        "threw a knife at %s but missed and hit %s!",
        "drove over %s whilst they were talking a selfie!",
        "just killed %s.",
        ", %s, %s, and %s saw %s failed their Perception DC check of 5 and fell into a 10m wide, 1km deep zombie infested hole!",
        "saw %s, %s, and %s die by their own grenade… how?",
        "got chopped in half by a helicopter blade!",
        "shot their gun trying to hit %s, but they hit %s instead!",
        "killed %s with %s's body. Literally swung them at them!",
        "knew a person named %s who died choking on a hot dog.",
        "proctored the duel between Red [%s] and Blue [%s]. Red won by a landslide!",
        "chokes on a candy cane! %s was watching and didn't do anything about it.",
        "tried a grenade for the first time in military training! They accidentally killed their C.O. %s, but at least they tried it!",
        "convinced %s to give them explosives to kill %s, it was a major success!",
        "got killed by their own rocket… they weren't even pointing at anybody…",
        "tried to assassinate Pope %s, but ended up getting %s's spouse dead instead! What a blunder!",
        ", %s, %s, and %s were last sean at %s's house. Don't know what happened to them though.",
        "killed %s… **WITH THIS THUMB!**"
    };
    private final String[] targetStrings = {
        "tried to kill %s but they couldn't because that's bad manners!"
    };
    public static final String MODAL_NAME = "kill-suggestion";

    @Override
    public String getName() {
        return "kill";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        var metadata = new Metadata(this.getName(), "Time to un-alive random members!", """
            Take a chance and try to kill a random member in your server! Or just *that guy* cause they've been annoying you recently.
            """, Mastermind.USER,
            SlashModule.FUN,
            format.parse("24-08-2022_11:10"),
            format.parse("1-12-2022_12:37")
        );

        metadata.addSubcommands(
            new SubcommandData("random", "Try your hand at un-aliving someone!"),
            new SubcommandData("target", "Target someone for a kill.")
                .addOption(OptionType.USER, "target", "Your target", true),
            new SubcommandData("suggest", "Suggest a kill-string. Use \"%s\" to represent targets. Up to four can be in a kill-string.")
        );

        return metadata;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        Random random = new Random(new Date().getTime());
        List<String> victims = new ArrayList<>();
        String subcommand = event.getSubcommandName();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter("Requested by " + event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl());
        List<Member> memberList =
            blob.getGuild().getMembers().stream().filter(it -> !it.getUser().isBot() || it.getUser().equals(event.getJDA().getSelfUser())).toList();

        for (Member member : memberList) {
            victims.add(member.getAsMention());
        }

        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        String killer = "";
        if (event.getMember() != null) killer = blob.getMember().getEffectiveName();

        switch (subcommand) {
            case "random" -> {
                String message = randomStrings[random.nextInt(randomStrings.length)].formatted(
                    victims.get(random.nextInt(victims.size())),
                    victims.get(random.nextInt(victims.size())),
                    victims.get(random.nextInt(victims.size())),
                    victims.get(random.nextInt(victims.size()))
                );

                if (message.contains(blob.getGuild().getSelfMember().getAsMention()))
                    message += " :O";

                eb.setColor(EmbedColors.getDefault())
                    .setTitle(killer)
                    .setDescription("… " + message);

                event.replyEmbeds(eb.build()).queue();
            }

            case "suggest" -> {
                TextInput suggestion = TextInput.create(MODAL_NAME, "Suggestion", TextInputStyle.PARAGRAPH)
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
                    .setDescription("… %s".formatted(targetStrings[random.nextInt(targetStrings.length)]).formatted(target));
                event.replyEmbeds(eb.build()).queue();
            }
        }
    }
}