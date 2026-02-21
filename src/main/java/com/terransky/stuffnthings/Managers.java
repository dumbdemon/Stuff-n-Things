package com.terransky.stuffnthings;

import com.terransky.stuffnthings.interactions.buttons.AcceptKill;
import com.terransky.stuffnthings.interactions.buttons.DenyKill;
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
import com.terransky.stuffnthings.interactions.modals.SuggestCommand;
import com.terransky.stuffnthings.interfaces.interactions.*;
import com.terransky.stuffnthings.utilities.command.SlashCommandMetadata;
import com.terransky.stuffnthings.utilities.general.InteractionType;
import com.terransky.stuffnthings.utilities.managers.ButtonInteractionManager;
import com.terransky.stuffnthings.utilities.managers.CommandInteractionManager;
import com.terransky.stuffnthings.utilities.managers.InteractionManager;

import java.util.List;

public class Managers {

    Managers() {
    }

    public static class SlashCommands extends CommandInteractionManager<SlashCommandInteraction> {

        public SlashCommands() {
            super(InteractionType.COMMAND_SLASH);
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
            addInteraction(new About());
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

        public List<SlashCommandMetadata> getCommandMetadata() {
            return interactions.stream()
                .map(SlashCommandMetadata::new)
                .sorted()
                .toList();
        }
    }

    public static class DiscordButtons extends ButtonInteractionManager {

        public DiscordButtons() {
            addInteraction(new AcceptKill.Random());
            addInteraction(new AcceptKill.Target());
            addInteraction(new About.AboutCommand());
            addInteraction(new GetMoreDadJokes());
            addInteraction(new DenyKill());
        }
    }

    public static class MessageContextMenu extends CommandInteractionManager<MessageCommandInteraction> {

        public MessageContextMenu() {
            super(InteractionType.COMMAND_MESSAGE);
            addInteraction(new ReportMessage());
        }
    }

    public static class UserContextMenu extends CommandInteractionManager<UserCommandInteraction> {

        public UserContextMenu() {
            super(InteractionType.COMMAND_USER);
            addInteraction(new UserInfoMenu());
        }
    }

    public static class EntitySelectMenu extends InteractionManager<SelectMenuEntityInteraction> {

        public EntitySelectMenu() {
            super(InteractionType.SELECTION_ENTITY);
        }
    }

    public static class StringSelectMenu extends InteractionManager<SelectMenuStringInteraction> {

        public StringSelectMenu() {
            super(InteractionType.SELECTION_STRING);
        }
    }

    public static class ModalInteractions extends InteractionManager<ModalInteraction> {

        public ModalInteractions() {
            super(InteractionType.MODAL);
            addInteraction(new KillSuggest.Random());
            addInteraction(new KillSuggest.Target());
            addInteraction(new RandomMemeBuilder());
            addInteraction(new SuggestCommand());
        }
    }
}
