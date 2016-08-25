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
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Tests for REST API of {@link SignupController}.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Bootstrap.class)
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class SignupControllerTest extends AbstractRestTest {

    /**
     * One can sign up.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void registers() throws Exception {
        this.registerUser("userNameToSignUp", "password", Role.ROLE_USER);
    }

    /**
     * One cannot register a user with set id.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void cannotRegisterWithNotNullUserId() throws Exception {
        final Long uid = 10L;
        final String user = this.json(
            User.builder()
                .id(uid)
                .name("userNameWithId")
                .password("passw0rdWithId")
                .role(Role.ROLE_USER)
                .build()
        );
        final MvcResult result = this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .post(SignupController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(user)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
        Assert.assertEquals(
            "User id must be null", result.getResolvedException().getMessage()
        );
    }

    /**
     * One cannot register users with existing username.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void cannotRegisterExistingUser() throws Exception {
        final String username = "existingUser";
        this.registerUser(username, "passw00rd1902", Role.ROLE_USER);
        final String user = this.json(
            User.builder()
                .name(username)
                .password("passw0rd1902")
                .role(Role.ROLE_USER)
                .build()
        );
        final MvcResult result = this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .post(SignupController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(user)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andReturn();
        Assert.assertEquals(
            "User name 'existingUser' already exists",
            result.getResolvedException().getMessage()
        );
    }

    /**
     * One cannot register an empty user.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void testConstraintsEmptyUser() throws Exception {
        this.checkConstraints(this.json(User.builder().build()));
    }

    /**
     * One cannot register a user with empty name.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void testConstraintsEmptyName() throws Exception {
        this.checkConstraints(
            this.json(
                User.builder()
                    .password("ppp")
                    .role(Role.ROLE_USER)
                    .build()
            )
        );
    }

    /**
     * One cannot register a user with empty password.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void testConstraintsEmptyPassword() throws Exception {
        this.checkConstraints(
            this.json(
                User.builder()
                    .name("name")
                    .role(Role.ROLE_USER)
                    .build()
            )
        );
    }

    /**
     * One cannot register a user without any role.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void testConstraintsEmptyRoles() throws Exception {
        this.checkConstraints(
            this.json(
                User.builder()
                    .name("name1")
                    .password("ppp1")
                    .build()
            )
        );
    }

    /**
     * Sends request and checks that server returns constraint violation
     * exception.
     * @param user User as JSON.
     * @throws Exception If smth goes wrong.
     */
    private void checkConstraints(final String user) throws Exception {
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                .post(SignupController.PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(user)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(
                MockMvcResultMatchers
                    .status().reason(
                        Matchers.containsString("List of constraint violations")
                    )
            );
    }

}
