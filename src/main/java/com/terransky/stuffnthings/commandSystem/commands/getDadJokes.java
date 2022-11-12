package com.terransky.stuffnthings.commandSystem.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.Commons;
import com.terransky.stuffnthings.commandSystem.metadata.Mastermind;
import com.terransky.stuffnthings.commandSystem.metadata.Metadata;
import com.terransky.stuffnthings.dataSources.icanhazdadjoke.IcanhazdadjokeData;
import com.terransky.stuffnthings.interfaces.ISlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

public class getDadJokes implements ISlashCommand {
    private final Logger log = LoggerFactory.getLogger(getDadJokes.class);

    @Override
    public String getName() {
        return "dad-jokes";
    }

    @Override
    public Metadata getMetadata() throws ParseException {
        FastDateFormat formatter = FastDateFormat.getInstance("dd-MM-yyyy_HH:mm");
        return new Metadata(this.getName(), """
            An unoriginal or unfunny joke of a type supposedly told by middle-aged or older men.
            """, Mastermind.USER,
            formatter.parse("25-8-2022_20:53"),
            formatter.parse("12-11-2022_12:08"));
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(this.getName(), "Why was 6 afraid of 7? Because 7 was a registered 6 offender.");
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        URL iCanHazDadJoke = new URL("https://icanhazdadjoke.com/");
        String iCanHazDadJokeLogo = "https://icanhazdadjoke.com/static/smile.svg";
        HttpURLConnection dadJoke = (HttpURLConnection) iCanHazDadJoke.openConnection();
        dadJoke.addRequestProperty("User-Agent", Commons.CONFIG.get("BOT_USER_AGENT"));  //https://icanhazdadjoke.com/api#custom-user-agent
        dadJoke.addRequestProperty("Accept", "application/json");
        ObjectMapper om = new ObjectMapper();
        IcanhazdadjokeData theJoke = om.readValue(new InputStreamReader(dadJoke.getInputStream()), IcanhazdadjokeData.class);

        MessageCreateData message = new MessageCreateBuilder()
            .setEmbeds(new EmbedBuilder()
                .setDescription(theJoke.getJoke())
                .setThumbnail(iCanHazDadJokeLogo)
                .setColor(Commons.DEFAULT_EMBED_COLOR)
                .setFooter("Requested by %s | ID #%s".formatted(event.getUser().getAsTag(), theJoke.getId()))
                .build()
            )
            .addComponents(
                ActionRow.of(Button.primary("get-dad-joke", "Get new Dad Joke!"))
            )
            .build();

        event.reply(message).queue(msg -> {
            MessageEditData editData = new MessageEditBuilder()
                .setComponents(
                    ActionRow.of(Button.danger("expired-button", "Get new Dad Joke!"))
                )
                .build();
            msg.editOriginal(editData).queueAfter(10, TimeUnit.MINUTES, msg2 ->
                log.debug("Button on message [%s] on server with ID [%s] has expired.".formatted(msg2.getId(), msg2.getGuild().getId()))
            );
        });
    }
}
