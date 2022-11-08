package com.terransky.StuffnThings.commandSystem.commands;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.commands.admin.checkPerms;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class getInvite implements ISlashCommand {
    @Override
    public String getName() {
        return "get-invite";
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Get an invite for the bot.");
    }

    @Override
    public boolean isGlobalCommand() {
        return false;
    }

    @Override
    public @Nullable List<Long> getServerRestrictions() {
        final List<Long> ids = new ArrayList<>();
        ids.add(Long.parseLong(Commons.CONFIG.get("SUPPORT_GUILD_ID")));
        return ids;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) {
        event.replyEmbeds(
            new EmbedBuilder()
                .setColor(Commons.DEFAULT_EMBED_COLOR)
                .setTitle("You can't even use this.", event.getJDA().getInviteUrl(checkPerms.requiredPerms()))
                .build()
        ).queue();
    }
}
