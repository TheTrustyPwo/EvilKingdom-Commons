package net.evilkingdom.commons.utilities.time;

/*
 * Made with love by https://kodirati.com/.
 */

import java.util.concurrent.TimeUnit;

public class TimeUtilities {

    /**
     * Allows you to format a time.
     *
     * @param time ~ The time to be formatted.
     * @return The formatted time.
     */
    public static String format(final long time) {
        final long days = TimeUnit.MILLISECONDS.toDays(time);
        final long hours = TimeUnit.MILLISECONDS.toHours(time) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(time));
        final long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time));
        final long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time));
        StringBuilder formattedTime = new StringBuilder();
        if (days > 0L) {
            formattedTime.append(days).append("d");
        }
        if (hours > 0L) {
            formattedTime.append(hours).append("h");
        }
        if (minutes > 0L) {
            formattedTime.append(minutes).append("m");
        }
        if (seconds > 0L) {
            formattedTime.append(seconds).append("s");
        }

        return formattedTime.toString();
    }

    /**
     * Allows you to retrieve a time from a formatted time.
     *
     * @param time ~ The formatted time.
     * @return The time from the formatted time.
     */
    public static long get(String time) {
        try {
            long timeLong = parseLong(time);
            if (time.contains("d")) {
                timeLong += parseLong(time.split("d")[0]) * 86400;
                time = split(time, "d");
            }
            if (time.contains("h")) {
                timeLong += parseLong(time.split("h")[0]) * 3600;
                time = split(time, "h");
            }
            if (time.contains("m")) {
                timeLong += parseLong(time.split("m")[0]) * 60;
                time = split(time, "m");
            }
            if (time.contains("s")) {
                timeLong += parseLong(time.split("s")[0]);
                time = split(time, "s");
            }
            return timeLong * 1000;
        } catch (final Exception exception) {
            return -1;
        }
    }

    /**
     * Ignore this method, it's just an inner-working of the get method.
     */
    private static String split(final String arg0, final String arg1) {
        try {
            return arg0.split(arg1)[1];
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Ignore this method, it's just an inner-working of the get method.
     */
    private static long parseLong(final String arg0) {
        try {
            return Long.parseLong(arg0);
        } catch (final Exception exception) {
            return 0;
        }
    }

}
