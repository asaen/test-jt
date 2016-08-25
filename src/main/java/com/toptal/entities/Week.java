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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toptal.conf.Format;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;

/**
 * Representation and utility methods for weekly statistics.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings({ "PMD.SingularField", "PMD.BeanMembersShouldSerialize",
    "PMD.UnusedPrivateField" })
public final class Week {

    /**
     * Week number format: #00, 0000.
     */
    private static final String NUMBER_FORMAT = "%04d, #%02d";

    /**
     * Week number.
     */
    @Getter
    private final String number;

    /**
     * Week start.
     */
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Format.DATE_FORMAT)
    private final Date start;

    /**
     * Week end.
     */
    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Format.DATE_FORMAT)
    private final Date end;

    /**
     * Entries on the actual week.
     */
    private final List<Entry> entries = new LinkedList<>();

    /**
     * Total distance per week.
     */
    @Getter
    private long distance;

    /**
     * Total time per week.
     */
    @Getter
    private long time;

    /**
     * Ctor.
     * @param initial Initial entry to build week object.
     */
    public Week(final Entry initial) {
        final Date date = initial.getDate();
        this.number = formatNumber(date);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        this.start = calendar.getTime();
        final int last = 6;
        calendar.add(Calendar.DAY_OF_MONTH, last);
        this.end = calendar.getTime();
        this.addEntry(initial);
    }

    /**
     * Adds entry to the week.
     * @param entry Entry to be added.
     * @return If entry passes for this week it is added and {@code true} is
     *  returned.
     */
    public boolean addEntry(final Entry entry) {
        boolean result = false;
        if (this.number.equals(formatNumber(entry.getDate()))) {
            this.entries.add(entry);
            this.distance += entry.getDistance();
            this.time += entry.getTime();
            result = true;
        }
        return result;
    }

    /**
     * Average distance.
     * @return Average distance.
     */
    public double getAverageDistance() {
        double result = 0.;
        if (!this.entries.isEmpty()) {
            result = this.distance / (double) this.getAmountOfEntries();
        }
        return result;
    }

    /**
     * Average speed.
     * @return Average speed.
     */
    public double getAverageSpeed() {
        double result = 0.;
        if (!this.entries.isEmpty()) {
            result = this.distance / (this.time / (double) Time.MS_IN_SECOND);
        }
        return result;
    }

    /**
     * Amount of entries in the week object.
     * @return Amount.
     */
    public int getAmountOfEntries() {
        return this.entries.size();
    }

    /**
     * Formats week number.
     * @param date Date.
     * @return String.
     */
    public static String formatNumber(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.format(
            NUMBER_FORMAT,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.WEEK_OF_YEAR)
        );
    }

}
