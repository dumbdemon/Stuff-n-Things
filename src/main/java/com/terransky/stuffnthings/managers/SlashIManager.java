package com.terransky.stuffnthings.managers;

import com.terransky.stuffnthings.interactions.commands.slashCommands.general.About;
import com.terransky.stuffnthings.interfaces.interactions.ICommandSlash;
import com.terransky.stuffnthings.utilities.command.Metadata;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SlashIManager extends CommandIManager<ICommandSlash> {

    /**
     * Initializes an {@link IManager} for {@link ICommandSlash ICommandSlashes}
     *
     * @param interactions An array of {@link ICommandSlash ICommandSlashes}
     */
    public SlashIManager(@NotNull ICommandSlash... interactions) {
        for (ICommandSlash iCommandSlash : interactions) {
            noTypeCheckAddInteraction(iCommandSlash);
        }
    }

    /**
     * Get all command names as {@link Command.Choice Choises} for the {@link About} command.
     *
     * @param slashSet The set number from 1 to 4.
     * @return A {@link List} of {@link Command.Choice Choises}.
     */
    public List<Command.Choice> getCommandsAsChoices(@NotNull SlashSet slashSet) {
        List<ICommandSlash> slashes = interactions.stream().filter(super::checkIfGlobal).sorted().toList();
        int setMax = 25 * slashSet.getSetNumber();
        int setMin = setMax - 25;
        if (setMin > slashes.size()) {
            About about = new About();
            return List.of(new Command.Choice(about.getNameReadable(), about.getName()));
        }

        return new ArrayList<>() {{
            for (ICommandSlash slash : slashes.subList(setMin, Math.min(setMax, slashes.size()))) {
                add(new Command.Choice(slash.getNameReadable(), slash.getName()));
            }
        }};
    }

    /**
     * Get the {@link Metadata} of an {@link ICommandSlash}.
     *
     * @param search The name of the command to look for.
     * @return An {@link Optional} of {@link Metadata}.
     */
    public Optional<Metadata> getMetadata(@NotNull String search) {
        Optional<ICommandSlash> commandSlash = getInteraction(search);

        return commandSlash.map(ICommandSlash::getMetadata);
    }

    /**
     * Get the effective amount of global slash commands.
     *
     * @return The amount of slash commands.
     */
    public int getSlashCommandCount() {
        return (int) interactions.stream().filter(super::checkIfGlobal).count();
    }

    /**
     * Get the effective amount of guild slash commands for a guild.
     *
     * @param serverId The server id to check for.
     * @return The amount of slash commands.
     */
    public int getSlashCommandCount(long serverId) {
        return (int) interactions.stream().filter(iCommandSlash -> checkIfGuild(iCommandSlash, serverId)).count();
    }

    public enum SlashSet {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4);

        private final int setNumber;

        SlashSet(int setNumber) {
            this.setNumber = setNumber;
        }

        public int getSetNumber() {
            return setNumber;
        }
    }
}
