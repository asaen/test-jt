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
import com.toptal.conf.Format;
import com.toptal.dao.EntryDao;
import com.toptal.dao.UserDao;
import com.toptal.entities.Entry;
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.hamcrest.Matchers;
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
 * Tests for REST API of {@link EntryController}.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 * @checkstyle MagicNumberCheck (300 lines)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Bootstrap.class)
@Transactional
@SuppressWarnings({ "PMD.TestClassWithoutTestCases", "PMD.TooManyMethods" })
public class EntryControllerTest extends AbstractRestTest {

    /**
     * Id path.
     */
    private static final String ID_PATH = "$.id";

    /**
     * User DAO.
     */
    @Autowired
    private transient UserDao users;

    /**
     * Entry DAO.
     */
    @Autowired
    private transient EntryDao entries;

    /**
     * Anonymous cannot access list of entries.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void anonymousDoesNotAccessList() throws Exception {
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .get(EntryController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Any user can access list of his or her entries.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userDoesAccessList() throws Exception {
        final String name = "nameA";
        final String password = "passwordA";
        this.users.save(
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_USER)
                .build()
        );
        this
            .getMvc()
            .perform(
            MockMvcRequestBuilders
                .get(EntryController.PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Any user can access weekly statistics.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userDoesAccessWeeks() throws Exception {
        final String name = "nameB";
        final String password = "passwordB";
        this.users.save(
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_USER)
                .build()
        );
        this
            .getMvc()
            .perform(
            MockMvcRequestBuilders
                .get(this.subPath("weeks"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Any user can save an entry.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userSaves() throws Exception {
        final String name = "nameC";
        final String password = "passwordC";
        this.users.save(
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_USER)
                .build()
        );
        final Entry entry = new Entry(null, new Date(), 1000L, 100L, null);
        this
            .getMvc()
            .perform(
            MockMvcRequestBuilders
                .post(EntryController.PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(entry))
                .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers
                    .content().contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath(ID_PATH, Matchers.notNullValue())
            );
    }

    /**
     * Any user can update an entry.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userUpdates() throws Exception {
        final String name = "nameD";
        final String password = "passwordD";
        final User user = this.users.save(
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_USER)
                .build()
        );
        final Entry entry =
            this.entries.save(new Entry(null, new Date(), 1001L, 101L, user));
        entry.setDistance(1500L);
        this
            .getMvc()
            .perform(
            MockMvcRequestBuilders
                .post(EntryController.PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.json(entry))
                .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(
                MockMvcResultMatchers
                    .content().contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    ID_PATH, Matchers.is(entry.getId().intValue())
                )
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$.distance", Matchers.is(entry.getDistance().intValue())
                )
            );
    }

    /**
     * Any user can delete an entry.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userDeletes() throws Exception {
        final String name = "nameE";
        final String password = "passwordE";
        final User user = this.users.save(
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_USER)
                .build()
        );
        final Entry entry =
            this.entries.save(new Entry(null, new Date(), 1002L, 102L, user));
        this
            .getMvc()
            .perform(
            MockMvcRequestBuilders
                .delete(this.subPath(entry.getId()))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Any user can filter entries.
     * @throws Exception If smth goes wrong.
     */
    @Test
    public final void userFilters() throws Exception {
        final SimpleDateFormat sdf = new SimpleDateFormat(
            Format.DATE_FORMAT, Locale.getDefault()
        );
        final String name = "nameF";
        final String password = "passwordF";
        final User user = this.users.save(
            User
                .builder()
                .name(name)
                .password(password)
                .role(Role.ROLE_USER)
                .build()
        );
        final Date date = new Date();
        this.entries.save(new Entry(null, date, 1003L, 103L, user));
        this
            .getMvc()
            .perform(
            MockMvcRequestBuilders
                .get(
                    String.format(
                        "%s/%s/%s",
                        EntryController.PATH,
                        sdf.format(new Date(date.getTime() - 1)),
                        sdf.format(new Date(date.getTime() + 1))
                    )
                )
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .headers(this.addAuth(new HttpHeaders(), name, password))
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Path to entry controller.
     * @param obj Object.
     * @return Path.
     */
    private String subPath(final Object obj) {
        return String.format("%s/%s", EntryController.PATH, obj);
    }

}
