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

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.layouts.MHorizontalLayout;

/**
 * Provides create, update, delete, refresh buttons.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@SuppressWarnings("serial")
public final class CrudToolbar extends MHorizontalLayout {

    /**
     * Add button.
     */
    private final Button add;

    /**
     * Edit button.
     */
    private final Button edit;

    /**
     * Delete button.
     */
    private final Button delete;

    /**
     * Refresh button.
     */
    @SuppressWarnings("PMD.SingularField")
    private final Button refresh;

    /**
     * Ctor.
     * @param crud Crud implementation.
     * @param components Additional components.
     */
    public CrudToolbar(final Crud crud, final Component... components) {
        super();
        this.add = new MButton(FontAwesome.PLUS_CIRCLE, e -> crud.add());
        this.edit = new MButton(FontAwesome.PENCIL_SQUARE_O, e -> crud.edit());
        this.delete = new MButton(FontAwesome.MINUS_CIRCLE, e -> crud.delete());
        this.refresh = new MButton(FontAwesome.REFRESH, e -> crud.update());
        this.addComponents(this.add, this.edit, this.delete, this.refresh);
        this.addComponents(components);
        this.setEditAndDeleteEnabled(false);
    }

    /**
     * Enables / disables both edit and delete button.
     * @param enabled Will be enabled or not.
     */
    public void setEditAndDeleteEnabled(final boolean enabled) {
        this.setEditEnabled(enabled);
        this.setDeleteEnabled(enabled);
    }

    /**
     * Enables / disables edit button.
     * @param enabled Will be enabled or not.
     */
    public void setEditEnabled(final boolean enabled) {
        this.edit.setEnabled(enabled);
    }

    /**
     * Enables / disables delete button.
     * @param enabled Will be enabled or not.
     */
    public void setDeleteEnabled(final boolean enabled) {
        this.delete.setEnabled(enabled);
    }

    /**
     * Hides add button.
     */
    public void hideAddButton() {
        this.add.setVisible(false);
    }

    /**
     * Defines operations to create, edit, delete, refresh entities on the UI.
     * @author Alexey Saenko (alexey.saenko@gmail.com)
     * @version $Id$
     */
    public interface Crud extends Updateable {

        /**
         * Adds item.
         */
        void add();

        /**
         * Edits item.
         */
        void edit();

        /**
         * Deletes item.
         */
        void delete();
    }

}
