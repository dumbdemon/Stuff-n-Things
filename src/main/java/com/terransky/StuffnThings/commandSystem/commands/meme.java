package com.terransky.StuffnThings.commandSystem.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.StuffnThings.Commons;
import com.terransky.StuffnThings.commandSystem.ISlash;
import com.terransky.StuffnThings.jacksonMapper.freshMemeData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
import java.util.Objects;

public class meme implements ISlash {
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
    public void slashExecute(@NotNull SlashCommandInteractionEvent event) {
        Logger log = LoggerFactory.getLogger(meme.class);
        DecimalFormat largeNumber = new DecimalFormat("##,###");
        ObjectMapper om = new ObjectMapper();
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(new Commons().defaultEmbedColor);

        event.deferReply().queue();

        if ("reddit".equals(Objects.requireNonNull(event.getSubcommandName()))) {
            String subreddit = event.getOption("subreddit", "", OptionMapping::getAsString);
            try {
                URL memeURL = new URL("https://meme-api.herokuapp.com/gimme/" + subreddit);
                freshMemeData memeData = om.readValue(memeURL, freshMemeData.class);
                eb.setThumbnail("https://cdn.discordapp.com/attachments/1004795281734377564/1005203741026299954/Reddit_Mark_OnDark.png");

                if (!memeData.isSpoiler()) {
                    if (memeData.isNsfw()) {
                        if (event.getChannel().asTextChannel().isNSFW()) {
                            eb.setAuthor(memeData.getTitle() + " [NSFW]", memeData.getPostLink())
                                    .setImage(memeData.getUrl())
                                    .addField(new MessageEmbed.Field("Author", "[" + memeData.getAuthor() + "](https://www.reddit.com/user/" + memeData.getAuthor() + ")", true))
                                    .addField(new MessageEmbed.Field("Subreddit", "[" + memeData.getSubreddit() + "](https://www.reddit.com/r/" + memeData.getSubreddit() + ")", true))
                                    .addField(new MessageEmbed.Field("Upvote", largeNumber.format(memeData.getUps()), true));
                        } else {
                            eb.setAuthor("Whoops!")
                                    .setDescription("The meme presented was marked NSFW and this channel is not an NSFW channel.\nPlease check with your server's admins if this channel's settings are correct.");
                        }
                    } else {
                        eb.setAuthor(memeData.getTitle(), memeData.getPostLink())
                                .setImage(memeData.getUrl())
                                .addField(new MessageEmbed.Field("Author", "[" + memeData.getAuthor() + "](https://www.reddit.com/user/" + memeData.getAuthor() + ")", true))
                                .addField(new MessageEmbed.Field("Subreddit", "[" + memeData.getSubreddit() + "](https://www.reddit.com/r/" + memeData.getSubreddit() + ")", true))
                                .addField(new MessageEmbed.Field("Upvote", largeNumber.format(memeData.getUps()), true));
                    }
                } else eb.setAuthor("Spoilers!")
                        .setDescription("The fresh meme I got was marked as spoiler! [Go look if you dare!](" + memeData.getPostLink() + ")")
                        .setImage("https://media1.giphy.com/media/sYs8CsuIRBYfp2H9Ie/giphy.gif?cid=ecf05e4773n58x026pqkk7lzacutjm13jxvkkfv4z5j0gsc9&rid=giphy.gif&ct=g");
            } catch (IOException e) {
                eb.setAuthor("Whoops!", null, "https://toppng.com/uploads/preview/reddit-logo-reddit-icon-115628658968pe8utyxjt.png");
                eb.setDescription("Either the subreddit you provided does not exist, or you have been rate limited!\n\n[**Click here to check if the subreddit exists.**](https://www.reddit.com/r/" + subreddit + ")");
                log.error(String.valueOf(e));
            }
        } else {
            eb.setTitle("Wait a second...")
                    .setDescription("How did you get here?");
            log.warn("Someone got here!");
        }

        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
