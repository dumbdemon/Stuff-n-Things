package com.terransky.StuffnThings.interfaces;

import org.jetbrains.annotations.NotNull;

public interface IBaseDiscordElement extends Comparable<IBaseDiscordElement> {

    String getName();

    @Override
    default int compareTo(@NotNull IBaseDiscordElement iBaseDiscordElement) {
        return String.CASE_INSENSITIVE_ORDER.compare(this.getName(), iBaseDiscordElement.getName());
    }
}
