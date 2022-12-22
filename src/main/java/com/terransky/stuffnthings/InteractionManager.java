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
import com.terransky.stuffnthings.managers.*;

public class InteractionManager {

    private final CommandManager slashManager = new CommandManager(
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

        //General Commands
        new about(),
        new ping(),
        new suggest(),

        //Dev commands
        new getInvite(),
        new test(),
        new userInfo()
    );

    private final ButtonManager buttonManager = new ButtonManager(
        new expiredButton(),
        new getMoreDadJokes()
    );

    private final ModalManager modalManager = new ModalManager(
        new killSuggest()
    );

    private final MessageContextManager messageContextManager = new MessageContextManager();

    private final UserContextManager userContextManager = new UserContextManager();

    private final EntitySelectMenuManager entitySelectMenuManager = new EntitySelectMenuManager();

    private final StringSelectMenuManager stringSelectMenuManager = new StringSelectMenuManager();

    public CommandManager getSlashManager() {
        return slashManager;
    }

    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    public MessageContextManager getMessageContextManager() {
        return messageContextManager;
    }

    public UserContextManager getUserContextManager() {
        return userContextManager;
    }

    public ModalManager getModalManager() {
        return modalManager;
    }

    public EntitySelectMenuManager getEntitySelectMenuManager() {
        return entitySelectMenuManager;
    }

    public StringSelectMenuManager getStringSelectMenuManager() {
        return stringSelectMenuManager;
    }
}
