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
package com.toptal.ui;

import com.toptal.entities.User;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import java.util.Arrays;
import org.vaadin.viritin.fields.MPasswordField;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * Add/edit user.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings("serial")
public final class UserForm extends AbstractForm<User> {

    /**
     * Text field for name.
     */
    private final TextField name = new MTextField("Username");

    /**
     * Text field for password.
     */
    private final PasswordField password = new MPasswordField("Password");

    /**
     * Combobox for role.
     */
    private final ComboBox role = new ComboBox(
        "Roles", Arrays.asList(User.Role.values())
    );

    /**
     * Ctor.
     * @param user User.
     * @checkstyle MagicNumberCheck (25 lines)
     */
    public UserForm(final User user) {
        super();
        this.setSizeUndefined();
        this.setEntity(user);
        this.role.setNullSelectionAllowed(false);
        this.role.select(user.getRole());
        this.name.addValidator(
            new StringLengthValidator(
                "Username has to be at least 3 chars long",
                3,
                -1,
                false
            )
        );
        this.password.addValidator(
            new StringLengthValidator(
                "Password has to be at least 3 chars long",
                3,
                -1,
                false
            )
        );
    }

    @Override
    public Component createContent() {
        return new MVerticalLayout(
            new MFormLayout(
                this.name, this.password, this.role
            ),
            this.getToolbar()
        );
    }

}
