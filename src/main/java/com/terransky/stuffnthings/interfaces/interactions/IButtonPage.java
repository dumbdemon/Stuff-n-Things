package com.terransky.stuffnthings.interfaces.interactions;

import com.terransky.stuffnthings.interfaces.Page;

public interface IButtonPage extends IButton, Page {

    @Override
    default String getName() {
        return getInteractionName();
    }
}
