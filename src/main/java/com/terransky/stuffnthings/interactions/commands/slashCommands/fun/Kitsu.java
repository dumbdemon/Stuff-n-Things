package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.dataSources.kitsu.Attributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.EntryAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeDatum;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.AgeRating;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.Subtype;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaDatum;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.categories.CategoriesKitsuData;
import com.terransky.stuffnthings.exceptions.FailedInteractionException;
import com.terransky.stuffnthings.interfaces.interactions.SlashCommandInteraction;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.configobjects.UserPassword;
import com.terransky.stuffnthings.utilities.jda.BotEmojis;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Kitsu {

    @NotNull
    private static OffsetDateTime getGlobalLastUpdated() {
        return SlashCommandInteraction.parseDate(2024, 8, 21, 11, 1);
    }

    @NotNull
    private static OffsetDateTime getAnimeLastUpdated() {
        return SlashCommandInteraction.parseDate(2023, 1, 18, 16, 19);
    }

    @NotNull
    private static OffsetDateTime getMangaLastUpdated() {
        return SlashCommandInteraction.parseDate(2023, 2, 5, 11, 52);
    }

    @NotNull
    @Contract(" -> new")
    private static OptionData getKitsuOption() {
        return new OptionData(OptionType.STRING, "search", "Queary for search", true);
    }

    @NotNull
    private static String getDates(@NotNull EntryAttributes attributes) {
        if (attributes.getEndDate() == null)
            return String.format("from **%s** to **?**", attributes.getStartDate());

        if (attributes.getStartDate().equals(attributes.getEndDate()))
            return String.format("on **%s**", attributes.getStartDate());

        return String.format("from **%s** to **%s**", attributes.getStartDate(), attributes.getEndDate());
    }

    @NotNull
    private static <T extends EntryAttributes> Container getResponseContainer(@NotNull T attributes, @NotNull CategoriesKitsuData categories) {
        AgeRating ageRating = attributes.getAgeRatingEnum();
        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(Section.of(
                Thumbnail.fromUrl(attributes.getPosterImage().getOriginal()),
                TextDisplay.ofFormat("[%s](%s%s)", attributes.getCanonicalTitle(), attributes.getBaseUrl(), attributes.getSlug()),
                TextDisplay.of(attributes.getSynopsis())
            )
        );

        children.add(Separator.createDivider(Separator.Spacing.SMALL));

        children.add(TextDisplay.ofFormat("## :hourglass_flowing_sand: Status\n%s", attributes.getStatusEnum().getState()));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("## :dividers: Type\n%s", attributes.getSubtypeEnum().getCode()));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("## :lock: Rating\n%s [%s]", ageRating.getCode(), ageRating.getCodename()));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("## :arrow_right: Genres\n%s", categories.getCategoriesString()));

        children.add(Separator.createInvisible(Separator.Spacing.SMALL));

        if (attributes instanceof AnimeAttributes animeAttributes) {
            children.add(TextDisplay.ofFormat("## :calendar_spiral: Aired\n%s", getDates(attributes)));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(TextDisplay.ofFormat("## :minidisc: Total Episodes\n%s", String.valueOf(animeAttributes.getEpisodeCount())));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(TextDisplay.ofFormat("## :stopwatch: Episode Duration\n%s minutes", animeAttributes.getEpisodeLength()));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(Formatter.getLinkButtonSection(
                String.format("https://www.youtube.com/watch?v=%s", animeAttributes.getYoutubeVideoId()),
                String.format("## %s Trailer", BotEmojis.getEmoji(BotEmojis.YOUTUBE))
            ));
        } else if (attributes instanceof MangaAttributes mangaAttributes) {
            String chapters = mangaAttributes.getChapterCount() == 0 ? "?" : String.valueOf(mangaAttributes.getChapterCount()),
                volumes = mangaAttributes.getVolumeCount() == 0 ? "?" : String.valueOf(mangaAttributes.getVolumeCount());

            children.add(TextDisplay.ofFormat("## :calendar_spiral: Published\n%s", getDates(attributes)));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(TextDisplay.ofFormat("## :newspaper: Chapters\n%s", chapters));
            children.add(Separator.createDivider(Separator.Spacing.SMALL));
            children.add(TextDisplay.ofFormat("## :books: Volumes\n%s", volumes));

            if (mangaAttributes.getSubtypeEnum() != Subtype.OEL && mangaAttributes.getSerialization() != null)
                children.add(Section.of(
                    Button.link(
                        String.format("https://www.google.com/search?q=%s", URLEncoder.encode(mangaAttributes.getSerialization(), StandardCharsets.UTF_8)),
                        Emoji.fromUnicode("U+1F517")
                    ),
                    TextDisplay.ofFormat(":office: Serialization\n%s",
                        mangaAttributes.getSerialization())
                ));
        } else
            throw new IllegalArgumentException(String.format("Root type '%s.class' cannot be used.", Formatter.getNameOfClass(Attributes.class)));

        children.add(Separator.createInvisible(Separator.Spacing.SMALL));

        String averageRating = attributes.getAverageRating() == null ? "**Not Rated**" : String.format("**%s/100**", attributes.getAverageRating());

        DecimalFormat rank = new DecimalFormat("##,###");
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("## :star: Average Rating\n%s", averageRating));
        children.add(Separator.createDivider(Separator.Spacing.SMALL));
        children.add(TextDisplay.ofFormat("## :trophy: Ranking\n**TOP %s**", rank.format(attributes.getPopularityRank())));
        return Container.of(children).withAccentColor(BotColors.DEFAULT.getColor());
    }

    @NotNull
    private static Container getNSFWMessage(String keyword) {
        return StandardResponse.getResponseContainer(
            String.format("%s Search", keyword),
            TextDisplay.ofFormat("%s recieved was rated for abults and this channel is not NSFW. Verify with your admins that this is correct.", keyword)
        );
    }

    @NotNull
    private static Container getNoResultsMessage(String keyword) {
        return StandardResponse.getResponseContainer(
            String.format("%s Search", keyword),
            TextDisplay.of("Your search returned nothing. Try searching something else?")
        );
    }

    private static boolean hasKitsuToken() {
        UserPassword kistuIo = StuffNThings.getConfig().getTokens().getKitsuIo();
        return !kistuIo.getUsername().isEmpty() || !kistuIo.getPassword().isEmpty();
    }

    public static class Anime extends SlashCommandInteraction {

        public Anime() {
            super("anime", "Search for an anime using Kitsu.app",
                Mastermind.DEVELOPER, CommandCategory.FUN,
                parseDate(2023, 1, 17, 12, 43),
                getAnimeLastUpdated().isAfter(getGlobalLastUpdated()) ? getAnimeLastUpdated() : getGlobalLastUpdated()
            );
            addOptions(getKitsuOption());
        }

        @Override
        public boolean isWorking() {
            return hasKitsuToken();
        }

        @Override
        public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            event.deferReply().queue();
            String query = event.getOption("search", "dragon maid", OptionMapping::getAsString);

            try {
                KitsuHandler handler = new KitsuHandler();
                List<AnimeDatum> animeData = handler.getAnime(query).getData();
                if (animeData.isEmpty()) {
                    event.getHook().sendMessageComponents(getNoResultsMessage("Anime")).queue();
                    return;
                }
                AnimeDatum animeDatum = animeData.get(0);
                CategoriesKitsuData categories = handler.getCategories(animeDatum.getRelationships());
                AnimeAttributes attributes = animeDatum.getAttributes();
                Container message;

                if (attributes.getNsfw() && !event.getChannel().asTextChannel().isNSFW())
                    message = getNSFWMessage("Anime");
                else message = getResponseContainer(attributes, categories);

                event.getHook().sendMessageComponents(message).queue();
            } catch (InterruptedException e) {
                LoggerFactory.getLogger(getClass()).error("error during network operation", e);
                event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer("Anime Search", Responses.NETWORK_OPERATION)
                ).queue();
            }
        }
    }

    public static class Manga extends SlashCommandInteraction {

        public Manga() {
            super("manga", "Search for a manga using Kitsu.app",
                Mastermind.DEVELOPER, CommandCategory.FUN,
                parseDate(2023, 2, 5, 11, 52),
                getMangaLastUpdated().isAfter(getGlobalLastUpdated()) ? getMangaLastUpdated() : getGlobalLastUpdated()
            );
            addOptions(getKitsuOption());
        }

        @Override
        public boolean isWorking() {
            return hasKitsuToken();
        }

        @Override
        public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws FailedInteractionException, IOException {
            event.deferReply().queue();
            String query = event.getOption("search", "dragon maid", OptionMapping::getAsString);

            try {
                KitsuHandler handler = new KitsuHandler();
                List<MangaDatum> mangaData = handler.getManga(query).getData();
                if (mangaData.isEmpty()) {
                    event.getHook().sendMessageComponents(getNoResultsMessage("Manga")).queue();
                    return;
                }
                MangaDatum mangaDatum = mangaData.get(0);
                CategoriesKitsuData categories = handler.getCategories(mangaDatum.getRelationships());
                MangaAttributes attributes = mangaDatum.getAttributes();
                Container message;

                if (attributes.getAgeRatingEnum().isAdult() && !event.getChannel().asTextChannel().isNSFW())
                    message = getNSFWMessage("Manga");
                else message = getResponseContainer(attributes, categories);

                event.getHook().sendMessageComponents(message).queue();
            } catch (InterruptedException e) {
                LoggerFactory.getLogger(getClass()).error("error during network operation", e);
                event.getHook().sendMessageComponents(
                    StandardResponse.getResponseContainer("Manga Search", Responses.NETWORK_OPERATION)
                ).queue();
            }
        }
    }
}
