package com.terransky.stuffnthings.interactions.commands.slashCommands.fun;

import com.terransky.stuffnthings.dataSources.kitsu.Attributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.EntryAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.anime.AnimeDatum;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.AgeRating;
import com.terransky.stuffnthings.dataSources.kitsu.entries.enums.Subtype;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaAttributes;
import com.terransky.stuffnthings.dataSources.kitsu.entries.manga.MangaDatum;
import com.terransky.stuffnthings.dataSources.kitsu.relationships.categories.CategoriesKitsuData;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.apiHandlers.KitsuHandler;
import com.terransky.stuffnthings.utilities.command.*;
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
        return FORMAT.parse("20-1-2023_16:07").getTime();
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

        if (attributes.getEndDate() == null)
            return String.format("from **%s** to **?**", format.format(attributes.getStartDate()));

        if (attributes.getStartDate().compareTo(attributes.getEndDate()) == 0)
            return String.format("on **%s**", format.format(attributes.getStartDate()));

        return String.format("from **%s** to **%s**", format.format(attributes.getStartDate()), format.format(attributes.getEndDate()));
    }

    @NotNull
    private static <T extends EntryAttributes> MessageEmbed getResponseEmbed(@NotNull T attributes, @NotNull CategoriesKitsuData categories,
                                                                             @NotNull EventBlob blob) {
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(EmbedColors.getDefault())
            .setFooter("Requested by " + blob.getMemberAsTag(), blob.getMemberEffectiveAvatarUrl())
            .setTitle(attributes.getCanonicalTitle(), attributes.getBaseUrl() + attributes.getSlug())
            .setDescription(attributes.getSynopsis())
            .setThumbnail(attributes.getPosterImage().getOriginal())
            .addField(":hourglass_flowing_sand: Status", attributes.getStatus().getState(), true)
            .addField(":dividers: Type", attributes.getSubtype().getCode(), true);

        if (attributes instanceof AnimeAttributes animeAttributes) {
            AgeRating ageRating = animeAttributes.getAgeRating();
            builder.addField(":lock: Rating", String.format("%s [%s]", ageRating.getCode(), ageRating.getCodename()), true);
        }

        builder.addField(":arrow_right: Genres", categories.getCategoriesString(), false);

        if (attributes instanceof AnimeAttributes animeAttributes) {
            builder.addField(":calendar_spiral: Aired", getDates(attributes), false)
                .addField(":minidisc: Total Episodes", String.valueOf(animeAttributes.getEpisodeCount()), true)
                .addField(":stopwatch: Duration", String.format("%s minutes", animeAttributes.getEpisodeLength()), true)
                .addField("<:youtube:1066104323987230720> Trailer", String.format("[link](https://www.youtube.com/watch?v=%s)", animeAttributes.getYoutubeVideoId()), true);
        } else if (attributes instanceof MangaAttributes mangaAttributes) {
            String chapters = mangaAttributes.getChapterCount() == 0 ? "?" : String.valueOf(mangaAttributes.getChapterCount()),
                volumes = mangaAttributes.getVolumeCount() == 0 ? "?" : String.valueOf(mangaAttributes.getVolumeCount());

            builder.addField(":calendar_spiral: Published", getDates(attributes), false)
                .addField(":newspaper: Chapters", chapters, true)
                .addField(":books: Volumes", volumes, true);

            if (mangaAttributes.getSubtype() != Subtype.OEL && mangaAttributes.getSerialization() != null)
                builder.addField(":office: Serialization", String.format("[%s](https://www.google.com/search?q=%s)",
                    mangaAttributes.getSerialization(), URLEncoder.encode(mangaAttributes.getSerialization(), StandardCharsets.UTF_8)), true);
        } else
            throw new IllegalArgumentException(String.format("Root type '%s.class' cannot be used.", Attributes.class.getName()));

        String averageRating = attributes.getAverageRating() == null ? "**Not Rated**" : String.format("**%s/100**", attributes.getAverageRating()),
            ranking = attributes.getRatingRank() == 0 ? "**No Ranking**" : String.format("**TOP %s**", attributes.getRatingRank());

        builder.addField(":star: Average Rating", averageRating, true)
            .addField(":trophy: Ranking", ranking, true);
        return builder.build();
    }

    public static class anime implements ICommandSlash {

        @Override
        public String getName() {
            return "anime";
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
            CategoriesKitsuData categories = handler.getCategories(animeDatum.getRelationships());

            event.getHook().sendMessageEmbeds(getResponseEmbed(animeDatum.getAttributes(), categories, blob)).queue();
        }
    }

    public static class manga implements ICommandSlash {

        @Override
        public String getName() {
            return "manga";
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
            CategoriesKitsuData categories = handler.getCategories(mangaDatum.getRelationships());

            event.getHook().sendMessageEmbeds(getResponseEmbed(mangaDatum.getAttributes(), categories, blob)).queue();
        }
    }
}
