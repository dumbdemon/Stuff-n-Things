package com.terransky.stuffnthings.utilities.command;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CommandCategory {

    TEST("Test"),
    ADMIN("Admin", "admin"),
    DEVS("Developer", "developer"),
    FUN("Fun", "fun"),
    GENERAL("General", "general"),
    MATHS("Math", "math"),
    MTG("Magic: the Gathering", "mtg");

    private final String name;
    private final String optionName;

    CommandCategory(String name) {
        this(name, null);
    }

    CommandCategory(String name, String optionName) {
        this.name = name;
        this.optionName = optionName;
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static List<Command.Choice> getCategoriesAsChoices() {
        return new ArrayList<>() {{
            for (CommandCategory value : Arrays.stream(CommandCategory.values()).filter(value -> value != TEST).toList()) {
                add(new Command.Choice(value.name, value.optionName));
            }
        }};
    }

    @NotNull
    public static Optional<CommandCategory> getCategoryByName(String optionName) {
        return Arrays.stream(CommandCategory.values())
            .filter(value -> value != TEST)
            .filter(value -> value.optionName.equals(optionName))
            .findFirst();
    }

    public String getName() {
        return name;
    }
}
