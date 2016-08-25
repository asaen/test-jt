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
package com.toptal.ui.tab;

import com.toptal.conf.SecurityUtils;
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import com.toptal.ui.CrudToolbar;
import com.toptal.ui.UserForm;
import com.toptal.ui.converter.RoleConverter;
import com.toptal.ui.view.MainView;
import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import java.util.LinkedList;
import java.util.List;
import org.vaadin.viritin.fields.MTable;

/**
 * Shows and CRUD users.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings({ "serial", "PMD.UnusedPrivateMethod" })
public final class UserTab extends AbstractTab implements CrudToolbar.Crud {

    /**
     * Table of users.
     */
    private final MTable<User> tbl = this.table();

    /**
     * CRUD toolbar.
     */
    private final CrudToolbar toolbar = new CrudToolbar(this);

    /**
     * Ctor.
     * @param view Main view.
     * @param caption Caption.
     */
    public UserTab(final MainView view, final String caption) {
        super(view, caption);
    }

    /**
     * Builds user tab.
     * @return User tab.
     */
    public UserTab build() {
        this.toolbar.hideAddButton();
        this.addComponent(this.toolbar.alignAll(Alignment.MIDDLE_LEFT));
        this.addComponent(this.tbl);
        this.expand(this.tbl);
        this.update();
        return this;
    }

    @Override
    public void add() {
        this.userForm(User.builder().role(Role.ROLE_USER).build());
    }

    @Override
    public void edit() {
        this.userForm(this.tbl.getValue());
    }

    @Override
    public void delete() {
        this.getView().getUi().getUsers().delete(this.tbl.getValue().getId());
        this.getView().onUserChange();
        this.tbl.setValue(null);
    }

    @Override
    public void update() {
        final List<User> users = new LinkedList<>();
        // @checkstyle RequireThisCheck (1 line)
        this.getView().getUi().getUsers().list().forEach(users::add);
        this.tbl.setBeans(users);
    }

    @Override
    public MTable<User> table() {
        final String uid = "id";
        final String name = "name";
        final String role = "role";
        final MTable<User> result =
            new MTable<>(User.class)
                .withProperties(
                    uid,
                    name,
                    role
                )
                .withColumnHeaders(
                    "ID",
                    "Name",
                    "Role"
                )
                .setSortableProperties(uid, name)
                .withFullWidth();
        // @checkstyle RequireThisCheck (1 line)
        result.addValueChangeListener(this::onValueChanged);
        result.setConverter(role, new RoleConverter());
        return result;
    }

    /**
     * Creates user form.
     * @param user User.
     * @checkstyle RequireThisCheck (5 lines)
     */
    private void userForm(final User user) {
        final UserForm form = new UserForm(user);
        form.setSavedHandler(this::save);
        form.setResetHandler(this::reset);
        form.openInModalPopup();
    }

    /**
     * Saves user.
     * @param user User.
     */
    private void save(final User user) {
        this.getView().getUi().getUsers().update(user);
        this.reset(user);
    }

    /**
     * Resets table.
     * @param user User.
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void reset(final User user) {
        this.getView().onUserChange();
        this.getView().getUi().closeWindow();
    }

    /**
     * Actions to be performed when actual value changed.
     * @param event Value change event.
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void onValueChanged(final Property.ValueChangeEvent event) {
        this.toolbar.setEditEnabled(this.tbl.getValue() != null);
        this.toolbar.setDeleteEnabled(
            this.tbl.getValue() != null
            && !this.tbl
                .getValue()
                .getId()
                .equals(SecurityUtils.actualUser().getId())
        );
    }

}
