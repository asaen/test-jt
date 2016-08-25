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
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest API for managing users.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@RestController
@RequestMapping(
    value = UserController.PATH,
    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
@Slf4j
public class UserController {

    /**
     * Signup REST path.
     */
    public static final String PATH = "/rest/user";

    /**
     * User DAO.
     */
    @Autowired
    private transient UserDao dao;

    /**
     * Finds all existing users.
     * @return Users.
     */
    @RequestMapping(method = RequestMethod.GET)
    public final Iterable<User> list() {
        return this.dao.findAll();
    }

    /**
     * Saves (creates new one or updates existing).
     * @param user User.
     * @return Saved user.
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(code = HttpStatus.OK)
    public final User update(@RequestBody final User user) {
        final User existing = this.checkId(user.getId());
        this.checkName(user);
        User result = null;
        if (existing != null
            && existing.getPassword().equals(user.getPassword())) {
            result = this.dao.saveIgnoringPassword(user);
        } else {
            result = this.dao.save(user);
        }
        log.debug("Saved user {}", result);
        return result;
    }

    /**
     * Deletes.
     * @param uid User ID.
     */
    @RequestMapping(value = "/{uid}", method = RequestMethod.DELETE)
    public final void delete(@PathVariable final Long uid) {
        final User user = this.checkId(uid);
        if (Objects.equals(user.getId(), SecurityUtils.actualUser().getId())) {
            AbstractException.throwActualUserCannotBeRemoved(user.getName());
        }
        this.dao.delete(uid);
        log.debug("Deleted user id {}", uid);
    }

    /**
     * Checks if there is a user with the specified id.
     * @param uid User id.
     * @return User object if found.
     */
    private User checkId(final Long uid) {
        final User existing = this.dao.findOne(uid);
        if (uid == null || existing == null) {
            AbstractException.throwUserIdNotFound(uid);
        }
        return existing;
    }

    /**
     * Checks if there is a user with the same name and not same id.
     * @param user User to be checked.
     * @return User object if found.
     */
    private User checkName(final User user) {
        final User existing = this.dao.findByName(user.getName());
        if (existing != null && !existing.getId().equals(user.getId())) {
            AbstractException.throwUserNameExists(user.getName());
        }
        return existing;
    }

}
