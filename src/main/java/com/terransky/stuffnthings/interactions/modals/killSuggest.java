package com.terransky.stuffnthings.interactions.modals;

import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.kill;
import com.terransky.stuffnthings.interfaces.interactions.IModal;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class killSuggest implements IModal {

    public static final String ACCEPT_BUTTON = "accept-kill";
    public static final String DENY_BUTTON = "deny-kill";

    @Override
    public String getName() {
        return "kill-suggest";
    }

    @Override
    public void execute(@NotNull ModalInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        Random rando = new Random(new Date().getTime());
        event.deferReply().queue();
        Optional<ModalMapping> ifSuggestion = Optional.ofNullable(event.getValue(kill.MODAL_NAME));
        String suggestion = ifSuggestion.orElseThrow(DiscordAPIException::new).getAsString();

        List<String> victims = new ArrayList<>() {{
            blob.getGuild().getMembers().stream()
                .filter(member -> !member.getUser().isBot() ||
                    member.getUser().equals(event.getJDA().getSelfUser())
                ).forEach(member -> add(member.getAsMention()));
        }};

        TextChannel requestChannel = event.getJDA().getTextChannelById(Config.getRequestChannelID());

        if (requestChannel == null)
            throw new DiscordAPIException("Either the channel channel does not exist or I could not obtain the channel.");

        requestChannel.sendMessage(suggestion)
            .addActionRow(
                Button.success(ACCEPT_BUTTON, "Accept"),
                Button.danger(DENY_BUTTON, "Deny")
            ).queue();

        String testKillString = suggestion.formatted(
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size())),
            victims.get(rando.nextInt(victims.size()))
        );

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColors.getSecondary())
                .setTitle("Suggestion received!")
                .setDescription("The next embed will show what your suggestion will look like!\n" +
                    "***Note: Will not show up automatically!***")
                .build(),
            new EmbedBuilder()
                .setColor(EmbedColors.getDefault())
                .setTitle(Objects.requireNonNull(event.getMember()).getEffectiveName())
                .setDescription("… " + testKillString)
                .setFooter("Suggestion by " + event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
                .build()
        ).queue();
    }
}
