package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.freshMemes.FreshMemeData;
import com.terransky.stuffnthings.exceptions.DiscordAPIException;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interactions.modals.RandomMemeBuilder;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.configobjects.CoreConfig;
import com.terransky.stuffnthings.utilities.jda.BotEmojis;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Meme extends SlashCommandInteraction {
    private final Logger log = LoggerFactory.getLogger(Meme.class);
    private final CoreConfig CORE_CONFIG = StuffNThings.getConfig().getCore();

    public Meme() {
        super("meme", "Get a random meme.",
            Mastermind.DEVELOPER,
            CommandCategory.FUN,
            parseDate(2022, 8, 24, 11, 10),
            parseDate(2025, 12, 27, 22, 30)
        );
        addSubcommands(
            new SubcommandData("reddit", "Get a random meme from Reddit. DEFAULT: pulls from r/memes, r/dankmemes, or from r/me_irl.")
                .addOption(OptionType.STRING, "subreddit", "You can specify a subreddit outside of the default."),
            new SubcommandData("create", "Create an original meme")
                .addOption(OptionType.BOOLEAN, "random", "Get a random base!")
        );
    }

    @Override
    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
        String subcommand = event.getSubcommandName();
        if (subcommand == null) throw new DiscordAPIException("No subcommand was given.");
        DecimalFormat largeNumber = new DecimalFormat("##,###");

        switch (subcommand) {
            case "reddit" -> goForReddit(event, largeNumber);
            case "create" -> goCreate(event);
            default -> event.replyComponents(
                StandardResponse.getResponseContainer(this, List.of(
                    TextDisplay.ofFormat("Unknown subcommand received!%n%n[***Please report this event!***](%s)", CORE_CONFIG.getReportingUrl()),
                    Separator.createDivider(Separator.Spacing.SMALL),
                    TextDisplay.ofFormat("### Subcommand Received\n`%s`", subcommand)
                ), BotColors.ERROR)
            ).queue();
        }
    }

    private void goCreate(@NotNull SlashCommandInteractionEvent event) {
        boolean isRandom = event.getOption("random", true, OptionMapping::getAsBoolean);

        if (isRandom && CORE_CONFIG.getTestingMode()) {
            event.replyModal(new RandomMemeBuilder().getContructedModal()).queue();
            return;
        }

        event.replyComponents(
            StandardResponse.getResponseContainer("Create Not Ready", "Meme Creation system is not yet been fully implemented.", BotColors.ERROR)
        ).queue();
    }

    private void goForReddit(@NotNull SlashCommandInteractionEvent event, DecimalFormat largeNumber) {
        event.deferReply().queue();
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
                event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer("Whoops! - Code " + memeData.getCode(), List.of(
                        TextDisplay.of(memeData.getMessage()),
                        Separator.createDivider(Separator.Spacing.SMALL),
                        TextDisplay.ofFormat("Verify Subreddit - [**[link]**](https://www.reddit.com/r/%s)", subreddit)
                    ), BotColors.ERROR)
                ).queue();
                return;
            }
            List<ContainerChildComponent> children = new ArrayList<>();

            if (memeData.isExplicit() && !event.getChannel().asTextChannel().isNSFW()) {
                children.add(
                    TextDisplay.of("The meme presented was marked NSFW and this channel is not an NSFW channel.\n" +
                        "Please check with your server's admins if this channel's settings are correct.")
                );
                event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer("Whoops!", children, BotColors.ERROR)
                ).queue();
                return;
            }

            if (memeData.isSpoiler()) {
                children.add(TextDisplay.ofFormat("The fresh meme I got was marked as a spoiler! [Go look if you dare!](%s)", memeData.getPostLink()));
                children.add(
                    MediaGallery.of(
                        MediaGalleryItem.fromUrl("https://media1.giphy.com/media/sYs8CsuIRBYfp2H9Ie/giphy.gif?cid=ecf05e4773n58x026pqkk7lzacutjm13jxvkkfv4z5j0gsc9&rid=giphy.gif&ct=g")
                    )
                );
                event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer("Spoilers!", children, BotColors.SUB_DEFAULT)
                ).queue();
                return;
            }

            children.add(Formatter.getLinkButtonSection(memeData.getPostLink(), String.format("# %s%S", memeData.getTitle(), (memeData.isExplicit() ? " [NSFW]" : ""))));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(MediaGallery.of(MediaGalleryItem.fromUrl(memeData.getUrl())));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(Formatter.getLinkButtonSection(String.format("https://www.reddit.com/user/%s", memeData.getAuthor()), String.format("## Author\n%s", memeData.getAuthor())));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(Formatter.getLinkButtonSection(String.format("https://www.reddit.com/r/%s", memeData.getSubreddit()), String.format("## Subreddit\n%s", memeData.getSubreddit())));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(TextDisplay.ofFormat("## %s %s Upvotes", BotEmojis.getEmoji(BotEmojis.UPVOTE), largeNumber.format(memeData.getUps())));

            event.getHook().sendMessageComponents(Container.of(children).withAccentColor(Color.ORANGE)).queue();
        } catch (InterruptedException | IOException e) {
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer("Whoops!", TextDisplay.ofFormat("Error whilst executing code. Please report it [here](%s)", CORE_CONFIG.getReportingUrl()), BotColors.ERROR)
            ).queue();
            log.error("Unable to get meme", e);
        }
    }
}
