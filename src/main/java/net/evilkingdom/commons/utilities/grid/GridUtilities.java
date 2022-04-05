package net.evilkingdom.commons.utilities.grid;

/*
 * This file is part of Commons (Server), licensed under the MIT License.
 *
 *  Copyright (c) kodirati (kodirati.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
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
