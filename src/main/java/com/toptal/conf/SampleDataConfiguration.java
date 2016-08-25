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

import com.toptal.dao.EntryDao;
import com.toptal.dao.UserDao;
import com.toptal.entities.Entry;
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Fills the database with the sample data.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@Configuration
public class SampleDataConfiguration {

    /**
     * Entry DAO.
     */
    @Autowired
    private transient EntryDao entries;

    /**
     * User DAO.
     */
    @Autowired
    private transient UserDao users;

    /**
     * Fills database with the data.
     * @checkstyle LocalFinalVariableNameCheck (10 lines)
     * @checkstyle MagicNumberCheck (10 lines)
     */
    @PostConstruct
    public final void fill() {
        // @checkstyle MultipleStringLiteralsCheck (1 line)
        this.addEntries(this.createUser("mng", "mng", Role.ROLE_MANAGER));
        for (int idx = 1; idx <= 10; ++idx) {
            this.addEntries(
                this.createUser(
                    String.format("usr%s", idx),
                    String.format("pwd%s", idx),
                    Role.ROLE_USER
                )
            );
        }
    }

    /**
     * Creates user.
     * @param name Name.
     * @param password Password.
     * @param role Role.
     * @return Saved user.
     */
    private User createUser(
        final String name,
        final String password,
        final Role role
    ) {
        final User result = new User();
        result.setName(name);
        result.setPassword(password);
        result.setRole(role);
        return this.users.save(result);
    }

    /**
     * Adds entries to the given user.
     * @param user User.
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void addEntries(final User user) {
        final ThreadLocalRandom rnd = ThreadLocalRandom.current();
        final int size = rnd.nextInt(10, 100);
        final Calendar cal = Calendar.getInstance();
        final long end = cal.getTimeInMillis();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 2);
        final long start = cal.getTimeInMillis();
        for (int idx = 0; idx < size; ++idx) {
            this.entries.save(
                Entry
                    .builder()
                    .date(new Date(rnd.nextLong(start, end)))
                    // @checkstyle MagicNumberCheck (2 lines)
                    .distance(rnd.nextLong(500, 10000))
                    .time(rnd.nextLong(120000, 2400000))
                    .user(user)
                    .build()
            );
        }
    }

}
