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

import com.toptal.conf.SecurityUtils;
import com.toptal.controller.EntryController;
import com.toptal.controller.SignupController;
import com.toptal.controller.UserController;
import com.toptal.ui.view.LoginView;
import com.toptal.ui.view.MainView;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Start point of vaadin based ui.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SpringUI
@Theme("valo")
@Title("Jogging Tracker")
@PreserveOnRefresh
@SuppressWarnings({"serial", "PMD.UnusedPrivateField"})
public final class VaadinUI extends UI implements Updateable {

    /**
     * Authentication service.
     */
    @Getter
    @Autowired
    private AuthenticationService auth;

    /**
     * Signup controller.
     */
    @Getter
    @Autowired
    private SignupController signup;

    /**
     * Entry controller.
     */
    @Getter
    @Autowired
    private EntryController entries;

    /**
     * User controller.
     */
    @Getter
    @Autowired
    private UserController users;

    @Override
    public void update() {
        if (SecurityUtils.actualUser() == null) {
            this.setContent(new LoginView(this));
        } else {
            this.setContent(new MainView(this));
        }
    }

    @Override
    public void init(final VaadinRequest request) {
        Responsive.makeResponsive(this);
        this.addStyleName(ValoTheme.UI_WITH_MENU);
        this.update();
    }

    /**
     * Close all windows.
     */
    public void closeWindow() {
        this.getWindows().forEach(this::removeWindow);
    }

}
