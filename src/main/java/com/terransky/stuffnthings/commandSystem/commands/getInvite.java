package com.terransky.stuffnthings.commandSystem.commands;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.commands.admin.checkPerms;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
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
    public Metadata getMetadata() {
        return new Metadata(this.getName(), """
            Returns the invite of the bot.
            """, Mastermind.DEFAULT);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Get an invite for the bot.");
    }

    @Override
    public boolean isGlobal() {
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
