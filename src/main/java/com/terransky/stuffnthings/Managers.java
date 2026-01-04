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
import com.terransky.stuffnthings.utilities.managers.CommandInteractionManager;
import com.terransky.stuffnthings.utilities.managers.InteractionManager;

public class Managers {

    Managers() {
    }

    public static class SlashCommands extends CommandInteractionManager<SlashCommandInteraction> {

        public SlashCommands() {
            //Admin
            addInteraction(new ChannelUnLock());
            addInteraction(new CheckPerms());
            addInteraction(new ConfigCmd());
            addInteraction(new RolesController());

            //Developer
            addInteraction(new AddToWatchlist());
            addInteraction(new BotBan());
            addInteraction(new GetInvite());
            addInteraction(new test());
            addInteraction(new TinyURL());
            addInteraction(new UserInfo());

            //Games
            addInteraction(new Bingo());

            //Fun
            addInteraction(new ColorInfo());
            addInteraction(new CypherCmd());
            addInteraction(new Dictionary());
            addInteraction(new EightBall());
            addInteraction(new GetDadJokes());
            addInteraction(new GetRandomCat());
            addInteraction(new GetRandomDog());
            addInteraction(new GetWeather());
            addInteraction(new JokesV2());
            addInteraction(new Kill());
            addInteraction(new Kitsu());
            addInteraction(new Lmgtfy());
            addInteraction(new Meme());
            addInteraction(new RandomCatFacts());
            addInteraction(new RobFailChance());
            addInteraction(new Say());

            //General
            addInteraction(new Ping());
            addInteraction(new Suggest());

            //Maths
            addInteraction(new NumbersAPI());
            addInteraction(new SolveQuadratic());
            addInteraction(new FibonacciSequence());

            //Magic: the Gathering
            addInteraction(new CalculateRats());
            addInteraction(new WhatsInStandard());
        }
    }

    public static class DiscordButtons extends InteractionManager<ButtonInteraction> {

        public DiscordButtons() {
            addInteraction(new ExpiredButton());
            addInteraction(new AcceptKill.Random());
            addInteraction(new AcceptKill.Target());
            addInteraction(new GetMoreDadJokes());
            addInteraction(new DenyKill());
        }
    }

    public static class MessageContextMenu extends CommandInteractionManager<MessageCommandInteraction> {

        public MessageContextMenu() {
            addInteraction(new ReportMessage());
        }
    }

    public static class UserContextMenu extends CommandInteractionManager<UserCommandInteraction> {

        public UserContextMenu() {
            addInteraction(new UserInfoMenu());
        }
    }

    public static class EntitySelectMenu extends InteractionManager<SelectMenuEntityInteraction> {

        public EntitySelectMenu() {
        }
    }

    public static class StringSelectMenu extends InteractionManager<SelectMenuStringInteraction> {

        public StringSelectMenu() {
        }
    }

    public static class ModalInteractions extends InteractionManager<ModalInteraction> {

        public ModalInteractions() {
            addInteraction(new KillSuggest.Random());
            addInteraction(new KillSuggest.Target());
            addInteraction(new RandomMemeBuilder());
        }
    }
}
