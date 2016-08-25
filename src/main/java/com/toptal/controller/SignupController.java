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
package com.toptal.controller;

import com.toptal.conf.SecurityUtils;
import com.toptal.dao.UserDao;
import com.toptal.entities.User;
import com.toptal.error.AbstractException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API to register new users.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@RestController
@RequestMapping(
    value = SignupController.PATH,
    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
@Slf4j
public class SignupController {

    /**
     * Signup REST path.
     */
    public static final String PATH = "/rest/signup";

    /**
     * User DAO.
     */
    @Autowired
    private transient UserDao dao;

    /**
     * Registers new user.
     * @param user User.
     * @return Registered user.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public final User register(@RequestBody final User user) {
        this.validateIdAndName(user);
        user.setPassword(SecurityUtils.decode(user.getPassword()));
        final User result = this.dao.save(user);
        log.debug("Registered new user {}", result);
        return result;
    }

    /**
     * Checks if id is null and user name doesn't exist.
     * @param user User to be checked.
     */
    private void validateIdAndName(final User user) {
        if (user.getId() != null) {
            AbstractException.throwUserIdIsNotNull();
        }
        if (this.dao.findByName(user.getName()) != null) {
            AbstractException.throwUserNameExists(user.getName());
        }
    }

}
