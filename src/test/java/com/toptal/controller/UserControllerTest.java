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

import com.toptal.Bootstrap;
import com.toptal.dao.UserDao;
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests for REST API of {@link UserController}.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Bootstrap.class)
@Transactional
@SuppressWarnings({ "PMD.TestClassWithoutTestCases", "PMD.TooManyMethods" })
public class UserControllerTest extends AbstractRestTest {

    /**
     * User DAO.
     */
    @Autowired
    private transient UserDao dao;

    /**
     * User cannot update users.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userDoesNotUpdateUser() throws Exception {
        final String name = "userUpdatesNoUser";
        final String password = "asdasd1";
        this.registerUser(name, password, Role.ROLE_USER);
        final User user =
            User
                .builder()
                .name("newUser1")
                .password("asdasda1")
                .role(Role.ROLE_USER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        final String nname = UUID.randomUUID().toString();
        saved.setName(nname);
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .post(UserController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.json(saved))
                    .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Manager can update users.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void managerDoesUpdateUser() throws Exception {
        final String name = "newManager";
        final String password = "asdasd";
        this.registerUser(name, password, Role.ROLE_MANAGER);
        final User user =
            User
                .builder()
                .name("newUser")
                .password("asdasda")
                .role(Role.ROLE_USER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        final String nname = UUID.randomUUID().toString();
        saved.setName(nname);
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .post(UserController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.json(saved))
                    .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$.id", Matchers.is(saved.getId().intValue())
                )
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$.name", Matchers.is(nname)
                )
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.password").doesNotExist()
            )
            .andExpect(
                MockMvcResultMatchers
                    .jsonPath(
                        "$.role",
                        Matchers.is(Role.ROLE_USER.toString())
                    )
            );
    }

    /**
     * Anonymous cannot delete any user.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void anonymousDoesNotDelete() throws Exception {
        final User user =
            User
                .builder()
                .name("newUserToDeleteA")
                .password("asdoqiwassfA")
                .role(Role.ROLE_USER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .delete(this.deletePath(saved.getId()))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
    }

    /**
     * User cannot delete any user.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userDoesNotDelete() throws Exception {
        final String name = "aaa";
        final String password = "bbb";
        this.registerUser(name, password, Role.ROLE_USER);
        final User user =
            User
                .builder()
                .name("newUserToDeleteB")
                .password("asdoqiwassfB")
                .role(Role.ROLE_USER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .delete(this.deletePath(saved.getId()))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden());
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
    }

    /**
     * Manager can delete any user.
     * @throws Exception If smth goes wrong.
     */
    @Test
    @Ignore
    public final void managerDoesDelete() throws Exception {
        final String name = "aaaa";
        final String password = "bbbb";
        this.registerUser(name, password, Role.ROLE_MANAGER);
        final User user =
            User
                .builder()
                .name("newUserToDeleteC")
                .password("asdoqiwassfC")
                .role(Role.ROLE_USER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        this.dao.delete(saved);
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .delete(this.deletePath(saved.getId()))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
        Assert.assertNull(this.dao.findOne(saved.getId()));
    }

    /**
     * Anonymous cannot list all users.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void anonymousDoesNotAccessList() throws Exception {
        final User user =
            User
                .builder()
                .name("aaaaaa")
                .password("bbbbb")
                .role(Role.ROLE_USER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .get(UserController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * User cannot list all users.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userDoesNotAccessList() throws Exception {
        final String name = "newUserToTestList";
        final String password = "asdoqissfdfa";
        final User user =
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_USER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .get(UserController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Manager can list users.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void managerDoesAccessList() throws Exception {
        final int size = this.toList(this.dao.findAll()).size();
        final String name = "newManagerToTestList";
        final String password = "asdoqissfdf";
        final User user =
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_MANAGER)
                .build();
        final User saved = this.dao.save(user);
        Assert.assertNotNull(this.dao.findOne(saved.getId()));
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .get(UserController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(this.listHasSize(size + 1));
    }

    /**
     * Path to delete a user.
     * @param uid User id.
     * @return Path.
     */
    private String deletePath(final Long uid) {
        return UserController.PATH + "/" + uid;
    }

}
