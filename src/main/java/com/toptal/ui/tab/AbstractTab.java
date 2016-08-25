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

import com.toptal.ui.Updateable;
import com.toptal.ui.view.MainView;
import lombok.Getter;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Abstract tab.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings({ "serial", "PMD.SingularField", "PMD.UnusedPrivateField" })
public abstract class AbstractTab
    extends MVerticalLayout implements Updateable {

    /**
     * Main view.
     */
    @Getter
    private final MainView view;

    /**
     * Ctor.
     * @param vieww Main view.
     * @param caption Caption.
     */
    public AbstractTab(final MainView vieww, final String caption) {
        super();
        this.view = vieww;
        this.setCaption(caption);
    }

    /**
     * Builds table for the tab.
     * @return Table.
     */
    public abstract MTable<?> table();

}
