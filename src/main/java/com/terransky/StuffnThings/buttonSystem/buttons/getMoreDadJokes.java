package com.terransky.StuffnThings.buttonSystem.buttons;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.buttonSystem.IButton;
import com.terransky.StuffnThings.jacksonMapper.icanhazdadjoke.IcanhazdadjokeData;
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
    public String getID() {
        return "get-dad-joke";
    }

    @Override
    public void execute(@NotNull ButtonInteractionEvent event) throws Exception {
        URL iCanHazDadJoke = new URL("https://icanhazdadjoke.com/");
        HttpURLConnection dadJoke = (HttpURLConnection) iCanHazDadJoke.openConnection();
        dadJoke.addRequestProperty("User-Agent", Commons.config.get("BOT_USER_AGENT")); //https://icanhazdadjoke.com/api#custom-user-agent
        dadJoke.addRequestProperty("Accept", "application/json");
        ObjectMapper om = new ObjectMapper();
        IcanhazdadjokeData theJoke = om.readValue(new InputStreamReader(dadJoke.getInputStream()), IcanhazdadjokeData.class);

        MessageEditData message = new MessageEditBuilder()
            .setEmbeds(new EmbedBuilder()
                .setDescription(theJoke.getJoke())
                .setColor(Commons.defaultEmbedColor)
                .setFooter("Requested by %s | ID#%s".formatted(event.getUser().getAsTag(), theJoke.getId()))
                .build()
            ).build();

        event.editMessage(message).queue();
    }
}
