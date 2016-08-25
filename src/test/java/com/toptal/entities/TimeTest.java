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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link Time}.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 * @checkstyle MagicNumberCheck (100 lines)
 */
public class TimeTest {

    /**
     * Time object can parse string.
     */
    @Test
    public final void parsesString() {
        final Time time = new Time("0:00:01:000");
        Assert.assertEquals(1000L, time.getMillis());
        final Time tiime = new Time("21000");
        Assert.assertEquals(21000L, tiime.getMillis());
        Assert.assertEquals("0:00:21:000", tiime.toString());
    }

    /**
     * Time object can convert to string.
     */
    @Test
    public final void convertsToString() {
        final Time time = new Time(1999L);
        Assert.assertEquals("0:00:01:999", time.toString());
        final Time tiime = new Time(0L);
        Assert.assertEquals("0:00:00:000", tiime.toString());
    }

}
