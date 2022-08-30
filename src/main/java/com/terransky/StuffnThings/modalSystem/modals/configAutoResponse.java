package com.terransky.StuffnThings.modalSystem.modals;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.database.SQLiteDataSource;
import com.terransky.StuffnThings.modalSystem.IModal;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class configAutoResponse implements IModal {
    private final Logger log = LoggerFactory.getLogger(configAutoResponse.class);

    @Override
    public String getModalID() {
        return "config-auto-response";
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void modalExecute(@NotNull ModalInteractionEvent event) {
        String autoResponse = event.getValue("config-ar-text").getAsString();
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Commons().secondaryEmbedColor)
                .setFooter(event.getUser().getAsTag(), event.getMember().getEffectiveAvatarUrl());
        MessageEmbed testAR = new EmbedBuilder()
                .setColor(new Commons().defaultEmbedColor)
                .setTitle("Report Message")
                .setDescription(autoResponse)
                .setFooter(event.getUser().getAsTag(), event.getMember().getEffectiveAvatarUrl())
                .build();

        try (final PreparedStatement stmt = SQLiteDataSource.getConnection()
                .prepareStatement("UPDATE guilds SET reporting_ar = ? WHERE guild_id = ?")) {
            stmt.setString(1, autoResponse);
            stmt.setString(2, event.getGuild().getId());
            stmt.execute();

            eb.setTitle("Config Updated")
                    .setDescription("You have updated the auto response for the `Report Message` context action.\nThe next embed will show what it will look like.");
            event.replyEmbeds(eb.build(), testAR).queue();
        } catch (SQLException e) {
            log.error("%s : %s".formatted(e.getClass().getName(), e.getMessage()));
            e.printStackTrace();
        }
    }
}
