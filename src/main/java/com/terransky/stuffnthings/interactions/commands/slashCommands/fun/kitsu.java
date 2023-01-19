package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.dataSources.kitsu.Attributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.EntryAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeDatum;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.AgeRating;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaDatum;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.genre.GenreKitsuData;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;

@SuppressWarnings("SpellCheckingInspection")
public class kitsu {

    private static final FastDateFormat FORMAT = Metadata.getFastDateFormat();

    private static long getGlobalLastUpdated() throws ParseException {
        return FORMAT.parse("19-1-2023_12:51").getTime();
    }

    private static Metadata getStandard(String name) throws ParseException {
        return new Metadata()
            .setCommandName(name)
            .setMastermind(Mastermind.DEVELOPER)
            .setCategory(CommandCategory.FUN)
            .setCreatedDate(FORMAT.parse("17-1-2023_12:43"))
            .addOptions(
                new OptionData(OptionType.STRING, "search", "Queary for search", true)
            );
    }

    private static String getDates(@NotNull EntryAttributes attributes) {
        FastDateFormat format = FastDateFormat.getInstance("yyy-MM-dd");
        return String.format("from **%s** to **%s**", format.format(attributes.getStartDate()),
            attributes.getEndDate() == null ? "current" : format.format(attributes.getEndDate()));
    }

    @NotNull
    private static <T extends EntryAttributes> MessageEmbed getResponseEmbed(@NotNull T attributes, @NotNull GenreKitsuData genres,
                                                                             @NotNull EventBlob blob) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter("Requested by " + blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setTitle(attributes.getCanonicalTitle(), attributes.getBaseUrl() + attributes.getSlug())
            .setDescription(attributes.getSynopsis())
            .setThumbnail(attributes.getPosterImage().getOriginal())
            .addField("Status", attributes.getStatus().getState(), true)
            .addField("Type", attributes.getSubtype().getCode(), true);

        if (attributes instanceof AnimeAttributes animeAttributes) {
            AgeRating ageRating = animeAttributes.getAgeRating();
            builder.addField("Rating", String.format("%s [%s]", ageRating.getCode(), ageRating.getCodename()), true);
        }

        builder.addField("Genres", genres.getGenreString(), false);

        if (attributes instanceof AnimeAttributes animeAttributes) {
            builder.addField("Aired", getDates(attributes), false)
                .addField("Total Episodes", String.valueOf(animeAttributes.getEpisodeCount()), true)
                .addField("Duration", String.format("%s minutes", animeAttributes.getEpisodeLength()), true)
                .addField("Trailer", String.format("[link](https://www.youtube.com/watch?v=%s)", animeAttributes.getYoutubeVideoId()), true);
        } else if (attributes instanceof MangaAttributes mangaAttributes) {
            String chapters = mangaAttributes.getChapterCount() == 0 ? "?" : String.valueOf(mangaAttributes.getChapterCount()),
                volumes = mangaAttributes.getVolumeCount() == 0 ? "?" : String.valueOf(mangaAttributes.getVolumeCount());

            builder.addField("Published", getDates(attributes), false)
                .addField("Chapters", chapters, true)
                .addField("Valumes", volumes, true)
                .addField("Serialization", String.format("[%s](https://www.google.com/search?q=%s)",
                    mangaAttributes.getSerialization(), URLEncoder.encode(mangaAttributes.getSerialization(), StandardCharsets.UTF_8)), true);
        } else
            throw new IllegalArgumentException(String.format("Root type '%s.class' cannot be used.", Attributes.class.getName()));

        builder.addField("Average Rating", String.format("**%s/100**", attributes.getAverageRating()), true)
            .addField("Ranking", String.format("**TOP %s**", attributes.getRatingRank()), true);
        return builder.build();
    }

    public static class anime implements ICommandSlash {

        @Override
        public String getName() {
            return "anime";
        }

        @Override
        public boolean isWorking() {
            return Config.isTestingMode();
        }

        @Override
        public Metadata getMetadata() throws ParseException {
            long animeLastUpdated = FORMAT.parse("18-1-2023_16:19").getTime();
            return getStandard(getName())
                .setLastUpdated(new Date(Math.max(getGlobalLastUpdated(), animeLastUpdated)))
                .setDescripstions("Search for an anime using Kitsu.io");
        }

        @Override
        public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
            event.deferReply().queue();
            String query = event.getOption("search", "dragon maid", OptionMapping::getAsString);

            KitsuHandler handler = new KitsuHandler();
            AnimeDatum animeDatum = handler.getAnime(query).getData().get(0);
            GenreKitsuData genres = handler.getGenres(animeDatum.getRelationships());

            event.getHook().sendMessageEmbeds(getResponseEmbed(animeDatum.getAttributes(), genres, blob)).queue();
        }
    }

    public static class manga implements ICommandSlash {

        @Override
        public String getName() {
            return "manga";
        }

        @Override
        public boolean isWorking() {
            return Config.isTestingMode();
        }

        @Override
        public Metadata getMetadata() throws ParseException {
            long mangaLastUpdated = FORMAT.parse("18-1-2023_16:19").getTime();
            return getStandard(getName())
                .setLastUpdated(new Date(Math.max(getGlobalLastUpdated(), mangaLastUpdated)))
                .setDescripstions("Search for a manga using Kitsu.io");
        }

        @Override
        public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) throws Exception {
            event.deferReply().queue();
            String query = event.getOption("search", "dragon maid", OptionMapping::getAsString);

            KitsuHandler handler = new KitsuHandler();
            MangaDatum mangaDatum = handler.getManga(query).getData().get(0);
            GenreKitsuData genres = handler.getGenres(mangaDatum.getRelationships());

            event.getHook().sendMessageEmbeds(getResponseEmbed(mangaDatum.getAttributes(), genres, blob)).queue();
        }
    }
}
