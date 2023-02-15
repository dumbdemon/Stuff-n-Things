package com.terransky.stuffnthings.interactions.commands.slashCommands.fun.games;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terransky.stuffnthings.games.Bingo.BingoGame;
import com.terransky.stuffnthings.games.Bingo.BingoPlayer;
import com.terransky.stuffnthings.interfaces.Pojo;
import com.terransky.stuffnthings.interfaces.interactions.ISlashGame;
import com.terransky.stuffnthings.utilities.command.*;
import com.terransky.stuffnthings.utilities.general.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Bingo implements ISlashGame {

    private static final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(10);
    private static ScheduledFuture<?> future;

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
            `ping` - Whether the bot should ping after the game has been created. **NOTE**: You must have the ability to mention everyone if `to-ping` is not a user.
            `to-ping` - What the bot should ping if the ping is true.
            `verbose` - If set to true, the will go though **all** calls in order and print them out one by one. **WARNING: POTENTIAL SPAM!**
                        
            Options with an `*` are required.
            """, Mastermind.DEVELOPER, CommandCategory.FUN,
            Metadata.parseDate("2023-02-14T09:59Z"),
            Metadata.parseDate("2023-02-14T09:59Z")
        )
            .addSubcommands(
                new SubcommandData("new", "Start a new Bingo game in this channel.")
                    .addOptions(
                        new OptionData(OptionType.BOOLEAN, "join-game", "Join the game you started?", true),
                        new OptionData(OptionType.INTEGER, "max-players", "Change the max player count for this game. DEFAULT: 100")
                            .setMinValue(BingoGame.getMinimumPlayers()),
                        new OptionData(OptionType.INTEGER, "delay", "The amount of minutes to delay the game by. DEFAULT: 10")
                            .setRequiredRange(0, 60),
                        new OptionData(OptionType.BOOLEAN, "ping", "Mention users in this channel that a bingo game has started."),
                        new OptionData(OptionType.ROLE, "to-ping", "If ping is true, mention this role."),
                        new OptionData(OptionType.BOOLEAN, "verbose", "Iterate through all called numbers into chat. WARNING: POTENTIAL SPAM!")
                    ),
                new SubcommandData("join", "Join in a game. You cannot join a game that has already started."),
                new SubcommandData("start", "No waiting! Start the game now!"),
                //todo: implement store of BingoPlayer to PerServer and brag that instead of global last
                new SubcommandData("last", "See your result on that last game on this channel.")
                    .addOptions(
                        new OptionData(OptionType.BOOLEAN, "hide-result", "Hide your results.", true)
                    )
            );
    }

    @Override
    public void newGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        boolean willHostJoin = event.getOption("join-game", false, OptionMapping::getAsBoolean);
        Optional<Long> ifDelay = Optional.ofNullable(event.getOption("delay", OptionMapping::getAsLong));
        Member host = blob.getMember();

        BingoGame bingoGame = ifDelay.map(delay -> new BingoGame(event.getChannel(), host, delay))
            .orElse(new BingoGame(event.getChannel(), host));
        if (willHostJoin) bingoGame.addPlayer(host);

        if (Config.isTestingMode())
            blob.getGuild().getMembers().stream().filter(member -> !member.getUser().isBot())
                .forEach(bingoGame::addPlayer);

        int maxPlayers = event.getOption("max-players", bingoGame.getPlayersMax(), OptionMapping::getAsInt);
        boolean verbose = event.getOption("verbose", false, OptionMapping::getAsBoolean);

        bingoGame.setPlayersMax(maxPlayers);
        bingoGame.setVerbose(verbose);

        boolean toPing = event.getOption("ping", false, OptionMapping::getAsBoolean);
        Optional<Role> optionalRole = Optional.ofNullable(event.getOption("to-ping", OptionMapping::getAsRole));

        ReplyCallbackAction reply;
        response.setDescription("A game of BINGO has started! Join for funsies!~")
            .addField("Host", host.getAsMention(), false)
            .addField("Minimum Players", String.valueOf(bingoGame.getPlayersMin()), true)
            .addField("Maximum Players", String.valueOf(maxPlayers), true)
            .addField("Start Time", bingoGame.getStartTimeAsTimestampWithRelative(), false)
            .addField("Host is Joining?", willHostJoin ? "Yes." : "No.", true);

        if (toPing && blob.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
            reply = event.reply(optionalRole.map(IMentionable::getAsMention).orElse("@here"))
                .setEmbeds(response.build());
        } else reply = event.replyEmbeds(response.build());

        reply.queue();

        future = executor.schedule(new StartBingoTask(bingoGame, event.getChannel().asTextChannel(), blob), bingoGame.getDelay(), TimeUnit.MINUTES);
        try {
            bingoGame.saveAsJsonFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void joinGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        Optional<BingoGame> game = getBingoGame(event, response);
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

        try {
            bingoGame.saveAsJsonFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        event.replyEmbeds(
            response.setDescription(
                String.format("Player %s has been added! %s players are now playing!", blob.getMember().getAsMention(), bingoGame.getPlayers().size())
            ).build()
        ).queue();
    }

    @Override
    public void startGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        Optional<BingoGame> game = getBingoGame(event, response);
        if (game.isEmpty()) return;
        event.deferReply(true).queue();

        BingoGame bingoGame = game.get();

        if (bingoGame.isGameCompleted() || OffsetDateTime.now().isAfter(bingoGame.getStartTimeAsODT())) {
            event.getHook().sendMessageEmbeds(
                noGameHasStartedEmbed(response)
            ).queue();
            return;
        }

        if (bingoGame.isPlayerCountUnderMin()) {
            event.getHook().sendMessageEmbeds(
                response.setDescription(String.format("Unable to start game! Not Enough players!\nThere is %s player!", bingoGame.getPlayers().size()))
                    .setColor(EmbedColors.getError())
                    .build()
            ).queue();
            return;
        }

        event.getHook().sendMessage("Started Game…").queue();

        future.cancel(false);
        new StartBingoTask(bingoGame, event.getChannel().asTextChannel(), blob).run();
    }

    @Override
    public void lastGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob, EmbedBuilder response) {
        Optional<BingoGame> game = getBingoGame(event, response);
        if (game.isEmpty()) return;

        BingoGame bingoGame = game.get();

        if (!bingoGame.isGameCompleted()) {
            event.replyEmbeds(
                response.setDescription("Cannot view stats of last game when a game is currently running.")
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        Optional<BingoPlayer> userPlayer = bingoGame.getPlayers()
            .stream().filter(player -> player.getId().equals(blob.getMemberId()))
            .findFirst();

        if (userPlayer.isEmpty()) {
            event.replyEmbeds(
                response.setDescription("You did not participate in the last game on " + bingoGame.getChannelMention() + ".")
                    .addField("Last Game was on...", bingoGame.getStartTimeAsTimestampWithRelative(), false)
                    .build()
            ).setEphemeral(true).queue();
            return;
        }

        BingoPlayer player = userPlayer.get();

        event.replyEmbeds(
            response.setTitle(getNameReadable())
                .setDescription(player.getPrettyBoard())
                .addField(player.getNumberGotField())
                .addField("Won?", player.checkWinner() ? "Yes." : "No.", true)
                .addField("Win Method", player.getWinMethod(), true)
                .addField("When", bingoGame.getStartTimeAsTimestampWithRelative(true), false)
                .build()
        ).setEphemeral(event.getOption("hide-result", true, OptionMapping::getAsBoolean)).queue();
    }

    @NotNull
    private MessageEmbed noGameHasStartedEmbed(@NotNull EmbedBuilder response) {
        return response.setDescription("No game has started. Start one with `/bingo new`.")
            .setColor(EmbedColors.getError())
            .build();
    }

    @NotNull
    private Optional<BingoGame> getBingoGame(@NotNull SlashCommandInteractionEvent event, EmbedBuilder response) {
        try {
            return Optional.ofNullable(new ObjectMapper().readValue(new File("jsons/" + Pojo.toSafeFileName("BingoGame On " + event.getChannel().getId()) + ".json"),
                BingoGame.class));
        } catch (IOException e) {
            LoggerFactory.getLogger(getName()).error("Could not read file", e);
            event.replyEmbeds(noGameHasStartedEmbed(response)).setEphemeral(true).queue();
            return Optional.empty();
        }
    }

    @Override
    public boolean isWorking() {
        return Config.isTestingMode();
    }

    static class StartBingoTask implements Runnable {

        private final BingoGame bingoGame;
        private final TextChannel textChannel;
        private final Member selfMember;

        //todo: replace constructor parameter for BingoGame with database call in run()
        public StartBingoTask(@NotNull BingoGame bingoGame, TextChannel textChannel, @NotNull EventBlob blob) {
            this.bingoGame = bingoGame;
            this.textChannel = textChannel;
            this.selfMember = blob.getSelfMember();
        }

        @Override
        public void run() {
            if (bingoGame.isGameCompleted())
                return;

            bingoGame.setStarted(true);

            if (bingoGame.isPlayerCountUnderMin()) {
                textChannel.sendMessageEmbeds(
                    new EmbedBuilder()
                        .setTitle(new Bingo().getNameReadable())
                        .setColor(EmbedColors.getError())
                        .setDescription("Bingo game was cancelled! Not Enough players joined!")
                        .setFooter(selfMember.getUser().getAsTag(), selfMember.getEffectiveAvatarUrl())
                        .build()
                ).queue();
                bingoGame.setGameCompleted(true);
                try {
                    bingoGame.saveAsJsonFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            List<BingoPlayer> winners = bingoGame.play(bingoGame.getPlayerSeed());
            bingoGame.setGameCompleted(true);

            if (bingoGame.isVerbose()) { //todo: set up verbose
                textChannel.sendMessageEmbeds(
                    new EmbedBuilder()
                        .setTitle(new Bingo().getNameReadable())
                        .setDescription("Verbose has not been set up; skipping…")
                        .setColor(EmbedColors.getError())
                        .setFooter(selfMember.getUser().getAsTag(), selfMember.getEffectiveAvatarUrl())
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
                new EmbedBuilder()
                    .setTitle(new Bingo().getNameReadable())
                    .setColor(EmbedColors.getDefault())
                    .setDescription(String.format("There %s %s winner%s after %s called numbers!",
                        plural ? "were" : "was",
                        winners.isEmpty() ? "no" : winners.size(),
                        plural ? "s" : "",
                        bingoGame.getCalledNumbers().size())
                    )
                    .appendDescription("\n\nWinner(s):\n" + winnerString.substring(0, winnerString.length()))
                    .addField(String.format("Participated (%s users)", bingoGame.getPlayers().size()), bingoGame.getPlayersAsMentions(), false)
                    .setFooter(selfMember.getUser().getAsTag(), selfMember.getEffectiveAvatarUrl())
                    .build()
            ).queue();
            bingoGame.setGameCompleted(true);
            try {
                bingoGame.saveAsJsonFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
