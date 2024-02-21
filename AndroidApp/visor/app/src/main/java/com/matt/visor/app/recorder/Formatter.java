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
import java.util.Date;

public class Formatter {

    /**
     * Converts epoch time in seconds to a formatted date and time string.
     *
     * @param epochTime The epoch time in seconds.
     * @return The formatted date and time string.
     */
    public static String secondsToDateAndTime(Long epochTime) {
        return secondsToPattern(epochTime, "HH:mm:ss - dd/MM/yy");
    }

    /**
     * Converts epoch time in seconds to a string based on a specified pattern.
     *
     * @param epochTime The epoch time in seconds.
     * @param pattern The pattern for formatting the date and time.
     * @return The formatted string.
     */
    public static String secondsToPattern(Long epochTime, String pattern) {
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












    public static String formatDistance(double distance) {
        // Takes in KM
        // TODO do math that convert to metres if b


        BigDecimal bd = new BigDecimal(Double.toString(distance));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return String.valueOf(bd.doubleValue()) + " Km"; // TODO hardcoded
    }




    public static String formatSpeed(double speed) {
        if (speed < 1)
            speed = 0;

        BigDecimal bd = new BigDecimal(Double.toString(speed));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return String.valueOf(bd.floatValue()) + " km/h";
    }


}
