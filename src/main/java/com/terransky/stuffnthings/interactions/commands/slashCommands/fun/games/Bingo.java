package com.terransky.stuffnthings.interactions.commands.slashCommands.fun.games;

import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.games.Bingo.BingoGame;
import com.terransky.stuffnthings.games.Bingo.BingoPlayer;
import com.terransky.stuffnthings.games.Host;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.ISlashGame;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Bingo implements ISlashGame {

    @NotNull
    private static MessageEmbed noGameHasStartedEmbed(@NotNull EmbedBuilder response) {
        return response.setDescription("No game has started. Start one with `/bingo new`.")
            .setColor(EmbedColors.getError())
            .build();
    }

    @Override
    public String getName() {
        return "bingo";
    }

    @Override
    public Metadata getMetadata() {
        return new Metadata(getName(), "Play a game of bingo with up to 100 players!", """
            Play a game of bingo with up to 100 players!

            Game Options:
            `join-game`* - If true, the host will join the game.
            `max-players` - Override the max player count.
            `delay` - How long until the game automatically starts in minutes. DEFAULT: 10
            `ping` - Whether the bot should ping after the game has been created. **NOTE**: You must have the ability to mention everyone.
            `to-ping` - What the bot should ping if the ping is true.
            `verbose` - If set to true, the will go though **all** calls in order and print them out one by one. **WARNING: POTENTIAL SPAM!**

            Options with an `*` are required.
            """, Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate("2023-02-14T09:59Z"),
            Metadata.parseDate("2023-02-19T19:01Z")
        )
            .addSubcommands(
                new SubcommandData("new", "Start a new Bingo game in this channel.")
                    .addOptions(
                        new OptionData(OptionType.BOOLEAN, "join-game", "Join the game you started?", true),
                        new OptionData(OptionType.INTEGER, "max-players", "Change the max player count for this game. DEFAULT: 100")
                            .setMinValue(2),
                        new OptionData(OptionType.INTEGER, "delay", "The amount of minutes to delay the game by. DEFAULT: 10")
                            .setRequiredRange(0, 60),
                        new OptionData(OptionType.BOOLEAN, "ping", "Mention users in this channel that a bingo game has started."),
                        new OptionData(OptionType.ROLE, "to-ping", "If ping is true, mention this role."),
                        new OptionData(OptionType.BOOLEAN, "verbose", "Iterate through all called numbers into chat. WARNING: POTENTIAL SPAM!")
                    ),
                new SubcommandData("join", "Join in a game. You cannot join a game that has already started.")
                    .addOption(OptionType.BOOLEAN, "dm-board", "Whether to send your board here or in you DMs. NOTE: DMs must be open!", true),
                new SubcommandData("start", "No waiting! Start the game now!"),
                new SubcommandData("last", "See your result on that last game on this channel.")
                    .addOptions(
                        new OptionData(OptionType.BOOLEAN, "hide-result", "Hide your results.", true)
                    ),
                new SubcommandData("cancel", "Cancel a game from running.")
            );
    }

    @Override
    public void joinGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        Optional<BingoGame> game = getBingoGame(event, blob, response);
        if (game.isEmpty()) return;

        BingoGame bingoGame = game.get();

        if (bingoGame.isGameCompleted() || OffsetDateTime.now().isAfter(bingoGame.getStartTimeAsODT())) {
            event.replyEmbeds(noGameHasStartedEmbed(response)).queue();
            return;
        }

        if (!bingoGame.addPlayer(blob.getMember())) {
            if (bingoGame.hasMaxPlayers()) {
                response.setDescription("Max players reach! You cannot join this game! Start a new one in a different channel?");
            } else
                response.setDescription("You're already playing! Please wait until the timer runs out or until the host starts the game.");
            event.replyEmbeds(response.setColor(EmbedColors.getError()).build()).queue();
            return;
        }

        DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);
        event.replyEmbeds(
            response.setDescription(
                String.format("Player %s has been added! %s players are now playing!", blob.getMember().getAsMention(), bingoGame.getPlayers().size())
            ).build()
        ).queue();
        Optional<BingoPlayer> ifPlayer = bingoGame.getPlayers().stream().filter(bingoPlayer -> bingoPlayer.getId().equals(blob.getMemberId()))
            .findFirst();

        if (ifPlayer.isEmpty())
            return;

        boolean sendToDms = event.getOption("dm-board", false, OptionMapping::getAsBoolean);
        if (sendToDms) {
            sendBoardToPlayer(ifPlayer.get(), bingoGame.getHost(), blob, true);
            return;
        }

        event.getHook().sendMessageEmbeds(
            new EmbedBuilder()
                .setColor(EmbedColors.getDefault())
                .setTitle("Bingo Game")
                .setDescription(ifPlayer.get().getPrettyBoard())
                .addField("Host", bingoGame.getHost().getHostMention(), false)
                .addField("Guild", blob.getGuild().getName(), false)
                .addField("Channel", blob.getChannelUnion().getAsMention(), false)
                .build()
        ).setEphemeral(true).queue();
    }

    @Override
    public void newGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        event.deferReply().queue();
        Optional<BingoGame> lastGame = DatabaseManager.INSTANCE.getGameData(blob, event.getChannel().getId(), Property.Games.BINGO, PropertyMapping::getAsBingoGame);

        if (lastGame.isPresent() && !lastGame.get().isGameCompleted()) {
            event.getHook().sendMessageEmbeds(
                response.setDescription("Unable to start a game. A game is already running! Join it using `/bingo join`!")
                    .build()
            ).queue();
            return;
        }

        boolean willHostJoin = event.getOption("join-game", false, OptionMapping::getAsBoolean);
        Optional<Long> ifDelay = Optional.ofNullable(event.getOption("delay", OptionMapping::getAsLong));
        Member host = blob.getMember();

        BingoGame bingoGame = ifDelay.map(delay -> new BingoGame(event.getChannel(), host, delay))
            .orElse(new BingoGame(event.getChannel(), host));
        if (willHostJoin) {
            BingoPlayer hostPlayer = new BingoPlayer(host);
            bingoGame.addPlayer(hostPlayer);
            sendBoardToPlayer(hostPlayer, bingoGame.getHost(), blob, false);
        }

        if (Config.isTestingMode())
            blob.getNonBotMembers(member -> !member.equals(host)).forEach(bingoGame::addPlayer);

        int maxPlayers = event.getOption("max-players", bingoGame.getPlayersMax(), OptionMapping::getAsInt);
        boolean verbose = event.getOption("verbose", false, OptionMapping::getAsBoolean);

        bingoGame.setPlayersMax(maxPlayers);
        bingoGame.setVerbose(verbose);

        boolean toPing = event.getOption("ping", false, OptionMapping::getAsBoolean);
        Optional<Role> optionalRole = Optional.ofNullable(event.getOption("to-ping", OptionMapping::getAsRole));

        WebhookMessageCreateAction<?> reply;
        response.setDescription("A game of BINGO has started! Join for funsies!~")
            .addField("Host", host.getAsMention(), false)
            .addField("Minimum Players", String.valueOf(bingoGame.getPlayersMin()), true)
            .addField("Maximum Players", String.valueOf(maxPlayers), true)
            .addField("Start Time", bingoGame.getStartTimeAsTimestampWithRelative(), false)
            .addField("Host is Joining?", willHostJoin ? "Yes." : "No.", true);

        if (toPing && blob.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
            reply = event.getHook().sendMessage(optionalRole.map(IMentionable::getAsMention).orElse("@here"))
                .setEmbeds(response.build());
        } else reply = event.getHook().sendMessageEmbeds(response.build());

        reply.queue();

        new Timer(getName()).schedule(new StartBingoTask(blob, event.getChannel().asTextChannel()), TimeUnit.MINUTES.toMillis(bingoGame.getDelay()));
        DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);
    }

    @Override
    public void startGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        event.reply("Attempting to start game…").setEphemeral(true).queue();

        new Timer(getName()).schedule(new StartBingoTask(blob, event.getChannel().asTextChannel(), true), 0);
    }

    @Override
    public void lastGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        Optional<BingoGame> game = getBingoGame(event, blob, response);
        if (game.isEmpty()) return;

        BingoGame bingoGame = game.get();

        Optional<BingoPlayer> userPlayer = bingoGame.getPlayers().stream().filter(player -> player.getId().equals(blob.getMemberId()))
            .findFirst();

        if (userPlayer.isEmpty()) {
            event.replyEmbeds(
                response.setDescription("You did not participate in the last game on " + event.getChannel().getAsMention() + ".")
                    .addField("Last Game was on...", bingoGame.getStartTimeAsTimestampWithRelative(), false)
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        BingoPlayer player = userPlayer.get();
        player.loadPlayer();

        event.replyEmbeds(
            response.setTitle(getNameReadable())
                .setDescription(player.getPrettyBoard())
                .addField(player.getNumberGotField())
                .addField("Won?", player.checkWinner() ? "Yes." : "No.", true)
                .addField("Win Method", player.getWinMethod(), true)
                .addField("When", bingoGame.getCompletedOnAsTimestampWithRelative(true), false)
                .addField("Host", bingoGame.getHost().getHostId().equals(blob.getMemberId()) ? "You were Host :star:" :
                    bingoGame.getHost().getHostMention(), false)
                .build()
        ).setEphemeral(event.getOption("hide-result", true, OptionMapping::getAsBoolean)).queue();
    }

    @Override
    public void cancelGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        String channelId = event.getChannel().getId();

        Optional<BingoGame> gameData = DatabaseManager.INSTANCE.getGameData(blob, channelId, Property.Games.BINGO, PropertyMapping::getAsBingoGame);

        if (gameData.isEmpty() || gameData.get().isGameCompleted()) {
            event.replyEmbeds(noGameHasStartedEmbed(response)).queue();
            return;
        }

        BingoGame bingoGame = gameData.get();

        if (!bingoGame.isMemberHost(blob.getMember())) {
            event.replyEmbeds(
                response.setDescription("You are not the host. Only the host can cancel the game.")
                    .setColor(EmbedColors.getError())
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        if (bingoGame.isStarted()) {
            event.replyEmbeds(
                response.setDescription("You cannot cancel a game in progress.")
                    .setColor(EmbedColors.getError())
                    .build()
            ).queue();
            return;
        }

        bingoGame.setGameCompleted(true);
        DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);

        event.replyEmbeds(
            response.setDescription("Bingo Game has been canceled!\nStart a new one with `/bingo new`!")
                .build()
        ).queue();
    }

    private void sendBoardToPlayer(BingoPlayer player, Host host, EventBlob blob, boolean pastTense) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(host);
        Objects.requireNonNull(blob);

        player.loadPlayer();

        try {
            PrivateChannel userChannel = blob.getMember().getUser().openPrivateChannel().submit().get();

            userChannel.sendMessageEmbeds(
                new EmbedBuilder()
                    .setColor(EmbedColors.getDefault())
                    .setTitle("Your Board")
                    .setDescription(player.getPrettyBoard())
                    .appendDescription("Check channel for start time.")
                    .addField("Host", host.getHostId().equals(blob.getMemberId()) ? String.format("You %s the Host :star:", pastTense ?
                        "were" : "are") : host.getHostMention(), false)
                    .addField("Guild", blob.getGuild().getName(), false)
                    .addField("Channel", blob.getChannelUnion().getAsMention(), false)
                    .build()
            ).queue();
        } catch (Exception e) {
            LoggerFactory.getLogger(Bingo.class).warn("Couldn't open private channel", e);
        }
    }

    @NotNull
    private Optional<BingoGame> getBingoGame(@NotNull SlashCommandInteractionEvent event, EventBlob blob, EmbedBuilder response) {
        Optional<BingoGame> bingoGame = DatabaseManager.INSTANCE
            .getGameData(blob, event.getChannel().asTextChannel().getId(), Property.Games.BINGO, PropertyMapping::getAsBingoGame);
        if (bingoGame.isPresent()) return bingoGame;

        event.replyEmbeds(noGameHasStartedEmbed(response)).setEphemeral(true).queue();
        return Optional.empty();
    }

    static class StartBingoTask extends TimerTask {

        private final EventBlob blob;
        private final TextChannel textChannel;
        private final Member selfMember;
        private final boolean isForced;

        protected StartBingoTask(@NotNull EventBlob blob, TextChannel textChannel) {
            this(blob, textChannel, false);
        }

        protected StartBingoTask(@NotNull EventBlob blob, TextChannel textChannel, boolean isForced) {
            this.blob = blob;
            this.textChannel = textChannel;
            this.selfMember = blob.getSelfMember();
            this.isForced = isForced;
        }

        @Override
        public void run() {
            Optional<BingoGame> serverGame = DatabaseManager.INSTANCE.getGameData(blob, textChannel.getId(), Property.Games.BINGO, PropertyMapping::getAsBingoGame);
            EmbedBuilder response = new EmbedBuilder()
                .setTitle(new Bingo().getNameReadable())
                .setColor(EmbedColors.getDefault())
                .setFooter(selfMember.getUser().getAsTag(), selfMember.getEffectiveAvatarUrl());

            if (serverGame.isEmpty()) {
                if (isForced) textChannel.sendMessageEmbeds(noGameHasStartedEmbed(response)).queue();
                return;
            }

            BingoGame bingoGame = serverGame.get();
            if (bingoGame.isGameCompleted()) {
                if (isForced) textChannel.sendMessageEmbeds(noGameHasStartedEmbed(response)).queue();
                return;
            }

            if (bingoGame.isPlayerCountUnderMin()) {
                textChannel.sendMessageEmbeds(
                    response.setDescription("Bingo game was cancelled! Not Enough players joined!")
                        .setFooter(selfMember.getUser().getAsTag(), selfMember.getEffectiveAvatarUrl())
                        .build()
                ).queue();
                bingoGame.setGameCompleted(true);
                return;
            }

            bingoGame.setStarted(true);
            List<BingoPlayer> winners = bingoGame.play(bingoGame.getPlayerSeed());
            boolean verboseOverride = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.VERBOSE, true, PropertyMapping::getAsBoolean);

            if (verboseOverride && bingoGame.isVerbose()) {
                doVerbose(response, bingoGame);
            } else if (!verboseOverride && bingoGame.isVerbose()) {
                textChannel.sendMessageEmbeds(
                    response.setDescription("Host requested verbose, but it is disabled by the server. Skipping...")
                        .build()
                ).queue();
            }

            boolean plural = winners.size() > 1;
            StringBuilder winnerString = new StringBuilder();
            for (BingoPlayer winner : winners) {
                winnerString.append(winner.getMention())
                    .append(" [")
                    .append(winner.getWinMethod())
                    .append("]")
                    .append("\n");
            }
            textChannel.sendMessageEmbeds(
                response.setDescription(String.format("There %s %s winner%s after %s called numbers!",
                        plural ? "were" : "was",
                        winners.isEmpty() ? "no" : winners.size(),
                        plural ? "s" : "",
                        bingoGame.getCalledNumbers().size())
                    )
                    .addField("Winner(s)", winnerString.substring(0, winnerString.length()), false)
                    .addField(String.format("Participated (%s users)", bingoGame.getPlayers().size()), bingoGame.getPlayersAsMentions(), false)
                    .build()
            ).queue();
            bingoGame.setGameCompleted(true);
            DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);
        }

        private void doVerbose(@NotNull EmbedBuilder embedBuilder, @NotNull BingoGame bingoGame) {
            EmbedBuilder response = new EmbedBuilder(embedBuilder);
            textChannel.sendMessageEmbeds(
                response
                    .setDescription("Verbose Enabled! Calling out numbers...")
                    .build()
            ).queue();

            LinkedHashMap<String, List<BingoPlayer>> verboseOrder = bingoGame.getVerboseOrder();
            int callNumber = 1;

            for (Map.Entry<String, List<BingoPlayer>> entry : verboseOrder.entrySet()) {
                StringBuilder players = new StringBuilder();
                List<BingoPlayer> value = entry.getValue();
                value.forEach(player -> players.append(player.getMention()).append(", "));
                boolean plural = value.size() != 1;
                textChannel.sendMessageEmbeds(
                    new EmbedBuilder(embedBuilder)
                        .setTitle("Call #" + callNumber)
                        .addField("Number Called", entry.getKey(), false)
                        .addField(String.format("%s player%s %s it", value.size(), plural ? "s" : "", plural ? "have" : "has"),
                            players.isEmpty() ? "None" : players.substring(0, players.length() - 2), false)
                        .build()
                ).queue();
                callNumber++;

                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    textChannel.sendMessageEmbeds(
                        response.setDescription("Error occurred during verbose!\nSkipping to winner...")
                            .setColor(EmbedColors.getError())
                            .build()
                    ).queue();
                    LoggerFactory.getLogger(Bingo.class)
                        .error(String.format("Verbose for channel id %s interrupted", bingoGame.getChannelId()), e);
                    break;
                }
            }
        }
    }
}
