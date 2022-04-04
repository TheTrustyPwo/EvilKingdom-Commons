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
        int dx = 0;
        int dz = 1;
        int segment_length = 1;
        int x = 0;
        int z = 0;
        int segment_passed = 0;
        if (index == 0) {
            point[0] = x;
            point[1] = z;
            return point;
        }
        for (int n = 0; n < index; ++n) {
            x += dx;
            z += dz;
            ++segment_passed;
            if (segment_passed == segment_length) {
                segment_passed = 0;
                int buffer = dz;
                dz = -dx;
                dx = buffer;
                if (dx == 0) {
                    ++segment_length;
                }
            }
        }
        point[0] = x;
        point[1] = z;
        return point;
    }

}
