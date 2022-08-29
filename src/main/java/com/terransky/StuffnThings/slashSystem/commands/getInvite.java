package com.terransky.StuffnThings.slashSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.slashSystem.ISlash;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class getInvite implements ISlash {
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final Dotenv config = Dotenv.configure().load();

    @Override
    public String getName() {
        return "get-invite";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Get an invite for the bot.");
    }

    @Override
    public boolean isGlobalCommand() {
        return false;
    }

    @Override
    public @Nullable List<Long> getServerRestrictions() {
        final List<Long> ids = new ArrayList<>();
        ids.add(Long.parseLong(config.get("SUPPORT_GUILD_ID")));
        return ids;
    }

    @Override
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        List<Permission> perms = new ArrayList<>();
        perms.add(Permission.MESSAGE_SEND);
        perms.add(Permission.MESSAGE_ADD_REACTION);
        perms.add(Permission.MESSAGE_EMBED_LINKS);
        perms.add(Permission.MESSAGE_EXT_EMOJI);
        perms.add(Permission.MESSAGE_EXT_STICKER);
        perms.add(Permission.VIEW_CHANNEL);

        EmbedBuilder eb = new EmbedBuilder().setColor(embedColor)
                .setTitle("You can't even use this.", event.getJDA().getInviteUrl(perms));
        event.replyEmbeds(eb.build()).queue();
    }
}
