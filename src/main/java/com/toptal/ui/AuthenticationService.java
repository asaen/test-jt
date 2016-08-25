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

import com.toptal.dao.UserDao;
import com.toptal.entities.User;
import com.toptal.error.AbstractException;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication service.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@Service
public final class AuthenticationService {

    /**
     * User DAO.
     */
    @Autowired
    private transient UserDao dao;

    /**
     * Password encoder.
     */
    @Autowired
    private transient PasswordEncoder encoder;

    /**
     * Login.
     * @param usr User name.
     * @param pwd Password.
     */
    public void login(final String usr, final String pwd) {
        final User user = this.dao.findByName(usr);
        if (user == null || !this.encoder.matches(pwd, user.getPassword())) {
            AbstractException.throwInvalidLoginPassword();
        }
        VaadinSession.getCurrent().setAttribute(User.class, user);
    }

    /**
     * Logout.
     */
    public void logout() {
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

}
