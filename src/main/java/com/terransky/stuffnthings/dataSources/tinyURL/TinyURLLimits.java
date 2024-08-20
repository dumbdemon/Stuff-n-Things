package com.terransky.stuffnthings.dataSources.tinyURL;

import com.terransky.stuffnthings.StuffNThings;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TinyURLLimits {

    private TinyURLLimits() {
    }

    @NotNull
    static String getEffectiveString(String s, TinyURLLimits.Lengths lengths) {
        if ("".equals(s) || s == null) return "";

        if (s.length() < lengths.getMin())
            return "_".repeat((lengths.getMin() - s.length())) + s;

        if (s.length() > lengths.getMax())
            return s.substring(0, lengths.getMax());

        return s;
    }

    public enum Lengths {
        DOMAIN(Integer.MAX_VALUE),
        ALIAS(5, 30),
        TAGS(45);

        private final int min;
        private final int max;

        Lengths(int max) {
            this(0, max);
        }

        Lengths(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }

    public enum Domain {
        DEFAULT("tinyurl.com", 0),
        LOL("rotf.lol", 1),
        ONE("tiny.one", 2),
        CUSTOM(StuffNThings.getConfig().getTokens().getTinyUrl().getDomain(), 3);

        private final String domain;
        private final int key;

        Domain(String domain, int key) {
            this.domain = domain;
            this.key = key;
        }

        @NotNull
        public static List<Command.Choice> getDomainsAsChoices() {
            final List<Domain> domains = new ArrayList<>(EnumSet.allOf(Domain.class).stream().toList());

            if (CUSTOM.getDomain() == null)
                domains.remove(CUSTOM);

            return new ArrayList<>() {{
                for (Domain domain : domains) {
                    add(new Command.Choice(domain.getDomain(), domain.getKey()));
                }
            }};
        }

        public static Domain getDomainByKey(int key) {
            switch (key) {
                case 1 -> {
                    return LOL;
                }
                case 2 -> {
                    return ONE;
                }
                case 3 -> {
                    return CUSTOM;
                }
                default -> {
                    return DEFAULT;
                }
            }
        }

        public String getDomain() {
            return domain;
        }

        public int getKey() {
            return key;
        }
    }
}
