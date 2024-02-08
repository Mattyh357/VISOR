package com.matt.visor;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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


    public static String secondsToTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        // Format the time parts to ensure they are displayed as two digits
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);

    }


    public static String epochToDateTime(Long epochTime) {
//        long epochTime = Long.parseLong(epochTimeStr);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return format.format(new Date(epochTime));

    }

}
