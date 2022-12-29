package com.terransky.stuffnthings.utilities.general;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@SuppressWarnings("unused")
public class LogList {

    private LogList() {
    }

    public static void info(@NotNull Collection<?> objects, Logger log) {
        for (Object obj : objects) {
            log.info(obj.toString());
        }
    }

    public static void info(@NotNull Collection<?> objects, Class<?> clazz) {
        info(objects, LoggerFactory.getLogger(clazz));
    }

    public static void debug(@NotNull Collection<?> objects, Logger log) {
        for (Object obj : objects) {
            log.debug(obj.toString());
        }
    }

    public static void debug(@NotNull Collection<?> objects, Class<?> clazz) {
        debug(objects, LoggerFactory.getLogger(clazz));
    }

    public static void error(@NotNull Collection<?> objects, Logger log) {
        for (Object obj : objects) {
            log.error(obj.toString());
        }
    }

    public static void error(@NotNull Collection<?> objects, Class<?> clazz) {
        error(objects, LoggerFactory.getLogger(clazz));
    }

    public static void warn(@NotNull Collection<?> objects, Logger log) {
        for (Object obj : objects) {
            log.warn(obj.toString());
        }
    }

    public static void warn(@NotNull Collection<?> objects, Class<?> clazz) {
        warn(objects, LoggerFactory.getLogger(clazz));
    }
}
