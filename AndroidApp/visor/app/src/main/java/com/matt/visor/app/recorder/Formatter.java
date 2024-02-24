/**
 * This class is part of the V.I.S.O.R app.
 * Utility type class to format values (times, distance, speed) into appropriate version for display.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.app.recorder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Formatter {

    /**
     * Converts epoch time in seconds to a formatted date and time string.
     *
     * @param epochTime The epoch time in seconds.
     * @return The formatted date and time string.
     */
    public static String epochToDateAndTime(Long epochTime) {
        return epochToPattern(epochTime, "HH:mm:ss - dd/MM/yy");
    }

    /**
     * Converts epoch time in seconds to a string based on a specified pattern.
     *
     * @param epochTime The epoch time in seconds.
     * @param pattern The pattern for formatting the date and time.
     * @return The formatted string.
     */
    public static String epochToPattern(Long epochTime, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(epochTime * 1000));
    }

    /**
     * Formats a total number of seconds as elapsed time in HH:mm:ss format.
     *
     * @param totalSeconds The total seconds to format.
     * @return The elapsed time as a formatted string.
     */    public static String secondsAsElapsedTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Formats a distance value with optional units, converting to meters if less than 1 km.
     *
     * @param distance The distance in kilometers.
     * @param addUnits Flag to include units in the returned string.
     * @return Formatted distance, optionally with units.
     */
    public static String formatDistance(double distance, boolean addUnits) {
        // Takes in KM

        String units = " Km";
        int decPlaces = 2;

        //  math that convert to metres if below 0
        if(distance < 0){
            distance *= 1000;
            units = " m";
            decPlaces = 0;
        }

        BigDecimal bd = new BigDecimal(Double.toString(distance));
        bd = bd.setScale(decPlaces, RoundingMode.HALF_UP);

        String str = String.valueOf(bd.doubleValue());

        if(addUnits)
            str += units;

        return str;
    }

    /**
     * Formats a speed value with optional units, rounding to zero if less than 1 km/h.
     *
     * @param speed The speed in kilometers per hour.
     * @param addUnits Flag to include units in the returned string.
     * @return Formatted speed, optionally with units.
     */
    public static String formatSpeed(double speed, boolean addUnits) {
        if (speed < 1)
            speed = 0;

        BigDecimal bd = new BigDecimal(Double.toString(speed));
        bd = bd.setScale(2, RoundingMode.HALF_UP);


        String str = String.valueOf(bd.floatValue());

        if(addUnits)
            str += " km/h";

        return str;
    }

    /**
     * Converts epoch time to Zulu time format.
     *
     * @param sec The epoch time in seconds.
     * @return Zulu time as a formatted string.
     */
    public static String epochToZulu(long sec) {
        Instant instant = Instant.ofEpochSecond(sec);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("Z"));
        return formatter.format(instant);
    }


}
