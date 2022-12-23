package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interfaces.IModal;
import com.terransky.stuffnthings.utilities.cannedAgenda.GuildOnly;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.EmbedColors;
import com.terransky.stuffnthings.utilities.command.EventBlob;
import com.terransky.stuffnthings.utilities.general.Interactions;
import com.terransky.stuffnthings.utilities.general.LogList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ModalManager extends ListenerAdapter {
    private final List<IModal> iModal = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(ModalManager.class);

    public ModalManager(@NotNull IModal... iModals) {
        for (IModal iModal : iModals) {
            addModal(iModal);
        }
    }

    /**
     * Add an {@link IModal} to be indexed and used.
     *
     * @param iModal An {@link IModal} object.
     */
    private void addModal(IModal iModal) {
        boolean modalFound = this.iModal.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iModal.getName()));

        if (modalFound) throw new IllegalArgumentException("A modal with that name already exists");

        this.iModal.add(iModal);
    }

    /**
     * Get a modal to be used at {@code onModalInteraction()}.
     *
     * @param search the {@link IModal}'s ID.
     * @return An {@link Optional} of {@link IModal}.
     */
    private Optional<IModal> getModal(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (IModal iModal : iModal) {
            if (iModal.getName().equals(toSearch)) {
                return Optional.of(iModal);
            }
        }
        return Optional.empty();
    }

    /**
     * The main event handler for all modals.
     *
     * @param event A {@link ModalInteractionEvent}
     */
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getGuild() == null) {
            GuildOnly.interactionResponse(event, Interactions.MODAL);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<IModal> ifModal = getModal(event.getModalId());
        MessageEmbed modalFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription(Responses.INTERACTION_FAILED.getMessage(Interactions.MODAL))
            .setColor(EmbedColors.getError())
            .setFooter(event.getUser().getAsTag(), blob.getMemberEffectiveAvatarUrl())
            .build();

        if (ifModal.isPresent()) {
            IModal modal = ifModal.get();
            log.debug("Modal %s called on %s [%d]".formatted(modal.getName().toUpperCase(), blob.getGuild().getName(), blob.getGuildIdLong()));
            try {
                modal.execute(event, blob);
            } catch (Exception e) {
                log.debug(event.getModalId() + " interaction failed on %s [%d]".formatted(blob.getGuildName(), blob.getGuildIdLong()));
                log.error(e.getClass().getName() + ": " + e.getMessage());
                LogList.error(Arrays.asList(e.getStackTrace()), log);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(modalFailed).queue();
                } else event.replyEmbeds(modalFailed).setEphemeral(true).queue();
            }
        }
    }
}
