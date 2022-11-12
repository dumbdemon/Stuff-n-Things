package com.terransky.stuffnthings.modalSystem;

import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.interfaces.IModal;
import com.terransky.stuffnthings.modalSystem.modals.killSuggest;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ModalManager extends ListenerAdapter {
    private final List<IModal> iModalList = new ArrayList<>();
    private final Dotenv config = Dotenv.configure().load();
    private final Logger log = LoggerFactory.getLogger(ModalManager.class);

    public ModalManager() {
        addModal(new killSuggest());
    }

    private void addModal(IModal iModal) {
        boolean modalFound = iModalList.stream().anyMatch(it -> it.getName().equalsIgnoreCase(iModal.getName()));

        if (modalFound) throw new IllegalArgumentException("A modal with that name already exists");

        iModalList.add(iModal);
    }

    @SuppressWarnings("unused")
    @Nullable
    private IModal getModal(@NotNull String search) {
        String toSearch = search.toLowerCase();

        for (IModal iModal : iModalList) {
            if (iModal.getName().equals(toSearch)) {
                return iModal;
            }
        }
        return null;
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        if (event.getGuild() == null) return;

        IModal modal = getModal(event.getModalId());
        EmbedBuilder eb = new EmbedBuilder()
            .setTitle("Oops!")
            .setDescription("An error occurred while executing the prompt!\nPlease contact <@" + config.get("OWNER_ID") + "> with the command you used and when.")
            .setColor(Commons.DEFAULT_EMBED_COLOR)
            .setFooter(event.getUser().getAsTag());

        if (modal != null) {
            log.debug("Modal " + modal.getName().toUpperCase() + " called on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
            try {
                modal.execute(event);
            } catch (Exception e) {
                log.debug(event.getModalId() + " interaction on %s [%d]".formatted(event.getGuild().getName(), event.getGuild().getIdLong()));
                log.error(e.getClass().getName() + ": " + e.getMessage());
                if (event.isAcknowledged()) {
                    event.getHook().sendMessageEmbeds(eb.build()).queue();
                } else event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            }
        }
    }
}
