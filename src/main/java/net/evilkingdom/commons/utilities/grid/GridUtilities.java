package net.evilkingdom.commons.utilities.grid;

/*
 * Made with love by https://kodirati.com/.
 */

import java.awt.*;

public class GridUtilities {

    /**
     * Allows you to retrieve a position as an array from an index.
     * This should be used highly for mine based systems and creating them.
     *
     * @param index ~ The index.
     * @return The position as an array from the index.
     */
    public static int[] getPoint(final int index) {
        final int[] point = new int[2];
        final double sqrt = Math.sqrt(index);
        final int lower_sqrt = (int) ((sqrt % 1 == 0) ? sqrt - 1 : Math.floor(sqrt));
        final int higher_sqrt = lower_sqrt + 1;
        final int layers = (int) Math.ceil((float) (lower_sqrt - 2) / 2);
        final int corner_number = (int) Math.ceil((float) (higher_sqrt ^ 2 - lower_sqrt ^ 2) / 2) + lower_sqrt ^ 2;
        final int diff = index - corner_number;
        if (higher_sqrt % 2 == 0) {
            point[0] = -layers;
            point[1] = layers;
            point[0] += (diff <= 0) ? 1 : 1 + (corner_number - index);
            point[1] -= (diff >= 0) ? 1 : 1 - (corner_number - index);
        } else {
            point[0] = layers;
            point[1] = -layers;
            point[0] -= (diff <= 0) ? 1 : 1 + (corner_number - index);
            point[1] += (diff >= 0) ? 1 : 1 - (corner_number - index);
        }
        return point;
    }

}
