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

import com.toptal.conf.SecurityUtils;
import com.toptal.entities.Entry;
import com.toptal.ui.CrudToolbar;
import com.toptal.ui.EntryForm;
import com.toptal.ui.converter.DateConverter;
import com.toptal.ui.converter.DoubleConverter;
import com.toptal.ui.converter.TimeConverter;
import com.toptal.ui.view.MainView;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.vaadin.viritin.fields.MDateField;
import org.vaadin.viritin.fields.MTable;

/**
 * Tab to show actual user's entries.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (200 lines)
 */
@SuppressWarnings({ "serial", "PMD.TooManyMethods", "PMD.UnusedPrivateMethod" })
public final class EntryTab extends AbstractTab implements CrudToolbar.Crud {

    /**
     * Table of entries.
     */
    private final MTable<Entry> tbl = this.table();

    /**
     * Start date to filter entries out.
     */
    private final DateField start = new MDateField();

    /**
     * End date to filter entries out.
     */
    private final DateField end = new MDateField();

    /**
     * CRUD toolbar for entries.
     */
    private final CrudToolbar toolbar = new CrudToolbar(
        this,
        new Label("Start"),
        this.start,
        new Label("End"),
        this.end
    );

    /**
     * Ctor.
     * @param view Main view.
     * @param caption Caption.
     */
    public EntryTab(final MainView view, final String caption) {
        super(view, caption);
    }

    /**
     * Builds entry tab.
     * @return Entry tab.
     * @checkstyle RequireThisCheck (10 lines)
     */
    public EntryTab build() {
        this.addComponent(this.toolbar.alignAll(Alignment.MIDDLE_LEFT));
        this.addComponent(this.tbl);
        this.expand(this.tbl);
        this.start.addValueChangeListener(this::onDatesChanged);
        this.end.addValueChangeListener(this::onDatesChanged);
        this.update();
        return this;
    }

    @Override
    public void add() {
        this.entryForm(
            Entry
                .builder()
                .id(null)
                .date(new Date())
                .distance(0L)
                .time(0L)
                .user(SecurityUtils.actualUser())
                .build()
        );
    }

    @Override
    public void edit() {
        this.entryForm(this.tbl.getValue());
    }

    @Override
    public void delete() {
        final Entry value = this.tbl.getValue();
        if (value == null) {
            return;
        }
        this.getView().getUi().getEntries().delete(value.getId());
        this.getView().onEntryChange();
        this.tbl.setValue(null);
    }

    @Override
    public void update() {
        Date startv = this.start.getValue();
        if (startv == null) {
            startv = new Date(Long.MIN_VALUE);
        }
        Date endv = this.end.getValue();
        if (endv == null) {
            endv = new Date(Long.MAX_VALUE);
        }
        final List<Entry> entries = new LinkedList<>();
        this.getView()
            .getUi()
            .getEntries()
            .filter(startv, endv)
            // @checkstyle RequireThisCheck (1 line)
            .forEach(entries::add);
        this.tbl.setBeans(entries);
    }

    @Override
    public MTable<Entry> table() {
        final String date = "date";
        final String distance = "distance";
        final String time = "time";
        final String speed = "speed";
        final MTable<Entry> result = new MTable<>(Entry.class)
            .withProperties(date, distance, time, speed)
            .withColumnHeaders(
                "Date",
                "Distance, m",
                "Time, h:m:s:ms",
                "Average Speed, m/s"
            )
            .setSortableProperties(date)
            .withFullWidth();
        result.setConverter(date, new DateConverter());
        result.setConverter(time, new TimeConverter());
        result.setConverter(speed, new DoubleConverter());
        // @checkstyle RequireThisCheck (1 line)
        result.addValueChangeListener(this::onValueChanged);
        return result;
    }

    /**
     * Creates entry form.
     * @param entry Entry.
     * @checkstyle RequireThisCheck (10 lines)
     */
    private void entryForm(final Entry entry) {
        final EntryForm form = new EntryForm(entry);
        form.setSavedHandler(this::save);
        form.setResetHandler(this::reset);
        form.openInModalPopup();
    }

    /**
     * Save entry.
     * @param entry Entry.
     */
    private void save(final Entry entry) {
        this.getView().getUi().getEntries().save(entry);
        this.reset(entry);
    }

    /**
     * Reset entry.
     * @param entry Entry.
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void reset(final Entry entry) {
        this.getView().onEntryChange();
        this.getView().getUi().closeWindow();
    }

    /**
     * Actions to perform when value changed.
     * @param event Value change event.
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void onValueChanged(final Property.ValueChangeEvent event) {
        this.toolbar.setEditAndDeleteEnabled(this.tbl.getValue() != null);
    }

    /**
     * Actions to perform when filter dates updated.
     * @param vce Value change event.
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void onDatesChanged(final Property.ValueChangeEvent vce) {
        this.update();
    }

}
