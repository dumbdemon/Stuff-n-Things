package com.terransky.stuffnthings;

import com.terransky.stuffnthings.interactions.buttons.AcceptKill;
import com.terransky.stuffnthings.interactions.buttons.DenyKill;
import com.terransky.stuffnthings.interactions.buttons.ExpiredButton;
import com.terransky.stuffnthings.interactions.buttons.GetMoreDadJokes;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.ChannelUnLock;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.CheckPerms;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.ConfigCmd;
import com.terransky.stuffnthings.interactions.commands.slashCommands.devs.*;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.*;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.About;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.Ping;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.Suggest;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.FibonacciSequence;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.NumbersAPI;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.SolveQuadratic;
import com.terransky.stuffnthings.interactions.commands.slashCommands.mtg.CalculateRats;
import com.terransky.stuffnthings.interactions.commands.slashCommands.mtg.WhatsInStandard;
import com.terransky.stuffnthings.interactions.modals.KillSuggest;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.managers.CommandIManager;
import com.terransky.stuffnthings.managers.IManager;
import com.terransky.stuffnthings.managers.SlashIManager;

public class ManagersManager {

    private final SlashIManager slashIManager = new SlashIManager(
        //Admin Commands
        new ChannelUnLock(),
        new CheckPerms(),
        new ConfigCmd(),

        //Math Commands
        new FibonacciSequence(),
        new NumbersAPI(),
        new SolveQuadratic(),

        //M:tG Commands
        new CalculateRats(),
        new WhatsInStandard(),

        //Fun Commands
        new ColorInfo(),
        new Dictionary(),
        new GetDadJokes(),
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
        new GetInvite(),
        new UpsertKitsuToken(),
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
        new KillSuggest.Target()
    );

    private final CommandIManager<ICommandMessage> messageCommandIManager = new CommandIManager<>();

    private final CommandIManager<ICommandUser> userCommandIManager = new CommandIManager<>();

    private final IManager<ISelectMenuEntity> entitySelectMenuIManager = new IManager<>();

    private final IManager<ISelectMenuString> stringSelectMenuIManager = new IManager<>();

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
