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
package com.toptal.ui;

import com.toptal.entities.Entry;
import com.toptal.ui.converter.TimeConverter;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import java.util.Date;
import org.vaadin.viritin.fields.MDateField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Add/edit entry.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings("serial")
public final class EntryForm extends AbstractForm<Entry> {

    /**
     * Entry Id text field.
     */
    private final TextField eid = new MTextField("Id");

    /**
     * Date field.
     */
    private final DateField date = new MDateField("Date");

    /**
     * Distance text field.
     */
    private final TextField distance = new MTextField("Distance, m")
            .withInputPrompt("Distance in kilometers");

    /**
     * Time text field.
     */
    private final TextField time = new TextField("Time");

    /**
     * Ctor.
     * @param entry Entry.
     */
    public EntryForm(final Entry entry) {
        super();
        this.eid.setEnabled(false);
        this.setSizeUndefined();
        this.setEntity(entry);
        this.time.setConverter(new TimeConverter());
        this.date.setRangeEnd(new Date());
    }

    @Override
    public Component createContent() {
        return new MVerticalLayout(
            new MFormLayout(
                this.date, this.distance, this.time
            ),
            this.getToolbar()
        );
    }

}
