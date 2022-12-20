package com.terransky.stuffnthings.interfaces;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import com.terransky.stuffnthings.utilities.SelectMenuType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface ISelectMenu extends IInteractionElement {

    SelectMenuType getSelectMenuType();

    /**
     * The main handler for select menus.
     *
     * @param event {@link EntitySelectInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown across all ISelectMenus.
     */
    default void execute(@NotNull EntitySelectInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.replyEmbeds(new EmbedBuilder()
            .setTitle("Generic Select Response")
            .setDescription("It seems you somehow got here. Please report this incident [here](%s)".formatted(Config.getErrorReportingURL()))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).setEphemeral(true).queue();
    }

    /**
     * The main handler for select menus.
     *
     * @param event {@link StringSelectInteractionEvent}.
     * @param blob  An {@link EventBlob} containing checked non-null {@link net.dv8tion.jda.api.entities.Guild Guild} object
     *              and {@link net.dv8tion.jda.api.entities.Member Member} object.
     * @throws Exception Any exception that could get thrown across all ISelectMenus.
     */
    default void execute(@NotNull StringSelectInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        event.replyEmbeds(new EmbedBuilder()
            .setTitle("Generic Select Response")
            .setDescription("It seems you somehow got here. Please report this incident [here](%s)".formatted(Config.getErrorReportingURL()))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build()
        ).setEphemeral(true).queue();
    }
}
