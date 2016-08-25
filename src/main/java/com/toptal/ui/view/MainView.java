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
package com.toptal.ui.view;

import com.toptal.conf.SecurityUtils;
import com.toptal.entities.User;
import com.toptal.ui.VaadinUI;
import com.toptal.ui.tab.EntryTab;
import com.toptal.ui.tab.UserTab;
import com.toptal.ui.tab.WeekTab;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import java.util.Optional;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * Main view.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings("serial")
public final class MainView extends AbstractView {

    /**
     * Tab to show and CRUD entries.
     */
    private final EntryTab entries;

    /**
     * Tab to show weekly statistics.
     */
    private final WeekTab weeks;

    /**
     * Tab to show and CRUD users.
     */
    private final Optional<UserTab> users;

    /**
     * Ctor.
     * @param vui Vaadin UI entry point.
     */
    public MainView(final VaadinUI vui) {
        super(vui);
        final TabSheet tabsheet = new TabSheet();
        this.entries = new EntryTab(this, "Entries").build();
        tabsheet.addTab(this.entries);
        this.weeks = new WeekTab(this, "Statistics").build();
        tabsheet.addTab(this.weeks);
        if (SecurityUtils.actualUser().isManager()) {
            this.users = Optional.of(new UserTab(this, "Users").build());
            tabsheet.addTab(this.users.get());
        } else {
            this.users = Optional.empty();
        }
        this.addComponent(this.header());
        this.addComponent(tabsheet);
    }

    /**
     * Actions to be performed on entry changes.
     */
    public void onEntryChange() {
        this.entries.update();
        this.weeks.update();
    }

    /**
     * Actions to be performed on user changes.
     */
    public void onUserChange() {
        this.users.get().update();
    }

    /**
     * Generates a header for main view.
     * @return Header.
     */
    private HorizontalLayout header() {
        final User user = SecurityUtils.actualUser();
        final Label info = new Label(
            String.format(
                "<h3>Welcome, %s. You are %s.</h3>",
                user.getName(),
                user.getRole().text()
            ),
            ContentMode.HTML
        );
        final Button logout =
            new MButton(
                FontAwesome.SIGN_OUT, e -> this.getUi().getAuth().logout()
            );
        return new MHorizontalLayout(info, logout)
            .withAlign(info, Alignment.MIDDLE_LEFT)
            .withAlign(logout, Alignment.MIDDLE_RIGHT)
            .withFullWidth();
    }

}
