package com.terransky.stuffnthings.interfaces;

public interface Page {

    String getPageReference();

    int getPageNumber();

    default String getInteractionName() {
        return getPageReference() + "-page-" + getPageNumber();
    }
}
