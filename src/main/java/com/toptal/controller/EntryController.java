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

import com.toptal.conf.SecurityUtils;
import com.toptal.dao.EntryDao;
import com.toptal.entities.Entry;
import com.toptal.entities.Week;
import com.toptal.error.AbstractException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest API for managing entries.
 *
 * @author Alexey Saenko (alexey.saenko@gmail.com)
 * @version $Id$
 */
@RestController
@RequestMapping(
    value = EntryController.PATH,
    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
public class EntryController {

    /**
     * Signup REST path.
     */
    public static final String PATH = "/rest/entry";

    /**
     * Entry DAO.
     */
    @Autowired
    private transient EntryDao dao;

    /**
     * All entries of the actual user.
     * @return Entries.
     */
    @RequestMapping(method = RequestMethod.GET)
    public final Iterable<Entry> list() {
        Iterable<Entry> result = SecurityUtils.actualUser().getEntries();
        if (result == null) {
            result = Collections.emptyList();
        }
        return result;
    }

    /**
     * Weekly statistics.
     * @return Weeks.
     */
    @RequestMapping(value = "/weeks", method = RequestMethod.GET)
    @SuppressWarnings({"PMD.UseConcurrentHashMap",
        "PMD.AvoidInstantiatingObjectsInLoops"})
    public final Iterable<Week> weeks() {
        final Map<String, Week> map = new HashMap<>();
        for (final Entry entry : this.list()) {
            final String key = Week.formatNumber(entry.getDate());
            if (map.containsKey(key)) {
                map.get(key).addEntry(entry);
            } else {
                map.put(key, new Week(entry));
            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * Creates new one or modifies existing entry.
     * @param entry Entry.
     * @return Saved entry.
     */
    @RequestMapping(method = RequestMethod.POST)
    public final Entry save(@RequestBody final Entry entry) {
        this.checkId(entry.getId());
        entry.setUser(SecurityUtils.actualUser());
        return this.dao.save(entry);
    }

    /**
     * Deletes.
     * @param eid Entry ID.
     */
    @RequestMapping(value = "/{eid}", method = RequestMethod.DELETE)
    public final void delete(@PathVariable final Long eid) {
        this.checkId(eid);
        this.dao.delete(eid);
    }

    /**
     * Filtered entries of the actual user.
     * @param start Start date.
     * @param end End date.
     * @return Entries from the period start to end.
     */
    @RequestMapping(
        value = "/{start}/{end}",
        method = RequestMethod.GET
        )
    public final Iterable<Entry> filter(
        @PathVariable @DateTimeFormat(iso = ISO.DATE) final Date start,
        @PathVariable @DateTimeFormat(iso = ISO.DATE) final Date end
    ) {
        if (end.before(start)) {
            throw new IllegalArgumentException("Start date must be before end");
        }
        final List<Entry> result = new LinkedList<Entry>();
        List<Entry> entries = SecurityUtils.actualUser().getEntries();
        if (entries == null) {
            entries = Collections.emptyList();
        }
        for (final Entry entry : entries) {
            if (entry.getDate().after(start)
                && entry.getDate().before(end)) {
                result.add(entry);
            }
        }
        return result;
    }

    /**
     * Checks if there is an entry with the specified id and if it is associated
     * with the actual user.
     * @param eid Entry id.
     * @return Entry object if found.
     */
    private Optional<Entry> checkId(final Long eid) {
        Optional<Entry> result = Optional.empty();
        if (eid != null) {
            final Entry existing = this.dao.findOne(eid);
            if (existing != null
                && Objects.equals(
                    existing.getUser().getId(),
                    SecurityUtils.actualUser().getId()
                )
            ) {
                result = Optional.of(existing);
            }
            if (!result.isPresent()) {
                AbstractException.throwEntryIdNotFound(eid);
            }
        }
        return result;
    }

}
