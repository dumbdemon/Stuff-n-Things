package com.terransky.StuffnThings.commandSystem.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.ExtraDetails;
import com.terransky.StuffnThings.commandSystem.ExtraDetails.Mastermind;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.dataSources.icanhazdadjoke.IcanhazdadjokeData;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class getDadJokes implements ISlashCommand {
    private final Logger log = LoggerFactory.getLogger(getDadJokes.class);

    @Override
    public String getName() {
        return "dad-jokes";
    }

    @Override
    public ExtraDetails getExtraDetails() {
        return new ExtraDetails(this.getName(), """
            An unoriginal or unfunny joke of a type supposedly told by middle-aged or older men.
            """, Mastermind.USER);
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
