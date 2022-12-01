package com.terransky.stuffnthings.modalSystem;

import com.terransky.stuffnthings.commandSystem.utilities.EventBlob;
import com.terransky.stuffnthings.interfaces.IModal;
import com.terransky.stuffnthings.modalSystem.modals.killSuggest;
import com.terransky.stuffnthings.utilities.CannedBotResponses;
import com.terransky.stuffnthings.utilities.Config;
import com.terransky.stuffnthings.utilities.EmbedColors;
import com.terransky.stuffnthings.utilities.LogList;
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
    private final List<IModal> iModalList = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(ModalManager.class);

    public ModalManager() {
        addModal(new killSuggest());
    }

    /**
     * Add an {@link IModal} to be indexed and used.
     *
     * @param iModal An {@link IModal} object.
     */
    private void addModal(IModal iModal) {
        boolean modalFound = iModalList.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iModal.getName()));

        if (modalFound) throw new IllegalArgumentException("A modal with that name already exists");

        iModalList.add(iModal);
    }

    /**
     * Get a modal to be used at {@code onModalInteraction()}.
     *
     * @param search the {@link IModal}'s ID.
     * @return An {@link Optional} of {@link IModal}.
     */
    private Optional<IModal> getModal(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (IModal iModal : iModalList) {
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
            CannedBotResponses.botIsGuildOnly(event);
            return;
        }
        EventBlob blob = new EventBlob(event.getGuild(), event.getMember());

        Optional<IModal> ifModal = getModal(event.getModalId());
        MessageEmbed modalFailed = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing the prompt!\nPlease report this event [here](%s).".formatted(Config.getConfig().get("BOT_ERROR_REPORT")))
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
                LogList.error(Arrays.asList(e.getStackTrace()), ModalManager.class);
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(modalFailed).queue();
                } else event.replyEmbeds(modalFailed).setEphemeral(true).queue();
            }
        }
    }
}
