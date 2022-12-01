package com.terransky.stuffnthings.commandSystem.commands.devs;

import com.terransky.stuffnthings.commandSystem.commands.admin.checkPerms;
import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.commandSystem.utilities.Mastermind;
import com.terransky.stuffnthings.commandSystem.utilities.Metadata;
import com.terransky.stuffnthings.commandSystem.utilities.SlashModule;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import net.dv8tion.jda.api.EmbedBuilder;
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
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "Get an invite for the bot.", """
            Returns the invite of the bot.
            """, Mastermind.DEFAULT,
            SlashModule.DEVS,
            format.parse("24-08-2022_11:10"),
            format.parse("1-12-2022_12:37")
        );
    }

    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public @Nullable List<Long> getServerRestrictions() {
        final List<Long> ids = new ArrayList<>();
        ids.add(Long.parseLong(Config.getConfig().get("SUPPORT_GUILD_ID")));
        return ids;
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        event.replyEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColors.getDefault())
                .setTitle("You can't even use this.", event.getJDA().getInviteUrl(checkPerms.getRequiredPerms()))
                .build()
        ).queue();
    }
}
