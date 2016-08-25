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

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link SecurityUtils}.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
public final class SecurityUtilsTest {

    /**
     * Class can encode the given string into base64.
     */
    @Test
    public void encodes() {
        Assert.assertEquals(
            "U29tZSBpbnB1dCB0ZXh0",
            SecurityUtils.encode("Some input text")
        );
    }

    /**
     * Class can decode the given string from base64.
     */
    @Test
    public void decodes() {
        Assert.assertEquals(
            "Hello world",
            SecurityUtils.decode("SGVsbG8gd29ybGQ=")
        );
    }

    /**
     * Class can encode and decode the given string.
     */
    @Test
    public void encodesAndDecodes() {
        final String input =
            "Java is a general-purpose computer programming language that...";
        Assert.assertEquals(
            input,
            SecurityUtils.decode(SecurityUtils.encode(input))
        );
    }

}
