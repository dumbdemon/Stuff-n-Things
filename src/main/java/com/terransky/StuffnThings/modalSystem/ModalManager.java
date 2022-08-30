package com.terransky.StuffnThings.modalSystem;

import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.modalSystem.modals.configAutoResponse;
import com.terransky.StuffnThings.modalSystem.modals.killSuggest;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModalManager extends ListenerAdapter {
    private final Color embedColor = new Commons().defaultEmbedColor;
    private final List<IModal> iModalList = new ArrayList<>();
    private final Dotenv config = Dotenv.configure().load();
    private final Logger log = LoggerFactory.getLogger(ModalManager.class);

    public ModalManager() {
        addModal(new killSuggest());
        addModal(new configAutoResponse());
    }

    private void addModal(IModal iModal) {
        boolean modalFound = iModalList.stream().anyMatch(it -> it.getModalID().equalsIgnoreCase(iModal.getModalID()));

        if (modalFound) throw new IllegalArgumentException("A modal with that name already exists");

        iModalList.add(iModal);
    }

    @Nullable
    private IModal getModal(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (IModal iModal : iModalList) {
            if (iModal.getModalID().equals(toSearch)) {
                return iModal;
            }
        }
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        IModal modal = getModal(event.getModalId());
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Oops!")
                .setDescription("An error occurred while executing the prompt!\nPlease contact <@" + config.get("OWNER_ID") + "> with the command you used and when.")
                .setColor(embedColor)
                .setFooter(event.getUser().getAsTag());

        if (modal != null) {
            String origins = event.isFromGuild() ? "%s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()) : event.getUser().getAsTag() + "'s private channel";
            log.debug("Modal " + modal.getModalID().toUpperCase() + " called on " + origins);
            try {
                modal.modalExecute(event);
            } catch (Exception e) {
                log.debug(event.getModalId() + " interaction on " + origins);
                log.error(e.getClass().getName() + ": " + e.getMessage());
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
