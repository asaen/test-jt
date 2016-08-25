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
package com.toptal.error;

/**
 * Custom exception.
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
public abstract class AbstractException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -9004735644687799038L;

    /**
     * Ctor.
     * @param format Error message format.
     * @param args Arguments.
     */
    AbstractException(final String format, final Object... args) {
        super(String.format(format, args));
    }

    /**
     * User id must be null.
     */
    public static void throwUserIdIsNotNull() {
        throw new BadRequestException("User id must be null");
    }

    /**
     * User name '%s' already exists.
     * @param name User name.
     */
    public static void throwUserNameExists(final String name) {
        throw new BadRequestException("User name '%s' already exists", name);
    }

    /**
     * User '%s' cannot be removed.
     * @param name User name.
     */
    public static void throwActualUserCannotBeRemoved(final String name) {
        throw new BadRequestException("User '%s' cannot be removed", name);
    }

    /**
     * User with id %s does not exist.
     * @param uid User id.
     */
    public static void throwUserIdNotFound(final Long uid) {
        throw new NotFoundException("User with id %s does not exist", uid);
    }

    /**
     * Entry with id %s for the actual user was not found.
     * @param eid Entry id.
     */
    public static void throwEntryIdNotFound(final Long eid) {
        throw new NotFoundException(
            "Entry with id %s for the actual user was not found", eid
        );
    }

    /**
     * Invalid login or password.
     */
    public static void throwInvalidLoginPassword() {
        throw new UnauthorizedException("Invalid login or password");
    }

}
