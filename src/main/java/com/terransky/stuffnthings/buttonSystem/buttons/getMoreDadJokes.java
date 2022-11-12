package com.terransky.stuffnthings.buttonSystem.buttons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.dataSources.icanhazdadjoke.IcanhazdadjokeData;
import com.terransky.stuffnthings.interfaces.IButton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class getMoreDadJokes implements IButton {
    @Override
    public String getName() {
        return "get-dad-joke";
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event) throws Exception {
        URL iCanHazDadJoke = new URL("https://icanhazdadjoke.com/");
        HttpURLConnection dadJoke = (HttpURLConnection) iCanHazDadJoke.openConnection();
        dadJoke.addRequestProperty("User-Agent", Commons.CONFIG.get("BOT_USER_AGENT")); //https://icanhazdadjoke.com/api#custom-user-agent
        dadJoke.addRequestProperty("Accept", "application/json");
        ObjectMapper om = new ObjectMapper();
        IcanhazdadjokeData theJoke = om.readValue(new InputStreamReader(dadJoke.getInputStream()), IcanhazdadjokeData.class);

        MessageEditData message = new MessageEditBuilder()
            .setEmbeds(new EmbedBuilder()
                .setDescription(theJoke.getJoke())
                .setColor(Commons.DEFAULT_EMBED_COLOR)
                .setFooter("Requested by %s | ID#%s".formatted(event.getUser().getAsTag(), theJoke.getId()))
                .build()
            ).build();

        event.editMessage(message).queue();
    }
}
