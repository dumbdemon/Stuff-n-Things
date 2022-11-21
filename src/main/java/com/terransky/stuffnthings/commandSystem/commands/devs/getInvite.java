package com.terransky.stuffnthings.commandSystem.commands.devs;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.commands.admin.checkPerms;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class getInvite implements ISlashCommand {
    @Override
    public String getName() {
        return "get-invite";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat formatter = Commons.getFastDateFormat();
        return new Metadata(this.getName(), "Get an invite for the bot.", """
            Returns the invite of the bot.
            """, Mastermind.DEFAULT,
            formatter.parse("24-08-2022_11:10"),
            formatter.parse("19-11-2022_11:37")
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public @Nullable List<Long> getServerRestrictions() {
        final List<Long> ids = new ArrayList<>();
        ids.add(Long.parseLong(Commons.getConfig().get("SUPPORT_GUILD_ID")));
        return ids;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {
        event.replyEmbeds(
            new EmbedBuilder()
                .setColor(Commons.getDefaultEmbedColor())
                .setTitle("You can't even use this.", event.getJDA().getInviteUrl(checkPerms.getRequiredPerms()))
                .build()
        ).queue();
    }
}
