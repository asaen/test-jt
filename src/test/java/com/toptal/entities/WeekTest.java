/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Alexey Saenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.toptal.entities;

import java.util.Date;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link Week}.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 * @checkstyle MagicNumberCheck (100 lines)
 */
public final class WeekTest {

    /**
     * Delta.
     */
    private static final double DELTA = 0.00001;

    /**
     * Week can properly calculate average values.
     */
    @Test
    public void getAverages() {
        final long distance = 100L;
        final long time = 15000L;
        final Week week =
            new Week(new Entry(null, new Date(), distance, time, null));
        week.addEntry(new Entry(null, new Date(), distance, time, null));
        week.addEntry(new Entry(null, new Date(), distance, time, null));
        Assert.assertEquals(distance, week.getAverageDistance(), DELTA);
        Assert.assertEquals(
            distance / (time / (double) Time.MS_IN_SECOND),
            week.getAverageSpeed(),
            DELTA
        );
        Assert.assertEquals(3, week.getAmountOfEntries());
    }

}
