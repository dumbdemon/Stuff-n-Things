package com.terransky.stuffnthings;

import com.terransky.stuffnthings.interactions.buttons.expiredButton;
import com.terransky.stuffnthings.interactions.buttons.getMoreDadJokes;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.channelUnLock;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.checkPerms;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.configCmd;
import com.terransky.stuffnthings.interactions.commands.slashCommands.devs.*;
import com.terransky.stuffnthings.interactions.commands.slashCommands.fun.*;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.about;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.ping;
import com.terransky.stuffnthings.interactions.commands.slashCommands.general.suggest;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.fibonacciSequence;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.numbersAPI;
import com.terransky.stuffnthings.interactions.commands.slashCommands.maths.solveQuadratic;
import com.terransky.stuffnthings.interactions.commands.slashCommands.mtg.calculateRats;
import com.terransky.stuffnthings.interactions.commands.slashCommands.mtg.whatsInStandard;
import com.terransky.stuffnthings.interactions.modals.killSuggest;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.managers.CommandIManager;
import com.terransky.stuffnthings.managers.IManager;
import com.terransky.stuffnthings.managers.SlashIManager;

public class ManagersManager {

    private final SlashIManager slashIManager = new SlashIManager(
        //Admin Commands
        new channelUnLock(),
        new checkPerms(),
        new configCmd(),

        //Math Commands
        new fibonacciSequence(),
        new numbersAPI(),
        new solveQuadratic(),

        //M:tG Commands
        new calculateRats(),
        new whatsInStandard(),

        //Fun Commands
        new colorInfo(),
        new dictionary(),
        new getDadJokes(),
        new kill(),
        new kitsu.anime(),
        new kitsu.manga(),
        new lmgtfy(),
        new meme(),
        new robFailChance(),
        new say(),

        //General Commands
        new about(),
        new ping(),
        new suggest(),

        //Dev commands
        new getInvite(),
        new upsertKitsuToken(),
        new test(),
        new tinyURL(),
        new userInfo()
    );

    private final IManager<IButton> buttonIManager = new IManager<>(
        new expiredButton(),
        new getMoreDadJokes()
    );

    private final IManager<IModal> modalIManager = new IManager<>(
        new killSuggest()
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
