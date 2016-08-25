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

import lombok.Getter;

/**
 * Representation and utility methods for time.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
public final class Time {

    /**
     * Amount of milliseconds in a second.
     */
    public static final long MS_IN_SECOND = 1000;

    /**
     * Amount of milliseconds in a minute.
     */
    public static final long MS_IN_MINUTE = 60 * MS_IN_SECOND;

    /**
     * Amount of milliseconds in an hour.
     */
    public static final long MS_IN_HOUR = 60 * MS_IN_MINUTE;

    /**
     * Pattern.
     */
    private static final String PATTERN = "[0-9]+:[0-9]{2}:[0-9]{2}:[0-9]{3}";

    /**
     * Format.
     */
    private static final String FORMAT = "%01d:%02d:%02d:%03d";

    /**
     * Milliseconds, seconds, minutes, hours.
     * @checkstyle MultipleVariableDeclarationsCheck (3 lines)
     * @checkstyle MemberNameCheck (2 lines)
     */
    private transient long ms, s, m, h;

    /**
     * Time in milliseconds.
     */
    @Getter
    private transient long millis;

    /**
     * Ctor.
     * @param milliseconds Long representation of a time object.
     */
    public Time(final long milliseconds) {
        this.millis = milliseconds;
        this.initFields(this.millis);
    }

    /**
     * Ctor.
     * @param string String representation of a time object.
     */
    public Time(final String string) {
        if (string.matches(PATTERN)) {
            // @checkstyle MagicNumberCheck (5 lines)
            final String[] parts = string.split(":");
            this.h = Long.valueOf(parts[0]);
            this.m = Long.valueOf(parts[1]);
            this.s = Long.valueOf(parts[2]);
            this.ms = Long.valueOf(parts[3]);
            this.millis = this.ms + MS_IN_HOUR * this.h + MS_IN_MINUTE * this.m
                + MS_IN_SECOND * this.s;
        } else {
            this.millis = Long.valueOf(string);
            this.initFields(this.millis);
        }
    }

    @Override
    public String toString() {
        return String.format(FORMAT, this.h, this.m, this.s, this.ms);
    }

    /**
     * Initializes fields.
     * @param milliseconds Input.
     */
    private void initFields(final long milliseconds) {
        this.h = milliseconds / MS_IN_HOUR;
        this.m = (milliseconds - MS_IN_HOUR * this.h) / MS_IN_MINUTE;
        this.s =
            (milliseconds - MS_IN_HOUR * this.h - MS_IN_MINUTE * this.m)
                / MS_IN_SECOND;
        this.ms =
            milliseconds - MS_IN_HOUR * this.h - MS_IN_MINUTE * this.m
                - MS_IN_SECOND * this.s;
    }

}
