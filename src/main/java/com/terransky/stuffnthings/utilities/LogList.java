package com.terransky.stuffnthings.utilities;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@SuppressWarnings("unused")
public class LogList {

    private LogList() {
    }

    public static void info(@NotNull List<?> aList, Logger log) {
        for (Object listItem : aList) {
            log.info(listItem.toString());
        }
    }

    public static void info(@NotNull List<?> aList, Class<?> clazz) {
        info(aList, LoggerFactory.getLogger(clazz));
    }

    public static void debug(@NotNull List<?> aList, Logger log) {
        for (Object listItem : aList) {
            log.debug(listItem.toString());
        }
    }

    public static void debug(@NotNull List<?> aList, Class<?> clazz) {
        debug(aList, LoggerFactory.getLogger(clazz));
    }

    public static void error(@NotNull List<?> aList, Logger log) {
        for (Object listItem : aList) {
            log.error(listItem.toString());
        }
    }

    public static void error(@NotNull List<?> aList, Class<?> clazz) {
        error(aList, LoggerFactory.getLogger(clazz));
    }

    public static void warn(@NotNull List<?> aList, Logger log) {
        for (Object listItem : aList) {
            log.warn(listItem.toString());
        }
    }

    public static void warn(@NotNull List<?> aList, Class<?> clazz) {
        warn(aList, LoggerFactory.getLogger(clazz));
    }
}
