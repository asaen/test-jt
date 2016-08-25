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
import com.toptal.entities.User.Role;
import com.toptal.error.UnauthorizedException;
import com.toptal.ui.VaadinUI;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import lombok.extern.slf4j.Slf4j;

/**
 * Login view.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@Slf4j
@SuppressWarnings("serial")
public final class LoginView extends AbstractView {

    /**
     * Username.
     */
    private TextField username;

    /**
     * Password.
     */
    private PasswordField password;

    /**
     * Ctor.
     * @param vui Vaadin UI entry point.
     */
    public LoginView(final VaadinUI vui) {
        super(vui);
        this.setSizeFull();
        this.addForm();
    }

    /**
     * Adds login form.
     */
    private void addForm() {
        final VerticalLayout form = new VerticalLayout();
        form.setSizeUndefined();
        form.setSpacing(true);
        form.addComponent(this.header());
        form.addComponent(this.content());
        this.addComponent(form);
        this.setComponentAlignment(form, Alignment.MIDDLE_CENTER);
    }

    /**
     * Generates a header.
     * @return Header.
     */
    private Component header() {
        final Label greetings = new Label("Welcome to Jogging Tracker");
        greetings.addStyleName(ValoTheme.LABEL_H1);
        greetings.addStyleName(ValoTheme.LABEL_COLORED);
        return greetings;
    }

    /**
     * Generates content.
     * @return Content.
     */
    private Component content() {
        final HorizontalLayout content = new HorizontalLayout();
        content.setSpacing(true);
        this.username = new TextField("Username");
        this.username.setIcon(FontAwesome.USER);
        this.username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        this.password = new PasswordField("Password");
        this.password.setIcon(FontAwesome.LOCK);
        this.password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        final Button login = new Button("Log In");
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        login.setClickShortcut(KeyCode.ENTER);
        login.focus();
        login.addClickListener(
            e -> this.login(this.username.getValue(), this.password.getValue())
        );
        final Button signup = new Button("Sign Up");
        signup.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signup.addClickListener(
            e -> this.signup(this.username.getValue(), this.password.getValue())
        );
        content.addComponents(this.username, this.password, login, signup);
        content.setComponentAlignment(login, Alignment.BOTTOM_LEFT);
        content.setComponentAlignment(signup, Alignment.BOTTOM_LEFT);
        return content;
    }

    /**
     * Login.
     * @param usr Username.
     * @param pwd Password.
     * @checkstyle IllegalCatchCheck (10 lines)
     */
    private void login(final String usr, final String pwd) {
        try {
            this.getUi().getAuth().login(usr, pwd);
            this.getUi().update();
        } catch (final UnauthorizedException ex) {
            log.error("Login failed", ex);
            this.error(String.format("Login failed: %s", ex.getMessage()));
            this.username.clear();
            this.password.clear();
        }
    }

    /**
     * Register.
     * @param usr Username.
     * @param pwd Password.
     * @checkstyle IllegalCatchCheck (15 lines)
     */
    private void signup(final String usr, final String pwd) {
        this.getUi().getSignup().register(
            User
                .builder()
                .name(usr)
                .password(SecurityUtils.encode(pwd))
                .role(Role.ROLE_USER)
                .build()
        );
        this.login(usr, pwd);
    }

    /**
     * Error notification.
     * @param message Message.
     */
    private void error(final String message) {
        final int delay = 5000;
        final Notification notification =
            new Notification(message, Notification.Type.WARNING_MESSAGE);
        notification.setHtmlContentAllowed(true);
        notification.setStyleName("closable");
        notification.setPosition(Position.BOTTOM_CENTER);
        notification.setIcon(FontAwesome.WARNING);
        notification.setDelayMsec(delay);
        notification.show(Page.getCurrent());
    }

}
