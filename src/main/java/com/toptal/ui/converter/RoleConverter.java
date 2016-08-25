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
package com.toptal.ui.converter;

import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import com.vaadin.data.util.converter.Converter;
import java.util.Locale;

/**
 * Role converter.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings({ "serial", "rawtypes" })
public final class RoleConverter implements Converter<String, User.Role> {

    @Override
    public Role convertToModel(
        final String value,
        final Class<? extends Role> target,
        final Locale locale
    ) {
        // @checkstyle AvoidInlineConditionalsCheck (1 line)
        return value == null ? null : Role.byText(value);
    }

    @Override
    public String convertToPresentation(
        final Role value,
        final Class<? extends String> target,
        final Locale locale
    ) {
        // @checkstyle AvoidInlineConditionalsCheck (1 line)
        return value == null ? null : value.text();
    }

    @Override
    public Class<Role> getModelType() {
        return Role.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
