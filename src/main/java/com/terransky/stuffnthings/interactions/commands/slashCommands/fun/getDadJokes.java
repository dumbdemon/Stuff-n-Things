package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.icanhazdadjoke.IcanhazdadjokeData;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

public class getDadJokes implements ICommandSlash {
    private final Logger log = LoggerFactory.getLogger(getDadJokes.class);

    @Override
    public String getName() {
        return "dad-jokes";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat format = Metadata.getFastDateFormat();
        return new Metadata(this.getName(), "Why was 6 afraid of 7? Because 7 was a registered 6 offender.", """
            An unoriginal or unfunny joke of a type supposedly told by middle-aged or older men.
            """, Mastermind.USER,
            CommandCategory.FUN,
            format.parse("25-8-2022_20:53"),
            format.parse("21-1-2023_16:05")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
        URL iCanHazDadJoke = new URL("https://icanhazdadjoke.com/");
        String iCanHazDadJokeLogo = "https://icanhazdadjoke.com/static/smile.svg";
        HttpURLConnection dadJoke = (HttpURLConnection) iCanHazDadJoke.openConnection();
        dadJoke.addRequestProperty("User-Agent", Config.getBotUserAgent());  //https://icanhazdadjoke.com/api#custom-user-agent
        dadJoke.addRequestProperty("Accept", "application/json");
        ObjectMapper om = new ObjectMapper();
        IcanhazdadjokeData theJoke = om.readValue(dadJoke.getInputStream(), IcanhazdadjokeData.class);

        event.replyEmbeds(new EmbedBuilder()
            .setDescription(theJoke.getJoke())
            .setThumbnail(iCanHazDadJokeLogo)
            .setColor(EmbedColors.getDefault())
            .setFooter("Requested by %s | ID #%s".formatted(blob.getMemberAsTag(), theJoke.getId()))
            .build()
        ).addActionRow(
            Button.primary("get-dad-joke", "Get new Dad Joke!")
        ).queue(msg -> {
            MessageEditData editData = new MessageEditBuilder()
                .setComponents(
                    ActionRow.of(Button.danger("expired-button", "Get new Dad Joke!"))
                )
                .build();
            msg.editOriginal(editData).queueAfter(10, TimeUnit.MINUTES, msg2 ->
                log.debug("Button on message [{}] on server with ID [{}] has expired.", msg2.getId(), msg2.getGuild().getId())
            );
        });
    }
}
