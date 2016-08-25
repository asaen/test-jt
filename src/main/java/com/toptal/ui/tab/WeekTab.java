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
package com.toptal.ui.tab;

import com.toptal.entities.Week;
import com.toptal.ui.converter.DateConverter;
import com.toptal.ui.converter.DoubleConverter;
import com.toptal.ui.converter.TimeConverter;
import com.toptal.ui.view.MainView;
import java.util.LinkedList;
import java.util.List;
import org.vaadin.viritin.fields.MTable;

/**
 * Shows weekly statistics.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings("serial")
public final class WeekTab extends AbstractTab {

    /**
     * Week table.
     */
    private final MTable<Week> tbl = this.table();

    /**
     * Ctor.
     * @param view Main view.
     * @param caption Caption.
     */
    public WeekTab(final MainView view, final String caption) {
        super(view, caption);
    }

    /**
     * Builds actual tab.
     * @return Tab instance.
     */
    public WeekTab build() {
        this.add(this.tbl);
        this.update();
        return this;
    }

    @Override
    public void update() {
        final List<Week> weeks = new LinkedList<>();
        this.getView().getUi().getEntries().weeks().forEach(weeks::add);
        this.tbl.setBeans(weeks);
    }

    @Override
    public MTable<Week> table() {
        final String start = "start";
        final String end = "end";
        final String time = "time";
        final String avgdistance = "averageDistance";
        final String avgspeed = "averageSpeed";
        final String number = "number";
        final MTable<Week> result =
            new MTable<>(Week.class)
                .withProperties(
                    number,
                    start,
                    end,
                    "amountOfEntries",
                    "distance",
                    time,
                    avgdistance,
                    avgspeed
                )
                .withColumnHeaders(
                    "Week number",
                    "Week start",
                    "Week end",
                    "Number of sessions",
                    "Total distance, m",
                    "Total time, h:m:s:ms",
                    "Average distance, m/session",
                    "Average speed, m/s"
                )
                .setSortableProperties(number)
                .withFullWidth();
        result.setConverter(start, new DateConverter());
        result.setConverter(end, new DateConverter());
        result.setConverter(time, new TimeConverter());
        result.setConverter(avgdistance, new DoubleConverter());
        result.setConverter(avgspeed, new DoubleConverter());
        return result;
    }

}
