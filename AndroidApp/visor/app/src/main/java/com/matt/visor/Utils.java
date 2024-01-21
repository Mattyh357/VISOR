package com.matt.visor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Utils {

    public static String UnixToZulu(long sec) {
        Instant instant = Instant.ofEpochSecond(sec);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("Z"));
        return formatter.format(instant);
    }

    public static String formatUnixToDateAndTime(long sec) {
        return formatUnix(sec, "dd/MM/yyyy - HH:mm:ss");
    }

    public static String formatUnixToTime(long sec) {
        return formatUnix(sec, "HH:mm:ss");
    }

    public static String formatUnitToDate(long sec) {
        return formatUnix(sec, "dd/MM/yyyy");
    }

    public static String formatUnix(long sec, String pattern) {

        Instant instant = Instant.ofEpochMilli(sec);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("Z"));
        String out = formatter.format(instant);

        System.out.println("FORMAT TEST: " + sec + " -> " + out);

        return out;
    }

}
