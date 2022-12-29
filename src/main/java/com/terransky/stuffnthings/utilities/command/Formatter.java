package com.terransky.stuffnthings.utilities.command;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Formatter {
    private static final NavigableMap<Float, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1e3f, " Thousand");
        suffixes.put(1e6f, " Million");
        suffixes.put(1e9f, " Billion");
        suffixes.put(1e12f, " Trillion");
        suffixes.put(1e15f, " Quadrillion");
        suffixes.put(1e18f, " Quintillion");
        suffixes.put(1e21f, " Sextillion");
        suffixes.put(1e24f, " Septillion");
        suffixes.put(1e27f, " Octillion");
        suffixes.put(1e30f, " Nonillion");
        suffixes.put(1e33f, " Decillion");
        suffixes.put(1e36f, " Undecillion");
    }

    /**
     * Makes extra large numbers look nice~ <br />
     * Code Courtesy of <a href="https://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java">assylias's answer</a> on stackoverflow.
     *
     * @return {@link String} that contains a two-point decimal and the scale of value.
     */
    public static String largeNumberFormat(float value) {
        DecimalFormat simpleNum = new DecimalFormat("#.##");

        if (value == Float.MIN_VALUE) return largeNumberFormat(Float.MIN_VALUE + 1);
        if (value < 0) return "-" + largeNumberFormat(-value);
        if (value < 1000) return Float.toString(value);

        Map.Entry<Float, String> e = suffixes.floorEntry(value);
        float divideBy = e.getKey();
        String suffix = e.getValue();

        float truncated = value / (divideBy / 10);
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? simpleNum.format(truncated / 10d) + suffix : simpleNum.format(truncated / 10) + suffix;
    }
}
