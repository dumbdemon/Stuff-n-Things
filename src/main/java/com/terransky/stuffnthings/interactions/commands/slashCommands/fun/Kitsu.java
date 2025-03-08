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
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.cannedAgenda.Responses;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.configobjects.UserPassword;
import com.terransky.stuffnthings.utilities.jda.BotEmojis;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class Kitsu {

    @NotNull
    private static OffsetDateTime getGlobalLastUpdated() {
        return Metadata.parseDate(2024, 8, 21, 11, 1);
    }

    private static Metadata getStandard(String name) {
        return new Metadata()
            .setCommandName(name)
            .setMastermind(Mastermind.DEVELOPER)
            .setCategory(CommandCategory.FUN)
            .setCreatedDate(Metadata.parseDate(2023, 1, 17, 12, 43))
            .addOptions(
                new OptionData(OptionType.STRING, "search", "Queary for search", true)
            );
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
    private static <T extends EntryAttributes> MessageEmbed getResponseEmbed(@NotNull T attributes, @NotNull CategoriesKitsuData categories,
                                                                             @NotNull EventBlob blob) {
        AgeRating ageRating = attributes.getAgeRatingEnum();
        EmbedBuilder builder = blob.getStandardEmbed(attributes.getCanonicalTitle(), attributes.getBaseUrl() + attributes.getSlug())
            .setFooter("Requested by " + blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
            .setDescription(attributes.getSynopsis())
            .setThumbnail(attributes.getPosterImage().getOriginal())
            .addField(":hourglass_flowing_sand: Status", attributes.getStatusEnum().getState(), true)
            .addField(":dividers: Type", attributes.getSubtypeEnum().getCode(), true)
            .addField(":lock: Rating", String.format("%s [%s]", ageRating.getCode(), ageRating.getCodename()), true)
            .addField(":arrow_right: Genres", categories.getCategoriesString(), false);

        if (attributes instanceof AnimeAttributes animeAttributes) {
            builder.addField(":calendar_spiral: Aired", getDates(attributes), false)
                .addField(":minidisc: Total Episodes", String.valueOf(animeAttributes.getEpisodeCount()), true)
                .addField(":stopwatch: Duration", String.format("%s minutes", animeAttributes.getEpisodeLength()), true)
                .addField(BotEmojis.getEmoji(BotEmojis.YOUTUBE) + " Trailer", String.format("[link](https://www.youtube.com/watch?v=%s)", animeAttributes.getYoutubeVideoId()), true);
        } else if (attributes instanceof MangaAttributes mangaAttributes) {
            String chapters = mangaAttributes.getChapterCount() == 0 ? "?" : String.valueOf(mangaAttributes.getChapterCount()),
                volumes = mangaAttributes.getVolumeCount() == 0 ? "?" : String.valueOf(mangaAttributes.getVolumeCount());

            builder.addField(":calendar_spiral: Published", getDates(attributes), false)
                .addField(":newspaper: Chapters", chapters, true)
                .addField(":books: Volumes", volumes, true);

            if (mangaAttributes.getSubtypeEnum() != Subtype.OEL && mangaAttributes.getSerialization() != null)
                builder.addField(":office: Serialization", String.format("[%s](https://www.google.com/search?q=%s)",
                    mangaAttributes.getSerialization(), URLEncoder.encode(mangaAttributes.getSerialization(), StandardCharsets.UTF_8)), true);
        } else
            throw new IllegalArgumentException(String.format("Root type '%s.class' cannot be used.", Formatter.getNameOfClass(Attributes.class)));

        String averageRating = attributes.getAverageRating() == null ? "**Not Rated**" : String.format("**%s/100**", attributes.getAverageRating());

        DecimalFormat rank = new DecimalFormat("##,###");
        builder.addField(":star: Average Rating", averageRating, true)
            .addField(":trophy: Ranking", String.format("**TOP %s**", rank.format(attributes.getPopularityRank())), true);
        return builder.build();
    }

    @NotNull
    private static MessageEmbed getNSFWMessage(@NotNull EventBlob blob, String keyword) {
        return blob.getStandardEmbed(String.format("%s Search", keyword))
            .setFooter("Requested by " + blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
            .setDescription(String.format("%s recieved was rated for abults and this channel is not NSFW. Verify with your admins that this is correct.", keyword))
            .build();
    }

    @NotNull
    private static MessageEmbed getNoResultsMessage(@NotNull EventBlob blob, String keyword) {
        return blob.getStandardEmbed(String.format("%s Search", keyword))
            .setFooter(blob.getMemberName(), blob.getMemberEffectiveAvatarUrl())
            .setDescription("Your search returned nothing. Try searching something else?")
            .build();
    }

    private static boolean hasKitsuToken() {
        UserPassword kistuIo = StuffNThings.getConfig().getTokens().getKitsuIo();
        return !kistuIo.getUsername().isEmpty() || !kistuIo.getPassword().isEmpty();
    }

    public static class Anime implements ICommandSlash {

        @Override
        public String getName() {
            return "anime";
        }

        @Override
        public Metadata getMetadata() {
            OffsetDateTime animeLastUpdated = Metadata.parseDate(2023, 1, 18, 16, 19),
                lastUpdated = animeLastUpdated.isAfter(getGlobalLastUpdated()) ? animeLastUpdated : getGlobalLastUpdated();
            return getStandard(getName())
                .setLastUpdated(lastUpdated)
                .setDescripstions("Search for an anime using Kitsu.app");
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
                    event.getHook().sendMessageEmbeds(getNoResultsMessage(blob, "Anime")).queue();
                    return;
                }
                AnimeDatum animeDatum = animeData.get(0);
                CategoriesKitsuData categories = handler.getCategories(animeDatum.getRelationships());
                AnimeAttributes attributes = animeDatum.getAttributes();
                MessageEmbed message;

                if (attributes.getNsfw() && !event.getChannel().asTextChannel().isNSFW())
                    message = getNSFWMessage(blob, "Anime");
                else message = getResponseEmbed(attributes, categories, blob);

                event.getHook().sendMessageEmbeds(message).queue();
            } catch (InterruptedException e) {
                LoggerFactory.getLogger(getClass()).error("error during network operation", e);
                event.getHook().sendMessageEmbeds(
                    blob.getStandardEmbed("Anime Search", EmbedColor.ERROR)
                        .setDescription(Responses.NETWORK_OPERATION.getMessage())
                        .setColor(EmbedColor.ERROR.getColor())
                        .build()
                ).queue();
            }
        }
    }

    public static class Manga implements ICommandSlash {

        @Override
        public String getName() {
            return "manga";
        }

        @Override
        public Metadata getMetadata() {
            OffsetDateTime mangaLastUpdated = Metadata.parseDate(2023, 2, 5, 11, 52),
                lastUpdated = mangaLastUpdated.isAfter(getGlobalLastUpdated()) ? mangaLastUpdated : getGlobalLastUpdated();
            return getStandard(getName())
                .setLastUpdated(lastUpdated)
                .setDescripstions("Search for a manga using Kitsu.app");
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
                    event.getHook().sendMessageEmbeds(getNoResultsMessage(blob, "Manga")).queue();
                    return;
                }
                MangaDatum mangaDatum = mangaData.get(0);
                CategoriesKitsuData categories = handler.getCategories(mangaDatum.getRelationships());
                MangaAttributes attributes = mangaDatum.getAttributes();
                MessageEmbed message;

                if (attributes.getAgeRatingEnum().isAdult() && !event.getChannel().asTextChannel().isNSFW())
                    message = getNSFWMessage(blob, "Manga");
                else message = getResponseEmbed(attributes, categories, blob);

                event.getHook().sendMessageEmbeds(message).queue();
            } catch (InterruptedException e) {
                LoggerFactory.getLogger(getClass()).error("error during network operation", e);
                event.getHook().sendMessageEmbeds(
                    blob.getStandardEmbed("Manga Search", EmbedColor.ERROR)
                        .setDescription(Responses.NETWORK_OPERATION.getMessage())
                        .setColor(EmbedColor.ERROR.getColor())
                        .build()
                ).queue();
            }
        }
    }
}
