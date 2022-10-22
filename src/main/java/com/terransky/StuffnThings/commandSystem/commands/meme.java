package com.terransky.StuffnThings.commandSystem.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.interfaces.ISlashCommand;
import com.terransky.StuffnThings.exceptions.DiscordAPIException;
import com.terransky.StuffnThings.jacksonMapper.freshMemeData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;

public class meme implements ISlashCommand {
    @Override
    public String getName() {
        return "meme";
    }

    @Override
    public CommandData commandData() {
        return Commands.slash(this.getName(), "Get a random meme.")
            .addSubcommands(
                new SubcommandData("reddit", "Get a random meme from Reddit. DEFAULT: pulls from r/memes, r/dankmemes, and r/me_irl.")
                    .addOption(OptionType.STRING, "subreddit", "You can specify a subreddit outside of the default.")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event) throws Exception {
        Logger log = LoggerFactory.getLogger(meme.class);
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        ObjectMapper om = new ObjectMapper();
        EmbedBuilder eb = new EmbedBuilder()
            .setColor(Commons.defaultEmbedColor);
        String subCommand = event.getSubcommandName();
        if (subCommand == null) throw new DiscordAPIException("No subcommand was given.");

        event.deferReply().queue();

        if (subCommand.equals("reddit")) {
            String subreddit = event.getOption("subreddit", "", OptionMapping::getAsString);
            String redditLogo = "https://cdn.discordapp.com/attachments/1004795281734377564/1005203741026299954/Reddit_Mark_OnDark.png";
            try {
                URL memeURL = new URL("https://meme-api.herokuapp.com/gimme/" + subreddit);
                freshMemeData memeData = om.readValue(memeURL, freshMemeData.class);
                eb.setFooter("Reddit | u/%s | r/%s".formatted(memeData.getAuthor(), memeData.getSubreddit()), redditLogo);

                if (memeData.isSpoiler()) {
                    event.getHook().sendMessageEmbeds(
                        eb.setAuthor("Spoilers!")
                            .setDescription("The fresh meme I got was marked as a spoiler! [Go look if you dare!](" + memeData.getPostLink() + ")")
                            .setImage("https://media1.giphy.com/media/sYs8CsuIRBYfp2H9Ie/giphy.gif?cid=ecf05e4773n58x026pqkk7lzacutjm13jxvkkfv4z5j0gsc9&rid=giphy.gif&ct=g")
                            .build()
                    ).queue();
                    return;
                }

                if (memeData.isExplicit() && !event.getChannel().asTextChannel().isNSFW()) {
                    event.getHook().sendMessageEmbeds(
                        eb.setAuthor("Whoops!")
                            .setDescription("The meme presented was marked NSFW and this channel is not an NSFW channel.\nPlease check with your server's admins if this channel's settings are correct.")
                            .build()
                    ).queue();
                    return;
                }

                eb.setAuthor(memeData.getTitle() + (memeData.isExplicit() ? " [NSFW]" : ""), memeData.getPostLink())
                    .setImage(memeData.getUrl())
                    .addField("Author", "[" + memeData.getAuthor() + "](https://www.reddit.com/user/" + memeData.getAuthor() + ")", true)
                    .addField("Subreddit", "[" + memeData.getSubreddit() + "](https://www.reddit.com/r/" + memeData.getSubreddit() + ")", true)
                    .addField("Upvotes", largeNumber.format(memeData.getUps()), true);

                event.getHook().sendMessageEmbeds(eb.build()).queue();
            } catch (IOException e) {
                event.getHook().sendMessageEmbeds(
                    eb.setTitle("Whoops!")
                        .setDescription(("""
                            I wasn't able to grab a meme!

                            This could be either:
                            A) The subreddit does not exist or no longer exists,
                            B) The subreddit is set to private and therefore I am unable to access it ([**click here to check**](https://www.reddit.com/r/%s)), or
                            C) You have been rate limited and you must wait for a few moments and try again.""").formatted(subreddit))
                        .setFooter("Reddit", redditLogo)
                        .build()
                ).queue();
                log.error(String.valueOf(e));
            }
        }
    }
}
