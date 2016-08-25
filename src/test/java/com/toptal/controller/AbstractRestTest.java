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

import com.fasterxml.jackson.databind.MapperFeature;
import com.toptal.conf.SecurityUtils;
import com.toptal.entities.User;
import com.toptal.entities.User.Role;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Class has some abstract stuff for testing REST API.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@WebAppConfiguration
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
@Slf4j
public abstract class AbstractRestTest {

    /**
     * Mock MVC.
     */
    private transient MockMvc mvc;

    /**
     * Http Message converter.
     */
    private transient MappingJackson2HttpMessageConverter converter;

    /**
     * Web application context.
     */
    @Autowired
    private transient WebApplicationContext context;

    /**
     * Spring security filter.
     * @checkstyle MemberNameCheck (4 lines)
     */
    @Autowired
    @SuppressWarnings("PMD.LongVariable")
    private transient Filter springSecurityFilterChain;

    /**
     * Sets mock mvc.
     * @throws Exception If something goes wrong.
     */
    @Before
    public final void setMockMvc() throws Exception {
        this.mvc =
            MockMvcBuilders
                .webAppContextSetup(this.context)
                .addFilters(this.springSecurityFilterChain)
                .build();
    }

    /**
     * Sets http message converter.
     */
    @Before
    public final void setConverter() {
        this.converter = new MappingJackson2HttpMessageConverter();
        this.converter.getObjectMapper().disable(MapperFeature.USE_ANNOTATIONS);
    }

    /**
     * Gets mock mvc.
     * @return MVC.
     */
    public final MockMvc getMvc() {
        return this.mvc;
    }

    /**
     * Converts the given object to json string.
     * @param obj Given object.
     * @return Json string.
     * @throws IOException If something goes wrong.
     */
    protected final String json(final Object obj) throws IOException {
        final MockHttpOutputMessage result = new MockHttpOutputMessage();
        this.converter.write(obj, MediaType.APPLICATION_JSON_UTF8, result);
        final String json = result.getBodyAsString();
        log.debug("Json of object: {}", json);
        return json;
    }

    /**
     * Adds Authorization http header.
     * @param headers Http headers.
     * @param user User name.
     * @param password Password.
     * @return Http headers.
     */
    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    protected final HttpHeaders addAuth(
        final HttpHeaders headers,
        final String user,
        final String password
    ) {
        headers.add(
            "Authorization",
            String.format(
                "Basic %s",
                SecurityUtils.encode(
                    String.format("%s:%s", user, password)
                )
            )
        );
        return headers;
    }

    /**
     * Registers a user with rest.
     * @param name User name.
     * @param password Password.
     * @param role Role.
     * @throws Exception If smth goes wrong.
     */
    protected final void registerUser(
        final String name, final String password, final Role role
    ) throws Exception {
        this.getMvc()
            .perform(
                MockMvcRequestBuilders
                    .post(SignupController.PATH)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(
                        this.json(
                            User
                                .builder()
                                .name(name)
                                .password(SecurityUtils.encode(password))
                                .role(role)
                                .build()
                        )
                    )
            )
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(
                MockMvcResultMatchers
                    .content().contentType(MediaType.APPLICATION_JSON_UTF8)
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.id", Matchers.notNullValue())
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath(
                    "$.name", Matchers.is(name)
                )
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.password").doesNotExist()
            )
            .andExpect(
                MockMvcResultMatchers
                    .jsonPath(
                        "$.role", Matchers.is(role.toString())
                    )
            );
    }

    /**
     * Converts iterable to list.
     * @param iterable Iterable.
     * @param <T> Type of items.
     * @return List of items.
     */
    protected final <T> List<T> toList(final Iterable<T> iterable) {
        final List<T> result = new LinkedList<T>();
        for (final T item : iterable) {
            result.add(item);
        }
        return result;
    }

    /**
     * Check against size of elements.
     * @param size Size.
     * @return Result matcher.
     */
    protected final ResultMatcher listHasSize(final int size) {
        return MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(size));
    }

}
