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
package com.toptal.conf;

import com.toptal.dao.UserDao;
import com.toptal.entities.User;
import com.vaadin.server.VaadinSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security utils.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@Configuration
public class SecurityUtils implements ApplicationContextAware {

    /**
     * Application context.
     */
    private static ApplicationContext context;

    @Override
    public final void setApplicationContext(final ApplicationContext ctx) {
        SecurityUtils.context = ctx;
    }

    /**
     * Encodes given string into base64.
     * @param str String.
     * @return Encoded string.
     */
    public static String encode(final String str) {
        String result = null;
        if (str != null) {
            result = Base64.getEncoder().encodeToString(
                str.getBytes(StandardCharsets.UTF_8)
            );
        }
        return result;
    }

    /**
     * Decodes given string from base64.
     * @param str String.
     * @return Decoded string.
     */
    public static String decode(final String str) {
        String result = null;
        if (str != null) {
            result = new String(
                Base64.getDecoder().decode(str),
                StandardCharsets.UTF_8
            );
        }
        return result;
    }

    /**
     * Finds user in the database with actual username.
     * @return User instance.
     */
    public static User actualUser() {
        final UserDao dao = context.getBean(UserDao.class);
        User result = null;
        final Optional<User> vaadin = actualUserVaadin(dao);
        if (vaadin.isPresent()) {
            result = vaadin.get();
        }
        if (result == null) {
            result = dao.findByName(actualUserName());
        }
        return result;
    }

    /**
     * Actual Vaadin user.
     * @param dao User dao.
     * @return User.
     */
    private static Optional<User> actualUserVaadin(final UserDao dao) {
        Optional<User> result = Optional.empty();
        if (VaadinSession.getCurrent() != null) {
            final User user =
                VaadinSession.getCurrent().getAttribute(User.class);
            if (user != null) {
                result = Optional.ofNullable(dao.findOne(user.getId()));
            }
        }
        return result;
    }

    /**
     * Currently authenticated user name.
     * @return User name.
     */
    private static String actualUserName() {
        String result = null;
        final Optional<Authentication> auth = getAuthentication();
        if (auth.isPresent()) {
            result = auth.get().getName();
        }
        return result;
    }

    /**
     * Authentication object.
     * @return Authentication.
     */
    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(
            SecurityContextHolder.getContext().getAuthentication()
        );
    }

}
