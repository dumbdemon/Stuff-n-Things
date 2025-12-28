package com.terransky.stuffnthings.interactions.commands.slashCommands.fun.games;

import com.terransky.stuffnthings.StuffNThings;
import com.terransky.stuffnthings.database.helpers.Property;
import com.terransky.stuffnthings.database.helpers.PropertyMapping;
import com.terransky.stuffnthings.games.Bingo.BingoGame;
import com.terransky.stuffnthings.games.Bingo.BingoPlayer;
import com.terransky.stuffnthings.games.Host;
import com.terransky.stuffnthings.interfaces.DatabaseManager;
import com.terransky.stuffnthings.interfaces.interactions.GameSlashCommandInteraction;
import com.terransky.stuffnthings.utilities.command.*;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.container.ContainerChildComponent;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
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

public class Bingo extends GameSlashCommandInteraction {

    @NotNull
    private static Container noGameHasStartedContainer() {
        return StandardResponse.getResponseContainer(new Bingo(), "No game has started. Start one with `/bingo new`.", BotColors.ERROR);
    }

    public Bingo() {
        super("bingo", "Play a game of bingo with up to 100 players!",
            Mastermind.DEVELOPER, CommandCategory.FUN,
            parseDate(2023, 2, 14, 9, 59),
            parseDate(2024, 8, 20, 12, 3));

        addSubcommands(
                new SubcommandData(GameAction.NEW.getName(), "Start a new Bingo game in this channel.")
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
                new SubcommandData(GameAction.JOIN.getName(), "Join in a game. You cannot join a game that has already started.")
                    .addOption(OptionType.BOOLEAN, "dm-board", "Whether to send your board here or in you DMs. NOTE: DMs must be open!", true),
                new SubcommandData(GameAction.START.getName(), "No waiting! Start the game now!"),
                new SubcommandData(GameAction.LAST.getName(), "See your result on that last game on this channel.")
                    .addOptions(
                        new OptionData(OptionType.BOOLEAN, "hide-result", "Hide your results.", true)
                    ),
                new SubcommandData(GameAction.CANCEL.getName(), "Cancel a game from running.")
            );
    }

    @Override
    public void joinGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        Optional<BingoGame> game = getBingoGame(event, blob);
        if (game.isEmpty()) return;

        BingoGame bingoGame = game.get();

        if (bingoGame.isGameCompleted() || OffsetDateTime.now().isAfter(bingoGame.getStartTimeAsODT())) {
            event.replyComponents(noGameHasStartedContainer()).queue();
            return;
        }

        if (!bingoGame.addPlayer(blob.getMember())) {
            TextDisplay response;
            if (bingoGame.hasMaxPlayers()) {
                response = TextDisplay.of("Max players reach! You cannot join this game! Start a new one in a different channel?");
            } else
                response = TextDisplay.of("You're already playing! Please wait until the timer runs out or until the host starts the game.");
            event.replyComponents(StandardResponse.getResponseContainer(this, response, BotColors.ERROR)).queue();
            return;
        }

        DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);
        event.replyComponents(
            StandardResponse.getResponseContainer(
                this,
                TextDisplay.ofFormat("Player %s has been added! %s players are now playing!", blob.getMember().getAsMention(), bingoGame.getPlayers().size())
            )
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

        event.getHook().sendMessageComponents(
            StandardResponse.getResponseContainer("Bingo Game", List.of(
                TextDisplay.ofFormat(ifPlayer.get().getPrettyBoard()),
                Separator.createDivider(Separator.Spacing.SMALL),
                TextDisplay.ofFormat("## Host\n%s", bingoGame.getHost().getHostMention()),
                TextDisplay.ofFormat("## Guild\n%s", blob.getGuild().getName()),
                TextDisplay.ofFormat("## Channel\n%s", blob.getChannelUnion().getAsMention())
            ))
        ).setEphemeral(true).queue();
    }

    @Override
    public void newGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        event.deferReply().queue();
        Optional<BingoGame> lastGame = DatabaseManager.INSTANCE.getGameData(blob, event.getChannel().getId(), Property.Games.BINGO, PropertyMapping::getAsBingoGame);

        if (lastGame.isPresent() && !lastGame.get().isGameCompleted()) {
            event.getHook().sendMessageComponents(
                StandardResponse.getResponseContainer(this, "Unable to start a game. A game is already running! Join it using `/bingo join`!", BotColors.ERROR)
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

        if (StuffNThings.getConfig().getCore().getTestingMode())
            blob.getNonBotMembers(member -> !member.equals(host)).forEach(bingoGame::addPlayer);

        int maxPlayers = event.getOption("max-players", bingoGame.getPlayersMax(), OptionMapping::getAsInt);
        boolean verbose = event.getOption("verbose", false, OptionMapping::getAsBoolean);

        bingoGame.setPlayersMax(maxPlayers);
        bingoGame.setVerbose(verbose);

        boolean toPing = event.getOption("ping", false, OptionMapping::getAsBoolean);
        Optional<Role> optionalRole = Optional.ofNullable(event.getOption("to-ping", OptionMapping::getAsRole));

        WebhookMessageCreateAction<?> reply;
        List<ContainerChildComponent> children = new ArrayList<>();
        children.add(TextDisplay.of("A game of BINGO has started! Join for funsies!~"));
        children.add(TextDisplay.ofFormat("## Host\n%s", host.getAsMention()));
        children.add(TextDisplay.ofFormat("## Minimum Players\n%s", String.valueOf(bingoGame.getPlayersMin())));
        children.add(TextDisplay.ofFormat("## Maximum Players\n%s", String.valueOf(maxPlayers)));
        children.add(TextDisplay.ofFormat("## Start Time\n%s", bingoGame.getStartTimeAsTimestampWithRelative()));
        children.add(TextDisplay.ofFormat("## Host is Joining?\n%s", willHostJoin ? "Yes." : "No."));

        if (toPing && blob.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
            reply = event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(this, children))
                .setContent(optionalRole.map(IMentionable::getAsMention).orElse("@here"));
        } else reply = event.getHook().sendMessageComponents(StandardResponse.getResponseContainer(this, children));

        reply.queue();

        new Timer(getName()).schedule(new StartBingoTask(blob, event.getChannel()), TimeUnit.MINUTES.toMillis(bingoGame.getDelay()));
        DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);
    }

    @Override
    public void startGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        event.reply("Attempting to start game…").useComponentsV2(false).setEphemeral(true).queue();

        new Timer(getName()).schedule(new StartBingoTask(blob, event.getChannel(), true), 0);
    }

    @Override
    public void lastGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        Optional<BingoGame> game = getBingoGame(event, blob);
        if (game.isEmpty()) return;

        BingoGame bingoGame = game.get();

        Optional<BingoPlayer> userPlayer = bingoGame.getPlayers().stream().filter(player -> player.getId().equals(blob.getMemberId()))
            .findFirst();

        if (userPlayer.isEmpty()) {
            event.replyComponents(
                StandardResponse.getResponseContainer(this, List.of(
                    TextDisplay.ofFormat("You did not participate in the last game on %s.", event.getChannel().getAsMention()),
                    TextDisplay.ofFormat("Last Game was on...%s", bingoGame.getStartTimeAsTimestampWithRelative())
                ))
            ).setEphemeral(true).queue();
            return;
        }

        BingoPlayer player = userPlayer.get();
        player.loadPlayer();

        event.replyComponents(
            StandardResponse.getResponseContainer(this, List.of(
                TextDisplay.of(player.getPrettyBoard()),
                player.getNumberGotTextDisplay(),
                TextDisplay.ofFormat("## Won\n%s", player.checkWinner() ? "Yes." : "No."),
                TextDisplay.ofFormat("## Win Method\n%s", player.getWinMethod()),
                TextDisplay.ofFormat("## When\n%s", bingoGame.getCompletedOnAsTimestampWithRelative(true)),
                TextDisplay.ofFormat("## Host\n%s", bingoGame.getHost().getHostId().equals(blob.getMemberId()) ? "You were Host :star:" :
                    bingoGame.getHost().getHostMention())
            ))
        ).setEphemeral(event.getOption("hide-result", true, OptionMapping::getAsBoolean)).queue();
    }

    @Override
    public void cancelGame(@NotNull SlashCommandInteractionEvent event, @NotNull EventBlob blob) {
        String channelId = event.getChannel().getId();

        Optional<BingoGame> gameData = DatabaseManager.INSTANCE.getGameData(blob, channelId, Property.Games.BINGO, PropertyMapping::getAsBingoGame);

        if (gameData.isEmpty() || gameData.get().isGameCompleted()) {
            event.replyComponents(noGameHasStartedContainer()).queue();
            return;
        }

        BingoGame bingoGame = gameData.get();

        if (!bingoGame.isMemberHost(blob.getMember())) {
            event.replyComponents(
                StandardResponse.getResponseContainer(this, "You are not the host. Only the host can cancel the game.", BotColors.ERROR)
            ).setEphemeral(true).queue();
            return;
        }

        if (bingoGame.isStarted()) {
            event.replyComponents(
                StandardResponse.getResponseContainer(this, "You cannot cancel a game in progress.", BotColors.ERROR)
            ).queue();
            return;
        }

        bingoGame.setGameCompleted(true);
        DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);

        event.replyComponents(
            StandardResponse.getResponseContainer(this, "Bingo Game has been canceled!\nStart a new one with `/bingo new`!")
        ).queue();
    }

    private void sendBoardToPlayer(BingoPlayer player, Host host, EventBlob blob, boolean pastTense) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(host);
        Objects.requireNonNull(blob);

        player.loadPlayer();

        try {
            PrivateChannel userChannel = blob.getMember().getUser().openPrivateChannel().submit().get();

            userChannel.sendMessageComponents(
                StandardResponse.getResponseContainer("Your Board", List.of(
                    TextDisplay.of(player.getPrettyBoard()),
                    TextDisplay.of("Check channel for start time."),
                    Separator.createDivider(Separator.Spacing.SMALL),
                    TextDisplay.ofFormat("## Host\n%s", host.getHostId().equals(blob.getMemberId()) ? String.format("You %s the Host :star:", pastTense ?
                        "were" : "are") : host.getHostMention()),
                    TextDisplay.ofFormat("## Guild\n%s", blob.getGuild().getName()),
                    TextDisplay.ofFormat("## Channel\n%s", blob.getChannelUnion().getAsMention())
                ))
            ).queue();
        } catch (Exception e) {
            LoggerFactory.getLogger(Bingo.class).warn("Couldn't open private channel", e);
        }
    }

    @NotNull
    private Optional<BingoGame> getBingoGame(@NotNull SlashCommandInteractionEvent event, EventBlob blob) {
        Optional<BingoGame> bingoGame = DatabaseManager.INSTANCE
            .getGameData(blob, event.getChannel().getId(), Property.Games.BINGO, PropertyMapping::getAsBingoGame);
        if (bingoGame.isPresent()) return bingoGame;

        event.replyComponents(noGameHasStartedContainer()).setEphemeral(true).queue();
        return Optional.empty();
    }

    static class StartBingoTask extends TimerTask {

        private final EventBlob blob;
        private final MessageChannelUnion channelUnion;
        private final boolean isForced;

        protected StartBingoTask(@NotNull EventBlob blob, MessageChannelUnion channelUnion) {
            this(blob, channelUnion, false);
        }

        protected StartBingoTask(@NotNull EventBlob blob, MessageChannelUnion channelUnion, boolean isForced) {
            this.blob = blob;
            this.channelUnion = channelUnion;
            this.isForced = isForced;
        }

        @Override
        public void run() {
            Optional<BingoGame> serverGame = DatabaseManager.INSTANCE.getGameData(blob, channelUnion.getId(), Property.Games.BINGO, PropertyMapping::getAsBingoGame);

            if (serverGame.isEmpty()) {
                if (isForced) channelUnion.sendMessageComponents(noGameHasStartedContainer()).queue();
                return;
            }

            BingoGame bingoGame = serverGame.get();
            if (bingoGame.isGameCompleted()) {
                if (isForced) channelUnion.sendMessageComponents(noGameHasStartedContainer()).queue();
                return;
            }

            if (bingoGame.isPlayerCountUnderMin()) {
                channelUnion.sendMessageComponents(
                    StandardResponse.getResponseContainer(new Bingo(), "Bingo game was cancelled! Not Enough players joined!")
                ).queue();
                bingoGame.setGameCompleted(true);
                return;
            }

            bingoGame.setStarted(true);
            List<BingoPlayer> winners = bingoGame.play(bingoGame.getPlayerSeed());
            boolean verboseOverride = DatabaseManager.INSTANCE.getFromDatabase(blob, Property.VERBOSE, true, PropertyMapping::getAsBoolean);

            if (verboseOverride && bingoGame.isVerbose()) {
                doVerbose(bingoGame);
            } else if (!verboseOverride && bingoGame.isVerbose()) {
                channelUnion.sendMessageComponents(
                    StandardResponse.getResponseContainer(new Bingo(), "Host requested verbose, but it is disabled by the server. Skipping...")
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
            channelUnion.sendMessageComponents(
                StandardResponse.getResponseContainer(new Bingo(), List.of(
                    TextDisplay.ofFormat("There %s %s winner%s after %s called numbers!",
                        plural ? "were" : "was",
                        winners.isEmpty() ? "no" : winners.size(),
                        plural ? "s" : "",
                        bingoGame.getCalledNumbers().size()),
                    TextDisplay.ofFormat("## Winner(s)\n%s", winnerString.substring(0, winnerString.length())),
                    TextDisplay.ofFormat("## Participated (%s users)\n%s", bingoGame.getPlayers().size(), bingoGame.getPlayersAsMentions())
                ))
            ).queue();
            bingoGame.setGameCompleted(true);
            DatabaseManager.INSTANCE.uploadGameData(blob, Property.Games.BINGO, bingoGame);
        }

        private void doVerbose(@NotNull BingoGame bingoGame) {
            channelUnion.sendMessageComponents(
                StandardResponse.getResponseContainer(new Bingo(), "Verbose Enabled! Calling out numbers...")
            ).queue();

            LinkedHashMap<String, List<BingoPlayer>> verboseOrder = bingoGame.getVerboseOrder();
            int callNumber = 1;

            for (Map.Entry<String, List<BingoPlayer>> entry : verboseOrder.entrySet()) {
                StringBuilder players = new StringBuilder();
                List<BingoPlayer> value = entry.getValue();
                value.forEach(player -> players.append(player.getMention()).append(", "));
                boolean plural = value.size() != 1;
                channelUnion.sendMessageComponents(
                    Container.of(List.of(
                        TextDisplay.of("# Call #" + callNumber),
                        TextDisplay.ofFormat("## Number Called - %s", entry.getKey()),
                        TextDisplay.ofFormat("%s player%s %s it\n%s", value.size(), plural ? "s" : "", plural ? "have" : "has",
                            players.isEmpty() ? "" : players.substring(0, players.length() - 2))
                    )).withAccentColor(BotColors.SUB_DEFAULT.getColor())
                ).queue();
                callNumber++;

                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    channelUnion.sendMessageComponents(
                        StandardResponse.getResponseContainer(new Bingo(), "Error occurred during verbose!\nSkipping to winner...", BotColors.ERROR)
                    ).queue();
                    LoggerFactory.getLogger(Bingo.class)
                        .error(String.format("Verbose for channel id %s interrupted", bingoGame.getChannelId()), e);
                    break;
                }
            }
        }
    }
}
