package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.Page;

public interface ISelectMenuEntityPage extends ISelectMenuEntity, Page {

    @Override
    default String getName() {
        return getInteractionName();
    }
}
