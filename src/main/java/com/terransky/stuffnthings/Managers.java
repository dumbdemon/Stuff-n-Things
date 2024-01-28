package com.terransky.stuffnthings;

import com.terransky.stuffnthings.interactions.buttons.AcceptKill;
import com.terransky.stuffnthings.interactions.buttons.DenyKill;
import com.terransky.stuffnthings.interactions.buttons.ExpiredButton;
import com.terransky.stuffnthings.interactions.buttons.GetMoreDadJokes;
import com.terransky.stuffnthings.interactions.commands.messageContextMenus.ReportMessage;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.ChannelUnLock;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.CheckPerms;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.ConfigCmd;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.RolesController;
import com.terransky.stuffnthings.interactions.commands.slashCommands.devs.*;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.*;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.games.Bingo;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.About;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.Ping;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.Suggest;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.FibonacciSequence;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.NumbersAPI;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.SolveQuadratic;
import com.terransky.stuffnthings.interactions.commands.slashCommands.mtg.CalculateRats;
import com.terransky.stuffnthings.interactions.commands.slashCommands.mtg.WhatsInStandard;
import com.terransky.stuffnthings.interactions.commands.userContextMenus.UserInfoMenu;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interactions.modals.RandomMemeBuilder;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.managers.CommandIManager;
import com.terransky.stuffnthings.managers.IManager;
import com.terransky.stuffnthings.managers.SlashIManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Managers {

    private final SlashIManager slashIManager = new SlashIManager(
        //Admin Commands
        new ChannelUnLock(),
        new CheckPerms(),
        new ConfigCmd(),
        new RolesController(),

        //Math Commands
        new FibonacciSequence(),
        new NumbersAPI(),
        new SolveQuadratic(),

        //M:tG Commands
        new CalculateRats(),
        new WhatsInStandard(),

        //Games
        new Bingo(),

        //Fun Commands
        new ColorInfo(),
        new CypherCmd(),
        new Dictionary(),
        new EightBall(),
        new GetDadJokes(),
        new GetRandomCat(),
        new GetRandomDog(),
        new GetWeather(),
        new JokesV2(),
        new Kill(),
        new Kitsu.Anime(),
        new Kitsu.Manga(),
        new Lmgtfy(),
        new Meme(),
        new RobFailChance(),
        new Say(),

        //General Commands
        new About(),
        new Ping(),
        new Suggest(),

        //Dev commands
        new AddToWatchlist(),
        new BotBan(),
        new GetInvite(),
        new Test(),
        new TinyURL(),
        new UserInfo()
    );

    private final IManager<IButton> buttonIManager = new IManager<>(
        new AcceptKill.Random(),
        new AcceptKill.Target(),
        new DenyKill(),
        new ExpiredButton(),
        new GetMoreDadJokes()
    );

    private final IManager<IModal> modalIManager = new IManager<>(
        new KillSuggest.Random(),
        new KillSuggest.Target(),
        new RandomMemeBuilder()
    );

    private final CommandIManager<ICommandMessage> messageCommandIManager = new CommandIManager<>(
        new ReportMessage()
    );

    private final CommandIManager<ICommandUser> userCommandIManager = new CommandIManager<>(
        new UserInfoMenu()
    );

    private final IManager<ISelectMenuEntity> entitySelectMenuIManager = new IManager<>();

    private final IManager<ISelectMenuString> stringSelectMenuIManager = new IManager<>();

    @NotNull
    @Contract(" -> new")
    public static Managers getInstance() {
        return new Managers();
    }

    public SlashIManager getSlashManager() {
        return slashIManager;
    }

    public IManager<IButton> getButtonManager() {
        return buttonIManager;
    }

    public CommandIManager<ICommandMessage> getMessageContextManager() {
        return messageCommandIManager;
    }

    public CommandIManager<ICommandUser> getUserContextManager() {
        return userCommandIManager;
    }

    public IManager<IModal> getModalManager() {
        return modalIManager;
    }

    public IManager<ISelectMenuEntity> getEntitySelectMenuManager() {
        return entitySelectMenuIManager;
    }

    public IManager<ISelectMenuString> getStringSelectMenuManager() {
        return stringSelectMenuIManager;
    }
}
