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
package com.toptal.dao;

import com.toptal.Bootstrap;
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Tests for {@link UserDaoImpl}.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Bootstrap.class)
public class UserDaoImplTest {

    /**
     * User dao.
     */
    @Autowired
    private transient UserDao dao;

    /**
     * DAO can find user by name.
     */
    @Test
    public final void canFindByName() {
        final String name = "canFindByName";
        final User user = new User();
        user.setName(name);
        user.setPassword("testPassword");
        user.setRole(Role.ROLE_USER);
        this.dao.save(user);
        Assert.assertNotNull(this.dao.findByName(name));
    }

    /**
     * DAO returns {@code null} if no user found.
     */
    @Test
    public final void returnsNullIfNoneFound() {
        Assert.assertNull(this.dao.findByName("abcdef"));
    }

    /**
     * DAO can update user properly.
     */
    @Test
    public final void updatesUser() {
        final User user = new User();
        user.setName("updatesUser");
        user.setPassword("testPassword1");
        user.setRole(Role.ROLE_USER);
        final User saved = this.dao.save(user);
        saved.setName(String.format("%s_UPD", saved.getName()));
        final User updated = this.dao.save(saved);
        Assert.assertEquals(saved.getId(), updated.getId());
        Assert.assertEquals("updatesUser_UPD", updated.getName());
        user.setId(updated.getId());
        final User old = this.dao.save(user);
        Assert.assertEquals(user.getId(), old.getId());
        Assert.assertEquals(user.getName(), old.getName());
    }

    /**
     * DAO can save user ignoring password.
     */
    @Test
    public final void savesIgnoringPassword() {
        final String name = "nname";
        final User user =
            new User(null, name, "aqaqaq", Role.ROLE_USER, null);
        final User saved = this.dao.save(user);
        Assert.assertNotNull(saved.getId());
        Assert.assertEquals(name, saved.getName());
        saved.setName("nnnnnnnn");
        final User updated = this.dao.saveIgnoringPassword(saved);
        Assert.assertEquals(saved.getName(), updated.getName());
        Assert.assertNotEquals(name, updated.getName());
    }

}
