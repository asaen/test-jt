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

import com.toptal.entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * User DAO implementation.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
public class UserDaoImpl implements UserDaoCustom {

    /**
     * User name field.
     */
    private static final String NAME_FIELD = "name";

    /**
     * Entity manager.
     */
    @PersistenceContext
    private transient EntityManager manager;

    @Override
    public final User findByName(final String name) {
        final CriteriaBuilder builder = this.manager.getCriteriaBuilder();
        final CriteriaQuery<User> query = builder.createQuery(User.class);
        final Root<User> root = query.from(User.class);
        query.where(builder.equal(root.get(NAME_FIELD), name));
        return this.getSingleResult(query);
    }

    @Transactional
    @Override
    public final User saveIgnoringPassword(final User user) {
        final CriteriaBuilder builder = this.manager.getCriteriaBuilder();
        final CriteriaUpdate<User> update =
            builder.createCriteriaUpdate(User.class);
        final Root<User> root = update.from(User.class);
        update.set(root.get(NAME_FIELD), user.getName());
        update.set(root.get("role"), user.getRole());
        update.where(builder.equal(root.get("id"), user.getId()));
        this.manager.createQuery(update).executeUpdate();
        return this.manager.find(User.class, user.getId());
    }

    /**
     * Single result from query.
     * @param query Criteria query.
     * @return Single entity.
     */
    private User getSingleResult(final CriteriaQuery<User> query) {
        final List<User> users =
            this.manager.createQuery(query).getResultList();
        User result = null;
        if (!CollectionUtils.isEmpty(users)) {
            result = users.get(0);
        }
        return result;
    }

}
