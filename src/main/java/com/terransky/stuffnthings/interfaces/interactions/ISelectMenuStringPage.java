package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.Page;

public interface ISelectMenuStringPage extends ISelectMenuString, Page {

    @Override
    default String getName() {
        return getInteractionName();
    }
}
