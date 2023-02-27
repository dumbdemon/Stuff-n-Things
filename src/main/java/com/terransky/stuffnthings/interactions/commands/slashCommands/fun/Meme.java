package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.dataSources.freshMemes.FreshMemeData;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Meme implements ICommandSlash {
    private final Logger log = LoggerFactory.getLogger(Meme.class);

    @Override
    public String getName() {
        return "meme";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(this.getName(), "Get a random meme.", """
            Get your fresh hot (or cold) memes here!
            Reddit pulls from [r/memes](https://www.reddit.com/r/memes), [r/dankmemes](https://www.reddit.com/r/dankmemes), or from [r/me_irl](https://www.reddit.com/r/me_irl).
            """, Mastermind.DEVELOPER,
            CommandCategory.FUN,
            Metadata.parseDate("2022-08-24T11:10Z"),
            Metadata.parseDate("2023-02-27T16:36Z")
        )
            .addSubcommands(
                new SubcommandData("reddit", "Get a random meme from Reddit. DEFAULT: pulls from r/memes, r/dankmemes, or from r/me_irl.")
                    .addOption(OptionType.STRING, "subreddit", "You can specify a subreddit outside of the default.")
            );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        event.deferReply().queue();
        DecimalFormat largeNumber = new DecimalFormat("##,###");

        if (subcommand.equals("reddit")) goForReddit(event, largeNumber, blob.getStandardEmbed());
    }

    private void goForReddit(@NotNull SlashCommandInteractionEvent event, DecimalFormat largeNumber, @NotNull EmbedBuilder eb) {
        String subreddit = event.getOption("subreddit", "", OptionMapping::getAsString);
        String redditLogo = "https://cdn.discordapp.com/attachments/1004795281734377564/1005203741026299954/Reddit_Mark_OnDark.png";
        try (ExecutorService service = Executors.newSingleThreadExecutor()) {
            HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(5))
                .executor(service)
                .build();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://meme-api.com/gimme/" + subreddit))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            FreshMemeData memeData = new ObjectMapper().readValue(response.body(), FreshMemeData.class);

            if (memeData.getCode() != null) {
                event.getHook().sendMessageEmbeds(
                    eb.setTitle("Whoops! - Code " + memeData.getCode())
                        .setDescription(memeData.getMessage())
                        .setFooter("Reddit", redditLogo)
                        .setColor(EmbedColors.getError())
                        .addField("Verify Subreddit", String.format("[**[link]**](https://www.reddit.com/r/%s)", subreddit), false)
                        .build()
                ).queue();
                return;
            }

            eb.setFooter("Reddit | u/%s | r/%s".formatted(memeData.getAuthor(), memeData.getSubreddit()), redditLogo);

            if (memeData.isExplicit() && !event.getChannel().asTextChannel().isNSFW()) {
                event.getHook().sendMessageEmbeds(
                    eb.setTitle("Whoops!")
                        .setDescription("The meme presented was marked NSFW and this channel is not an NSFW channel.\nPlease check with your server's admins if this channel's settings are correct.")
                        .setColor(EmbedColors.getError())
                        .build()
                ).queue();
                return;
            }

            if (memeData.isSpoiler()) {
                event.getHook().sendMessageEmbeds(
                    eb.setTitle("Spoilers!")
                        .setDescription("The fresh meme I got was marked as a spoiler! [Go look if you dare!](" + memeData.getPostLink() + ")")
                        .setImage("https://media1.giphy.com/media/sYs8CsuIRBYfp2H9Ie/giphy.gif?cid=ecf05e4773n58x026pqkk7lzacutjm13jxvkkfv4z5j0gsc9&rid=giphy.gif&ct=g")
                        .setColor(EmbedColors.getSecondary())
                        .build()
                ).queue();
                return;
            }

            eb.setAuthor(memeData.getTitle() + (memeData.isExplicit() ? " [NSFW]" : ""), memeData.getPostLink())
                .setImage(memeData.getUrl())
                .addField("Author", "[" + memeData.getAuthor() + "](https://www.reddit.com/user/" + memeData.getAuthor() + ")", true)
                .addField("Subreddit", "[" + memeData.getSubreddit() + "](https://www.reddit.com/r/" + memeData.getSubreddit() + ")", true)
                .addField("<:reddit_upvote:1069025452250890330> Upvotes", largeNumber.format(memeData.getUps()), true);

            event.getHook().sendMessageEmbeds(eb.build()).queue();
        } catch (InterruptedException | IOException e) {
            event.getHook().sendMessageEmbeds(
                eb.setTitle("Whoops!")
                    .setDescription(String.format("Error whilst executing code. Please report it [here](%s)", Config.getErrorReportingURL()))
                    .setFooter("Reddit", redditLogo)
                    .setColor(EmbedColors.getError())
                    .setTimestamp(OffsetDateTime.now())
                    .build()
            ).queue();
            log.error("Unable to get meme", e);
        }
    }
}
