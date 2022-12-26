package com.terransky.stuffnthings;

import com.terransky.stuffnthings.interactions.buttons.expiredButton;
import com.terransky.stuffnthings.interactions.buttons.getMoreDadJokes;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.channelUnLock;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.checkPerms;
import com.terransky.stuffnthings.interactions.commands.slashCommands.admin.configCmd;
import com.terransky.stuffnthings.interactions.commands.slashCommands.devs.getInvite;
import com.terransky.stuffnthings.interactions.commands.slashCommands.devs.test;
import com.terransky.stuffnthings.interactions.commands.slashCommands.devs.userInfo;
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
import com.terransky.stuffnthings.interfaces.discordInteractions.*;
import com.terransky.stuffnthings.managers.CommandManager;
import com.terransky.stuffnthings.managers.Manager;
import com.terransky.stuffnthings.managers.SlashManager;

public class ManagersManager {

    private final SlashManager slashManager = new SlashManager(
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
        new lmgtfy(),
        new meme(),
        new robFailChance(),
        new say(),
        new twentyQuestions(),

        //General Commands
        new about(),
        new ping(),
        new suggest(),

        //Dev commands
        new getInvite(),
        new test(),
        new userInfo()
    );

    private final Manager<IButton> buttonManager = new Manager<>(
        new expiredButton(),
        new getMoreDadJokes()
    );

    private final Manager<IModal> modalManager = new Manager<>(
        new killSuggest()
    );

    private final CommandManager<ICommandMessage> messageContextManager = new CommandManager<>();

    private final CommandManager<ICommandUser> userContextManager = new CommandManager<>();

    private final Manager<ISelectMenuEntity> entitySelectMenuManager = new Manager<>();

    private final Manager<ISelectMenuString> stringSelectMenuManager = new Manager<>();

    public SlashManager getSlashManager() {
        return slashManager;
    }

    public Manager<IButton> getButtonManager() {
        return buttonManager;
    }

    public CommandManager<ICommandMessage> getMessageContextManager() {
        return messageContextManager;
    }

    public CommandManager<ICommandUser> getUserContextManager() {
        return userContextManager;
    }

    public Manager<IModal> getModalManager() {
        return modalManager;
    }

    public Manager<ISelectMenuEntity> getEntitySelectMenuManager() {
        return entitySelectMenuManager;
    }

    public Manager<ISelectMenuString> getStringSelectMenuManager() {
        return stringSelectMenuManager;
    }
}
